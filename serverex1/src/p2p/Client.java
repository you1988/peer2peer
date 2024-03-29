package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class Client {
	
	
	//private String host;
	//private int port;
	private DatagramSocket socket;
	private InetSocketAddress server;
	
	
	/**
	 * Create a new instance of Client.
	 * 
	 * @param host Hostname or IP adress
	 * @param port number
	 * @throws SocketException if Socket can't be opened
	 */
	public Client(String host, int port) throws SocketException {
		this.socket = new DatagramSocket(port);
		this.server = new InetSocketAddress(host, 1234);
	}
	
	public void send(String msg) {
		byte[] string = msg.getBytes();
		DatagramPacket packet;
		try {
			packet = new DatagramPacket(string, string.length, this.server);
			this.socket.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPrint() {
		this.send("print");
	}
	
	public String sendPull() {
		String res = "";
		this.send("pull");
		byte[] buffer = new byte[255];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			this.socket.receive(packet);
			byte[] answer = packet.getData();
			res = new String(answer, 0, packet.getLength(), "utf-8");
			System.out.println(new String(answer, 0, packet.getLength(), "utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public void sendPush(String string) {
		this.send("push " + string);
	}
	
}
