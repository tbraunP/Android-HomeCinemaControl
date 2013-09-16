package org.homecinecontrol.guiThread.impl;

import org.homecinecontrol.guiThread.GuiThreadOperation;

import android.content.Context;
import android.widget.Toast;

public class ToastGuiOperation implements GuiThreadOperation {
	private final Context context;
	private final String message;

	public ToastGuiOperation(Context context, String message) {
		this.context = context;
		this.message = message;
	}

	public void performGuiOperation() {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
