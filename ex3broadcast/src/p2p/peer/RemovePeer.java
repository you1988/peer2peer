package p2p.peer;

/**
 * @author Alexander Nigl
 */
public class RemovePeer implements ICallback {
	
	
	private Peer peer;
	private PeerAddress neighbor;
	
	
	/**
	 * Create a new instance of ReliableRunner.
	 * 
	 * @param peer instance of peer
	 * @param neighbor peer who needs to be notified
	 */
	public RemovePeer(Peer peer, PeerAddress neighbor) {
		this.peer = peer;
		this.neighbor = neighbor;
	}
	
	private void remove() {
		StringBuilder b = new StringBuilder(Constants.REMOVE_PEER);
		b.append(" ");
		b.append(this.peer.getInfo().serialize());
		this.peer.send(b.toString(), this.neighbor);
	}
	
	/** {@inheritDoc} */
	@Override
	public void success() {
		this.remove();
	}
	
	/** {@inheritDoc} */
	@Override
	public void error() {
		this.remove();
	}
	
}
