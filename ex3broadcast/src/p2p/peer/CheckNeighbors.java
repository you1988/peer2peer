package p2p.peer;

import java.util.TimerTask;


public class CheckNeighbors extends TimerTask {
	
	
	private Peer peer;
	
	
	/**
	 * Create a new instance of CheckNeighbors.
	 * 
	 * @param peer
	 */
	public CheckNeighbors(Peer peer) {
		this.peer = peer;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		for (PeerAddress neighbor : this.peer.neighbors) {
			this.peer.reliableSend(Constants.PING, neighbor, new CheckNeighbor(this.peer, neighbor));
		}
	}
}
