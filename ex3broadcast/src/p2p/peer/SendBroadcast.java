package p2p.peer;

import java.util.HashSet;
import java.util.Queue;
import java.util.TimerTask;



/**
 * @author Alexander Nigl
 */
public class SendBroadcast extends TimerTask {
	
	
	private Peer peer;
	
	
	/**
	 * Create a new instance of SendBroadcast.
	 * 
	 * @param peer
	 */
	public SendBroadcast(Peer peer) {
		this.peer = peer;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		Queue<PeerAddress> neighbours = this.peer.broadcastNeighbours;
		PeerAddress target;
		StringBuilder b = new StringBuilder(Constants.BROADCAST);
		b.append(" ");
		b.append(this.peer.getInfo().serialize());
		if (neighbours.size() > 0) {
			target = neighbours.remove();
			if (!this.peer.globalNetwork.containsKey(target.serialize())) {
				this.peer.globalNetwork.put(target, new HashSet<PeerAddress>());
			}
			this.peer.send(b.toString(), target);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean cancel() {
		for (PeerAddress serializedPeer : this.peer.globalNetwork.keySet()) {
			PeerAddress peer = serializedPeer;
			System.out.print(peer.toString());
			for (PeerAddress neighbour : this.peer.globalNetwork.get(peer)) {
				System.out.print(" ");
				System.out.print(neighbour.toString());
			}
			System.out.print("\n");
		}
		return super.cancel();
	}
}
