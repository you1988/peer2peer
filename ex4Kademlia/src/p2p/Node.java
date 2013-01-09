package p2p;

import p2p.handler.HandlerFactory;

public class Node {
	//TODO: Communication Loop and let HandlerFactory return concrete handler and start processPayload(...)
	//TODO: Implement Interface to start processes
	//TODO: Implement Manager to find process of given msg ID
	
	public RoutingManager routing;
	public StorageManager storage;
	public TimeoutManager timeout;
	public HandlerFactory handlers;
	
	
	public Node(RoutingManager routing, StorageManager storage, TimeoutManager timeout, HandlerFactory handlers) {
		this.routing = routing;
		this.storage = storage;
		this.timeout = timeout;
		this.handlers = handlers;
	}
	
	public static void main(String[] args) {
		//TODO: initialize Node, RoutingManager, StorageManager, TimeoutManager
		//TODO: Maybe initialize UI(not prepared)
	}
}
