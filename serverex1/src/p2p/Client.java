package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	
	
	/**
	 * Create a new instance of Client.
	 * 
	 * @param string
	 */
	public Client(String host, int port) {
		try {
			Socket client = new Socket(host, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
			PrintWriter out = new PrintWriter(client.getOutputStream());
			
			out.write("push bar");
			out.println();
			out.flush();
			out.write("push foo");
			out.println();
			out.flush();
			out.write("pull");
			out.println();
			out.flush();
			System.out.println(in.readLine());
			out.write("print");
			out.println();
			out.flush();
			out.close();
			in.close();
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Client c = new Client("127.0.0.1", 1234);
	}
}
