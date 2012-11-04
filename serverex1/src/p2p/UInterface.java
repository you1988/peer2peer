package p2p;

import java.awt.*;
import java.awt.event.*;
import java.net.*;

import javax.swing.JFrame;

// every UI is associated to a Client
public class UInterface extends JFrame {

	private static final long serialVersionUID = 1L;
	public TextArea		text;
	public TextField	data;

	public static void main(String argv[]) {
		Client client = null ;
		try {
			client = new Client("localhost", Integer.parseInt(argv[0])) ;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		// create the graphical part
		new UInterface(client);
	}

	public UInterface(Client s) {
	
		setLayout(new FlowLayout());
	
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);
	
		data=new TextField(60);
		add(data);
	
		Button push_button = new Button("push");
		push_button.addActionListener(new pushListener(s, this));
		add(push_button);
		Button pop_button = new Button("pop");
		pop_button.addActionListener(new popListener(s, this));
		add(pop_button);
		
		Button lock_button = new Button("print");
		lock_button.addActionListener(new printListener(s, this));
		add(lock_button);
		
		setSize(470,300);
		text.setBackground(Color.black); 
		this.show() ;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class printListener implements ActionListener {
	Client c;
	UInterface irc ;
	public printListener (Client s, UInterface irc) {
		c = s ;
		this.irc = irc ;
	}
	public void actionPerformed (ActionEvent e) {
		c.sendPrint();
		irc.text.append("\nprint server side \n");
	}
}

class popListener implements ActionListener {
	Client c;
	UInterface irc ;
	public popListener (Client s, UInterface irc) {
		c = s;
		this.irc = irc ;
	}
	public void actionPerformed (ActionEvent e) {
		String res = c.sendPull();
		irc.text.append("\n"+res+"\n");
	}
}

class pushListener implements ActionListener {
	Client c;
	UInterface irc ; 
	public pushListener (Client s, UInterface irc) {
        c = s;
        this.irc = irc ;
	}
	public void actionPerformed (ActionEvent e) {
        String s = irc.data.getText();
		c.sendPush(s);
		irc.data.setText("");
		
	}
}



