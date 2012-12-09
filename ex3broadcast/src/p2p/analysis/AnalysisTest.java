package p2p.analysis;

public class AnalysisTest {
	
	public static void main(String[] args) {
		ComputeAnalysis c = new ComputeAnalysis();
		c.addElements("0", 1.5, 2.6, 3, 4);
		c.addElements("1", 1.8, 6.5, 7, 0);
		c.addElements("2", 7.2, 6.3, 13, 9);
		c.addElements("3", 1.65, 2.55, 3, 4);
		AnalysisDiagram v = new AnalysisDiagram("Dia");
		v.start(c);
		
		c.display();
	}
}
