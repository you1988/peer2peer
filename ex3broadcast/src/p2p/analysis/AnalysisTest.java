package p2p.analysis;

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.category.CategoryDataset;

public class AnalysisTest {

	
	
	public static void main(String[] args) {
		ComputeAnalysis c = new ComputeAnalysis();
		c.addElements("0", 1, 2, 3, 4);
		c.addElements("1", 1, 6, 7, 0);
		c.addElements("2", 7, 6, 3, 9);
		c.addElements("3", 1, 2, 3, 4);
		AnalysisDiagram v = new AnalysisDiagram("Dia");
		v.start(c);
	}
}
