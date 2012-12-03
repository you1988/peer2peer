import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections15.buffer.CircularFifoBuffer;


public class Peer extends Thread {
	
	
	private static final String REMOVE_PEER = "DEL";
	private static final String ADD_PEER = "ADD";
	private static final String REL_SEND = "REL";
	private static final String ACKNOWLEDGE = "ACK";
	private static final String BROADCAST_ITERATIVE = "BCI";
	private static final String BROADCAST_RECURSIVE = "BCR";
	private static final PrintStream out = System.out;
	
	DatagramChannel channel;
	private List<PeerInfo> neighbours;
	private CircularFifoBuffer<String> relIDs;
	private HashSet<String> buffer;
	private HashMap<String, List<String>> globalNetwork;
	private ExecutorService timer;
	
	
	public Peer() {
		this.neighbours = new ArrayList<PeerInfo>();
		this.relIDs = new CircularFifoBuffer<String>(20);
		this.globalNetwork = new HashMap<String, List<String>>();
		this.timer = Executors.newSingleThreadExecutor();
		try {
			this.channel = DatagramChannel.open();
			this.channel.bind(new InetSocketAddress("localhost", 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Peer(Peer neighbour) {
		this();
		StringBuilder b = new StringBuilder(ADD_PEER);
		b.append(" ");
		b.append(this.getInfo().serialize());
		this.send(b.toString(), neighbour.getInfo());
		this.neighbours.add(neighbour.getInfo());
	}
	
	public void addPeer(PeerInfo peer) {
		this.neighbours.add(peer);
	}
	
	/**
	 * @return
	 */
	public List<PeerInfo> getNeighbours() {
		return this.neighbours;
	}
	
	/**
	 * stop the peer
	 * 
	 * @throws IOException
	 */
	public void leaveMe() {
		PeerInfo neighbour;
		while (!this.neighbours.isEmpty()) {
			neighbour = this.neighbours.remove(0);
			StringBuilder allPeers = new StringBuilder(ADD_PEER);
			for (PeerInfo peer : this.neighbours) {
				allPeers.append(" ");
				allPeers.append(peer.serialize());
			}
			this.reliableSend(allPeers.toString(), neighbour);
			//TODO: Relies on a blocking reliableSend method,
			//if this is not the case this has to be implemented in another way
			StringBuilder b = new StringBuilder(REMOVE_PEER);
			b.append(" ");
			b.append(this.getInfo().serialize());
			this.send(b.toString(), neighbour);
		}
	}
	
	/**
	 * @param peer
	 */
	public void removePeer(PeerInfo peer) {
		this.neighbours.remove(peer);
		//TODO: refresh UI (maybe Listener)
	}
	
	@Override
	public void run() {
		
		this.startServer();
		
	}
	
	public void startServer() {
		while (!this.isInterrupted()) {
			final ByteBuffer buffer = ByteBuffer.allocate(64 * 1028);
			try {
				final SocketAddress sender = this.channel.receive(buffer);
				
				buffer.flip();
				String message = Charset.forName("ascii").decode(buffer).toString();
				out.print(message);
				this.parseMsg(message, sender);
			} catch (IOException e) {
				e.printStackTrace();
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
		DatagramSocket socket = this.channel.socket();
		return new PeerInfo(socket.getLocalAddress(), socket.getLocalPort()).toString();
	}
	
	/**
	 * @return
	 */
	PeerInfo getInfo() {
		DatagramSocket socket = this.channel.socket();
		return new PeerInfo(socket.getLocalAddress(), socket.getLocalPort());
	}
	
	private void broadcastIterative(String msg) {
		//TODO: Do it right...
		for (PeerInfo neighbour : this.neighbours) {
			StringBuilder allPeers = new StringBuilder(BROADCAST_ITERATIVE);
			for (PeerInfo peer : this.neighbours) {
				allPeers.append(" ");
				allPeers.append(peer.serialize());
			}
			this.send(allPeers.toString(), neighbour);
		}
	}
	
	/**
	 * @param message
	 * @param sender
	 */
	private void parseMsg(String message, SocketAddress sender) {
		String[] parts = message.split(" ");
		if (parts.length >= 1) {
			String command = parts[0];
			if (command.equalsIgnoreCase(ADD_PEER)) {
				PeerInfo peer;
				for (int i = 1; i < parts.length; i++) {
					peer = PeerInfo.deserialize(parts[i]);
					this.addPeer(peer);
				}
			} else if (command.equalsIgnoreCase(REMOVE_PEER)) {
				PeerInfo peer = PeerInfo.deserialize(parts[1]);
				this.removePeer(peer);
			} else if (command.equalsIgnoreCase(REL_SEND)) {
				if (!this.relIDs.contains(parts[1])) {
					this.parseMsg(message.substring(parts[0].length() + 1 + parts[1].length() + 1 + parts[2].length() + 1), sender);
					this.relIDs.add(parts[1]);
				}
				this.send(ACKNOWLEDGE + " " + parts[1], sender);
			}
			//TODO: Parse BROADCAST
		}
	}
	
	private void reliableSend(String msg, PeerInfo target) {
		this.reliableSend(msg, target.getInfo());
	}
	
	private void reliableSend(String msg, SocketAddress target) {
		StringBuilder b = new StringBuilder(REL_SEND);
		b.append(" ");
		b.append(UUID.randomUUID());
		b.append(" ");
		b.append(this.channel.socket().getLocalPort());
		b.append(" ");
		b.append(msg);
		try {
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(b.toString())), target);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO: Timer; method must be blocking till it receives the ACK
	}
	
	private void send(String msg, SocketAddress target) {
		try {
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(msg)), target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void send(String msg, PeerInfo target) {
		InetSocketAddress f = target.getInfo();
		this.send(msg, f);
	}
	
}
