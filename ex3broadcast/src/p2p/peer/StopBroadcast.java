package p2p.peer;

import java.util.TimerTask;


/**
 * @author Alexander Nigl
 */
public class StopBroadcast extends TimerTask {
	
	
	private TimerTask task;
	
	
	/**
	 * Create a new instance of StopBroadcast.
	 * 
	 * @param t
	 */
	public StopBroadcast(TimerTask task) {
		this.task = task;
	}
	
	/** {@inheritDoc} */
	@Override
	public void run() {
		this.task.cancel();
	}
	
}
