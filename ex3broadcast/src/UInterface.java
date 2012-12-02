import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;


/**
 * UInterface GUI for P2P Exercise
 * Singleton
 */
public class UInterface extends JFrame {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UndirectedSparseGraph<String, String> graph;
	private BasicVisualizationServer<String, String> view;
	private static UInterface that;
	List<Peer> listOfPeers;
	JTextField number;
	
	
	/**
	 * @return unique UInterface
	 */
	public static UInterface getInterface() {
		if (that == null) {
			that = new UInterface();
		}
		return that;
	}
	
	private UInterface() {
		this.setLayout(new FlowLayout());
		Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		this.graph = new UndirectedSparseGraph<String, String>();
		this.view = this.construct();
		this.listOfPeers = new LinkedList<Peer>();
		
		JPanel panelSelector = new JPanel();
		panelSelector.setLayout(new BoxLayout(panelSelector, BoxLayout.Y_AXIS));
		// changed
		
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout());
		this.number = new JTextField("1", 5);
		panelButtons.add(this.number);
		JButton add_button = new JButton("Spawn");
		add_button.addActionListener(new addPeerListener(this));
		panelButtons.add(add_button);
		JButton remove_button = new JButton("Leave");
		remove_button.addActionListener(new removePeerListener(this));
		panelButtons.add(remove_button);
		
		content.add(this.view);
		content.add(panelSelector);
		content.add(panelButtons);
		
		this.setSize(500, 500);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public BasicVisualizationServer<String, String> construct() {
		Layout<String, String> layout = new SpringLayout<String, String>(this.graph);
		layout.setSize(new Dimension(300, 300));
		BasicVisualizationServer<String, String> view = new BasicVisualizationServer<String, String>(
				layout);
		
		view.setPreferredSize(new Dimension(350, 350));
		view.getRenderContext()
				.setVertexLabelTransformer(new ToStringLabeller<String>());
		view.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		return view;
	}
	
	public void addNumberOfPeers(int number) {
		for (int i = 0; i < number; i++) {
			this.addPeer();
		}
	}
	
	public void addPeer() {
		Peer newPeer;
		if (this.listOfPeers.size() > 0) {
			newPeer = new Peer(this.getRandomPeer());
		} else {
			newPeer = new Peer();
		}
		this.listOfPeers.add(newPeer);
		this.graph.addVertex(newPeer.toString());
		for (PeerInfo p : newPeer.getNeighbours()) {
			this.graph.addEdge(p.toString() + "-" + newPeer.toString(), p.toString(), newPeer.toString());
		}
		this.repaint();
	}
	
	public void removeConnection(Peer peer1, Peer peer2) {
		this.graph.removeEdge(peer1 + "-" + peer2);
		this.graph.removeEdge(peer2 + "-" + peer1);
		this.repaint();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UInterface.getInterface();
	}
	
	/**
	 * @return random Peer
	 */
	Peer getRandomPeer() {
		int rand = new Random().nextInt(this.listOfPeers.size());
		return this.listOfPeers.get(rand);
	}
	
	/**
	 * @param number
	 */
	public void removeNumberOfPeers(int number) {
		for (int i = 0; i < number; i++) {
			this.removePeer();
		}
	}
	
	/**
	 * 
	 * 
	 */
	private void removePeer() {
		if (this.listOfPeers.size() == 0) {
			return;
		}
		Peer peer = this.getRandomPeer();
		peer.leaveMe();
		this.listOfPeers.remove(peer);
		this.graph.removeVertex(peer.toString());
		for (PeerInfo p : peer.getNeighbours()) {
			this.removeConnection(peer, p.getPeer());
		}
		this.repaint();
	}
	
	
	class addPeerListener implements ActionListener {
		
		
		UInterface ui;
		
		
		public addPeerListener(UInterface ui) {
			this.ui = ui;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int number = Integer.getInteger(this.ui.number.getText());
			this.ui.addNumberOfPeers(number);
		}
		
	}
	
	class removePeerListener implements ActionListener {
		
		
		UInterface ui;
		
		
		public removePeerListener(UInterface ui) {
			this.ui = ui;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int number = Integer.getInteger(this.ui.number.getText());
			this.ui.removeNumberOfPeers(number);
		}
	}
}
