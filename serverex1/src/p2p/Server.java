package p2p;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
	
	
	private static final int SERVER_PORT = 1234;
	private static final String PUSH = "push";
	private static final String PULL = "pull";
	private static final String PRINT = "print";
	private static final PrintStream out = System.out;
	SynchStack stack;
	DatagramSocket socket;
	
	
	/**
	 * Create a new instance of Server.
	 */
	public Server() {
		this.stack = new SynchStack();
	}
	
	public void startServer() {
		try {
			ExecutorService exec = Executors.newFixedThreadPool(100);
			this.socket = new DatagramSocket(SERVER_PORT);
			while (true) {
				byte[] buf = new byte[256];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				this.socket.receive(dp);
				//FIXME feature : add a queue. 
				Worker w = new Worker(dp);
				exec.execute(w);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.startServer();
	}
	
	
	private class Worker implements Runnable{
		
		
		private DatagramPacket packet;
		
		
		public Worker(DatagramPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public void run() {
			String in;
			try {
				in = new String(this.packet.getData(), 0, this.packet.getLength(), "utf-8");
				out.println("in: " + in);
				String[] parts = in.split(" ");
				if (parts.length >= 1) {
					String command = parts[0];
					if (command.equalsIgnoreCase(PUSH)) {
						Server.this.stack.push(in.substring(PUSH.length() + 1));
					} else if (command.equalsIgnoreCase(PULL)) {
						DatagramPacket answer;
						byte[] resp;
						try {
							resp = Server.this.stack.pop().getBytes();
							answer = new DatagramPacket(resp, resp.length,
									this.packet.getSocketAddress());
							Server.this.socket.send(answer);
						} catch (SocketException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (command.equalsIgnoreCase(PRINT)) {
						out.println(Server.this.stack.toString());
					}
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
	}
}
