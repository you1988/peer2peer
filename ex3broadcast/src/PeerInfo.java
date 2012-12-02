import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class PeerInfo {
	
	
	private InetAddress address;
	private int port;
	
	
	/**
	 * Create a new instance of Peer.PeerInfo.
	 */
	public PeerInfo(InetAddress add, int port) {
		this.address = add;
		this.port = port;
	}
	
	InetSocketAddress getInfo() {
		return new InetSocketAddress(this.address, this.port);
	}
	
	Peer getPeer() {
		for (Peer p : UInterface.getInterface().listOfPeers) {
			if (p.getInfo().equals(this)) {
				return p;
			}
		}
		return null;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		return (((PeerInfo)obj).port == this.port && ((PeerInfo)obj).address == this.address);
		
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.address.toString() + "\n" + String.valueOf(this.port);
	}
	
	public String serialize() {
		return this.address.toString() + "-" + String.valueOf(this.port);
	}
	
	public static PeerInfo deserialize(String string) {
		String[] parts = string.split("-");
		try {
			return new PeerInfo(InetAddress.getByName(parts[0]), Integer.valueOf(parts[1]));
		} catch (NumberFormatException | UnknownHostException e) {
			throw new RuntimeException(e);
		}
		
	}
}
