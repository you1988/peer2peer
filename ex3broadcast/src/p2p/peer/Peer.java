package p2p.peer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.collections15.buffer.CircularFifoBuffer;


public class Peer implements Runnable {
	
	
	private static final int INITIALIZING = 0;
	private static final int RUNNING = 1;
	private static final int LEAVING = 2;
	private static final int STOPPED = 3;
	
	DatagramChannel channel;
	Set<PeerAddress> neighbors;
	private CircularFifoBuffer<String> relIDs;
	HashMap<UUID, ICallback> ackBuffer;
	HashMap<PeerAddress, Set<PeerAddress>> globalNetwork;
	Timer timer;
	Queue<PeerAddress> broadcastNeighbours;
	private int state;
	
	
	public Peer() throws IOException {
		this.state = INITIALIZING;
		this.neighbors = new HashSet<PeerAddress>();
		this.relIDs = new CircularFifoBuffer<String>(20);
		this.globalNetwork = new HashMap<PeerAddress, Set<PeerAddress>>();
		this.ackBuffer = new HashMap<UUID, ICallback>();
		this.timer = new Timer();
		this.channel = DatagramChannel.open();
		this.channel.bind(new InetSocketAddress("localhost", 0));
	}
	
	public Peer(Peer neighbour) throws IOException {
		this();
		StringBuilder b = new StringBuilder(Constants.ADD_PEER);
		b.append(" ");
		b.append(this.getInfo().serialize());
		this.send(b.toString(), neighbour.getInfo());
		this.neighbors.add(neighbour.getInfo());
	}
	
	/**
	 * Pings and after return adds Peer to neighbor list
	 * 
	 * @param peer peer to be added
	 */
	public void checkPeer(PeerAddress peer) {
		if (this.state == RUNNING && !peer.equals(this.getInfo())) {
			this.reliableSend(Constants.PING, peer, new CheckNeighbor(this, peer));
		}
	}
	
	/**
	 * Adds Peer to neighbor list
	 * 
	 * @param peer peer to be added
	 */
	void addPeer(PeerAddress peer) {
		if (this.state == RUNNING && !peer.equals(this.getInfo())) {
			this.neighbors.add(peer);
		}
	}
	
	/**
	 * Leave from network and stop peer
	 */
	public void leaveMe() {
		this.state = LEAVING;
		this.timer.schedule(new LeaveAnyway(this), Constants.LEAVE_DELAY);
		StringBuilder allPeers = new StringBuilder(Constants.ADD_PEER);
		if (this.neighbors.size() > 1) { //Send my neighbors a list of my neighbors
			for (PeerAddress peer : this.neighbors) {
				allPeers.append(" ");
				allPeers.append(peer.serialize());
			}
			for (PeerAddress peer : this.neighbors) {
				RemovePeer removePeer = new RemovePeer(this, peer);
				this.reliableSend(allPeers.toString(), peer, removePeer);
			}
		} else if (this.neighbors.size() == 1) { //If only one neighbor/just remove myself
			new RemovePeer(this, this.neighbors.iterator().next()).success();
		}
	}
	
	/**
	 * @param peer
	 */
	public void removePeer(PeerAddress peer) {
		if (this.state == RUNNING) {
			this.neighbors.remove(peer);
		}
	}
	
	@Override
	public void run() {
		this.state = RUNNING;
		this.startServer();
	}
	
	public void startServer() {
		int dither = new Random().nextInt(Constants.CHECKPERIOD_DITHER);
		this.timer.schedule(new CheckNeighbors(this), 0, Constants.CHECKPERIOD + dither);
		final ByteBuffer buffer = ByteBuffer.allocate(512);
		while (this.state == RUNNING || this.state == LEAVING) {
			try {
				this.channel.receive(buffer);
				buffer.flip();
				String message = Charset.forName("ascii").decode(buffer).toString();
				buffer.clear();
				System.out.println(this.getInfo().toString() + " " + message);
				this.parseMsg(message);
			} catch (IOException e) {
				//quit silent
			}
		}
		try {
			this.channel.close();
		} catch (IOException e) {
			//quit silent
		}
	}
	
