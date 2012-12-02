import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class Peer extends Thread {
	
	
	private static final String REMOVE_PEER = "DELETE";
	private static final String ADD_PEER = "ADD";
	
	private static final PrintStream out = System.out;
	DatagramSocket socket;
	int port;
	DatagramChannel channel;
	private List<PeerInfo> neighbours;
	
	
	private void send(String msg, PeerInfo target) {
		try {
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(msg)), target.getInfo());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reliableSend(String msg, PeerInfo target) {
		//TODO: Mit timeout auf ACK warten 
		try {
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(msg)), target.getInfo());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast(String msg) {
		for (PeerInfo info : this.neighbours) {
			try {
				this.channel.send(Charset.forName("ascii").encode(
						CharBuffer.wrap(msg)), info.getInfo());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * send the neighbours list .
	 * 
	 * @param port
	 *            of the neighbour that will receive the list.
	 */
	public void sendNeighborsListToNeigh(int port, int neighbour) {
		char[] resp = null;
		
		SocketAddress neighbourAddress = new InetSocketAddress("localhost",
				port);
		resp = new String(REMOVE_PEER + " " + this.port).toCharArray();
		try {
			
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(resp)), neighbourAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (port != neighbour) {
			
			resp = new String(ADD_PEER + " " + port).toCharArray();
			SocketAddress neighbourAddress1 = new InetSocketAddress("localhost",
					neighbour);
			
			try {
				
				this.channel.send(Charset.forName("ascii").encode(
						CharBuffer.wrap(resp)), neighbourAddress1);
				//this.socket.send(answer);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * stop the peer
	 * 
	 * @throws IOException
	 */
	
	public void leaveMe() {
		//TODO: reliableSend Neighbours...
	}
	
	/**
	 * Get the port of current peer
	 */
	public int getPort() {
		return this.port;
	}
	
	public Peer(Peer neighbour) {
		this();
		//TODO: send addPeer to neighbour
		this.neighbours.add(neighbour.getInfo());
	}
	
	/**
	 * @return
	 */
	PeerInfo getInfo() {
		return new PeerInfo(this.socket.getLocalAddress(), this.socket.getLocalPort());
	}
	
	public Peer() {
		this.neighbours = new ArrayList<PeerInfo>();
		
		try {
			this.channel = DatagramChannel.open();
			this.channel.bind(new InetSocketAddress("localhost", 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addPeer(PeerInfo peer) {
		this.neighbours.add(peer);
		//TODO: refresh UI (maybe Listener)
	}
	
	public void removePeer(PeerInfo peer) {
		this.neighbours.remove(peer);
		//TODO: refresh UI (maybe Listener)
	}
	
	public void startServer() {
		while (!this.isInterrupted()) {
			final ByteBuffer buffer = ByteBuffer.allocate(64 * 1028);
			try {
				final SocketAddress sender = this.channel.receive(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			buffer.flip();
			
			String message = Charset.forName("ascii").decode(buffer).toString();
			out.print(message);
			String[] parts = message.split(" ");
			if (parts.length >= 1) {
				String command = parts[0];
				if (command.equalsIgnoreCase(ADD_PEER)) {
					PeerInfo peer = PeerInfo.deserialize(parts[1]);
					this.addPeer(peer);
				} else if (command.equalsIgnoreCase(REMOVE_PEER)) {
					PeerInfo peer = PeerInfo.deserialize(parts[1]);
					this.removePeer(peer);
				}
			}
		}
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return new PeerInfo(this.socket.getLocalAddress(), this.socket.getLocalPort()).toString();
	}
	
	@Override
	public void run() {
		
		this.startServer();
		
	}
	
	/**
	 * @return
	 */
	public List<PeerInfo> getNeighbours() {
		return this.neighbours;
	}
	
}
