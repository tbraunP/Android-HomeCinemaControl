package org.homecinecontrol.protocol.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.homecinecontrol.ControlHomeActivity;
import org.homecinecontrol.guiThread.ThreadControlManager;
import org.homecinecontrol.protocol.observer.FrameObserverImpl;
import org.homecinecontrol.protocol.observer.Observable;

import android.content.Context;
import android.util.Log;

public class Connection extends FrameObserverImpl implements Observable<Frame>,
		Runnable {

	private static Connection instance = null;

	public static Connection getInstance() {
		if (instance == null)
			instance = new Connection();
		return instance;
	}

	// Connection parameters
	private final int port = 666;
	private String host;

	private Context context;
	private volatile Socket socket = null;
	private volatile Transmitter transmitter = null;
	private volatile Receiver receiver = null;
	private volatile BlockingQueue<Frame> outgoingFrames = new LinkedBlockingQueue<Frame>();

	private Connection() {
	}

	public void connect(String host) {
		this.host = host;
		new Thread(this).start();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private void reportError(final String errorMessage) {
		ThreadControlManager.getInstance().displayToastMessage(context,
				"Can not connect to " + host + ". " + errorMessage);
	}

	private void reportSuccess() {
		ThreadControlManager.getInstance().displayToastMessage(context,
				"Connection established.");
	}

	@Override
	public void run() {
		try {
			InetAddress serverAddr = InetAddress.getByName(host);
			socket = new Socket(serverAddr, port);
		} catch (Exception e) {
			reportError(e.toString());
			return;
		}

		Log.e(ControlHomeActivity.APP, "Connection");
		Log.e(ControlHomeActivity.APP, "Connection");

		// receiver
		receiver = new Receiver(this, socket);
		receiver.start();

		// transmitter
		transmitter = new Transmitter(socket, outgoingFrames);
		transmitter.start();

		// tell user
		reportSuccess();
	}

	public boolean transfer(Frame fp) {
		return outgoingFrames.offer(fp);
	}

	/**
	 * Transmit frames from queue
	 * 
	 * @author pyro
	 * 
	 */
	private static class Transmitter extends Thread {
		private final Socket socket;
		private final BlockingQueue<Frame> outgoingFrames;

		public Transmitter(Socket socket, BlockingQueue<Frame> outgoingFrames) {
			this.socket = socket;
			this.outgoingFrames = outgoingFrames;
		}

		public void run() {
			for (;;) {
				if (Thread.interrupted()) {
					return;
				}

				Frame fp = null;
				try {
					fp = outgoingFrames.take();
				} catch (InterruptedException e1) {
					return;
				}

				// build physical frame
				int len = 8 + fp.getPayload().limit();
				ByteBuffer bt = ByteBuffer.allocate(len);

				bt.put((byte) 0xAB);
				bt.put((byte) 0xCD);
				bt.put((byte) len);
				bt.put((byte) fp.getType());
				bt.put((byte) fp.getComponent());
				bt.put((byte) 0xFF);
				bt.put(fp.getPayload().array());
				bt.put((byte) 0xEF);
				bt.put((byte) 0xFE);

				// set position pointer to 0
				bt.position(0);

				// transfer frame
				try {
					socket.getOutputStream().write(bt.array());
				} catch (IOException e) {
					Log.e(ControlHomeActivity.APP, e.toString());
					return;
				}
			}
		}
	}

	/**
	 * Wait for incoming frames
	 * 
	 * @author pyro
	 * 
	 */
	private static class Receiver extends Thread {

		private enum ParseState {
			init, preambleFound, headerFound
		};

		private final Socket socket;
		private final FrameObserverImpl observer;

		public Receiver(FrameObserverImpl observer, Socket socket) {
			this.socket = socket;
			this.observer = observer;
		}

		private final List<Byte> dataBuffer = new ArrayList<Byte>();

		@Override
		public void run() {
			try {
				for (;;) {
					// receive data
					do {
						// exit on interrupt
						if (Thread.interrupted()) {
							return;
						}

						byte[] b = new byte[200];
						int bRead = socket.getInputStream()
								.read(b, 0, b.length);
						System.out.println("Receiving bytes....\n");
						for (int i = 0; i < bRead; i++)
							dataBuffer.add(b[i]);

					} while (socket.getInputStream().available() > 0);

					// try to parse frame
					parseFrame();
				}
			} catch (IOException e) {
			}
		}

		private ParseState state = ParseState.init;
		private int length, type, component;

		private void parseFrame() {
			for (;;) {
				switch (state) {
				case init: {
					// search preamble
					while (dataBuffer.size() >= 2) {
						if (dataBuffer.get(0) == ((byte) 0xAB)
								&& dataBuffer.get(1) == ((byte) 0xCD)) {
							state = ParseState.preambleFound;
							break;
						} else {
							// remove one byte and try again to match preamble
							dataBuffer.remove(0);
						}
					}
					// wait for more data
					if (state != ParseState.preambleFound)
						return;
				}
				case preambleFound: {
					// wait for more data
					if (dataBuffer.size() < (4 + 2))
						return;
					// interpret header
					length = ((int) dataBuffer.get(2)) & 0xFF;
					type = ((int) dataBuffer.get(3)) & 0xFF;
					component = ((int) dataBuffer.get(4)) & 0xFF;

					// check valid header format
					if (dataBuffer.get(5) == ((byte) 0xFF)) {
						state = ParseState.headerFound;
					} else {
						// preamble seems to be not valid, so try again with
						// next one
						dataBuffer.remove(0);
						state = ParseState.init;
						break;
					}
				}
				case headerFound: {
					// wait for more data
					if (dataBuffer.size() < length)
						return;
					// look for end
					if (dataBuffer.get(length - 2) == ((byte) 0xEF)
							&& dataBuffer.get(length - 1) == ((byte) 0xFE)) {
						// copy payload and store frame
						frameFound(type, component, copyPayload(dataBuffer));

						// remove frame from buffer
						for (int i = 0; i < length; i++)
							dataBuffer.remove(0);
					} else {
						// something went wrong, retry
						dataBuffer.remove(0);
					}
					state = ParseState.init;
					break;
				}
				default:
					Log.e(ControlHomeActivity.APP, "WTF unknown state\n");
				}
			}
		}

		private ByteBuffer copyPayload(List<Byte> dataBuffer2) {
			ByteBuffer payload = ByteBuffer.allocate(length - 8);
			payload.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 6; i < length - 2; i++)
				payload.put(dataBuffer.get(i));
			payload.position(0);
			return payload;
		}

		private void frameFound(int type, int component, ByteBuffer payload) {
			while (true)
				Log.e(ControlHomeActivity.APP, "Frame found from " + component);
			// Frame fp = new Frame(type, component, payload);
			// observer.notifyObserver(fp);
		}
	}

	private void terminate(Thread t) {
		if (t != null) {
			t.interrupt();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {

		terminate(transmitter);

		if (socket != null && socket.isConnected()) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}

		terminate(receiver);
	}

}
