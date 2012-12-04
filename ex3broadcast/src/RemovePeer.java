/**
 * @author Alexander Nigl
 */
public class RemovePeer implements Runnable {
	
	
	private Peer peer;
	private PeerInfo neighbour;
	
	
	/**
	 * Create a new instance of ReliableRunner.
	 */
	public RemovePeer(Peer peer, PeerInfo neigbour) {
		this.peer = peer;
		this.neighbour = neigbour;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		StringBuilder b = new StringBuilder(Constants.REMOVE_PEER);
		b.append(" ");
		b.append(this.peer.getInfo().serialize());
		this.peer.send(b.toString(), this.neighbour);
	}
	
}
