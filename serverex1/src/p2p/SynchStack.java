package p2p;

import java.util.Stack;

public class SynchStack extends Stack<String> {
	public synchronized String pop() {
		String resp = super.empty() ? "Stack empty" :super.pop() ;
		return resp;

	}

	public synchronized String push(String item) {
		return super.push(item);

	}

}
