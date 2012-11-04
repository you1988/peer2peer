package p2p;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.JFrame;


// every UI is associated to a Client
public class UInterface extends JFrame {
	
	
	private static final long serialVersionUID = 1L;
	public TextArea text;
	public TextField data;
	
	
	public static void main(String argv[]) {
		if (argv.length != 1) {
			System.out.println("UInterface <port number>");
			System.exit(0);
		}
		Client client = null;
		try {
			client = new Client("localhost", Integer.parseInt(argv[0]));
		} catch (SocketException e) {
			e.printStackTrace();
		}
		// create the graphical part
		new UInterface(client);
	}
	
	public UInterface(Client s) {
		
		this.setLayout(new FlowLayout());
		
		this.text = new TextArea(10, 60);
		this.text.setEditable(false);
		this.text.setForeground(Color.red);
		this.add(this.text);
		
		this.data = new TextField(60);
		this.add(this.data);
		
		Button push_button = new Button("push");
		push_button.addActionListener(new pushListener(s, this));
		this.add(push_button);
		Button pop_button = new Button("pop");
		pop_button.addActionListener(new popListener(s, this));
		this.add(pop_button);
		
		Button lock_button = new Button("print");
		lock_button.addActionListener(new printListener(s, this));
		this.add(lock_button);
		
		this.setSize(500, 300);
		this.setResizable(false);
		this.text.setBackground(Color.black);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}


class printListener implements ActionListener {
	
	
	Client c;
	UInterface irc;
	
	
	public printListener(Client s, UInterface irc) {
		this.c = s;
		this.irc = irc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.c.sendPrint();
		this.irc.text.append("\nprint server side \n");
		this.irc.data.requestFocus();
	}
}


class popListener implements ActionListener {
	
	
	Client c;
	UInterface irc;
	
	
	public popListener(Client s, UInterface irc) {
		this.c = s;
		this.irc = irc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String res = this.c.sendPull();
		this.irc.text.append("\n" + res + "\n");
		this.irc.data.requestFocus();
	}
}


class pushListener implements ActionListener {
	
	
	Client c;
	UInterface irc;
	
	
	public pushListener(Client s, UInterface irc) {
		this.c = s;
		this.irc = irc;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = this.irc.data.getText();
		this.c.sendPush(s);
		this.irc.data.setText("");
		this.irc.data.requestFocus();
	}
}
