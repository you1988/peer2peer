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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer extends Thread {
	
	public static int numport = 1000;
	public static UInterface uiInterface;
	private static final String PUSH = "push";
	private static final String PULL = "pull";
	private static final String PRINT = "print";
	private static final String ADD_PORT = "ADD_PORT";
	private static final String DELET_PORT = "DELET";
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
	public Peer spawnNeighbours() {
		Peer neighbour = new Peer();
		neighbour.portNeighbours.add(this.port);
		neighbour.start();
		portNeighbours.add(neighbour.port);
		return neighbour;
	}

	/**
	 * send the neighbours list .
	 * 
	 * @param port
	 *            of the neighbour that will receive the list.
	 */
	public void sendNeighborsListToNeigh(int port , int neighbour) {
		char[] resp = null;

		SocketAddress neighbourAddress = new InetSocketAddress("localhost",
				port);
		resp = new String("DELET " + this.port).toCharArray();
		DatagramPacket answer = null;
		try {
//			answer = new DatagramPacket(resp, resp.length, neighbourAddress);
//			this.socket.send(answer);
			channel.send(Charset.forName("ascii").encode(
					CharBuffer.wrap(resp)), neighbourAddress);

				
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(port != neighbour){
		resp = new String("ADD_PORT " + neighbour).toCharArray();

				try {

					channel.send(Charset.forName("ascii").encode(
							CharBuffer.wrap(resp)), neighbourAddress);
					//this.socket.send(answer);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resp = new String("ADD_PORT " + port).toCharArray();
				SocketAddress neighbourAddress1 = new InetSocketAddress("localhost",
						neighbour);

				try {

					channel.send(Charset.forName("ascii").encode(
							CharBuffer.wrap(resp)),neighbourAddress1 );
					//this.socket.send(answer);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	}

	public static int generatePort() {
		return numport++;
	}

	/**
	 * stop the peer
	 * @throws IOException 
	 */

	public void leaveMe() throws IOException {
		for (int i =0 ; i< portNeighbours.size(); i++ ) {
		int port = portNeighbours.get(i);
			uiInterface.removeConnection(this.port, port);
		if(i< portNeighbours.size()-1)
			sendNeighborsListToNeigh(port,portNeighbours.get(i+1));
		else
			sendNeighborsListToNeigh(port,portNeighbours.get(0));
		}
		// cut connection with all neighbours
		uiInterface.removeVertex(this.port);
		this.stop();
		
	}

	/**
	 * Get the port of current peer
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Create a new instance of Server.
	 */
	DatagramChannel channel ;
	public Peer() {
		this.stack = new SynchStack();

		this.portNeighbours = new ArrayList<Integer>();
		int port = generatePort();
		boolean isNotOK = true;
		while(isNotOK){
		try {
			channel = DatagramChannel.open();
			channel.bind(new InetSocketAddress("localhost", port));
		isNotOK = false;
		} catch (IOException e1) {
			port = generatePort();
		}
		}
		this.port = port;
//		try {
//			this.socket = new DatagramSocket(this.port);
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
public void addPort(int port){

	int i = 0;
	boolean notFinded = true;
	while (notFinded && i < portNeighbours.size()) {
		
		if (port == portNeighbours.get(i)){
			notFinded = false;
		}
i++;
	}

	if (notFinded){
		System.out
		.println("ME the port" + this.port
				+ "I have received a new neighbour"
				+ port);

		portNeighbours.add(port);
		uiInterface.addPeer(this.port, port);
	}
	
}
	public void removePort(int port) {
		int i = 0;
		boolean notFinded = true;
		while (notFinded && i < portNeighbours.size()) {
			if (port == portNeighbours.get(i)){
				notFinded = false;	
			}
			i++;
			
		}

		if (!notFinded) portNeighbours.remove(i-1);
	}

	public void startServer() {
//		try {
//			ExecutorService exec = Executors.newFixedThreadPool(100);

			while (!this.isInterrupted()) {
				final ByteBuffer buffer = ByteBuffer.allocate(64*1028);
				try {
					final SocketAddress sender = channel.receive(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buffer.flip();
				
				String message = Charset.forName("ascii").decode(buffer).toString();
out.print(message);
				//				byte[] buf = new byte[256];
//				DatagramPacket dp = new DatagramPacket(buf, buf.length);

//				this.socket.receive(dp);
//				String in = new String(dp.getData(), 0, dp.getLength(), "utf-8");
//				out.println("in: " + in);
				String[] parts = message.split(" ");
				if (parts.length >= 1) {
					String command = parts[0];
					if (command.equalsIgnoreCase("ADD_PORT")) {
						int portAdded = Integer.valueOf(parts[1]);

						
						addPort(portAdded);
						
					} else if (command.equalsIgnoreCase(DELET_PORT)) {
						int portDeleted = Integer.valueOf(parts[1]);
						removePort(portDeleted);
					}
				}
			}
			try {
				this.channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		Peer peer = new Peer();
		peer.startServer();
	}


	public String toString() {
		char c = (char)(port-1000+65);
		return "" +c;

	}

	@Override
	public void run() {

		startServer();

	}
}