package org.homecinecontrol.protocol.appInterface;

import org.homecinecontrol.protocol.components.Components;
import org.homecinecontrol.protocol.components.irmp.IRMP_Data;
import org.homecinecontrol.protocol.connection.Connection;
import org.homecinecontrol.protocol.connection.Frame;
import org.homecinecontrol.protocol.observer.ObservableImpl;
import org.homecinecontrol.protocol.observer.Observer;

public class IRMPManager extends ObservableImpl<IRMP_Data> implements
		Observer<Frame> {

	// Singleton-Pattern
	private static IRMPManager instance = null;

	public static IRMPManager getInstance() {
		if (instance == null) {
			instance = new IRMPManager();
			// only IRMP may send data
			Connection.getInstance().register(Components.IRMP, instance);
		}
		return instance;
	}

	private IRMPManager() {
	}

	/**
	 * Called on received frame from the IRMP component of the controller
	 */
	@Override
	public void update(Object observable, Frame notification) {
		// FIXME: IRMP Daten und Kommando trennen
		IRMP_Data irmp = new IRMP_Data(notification.getPayload());
		notifyObserver(irmp);
	}

	public void activateIRReceiver(){
		// TODO
	}
	
	public void deactivateIRReceiver(){
		// TODO
	}
	
	/**
	 * Send data via IR
	 * 
	 * @param irmp
	 */
	public void send(IRMP_Data irmp) {
		Frame fp = new Frame(0x01, Components.IRSND, irmp.getPayload());
		Connection.getInstance().transfer(fp);
	}

	@Override
	public void notifyObserver(IRMP_Data fp) {
		synchronized (observers) {
			for (Observer<IRMP_Data> o : observers.values()) {
				o.update(this, fp);
			}
		}

	}
}
