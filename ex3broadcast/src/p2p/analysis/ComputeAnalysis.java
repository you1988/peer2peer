package p2p.analysis;

import java.util.LinkedList;
import java.util.List;

public class ComputeAnalysis{
	
	List<AnalysisElements> values = new LinkedList<AnalysisElements>();
//	AnalysisDiagram diagram = new AnalysisDiagram("Analysis");
	
	/**
	 * @param time 
	 * @param spawnRate 
	 * @param leaveRate
	 * @param numOfNodes 
	 * @param dia 
	 */
	public void addElements(String time, int spawnRate, int leaveRate, int numOfNodes, int dia){
		AnalysisElements v = new AnalysisElements(time, spawnRate, leaveRate, numOfNodes, dia);
		this.values.add(v);
	}
	
	/**
	 * Displays Values
	 */
	public void display(){
		for(int i = 0; i < this.values.size(); i++){
			System.out.println(this.values.get(i).toString());		//Ausgabe der Tabelle
		}
	}
}