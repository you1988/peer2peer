package p2p.analysis;

import java.util.List;

import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;


/**
 * @author Markus Schneider
 */
public class AnalysisDiagram extends ApplicationFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ComputeAnalysis analysis;
	
	/**
	 * @param title 
	 */
	public AnalysisDiagram(String title){
		super(title);
	}
	
	public void start(ComputeAnalysis model){
		CategoryDataset dataset = createDataset(model.values);
		JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		pack();
		setVisible(true);
	}
	

	public CategoryDataset createDataset(List<AnalysisElements> values) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		for (AnalysisElements analysisElements : values) {
			result.addValue(analysisElements.spawn, "Spawn" , analysisElements.time);
			result.addValue(analysisElements.leave, "Leave" , analysisElements.time);
			result.addValue(analysisElements.numOfNodes, "numOfNodes" , analysisElements.time);
			result.addValue(analysisElements.dia, "Diameter" , analysisElements.time);
		}
		return result;
	}
	

	public JFreeChart createChart(CategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createStackedBarChart(
				"Analysis",
				"Time",
				"Number of Nodes",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		return chart;
	}
}