package p2p.analysis;

import java.util.LinkedList;
import java.util.List;

public class ComputeAnalysis{
	
	List<AnalysisElements> values = new LinkedList<AnalysisElements>();
//	AnalysisDiagram diagram = new AnalysisDiagram("Analysis");
	
	/**
	 * @param time 
	 * @param spawn 
	 * @param leave 
	 * @param numOfNodes 
	 * @param deg 
	 * @param dia 
	 */
	public void addElements(String time, int spawn, int leave, int numOfNodes, int dia){
		AnalysisElements v = new AnalysisElements(time, spawn, leave, numOfNodes, dia);
		this.values.add(v);
	}
	
	/**
	 * Displays Values
	 */
	public void display(){
		for(int i = 0; i < this.values.size(); i++){
			System.out.println(this.values.get(i));		//Ausgabe der Tabelle
		}
	}
}