import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;


/**
 * @author Alexander Nigl
 */
public class Test {
	
	
	/**
	 * @param args
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException {
		PeerInfo p = new PeerInfo(InetAddress.getByName("localhost"), 123);
		PeerInfo q = new PeerInfo(InetAddress.getByName("localhost"), 123);
		PeerInfo s = new PeerInfo(InetAddress.getByName("localhost"), 1234);
		HashMap<String, HashSet<PeerInfo>> h = new HashMap<String, HashSet<PeerInfo>>();
		h.put(p.serialize(), new HashSet<PeerInfo>());
		h.get(PeerInfo.deserialize(q.serialize()).serialize()).add(s);
		if (h.containsKey(q.serialize())) {
			System.out.println(h.get(q.serialize()));
		}
	}
}
