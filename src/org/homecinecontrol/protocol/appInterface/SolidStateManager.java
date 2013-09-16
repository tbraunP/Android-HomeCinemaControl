package org.homecinecontrol.protocol.appInterface;

import org.homecinecontrol.protocol.components.Components;
import org.homecinecontrol.protocol.components.solidstate.SolidState;
import org.homecinecontrol.protocol.connection.Connection;
import org.homecinecontrol.protocol.connection.Frame;
import org.homecinecontrol.protocol.observer.ObservableImpl;
import org.homecinecontrol.protocol.observer.Observer;

/**
 * Central manager for solid state relais states
 * 
 * This components registers as receiver (and sender) for requests regarding the
 * solid state relais controlled by the controller
 * 
 * @author pyro
 * 
 */
public class SolidStateManager extends ObservableImpl<SolidState> implements
		Observer<Frame> {

	// SolidState Devices
	public static final int BEAMER = 0x00;
	public static final int LEINWAND = 0x01;
	public static final int AMPLIFIER = 0x02;

	// On and off state
	public static final int ON = 0x01;
	public static final int OFF = 0x00;

	// Singleton-Pattern
	private static SolidStateManager instance = null;

	public static SolidStateManager getInstance() {
		if (instance == null) {
			instance = new SolidStateManager();
			Connection.getInstance().register(Components.SOLIDSTATE, instance);
		}
		return instance;
	}

	private SolidStateManager() {
	}

	/**
	 * Called on received frame from the solid state component of the controller
	 */
	@Override
	public void update(Object observable, Frame frame) {
		SolidState relais = new SolidState(frame.getPayload());
		notifyObserver(relais);
	}

	public void setRelais(int relaisNumber, int state) {
		SolidState relais = new SolidState(relaisNumber, state);
		setRelais(relais);
	}

	public void setRelais(SolidState relais) {
		Frame fp = new Frame(0x01, Components.SOLIDSTATE, relais.getPayload());
		Connection.getInstance().transfer(fp);
	}

	/**
	 * Inform registered observers about the state change of relais
	 */
	@Override
	public void notifyObserver(SolidState relais) {
		synchronized (observers) {
			if (observers.containsKey(relais.getRelais())) {
				observers.get(relais.getRelais()).update(this, relais);
			}
		}
	}
}
