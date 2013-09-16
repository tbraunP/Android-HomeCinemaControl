package org.homecinecontrol.protocol.components.irmp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IRMP_Data {

	private int protocol;
	private int address;
	private int command;
	private int flags;

	public IRMP_Data(int protocol, int address, int command, int flags) {
		this.protocol = protocol;
		this.address = protocol;
		this.command = command;
		this.flags = flags;

		normalize();
	}

	public IRMP_Data(ByteBuffer encoded) {
		encoded.position(0);
		this.protocol = encoded.get();
		this.address = encoded.getShort();
		this.command = encoded.getShort();
		this.flags = encoded.get();

		normalize();
	}

	private void normalize() {
		this.protocol &= 0xFF;
		this.address &= 0xFFFF;
		this.command &= 0xFFFF;
		this.flags &= 0xFF;
	}

	public ByteBuffer getPayload() {
		// create irmp data
		ByteBuffer irmp_data = ByteBuffer.allocate(6);
		irmp_data.order(ByteOrder.LITTLE_ENDIAN);

		// protocol
		irmp_data.put((byte) protocol);

		// address
		irmp_data.putShort((short) address);

		// command
		irmp_data.putShort((short) command);

		// flags
		irmp_data.put((byte) flags);

		// set position to zero
		irmp_data.position(0);

		return irmp_data;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

}
