package p2p.analysis;
/**
 * Elements of the AnalysisList
 */
public class AnalysisElements {
	String time;
	int spawn;
	int leave;
	int numOfNodes;
	int dia;
	
	public AnalysisElements(String time, int spawn, int leave, int numOfNodes, int dia){
		this.time = time;
		this.spawn = spawn;
		this.leave = leave;
		this.numOfNodes = numOfNodes;
		this.dia = dia;
	}
}