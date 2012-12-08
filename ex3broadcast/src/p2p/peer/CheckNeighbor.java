package p2p.peer;

/**
 * Checks if Neighbor is available, if true add peer to neighbor set, if false
 * removes him from neighbor set
 */
public class CheckNeighbor implements ICallback {
	
	
	private Peer peer;
	private PeerAddress neighbor;
	
	
	/**
	 * Create a new instance of CheckNeighbor.
	 * 
	 * @param peer instance of peer
	 * @param neighbor address of neighbor to be checked
	 */
	public CheckNeighbor(Peer peer, PeerAddress neighbor) {
		this.peer = peer;
		this.neighbor = neighbor;
	}
	
	/** {@inheritDoc} */
	@Override
	public void success() {
		this.peer.addPeer(this.neighbor);
	}
	
	/** {@inheritDoc} */
	@Override
	public void error() {
		this.peer.removePeer(this.neighbor);
	}
	
}