	@Override
	public String toString() {
		DatagramSocket socket = this.channel.socket();
		return new PeerAddress(socket.getLocalAddress(), socket.getLocalPort()).toString();
	}
	
	/**
	 * @return
	 */
	public PeerAddress getInfo() {
		DatagramSocket socket = this.channel.socket();
		return new PeerAddress(socket.getLocalAddress(), socket.getLocalPort());
	}
	
	public void startBroadcast() {
		this.globalNetwork = new HashMap<PeerAddress, Set<PeerAddress>>();
		this.globalNetwork.put(this.getInfo(), this.neighbors);
		this.broadcastNeighbours = new LinkedList<PeerAddress>(this.neighbors);
		TimerTask t = new SendBroadcast(this);
		this.timer.schedule(t, 1, Constants.BROADCAST_PERIOD);
		this.timer.schedule(new StopBroadcast(t), Constants.BROADCAST_TIMEOUT);
	}
	
	/**
	 * @param message
	 * @param sender
	 */
	private void parseMsg(String message) {
		String[] parts = message.split(" ");
		if (parts.length >= 1) {
			String command = parts[0];
			if (command.equalsIgnoreCase(Constants.ADD_PEER)) {
				PeerAddress peer;
				for (int i = 1; i < parts.length; i++) {
					peer = PeerAddress.deserialize(parts[i]);
					this.checkPeer(peer);
				}
			} else if (command.equalsIgnoreCase(Constants.REMOVE_PEER)) {
				PeerAddress peer = PeerAddress.deserialize(parts[1]);
				this.removePeer(peer);
			} else if (command.equalsIgnoreCase(Constants.REL_SEND)) {
				if (!this.relIDs.contains(parts[1])) {
					this.parseMsg(message.substring(parts[0].length() + 1 + parts[1].length() + 1 + parts[2].length() + 1));
					this.relIDs.add(parts[1]);
				}
				this.send(Constants.ACKNOWLEDGE + " " + parts[1], PeerAddress.deserialize(parts[2]));
			} else if (command.equalsIgnoreCase(Constants.ACKNOWLEDGE)) {
				UUID uuid = UUID.fromString(parts[1]);
				ICallback t = this.ackBuffer.get(uuid);
				if (t != null) {
					t.success();
				}
			} else if (command.equalsIgnoreCase(Constants.BROADCAST)) {
				PeerAddress sender = PeerAddress.deserialize(parts[1]);
				StringBuilder b = new StringBuilder(Constants.BROADCAST_ANSWER);
				b.append(" ");
				b.append(this.getInfo().serialize());
				for (PeerAddress neighbour : this.neighbors) {
					b.append(" ");
					b.append(neighbour.serialize());
				}
				this.send(b.toString(), sender);
			} else if (command.equalsIgnoreCase(Constants.BROADCAST_ANSWER)) {
				PeerAddress neighbour = PeerAddress.deserialize(parts[1]);
				Set<PeerAddress> l = this.globalNetwork.get(neighbour);
				for (int i = 2; i < parts.length; i++) {
					PeerAddress p = PeerAddress.deserialize(parts[i]);
					l.add(p);
					if (!this.globalNetwork.containsKey(p)) {
						this.broadcastNeighbours.add(p);
					}
				}
			}
		}
	}
	
	void reliableSend(String msg, PeerAddress target, ICallback runnable) {
		this.reliableSend(msg, target.getInfo(), runnable);
	}
	
	void reliableSend(String msg, SocketAddress target, ICallback runnable) {
		RelSendTask task = new RelSendTask(msg, this, target, runnable);
		this.timer.schedule(task, 0, Constants.ACK_DELAY);
	}
	
	void send(String msg, SocketAddress target) {
		try {
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(msg)), target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String msg, PeerAddress target) {
		InetSocketAddress f = target.getInfo();
		this.send(msg, f);
	}
	
	/**
	 * 
	 * 
	 */
	public void shutdown() {
		System.out.println("Bin weg!");
		this.timer.cancel();
		this.state = STOPPED;
		try {
			this.channel.close();
		} catch (IOException e) {
			//Don't care			
		}
	}
}
