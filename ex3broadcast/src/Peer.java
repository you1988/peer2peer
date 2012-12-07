import java.io.IOException;
import java.io.PrintStream;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.collections15.buffer.CircularFifoBuffer;


public class Peer extends Thread {
	
	
	private static final PrintStream out = System.out;
	
	DatagramChannel channel;
	private HashSet<PeerInfo> neighbours;
	private CircularFifoBuffer<String> relIDs;
	HashMap<UUID, RelSendTask> ackBuffer;
	HashMap<PeerInfo, HashSet<PeerInfo>> globalNetwork;
	Timer timer;
	Queue<PeerInfo> broadcastNeighbours;
	
	
	public Peer() {
		this.neighbours = new HashSet<PeerInfo>();
		this.relIDs = new CircularFifoBuffer<String>(20);
		this.globalNetwork = new HashMap<PeerInfo, HashSet<PeerInfo>>();
		this.ackBuffer = new HashMap<UUID, RelSendTask>();
		this.timer = new Timer();
		try {
			this.channel = DatagramChannel.open();
			this.channel.bind(new InetSocketAddress("localhost", 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Peer(Peer neighbour) {
		this();
		StringBuilder b = new StringBuilder(Constants.ADD_PEER);
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
	public HashSet<PeerInfo> getNeighbours() {
		return this.neighbours;
	}
	
	/**
	 * stop the peer
	 * 
	 * @throws IOException
	 */
	public void leaveMe() {
		HashSet<PeerInfo> lon = (HashSet<PeerInfo>)this.neighbours.clone();
		for (PeerInfo neighbour : this.neighbours) {
			lon.remove(neighbour);
			if (lon.size() > 0) {
				StringBuilder allPeers = new StringBuilder(Constants.ADD_PEER);
				for (PeerInfo peer : lon) {
					allPeers.append(" ");
					allPeers.append(peer.serialize());
				}
				RemovePeer removePeer = new RemovePeer(this, neighbour);
				this.reliableSend(allPeers.toString(), neighbour, removePeer);
			}
		}
		this.timer.schedule(new LeaveAnyway(this), Constants.LEAVE_DELAY);
	}
	
	/**
	 * @param peer
	 */
	public void removePeer(PeerInfo peer) {
		this.neighbours.remove(peer);
	}
	
	@Override
	public void run() {
		
		this.startServer();
		
	}
	
	public void startServer() {
		final ByteBuffer buffer = ByteBuffer.allocate(512);
		while (!this.isInterrupted()) {
			try {
				this.channel.receive(buffer);
				buffer.flip();
				String message = Charset.forName("ascii").decode(buffer).toString();
				buffer.clear();
				out.println(this.getInfo().toString() + " " + message);
				this.parseMsg(message);
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
	
	public void startBroadcast() {
		this.globalNetwork = new HashMap<PeerInfo, HashSet<PeerInfo>>();
		this.globalNetwork.put(this.getInfo(), this.neighbours);
		this.broadcastNeighbours = new LinkedList<PeerInfo>(this.neighbours);
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
				PeerInfo peer;
				for (int i = 1; i < parts.length; i++) {
					peer = PeerInfo.deserialize(parts[i]);
					this.addPeer(peer);
				}
			} else if (command.equalsIgnoreCase(Constants.REMOVE_PEER)) {
				PeerInfo peer = PeerInfo.deserialize(parts[1]);
				this.removePeer(peer);
			} else if (command.equalsIgnoreCase(Constants.REL_SEND)) {
				if (!this.relIDs.contains(parts[1])) {
					this.parseMsg(message.substring(parts[0].length() + 1 + parts[1].length() + 1 + parts[2].length() + 1));
					this.relIDs.add(parts[1]);
				}
				this.send(Constants.ACKNOWLEDGE + " " + parts[1], PeerInfo.deserialize(parts[2]));
			} else if (command.equalsIgnoreCase(Constants.ACKNOWLEDGE)) {
				UUID uuid = UUID.fromString(parts[1]);
				TimerTask t = this.ackBuffer.get(uuid);
				if (t != null) {
					t.cancel();
				}
			} else if (command.equalsIgnoreCase(Constants.BROADCAST)) {
				PeerInfo sender = PeerInfo.deserialize(parts[1]);
				StringBuilder b = new StringBuilder(Constants.BROADCAST_ANSWER);
				b.append(" ");
				b.append(this.getInfo().serialize());
				for (PeerInfo neighbour : this.neighbours) {
					b.append(" ");
					b.append(neighbour.serialize());
				}
				this.send(b.toString(), sender);
			} else if (command.equalsIgnoreCase(Constants.BROADCAST_ANSWER)) {
				PeerInfo neighbour = PeerInfo.deserialize(parts[1]);
				HashSet<PeerInfo> l = this.globalNetwork.get(neighbour);
				for (int i = 2; i < parts.length; i++) {
					PeerInfo p = PeerInfo.deserialize(parts[i]);
					l.add(p);
					if (!this.globalNetwork.containsKey(p)) {
						this.broadcastNeighbours.add(p);
					}
				}
			}
		}
	}
	
	private void reliableSend(String msg, PeerInfo target, Runnable runnable) {
		this.reliableSend(msg, target.getInfo(), runnable);
	}
	
	private void reliableSend(String msg, SocketAddress target, Runnable runnable) {
		RelSendTask task = new RelSendTask(msg, this, target, runnable);
		task.send();
	}
	
	private void send(String msg, SocketAddress target) {
		try {
			this.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(msg)), target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void send(String msg, PeerInfo target) {
		InetSocketAddress f = target.getInfo();
		this.send(msg, f);
	}
	
}
