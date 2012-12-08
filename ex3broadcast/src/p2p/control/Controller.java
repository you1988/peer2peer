package p2p.control;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import p2p.peer.Peer;
import p2p.view.UInterface;


/**
 * Controller of the network
 */
public class Controller {
	
	
	private List<Peer> listOfPeers;
	private Executor threadManager;
	
	
	/**
	 * @param args not used
	 */
	public static void main(String[] args) {
		new UInterface(new Controller());
	}
	
	/**
	 * Create a new instance of Controller.
	 */
	public Controller() {
		this.listOfPeers = new LinkedList<Peer>();
		this.threadManager = Executors.newCachedThreadPool();
	}
	
	/**
	 * Initializes Broadcast
	 */
	public void broadcast() {
		if (this.listOfPeers.size() > 0) {
			Peer peer = this.getRandomPeer();
			peer.startBroadcast();
		}
	}
	
	/**
	 * Adds a new Peer
	 * 
	 * @throws IOException if peer couldn't open a port
	 */
	public void addPeer() throws IOException {
		Peer newPeer;
		if (this.listOfPeers.size() > 0) {
			newPeer = new Peer(this.getRandomPeer());
		} else {
			newPeer = new Peer();
		}
		this.listOfPeers.add(newPeer);
		this.threadManager.execute(newPeer);
	}
	
	/**
	 * Adds new peers, throws IOException after first peer throws an IOException
	 * 
	 * @param number number of peers to add
	 * @throws IOException is thrown if a peer couldn't open an port
	 */
	public void addNumberOfPeers(int number) throws IOException {
		for (int i = 0; i < number; i++) {
			this.addPeer();
		}
	}
	
	/**
	 * Removes a number of peers
	 * 
	 * @param number of peers to be removed
	 */
	public void removeNumberOfPeers(int number) {
		Collections.shuffle(this.listOfPeers);
		Iterator<Peer> iter = this.listOfPeers.iterator();
		for (int i = 0; i < number && iter.hasNext(); i++) {
			iter.next().leaveMe();
			iter.remove();
		}
	}
	
	Peer getRandomPeer() {
		int rand = new Random().nextInt(this.listOfPeers.size());
		return this.listOfPeers.get(rand);
	}
	
}
