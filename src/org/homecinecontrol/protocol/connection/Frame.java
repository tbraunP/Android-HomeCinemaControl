package org.homecinecontrol.protocol.connection;
import java.nio.ByteBuffer;

/**
 * Frame received or to be transmitted
 * 
 * @author pyro
 */
public class Frame {

	private final int type;
	private final int component;

	private final ByteBuffer payload;

	public Frame(int type, int component, ByteBuffer payl) {
		this.type = type;
		this.component = component;

		// copy payload from payl buffer
		payload = ByteBuffer.allocate(payl.limit());
		int pos = payl.position();
		payl.position(0);

		// copy
		byte[] b = new byte[payl.limit()];
		payl.get(b);
		payload.put(b);

		// restore positions
		payl.position(pos);
		payload.position(0);
	}

	public int getType() {
		return type;
	}

	public int getComponent() {
		return component;
	}

	public ByteBuffer getPayload() {
		payload.position(0);
		return payload;
	}

}
