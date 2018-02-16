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
public class ScatterBearsRigidVSRPRisk extends ApplicationFrame {

	List<ReleasePlan> bears1Solutions;
	List<ReleasePlan> srpSolutions;

    /**
     * Creates a new fast scatter plot demo.
     *
     * @param title  the frame title.
     */
    public ScatterBearsRigidVSRPRisk(final String title, List<ReleasePlan> optimal, List<ReleasePlan> srpPlans) {
        super(title);
        bears1Solutions = optimal;
        srpSolutions = srpPlans;

    }

	public JFreeChart createChart(XYDataset data){
    	JFreeChart chart = ChartFactory.createScatterPlot(getTitle(), "Expected Effort Overrun (%)", "Net Present Value ('000 £)", data);
    	Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
    	Shape diamond = ShapeUtilities.createDiamond(3);
    	XYPlot plot = (XYPlot) chart.getPlot();
    	plot.setBackgroundPaint(Color.WHITE);
    	plot.setDomainGridlinesVisible(false);
    	XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesShape(0, cross);
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
        XYSeries series = new XYSeries("BEARS RIGID");
        for (ReleasePlan plan : bears1Solutions){
        		x = (float) plan.getRiskMeasure();
    			y = (float) plan.getBusinessValue();
    			series.add(x, y);
    	}
        XYSeries series1 = new XYSeries("SRPRisk");
        for (ReleasePlan plan : srpSolutions){
        	x = (float) plan.getRiskMeasure();
			y = (float) plan.getBusinessValue();
			series1.add(x, y);
    	}
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
