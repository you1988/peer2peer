package p2p;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Stack;

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
			this.socket = new DatagramSocket(SERVER_PORT);
			while (true) {
				byte[] buf = new byte[256];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				socket.receive(dp);
				//FIXME feature : add a queue. 
				Worker w = new Worker(dp);
				w.start();
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

	private class Worker extends Thread {
		private DatagramPacket packet;

		public Worker(DatagramPacket packet) {
			this.packet = packet;
		}

		public void run() {
			String in = new String(packet.getData(), 0, packet.getLength());
			out.println("in: " + in);
			String[] parts = in.split(" ");
			if (parts.length >= 1) {
				String command = parts[0];
				if (command.equalsIgnoreCase(PUSH)) {
					stack.push(in.substring(PUSH.length() + 1));
				} else if (command.equalsIgnoreCase(PULL)) {
					DatagramPacket answer;
					byte[] resp;
					try {
						resp = stack.pop().getBytes();
						answer = new DatagramPacket(resp, resp.length,
								packet.getSocketAddress());
						socket.send(answer);
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (command.equalsIgnoreCase(PRINT)) {
					out.println(stack.toString());
				}
			}
		}
	}
}
