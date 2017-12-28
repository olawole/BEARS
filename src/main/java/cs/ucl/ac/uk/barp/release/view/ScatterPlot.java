package cs.ucl.ac.uk.barp.release.view;


import java.awt.Color;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import cs.ucl.ac.uk.barp.release.ReleasePlan;


/**
 * A demo of the fast scatter plot.
 *
 */
@SuppressWarnings("serial")
public class ScatterPlot extends ApplicationFrame {


	//List<ReleasePlan> allSolutions;
	List<ReleasePlan> optimalSolutions;

    /**
     * Creates a new fast scatter plot demo.
     *
     * @param title  the frame title.
     */
    public ScatterPlot(final String title, List<ReleasePlan> optimal) {
        super(title);
        //allSolutions = all;
        optimalSolutions = optimal;

    }
    
    public JFreeChart createChart(XYDataset data){
    	JFreeChart chart = ChartFactory.createScatterPlot(getTitle(), "Expected Punctuality", "Net Present Value (Thousand Pounds)", data);
    	XYPlot plot = (XYPlot) chart.getPlot();
    	plot.setBackgroundPaint(Color.WHITE);
    	plot.setDomainGridlinesVisible(false);
    	XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotWidth(4);
        renderer.setDotHeight(4);
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLACK);
        plot.setRenderer(renderer);
    	plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.setBackgroundPaint(Color.white);
    	return chart;
    }
    
    public XYDataset createDataset(){
    	float x,y;
    	XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Candidate Release Plans");
        for (ReleasePlan plan : optimalSolutions){
    			x = (float) plan.getExpectedPunctuality();
    			y = (float) plan.getBusinessValue();
    			series.add(x, y);
    	}
        
        seriesCollection.addSeries(series);
        //seriesCollection.addSeries(lineData);
        
        return seriesCollection;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public void drawPlot(){
    	XYDataset data = createDataset();
    	JFreeChart chart = createChart(data);
    	ChartPanel panel = new ChartPanel(chart);
    	panel.setPreferredSize(new java.awt.Dimension(700, 400));
    	panel.setMouseWheelEnabled(true);
       // panel.addChartMouseListener(new MyChartMouseListener(panel));
    	this.add(panel);
    	this.pack();
    	this.setLocationRelativeTo(null);
    	this.setVisible(true);
    	this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
