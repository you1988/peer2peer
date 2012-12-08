package p2p.peer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class PeerAddress {
	
	
	private InetAddress address;
	private int port;
	
	
	/**
	 * Create a new instance of Peer.PeerInfo.
	 */
	public PeerAddress(InetAddress add, int port) {
		this.address = add;
		this.port = port;
	}
	
	InetSocketAddress getInfo() {
		return new InetSocketAddress(this.address, this.port);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		return (((PeerAddress)obj).port == this.port && ((PeerAddress)obj).address.equals(this.address));
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.valueOf(this.port);
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.port;
	}
	
	public String serialize() {
		return this.address.getHostAddress() + "-" + String.valueOf(this.port);
	}
	
	public static PeerAddress deserialize(String string) {
		String[] parts = string.split("-");
		try {
			return new PeerAddress(InetAddress.getByName(parts[0]), Integer.valueOf(parts[1]));
		} catch (NumberFormatException | UnknownHostException e) {
			throw new RuntimeException(e);
		}
		
	}
}
