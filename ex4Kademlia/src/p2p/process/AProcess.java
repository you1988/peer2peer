package p2p.process;

import p2p.Node;

public abstract class AProcess {
	protected Node node;

	abstract void process(ICallback callback);
	
	protected void initial(Node node){
		this.node = node;
	}
}
