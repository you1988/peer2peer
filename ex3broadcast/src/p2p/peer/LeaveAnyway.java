package p2p.peer;

import java.util.TimerTask;


public class LeaveAnyway extends TimerTask {
	
	
	private Peer peer;
	
	
	/**
	 * Create a new instance of LeaveAnyway.
	 */
	public LeaveAnyway(Peer peer) {
		this.peer = peer;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		this.peer.shutdown();
	}
	
}
