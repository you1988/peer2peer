package p2p.analysis;
/**
 * Elements of the AnalysisList
 */
public class AnalysisElements {
	String time;
	int spawnRate;
	int leaveRate;
	int numOfNodes;
	int dia;
	
	public AnalysisElements(String time, int spawnRate, int leaveRate, int numOfNodes, int dia){
		this.time = time;
		this.spawnRate = spawnRate;
		this.leaveRate = leaveRate;
		this.numOfNodes = numOfNodes;
		this.dia = dia;
	}
}