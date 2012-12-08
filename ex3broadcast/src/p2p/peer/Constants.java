package p2p.peer;

/**
 * @author Alexander Nigl
 */
public class Constants {
	
	
	public static final String REMOVE_PEER = "DEL";
	public static final String ADD_PEER = "ADD";
	public static final String REL_SEND = "REL";
	public static final String ACKNOWLEDGE = "ACK";
	public static final String BROADCAST = "BCT";
	public static final long ACK_DELAY = 5 * 1000;
	public static final long BROADCAST_PERIOD = 1 * 10;
	public static final long BROADCAST_TIMEOUT = 5 * 1000;
	public static final String BROADCAST_ANSWER = "BCA";
	public static final long LEAVE_DELAY = 5 * 1000;
	public static final String PING = "PING";
	public static final long CHECKPERIOD = 10 * 1000;
	public static final int CHECKPERIOD_DITHER = 1 * 1000;
	
}
