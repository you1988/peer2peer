import java.io.IOException;
import java.net.SocketAddress;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.TimerTask;
import java.util.UUID;


public class RelSendTask extends TimerTask {
	
	
	private Peer peer;
	private String msg;
	private SocketAddress target;
	private UUID uuid;
	private Runnable runnable;
	
	
	/**
	 * Create a new instance of AckTimeout.
	 */
	public RelSendTask(String msg, Peer peer, SocketAddress target, Runnable runnable) {
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
		try {
			this.peer.channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(b.toString())), this.target);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.peer.ackBuffer.put(this.uuid, this);
		this.peer.timer.schedule(this, Constants.ACK_DELAY);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean cancel() {
		boolean ret = super.cancel();
		this.runnable.run();
		return ret;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		this.send();
	}
	
}
