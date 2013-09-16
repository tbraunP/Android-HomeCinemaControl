package org.homecinecontrol.guiThread;

import org.homecinecontrol.guiThread.impl.ToastGuiOperation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Manager to execute GuiThreadOperation within the GUI-Thread
 * 
 * @author pyro
 * 
 */
public class ThreadControlManager {

	public static final int GUI_THREAD_OPERATION = 234;

	public static final ThreadControlManager instance = new ThreadControlManager();

	public static ThreadControlManager getInstance() {
		return instance;
	}

	private final Handler mHandler;

	@SuppressLint("HandlerLeak")
	private ThreadControlManager() {
		// Handler runs in UI Thread and can be used to update gui elements from
		// within a thread by calling
		mHandler = new Handler(Looper.getMainLooper()) {

			/*
			 * handleMessage() defines the operations to perform when the
			 * Handler receives a new Message to process.
			 */
			@Override
			public void handleMessage(Message inputMessage) {

				/*
				 * Chooses the action to take, based on the incoming message
				 */
				switch (inputMessage.what) {

				// If the download has started, sets background color to dark
				// green
				case GUI_THREAD_OPERATION:
					// Gets the image task from the incoming Message object.
					GuiThreadOperation guiOperation = (GuiThreadOperation) inputMessage.obj;
					guiOperation.performGuiOperation();
					break;

				default:
					// Otherwise, calls the super method
					super.handleMessage(inputMessage);
				}
			}
		};
	}

	public void performGUIOperation(GuiThreadOperation guiOperation) {
		Message completeMessage = mHandler.obtainMessage(GUI_THREAD_OPERATION,
				guiOperation);
		completeMessage.sendToTarget();
	}

	public void displayToastMessage(Context context, String message) {
		performGUIOperation(new ToastGuiOperation(context, message));
	}
}
