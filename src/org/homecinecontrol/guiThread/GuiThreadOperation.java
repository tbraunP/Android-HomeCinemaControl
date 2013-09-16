package org.homecinecontrol.guiThread;

/**
 * Perform a operation within the gui thread, be aware that this should be a
 * very short operation
 * 
 * @author pyro
 * 
 */
public interface GuiThreadOperation {

	/**
	 * Perform GUI operation
	 */
	public void performGuiOperation();
}
