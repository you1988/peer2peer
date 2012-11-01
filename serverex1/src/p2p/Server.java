package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Stack;


public class Server {
	
	
	private static final int SERVER_PORT = 1234;
	private static final String PUSH = "push";
	private static final String PULL = "pull";
	private static final String PRINT = "print";
	Stack<String> stack;
	private DatagramSocket s;
	
	
	/**
	 * Create a new instance of Server.
	 */
	public Server() {
		this.stack = new Stack<String>();
		try {
			this.s = new DatagramSocket(SERVER_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() {
		byte[] buf;
		DatagramPacket in;
		while (true) {
			buf = new byte[256];
			in = new DatagramPacket(buf, buf.length);
			try {
				this.s.receive(in);
				this.parse(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void parse(DatagramPacket packet) throws IOException {
		String in = new String(packet.getData(), 0, packet.getLength());
		System.out.println("in: " + in);
		String[] parts = in.split(" ");
		if (parts.length >= 1) {
			String command = parts[0];
			if (command.equalsIgnoreCase(PUSH)) {
				this.stack.push(in.substring(PUSH.length() + 1));
			} else if (command.equalsIgnoreCase(PULL)) {
				if (this.stack.size() > 0) {
					byte[] string = this.stack.pop().getBytes();
					DatagramPacket answer = new DatagramPacket(string, string.length, packet.getSocketAddress());
					this.s.send(answer);
				}
			} else if (command.equalsIgnoreCase(PRINT)) {
				System.out.println(this.stack.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.startServer();
	}
	
}
