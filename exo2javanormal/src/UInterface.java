import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class UInterface extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UndirectedSparseGraph<String, String> graph;
	private BasicVisualizationServer<String,String> vue;
	private JComboBox list;
	private String[] data = {};

	private UInterface(){
		
		this.setLayout(new FlowLayout());
		Container content = this.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		this.graph = new UndirectedSparseGraph<String,String>();
		Peer firstPeer = new Peer();
		graph.addVertex(""+firstPeer.getPort());
		this.data = append(this.data, ""+firstPeer.getPort());
		this.vue = construire();
        this.add(this.vue);
        
		JPanel panelSelector = new JPanel();
		panelSelector.setLayout(new BoxLayout(panelSelector,BoxLayout.Y_AXIS));
		this.list = new JComboBox(this.data);
		this.list.setPreferredSize(new Dimension(0,0));
		panelSelector.add(this.list);
        
        JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout());
		JButton add_button = new JButton("Spawn");
		add_button.addActionListener(new addListener(this, this.list.getSelectedItem()));
		panelButtons.add(add_button);
		JButton remove_button = new JButton("Leave");
		remove_button.addActionListener(new removeListener(this));
		panelButtons.add(remove_button);

		content.add(this.vue);
		content.add(panelSelector);
		content.add(panelButtons);

		this.setSize(500, 500);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public BasicVisualizationServer<String,String> construire() {
		Layout <String,String> layout = new FRLayout(this.graph);
		layout.setSize(new Dimension(300,300));
		BasicVisualizationServer<String,String> vue = new BasicVisualizationServer<String,String>(layout);
		vue.setPreferredSize(new Dimension(350,350));
		vue.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vue.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vue.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		return vue;
	}
	
	/**
	 * add element to data list
	 * @param arr : data list
	 * @param element : element to add
	 * @return the new data list
	 */
	static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	public void addPeer(int parentPort, int childPort){
		this.graph.addEdge("", ""+parentPort, ""+childPort);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new UInterface();
	}

}

class addListener implements ActionListener {

	UInterface ui;
	int parentPort;
	
	public addListener(UInterface ui, Object itemSelected){
		this.ui = ui;
		//this.parentPort = (int) itemSelected;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Peer newPeer = new Peer();
		this.ui.addPeer(this.parentPort, newPeer.getPort());
	}
	
}

class removeListener implements ActionListener {

	UInterface ui;
	
	public removeListener(UInterface ui){
		this.ui = ui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}