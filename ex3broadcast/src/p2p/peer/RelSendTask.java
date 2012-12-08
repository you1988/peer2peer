package p2p.peer;

import java.net.SocketAddress;
import java.util.TimerTask;
import java.util.UUID;


public class RelSendTask extends TimerTask implements ICallback {
	
	
	private Peer peer;
	private String msg;
	private SocketAddress target;
	private UUID uuid;
	private ICallback runnable;
	private int counter = 0;
	
	
	/**
	 * Create a new instance of AckTimeout.
	 */
	public RelSendTask(String msg, Peer peer, SocketAddress target, ICallback runnable) {
		this.peer = peer;
		this.msg = msg;
		this.target = target;
		this.uuid = UUID.randomUUID();
		this.runnable = runnable;
	}
	
	public void send() {
		StringBuilder b = new StringBuilder(Constants.REL_SEND);
		b.append(" ");
		b.append(this.uuid);
		b.append(" ");
		b.append(this.peer.getInfo().serialize());
		b.append(" ");
		b.append(this.msg);
		this.peer.send(b.toString(), this.target);
		this.peer.ackBuffer.put(this.uuid, this);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean cancel() {
		boolean ret = super.cancel();
		this.runnable.error();
		return ret;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		this.send();
		if (this.counter++ > 5) {
			this.error();
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void success() {
		this.runnable.success();
		super.cancel();
	}
	
	/** {@inheritDoc} */
	@Override
	public void error() {
		this.runnable.error();
		super.cancel();
	}
	
}
