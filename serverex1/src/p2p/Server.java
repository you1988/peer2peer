package p2p;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;


public class Server {
	
	
	private static final int SERVER_PORT = 1234;
	private static final String PUSH = "push";
	private static final String PULL = "pull";
	private static final String PRINT = "print";
	private static final String EXIT = "exit";
	Stack<String> stack;
	
	
	/**
	 * Create a new instance of Server.
	 */
	public Server() {
		this.stack = new Stack<String>();
		try {
			this.initializeSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws IOException
	 */
	private void initializeSocket() throws IOException {
		ServerSocket s = new ServerSocket(SERVER_PORT);
		Socket c;
		while (true) {
			try {
				c = s.accept();
				BufferedReader b = new BufferedReader(new InputStreamReader(c.getInputStream(), "utf-8"));
				String in;
				boolean cont = true;
				while (cont) {
					in = b.readLine();
					cont = this.parse(c, in);
				}
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * @param c
	 * @param c
	 * @return
	 * @throws IOException
	 */
	private boolean parse(Socket c, String in) throws IOException {
		if (in == null || c == null) {
			return false;
		}
		System.out.println("in: " + in);
		String[] parts = in.split(" ");
		if (parts.length >= 1) {
			String command = parts[0];
			if (command.equalsIgnoreCase(PUSH)) {
				this.stack.push(in.substring(PUSH.length() + 1));
			} else if (command.equalsIgnoreCase(PULL)) {
				BufferedWriter b = new BufferedWriter(new OutputStreamWriter(c.getOutputStream(), "utf-8"));
				if (this.stack.size() > 0) {
					b.write(this.stack.pop());
					b.newLine();
					b.flush();
				}
			} else if (command.equalsIgnoreCase(PRINT)) {
				System.out.println(this.stack.toString());
			} else if (command.equalsIgnoreCase(EXIT)) {
				return false;
			}
			return true;
		}
		return false;
		
	}
	
	public static void main(String[] args) {
		Server server = new Server();
	}
	
}
