package cs.ucl.ac.uk.barp.release.view;


import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.List;

import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.ShapeUtilities;

import cs.ucl.ac.uk.barp.model.ReleasePlan;


/**
 * A demo of the fast scatter plot.
 *
 */
@SuppressWarnings("serial")
public class Scatter extends ApplicationFrame {


	//List<ReleasePlan> allSolutions;
	List<ReleasePlan> optimalSolutions;
	List<ReleasePlan> evolveSolutions;
//	List<ReleasePlan> dominatedSolutions;

    /**
     * Creates a new fast scatter plot demo.
     *
     * @param title  the frame title.
     */
    public Scatter(final String title, List<ReleasePlan> optimal, List<ReleasePlan> evolve) {
        super(title);
        //allSolutions = all;
        optimalSolutions = optimal;
        evolveSolutions = evolve;
//        dominatedSolutions = dominated;

    }
    
    public JFreeChart createChart(XYDataset data){
    	JFreeChart chart = ChartFactory.createScatterPlot(getTitle(), "Expected Punctuality", "Expected Net Present Value", data);
    	Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
    	Shape diamond = ShapeUtilities.createDiamond(3);
    	Shape triangle = ShapeUtilities.createDownTriangle(2);
    	XYPlot plot = (XYPlot) chart.getPlot();
    	plot.setBackgroundPaint(Color.WHITE);
    	plot.setDomainGridlinesVisible(false);
    	//XYDotRenderer renderer = new XYDotRenderer();
    	XYItemRenderer renderer = plot.getRenderer();
//        renderer.setDotWidth(10);
//        renderer.setDotHeight(10);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, Color.BLACK);
        renderer.setSeriesShape(0, cross);
        renderer.setSeriesShape(2, triangle);
        renderer.setSeriesShape(1, diamond);
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
        XYSeries series = new XYSeries("BEARS Fixed optimal");
        for (ReleasePlan plan : optimalSolutions){
    			x = (float) plan.getExpectedPunctuality();
    			y = (float) plan.getBusinessValue();
    			series.add(x, y);
    	}
        
        XYSeries series1 = new XYSeries("Flexible optimal");
        for (ReleasePlan plan : evolveSolutions){
        	x = (float) plan.getExpectedPunctuality();
			y = (float) plan.getBusinessValue();
			series1.add(x, y);
    	}
        
//        XYSeries series2 = new XYSeries("EVOLVE optimal (ReleasePlanner)");
//        for (ReleasePlan plan : dominatedSolutions){
//        	x = (float) plan.getExpectedPunctuality();
//			y = (float) plan.getBusinessValue();
//			series2.add(x, y);
//    	}
        seriesCollection.addSeries(series);
        seriesCollection.addSeries(series1);
        //seriesCollection.addSeries(series2);
        
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
