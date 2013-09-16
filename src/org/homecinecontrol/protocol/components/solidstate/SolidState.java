package org.homecinecontrol.protocol.components.solidstate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Solidstate Control
 * 
 * @author pyro
 * 
 */
public class SolidState {

	private int relais;
	private int state;

	public SolidState(int relais, int state) {
		this.relais = relais;
		this.state = state;

		normalize();
	}

	public SolidState(ByteBuffer encoded) {
		encoded.position(0);
		this.relais = encoded.get();
		this.state = encoded.get();

		normalize();
	}

	private void normalize() {
		this.relais &= 0xFF;
		this.state &= 0xFFFF;
	}

	public int getRelais() {
		return relais;
	}

	public void setRelais(int relais) {
		this.relais = relais;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public ByteBuffer getPayload() {
		ByteBuffer relais_state = ByteBuffer.allocate(2);
		relais_state.order(ByteOrder.LITTLE_ENDIAN);

		// protocol
		relais_state.put((byte) relais);

		// address
		relais_state.put((byte) state);

		relais_state.position(0);
		return relais_state;
	}
}
