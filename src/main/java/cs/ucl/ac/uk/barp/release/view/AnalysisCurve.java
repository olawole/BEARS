package cs.ucl.ac.uk.barp.release.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

@SuppressWarnings("serial")
public class AnalysisCurve extends ApplicationFrame {
	
	double[] xData;
	int[] yData;

	public AnalysisCurve(String title, int[] xdata, double[] ydata,String legend) throws Exception {
		super(title);
		if (xdata.length != ydata.length){
			throw new Exception("Number of data in both Axes must be same");
		}
		// TODO Auto-generated constructor stub
		final XYSeries series = new XYSeries(legend);
		series.add(0, 0);
		for (int i = 0; i < xdata.length; i++){
			series.add(xdata[i], ydata[i]);
		}
        final XYSeriesCollection data = new XYSeriesCollection(series);
        
        final JFreeChart chart = ChartFactory.createXYLineChart(
        		"Cash Flow Analysis",
            "Period", 
            "Value", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
       
   //     Number minimum = DatasetUtilities.findMinimumRangeValue(data);
   //     ValueMarker min = new ValueMarker(minimum.floatValue());
   //     min.setPaint(Color.blue);
   //     min.setLabel("Self-funding status");
   //     min.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        final ChartPanel chartPanel = new ChartPanel(chart);
   //     chart.getXYPlot().addRangeMarker(min);
        chart.getXYPlot().setRangeZeroBaselineVisible(true);
        chart.getXYPlot().setDomainZeroBaselineVisible(true);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));

        setContentPane(chartPanel);
	}
	public AnalysisCurve(String title, HashMap<String, double[]> data, int period[]) throws Exception {
		super(title);
		final XYSeriesCollection datap = new XYSeriesCollection();
		for (Map.Entry<String, double[]> entry: data.entrySet()){
			int[] xdata = period;
			double[] ydata = entry.getValue();
			if (xdata.length != ydata.length){
				throw new Exception("Number of data in both Axes must be same");
			}
			final XYSeries series = new XYSeries(entry.getKey());
			series.add(0, 0);
			for (int i = 0; i < xdata.length; i++){
				series.add(xdata[i], ydata[i]);
			}
	        datap.addSeries(series);
	        
		}
		
		// TODO Auto-generated constructor stub
		
        
        final JFreeChart chart = ChartFactory.createXYLineChart(
        		"Cash Flow Analysis",
            "Investment Period", 
            "Value", 
            datap,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
       
//        Number minimum = DatasetUtilities.findMinimumRangeValue(datap);
//        ValueMarker min = new ValueMarker(minimum.floatValue());
//        min.setPaint(Color.blue);
//        min.setLabel("Self-funding status");
//        min.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        final ChartPanel chartPanel = new ChartPanel(chart);
//        chart.getXYPlot().addRangeMarker(min);
        chart.getXYPlot().setRangeZeroBaselineVisible(true);
        chart.getXYPlot().setDomainZeroBaselineVisible(true);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));

        setContentPane(chartPanel);
	}
	
}
