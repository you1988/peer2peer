package p2p.handler;

public abstract class AHandler {
	//TODO: Implement Type of Payload
	// Most of the Handlers start processes or are part of a process
	// mapping between payload and Process is handled by Node 
	abstract void processPayload();
}
