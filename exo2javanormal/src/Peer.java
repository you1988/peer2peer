import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {

	private static final String PUSH = "push";
	private static final String PULL = "pull";
	private static final String PRINT = "print";
	private static final String ADD_PORT = "ADD_PORT";

	private static final PrintStream out = System.out;
	SynchStack stack;
	DatagramSocket socket;
	int port;
	private ArrayList<Integer> portNeighbours;

	/**
	 * create a new neighbour
	 * 
	 * @throws SocketException
	 */
	public void spawnNeighbours() throws SocketException {
		Peer neighbour = new Peer();
		neighbour.portNeighbours.add(this.port);
		neighbour.startServer();
		portNeighbours.add(neighbour.port);
	}

	/**
	 * send the neighbours list .
	 * 
	 * @param port
	 *            of the neighbour that will receive the list.
	 */
	public void sendNeighborsListToNeigh(int port) {
		byte[] resp = null;

		SocketAddress neighbourAddress = new InetSocketAddress("localhost",
				port);
		for (byte b : resp) {

		}
		for (int portNei : portNeighbours) {
			if (portNei != port) {
				resp = new String("ADD_PORT " + portNei).getBytes();
				DatagramPacket answer = null;
				try {
					answer = new DatagramPacket(resp, resp.length,
							neighbourAddress);
					this.socket.send(answer);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * stop the peer
	 */

	public void leaveMe() {
		for (int port : portNeighbours) {
			sendNeighborsListToNeigh(port);
		}
		// cut connection with all neighbours
		this.socket.close();
	}
	
	/**
	 * Get the port of current peer
	 */
	public int getPort(){
		return this.port;
	}

	/**
	 * Create a new instance of Server.
	 */
	public Peer() {
		this.stack = new SynchStack();
		Random generator = new Random();
		this.port = generator.nextInt();
	}

	public void startServer() {
		try {
			ExecutorService exec = Executors.newFixedThreadPool(100);
			this.socket = new DatagramSocket(this.port);
			while (true) {
				byte[] buf = new byte[256];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				this.socket.receive(dp);
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
		Peer peer = new Peer();
		peer.startServer();
	}

	private class Worker implements Runnable {

		private DatagramPacket packet;

		public Worker(DatagramPacket packet) {
			this.packet = packet;
		}

		@Override
		public void run() {
			String in;
			try {
				in = new String(this.packet.getData(), 0,
						this.packet.getLength(), "utf-8");
				out.println("in: " + in);
				String[] parts = in.split(" ");
				if (parts.length >= 1) {
					String command = parts[0];
					if (command.equalsIgnoreCase("ADD_PORT")) {
						portNeighbours.add(Integer.valueOf(in
								.substring(ADD_PORT.length() + 1)));
					}
					if (command.equalsIgnoreCase(PUSH)) {
						Peer.this.stack.push(in.substring(PUSH.length() + 1));
					} else if (command.equalsIgnoreCase(PULL)) {
						DatagramPacket answer;
						byte[] resp;
						try {

							resp = Peer.this.stack.pop().getBytes();
							answer = new DatagramPacket(resp, resp.length,
									this.packet.getSocketAddress());
							Peer.this.socket.send(answer);
						} catch (SocketException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (command.equalsIgnoreCase(PRINT)) {
						out.println(Peer.this.stack.toString());
					}
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
	}
}