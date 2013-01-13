package p2p;

import java.net.InetAddress;

public class Peer {
	//TODO: ID generieren; id space: 0 - 1461501637330902918203684832716283019655932542976
	
	private InetAddress address;
	private int port;
	private long ID;
	
	
	public Peer(long ID, InetAddress addr, int port) {
		this.ID = ID;
		this.address = addr;
		this.port = port;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public long getID() {
		return ID;
	}
	
	public void setID(long iD) {
		ID = iD;
	}
	
}