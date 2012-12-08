package p2p.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import p2p.control.Controller;
import p2p.peer.Peer;
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
	Controller controller;
	
	JTextField number;
	
	
	public UInterface(Controller controller) {
		this.controller = controller;
		this.setLayout(new FlowLayout());
		Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		this.graph = new UndirectedSparseGraph<String, String>();
		this.view = this.construct();
		
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
		JButton broadcast_button = new JButton("Broadcast");
		broadcast_button.addActionListener(new BroadcastListener(this));
		panelButtons.add(broadcast_button);
		
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
	
	public void removeConnection(Peer peer1, Peer peer2) {
		this.graph.removeEdge(peer1 + "-" + peer2);
		this.graph.removeEdge(peer2 + "-" + peer1);
		this.repaint();
	}
	
	
	class addPeerListener implements ActionListener {
		
		
		UInterface ui;
		
		
		public addPeerListener(UInterface ui) {
			this.ui = ui;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int number = Integer.parseInt(this.ui.number.getText());
			try {
				this.ui.controller.addNumberOfPeers(number);
			} catch (IOException e) {
				System.err.println("Couldn't start all peers");
			}
		}
	}
	
	class BroadcastListener implements ActionListener {
		
		
		UInterface ui;
		
		
		public BroadcastListener(UInterface ui) {
			this.ui = ui;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			this.ui.controller.broadcast();
		}
	}
	
	class removePeerListener implements ActionListener {
		
		
		UInterface ui;
		
		
		public removePeerListener(UInterface ui) {
			this.ui = ui;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int number = Integer.parseInt(this.ui.number.getText());
			this.ui.controller.removeNumberOfPeers(number);
		}
	}
	
}
