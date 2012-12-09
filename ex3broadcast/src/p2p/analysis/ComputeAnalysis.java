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
	public void addElements(String time, double spawnRate, double leaveRate, int numOfNodes, int dia){
		AnalysisElements v = new AnalysisElements(time, spawnRate, leaveRate, numOfNodes, dia);
		this.values.add(v);
	}
	
	/**
	 * Displays Values
	 */
	public void display(){
		for(int i = 0; i < this.values.size(); i++){
			System.out.println("Time: \t\t\t"+ this.values.get(i).time);
			System.out.println("Spawn Rate: \t\t"+ this.values.get(i).spawnRate);
			System.out.println("Leave Rate: \t\t"+ this.values.get(i).leaveRate);
			System.out.println("Number of Nodes: \t"+ this.values.get(i).numOfNodes);
			System.out.println("Diameter: \t\t"+ this.values.get(i).dia +"\n");
		}
	}
}