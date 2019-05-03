package cs.ucl.ac.uk.barp.release.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import cs.ucl.ac.uk.barp.model.Release;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.model.WorkItem;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

@SuppressWarnings("serial")
public class BarChart extends ApplicationFrame {
	List<ReleasePlan> optimal;
	int noOfReleases;

	public BarChart(List<ReleasePlan> optimal, int releases) {
		super(ConfigSetting.BARCHART_TITLE);
		this.optimal = optimal;
		this.noOfReleases = releases;
		JFreeChart barChart = ChartFactory.createBarChart(ConfigSetting.BARCHART_SUBTITLE, "Release",
				"Percentage Occurence", createDataset(), PlotOrientation.VERTICAL, true, true, false);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	private CategoryDataset createDataset() {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		HashMap<Integer, HashMap<String, Integer>> frequencyData = getFeatureReleaseFrequency();
		for (Integer k : frequencyData.keySet())
			for (String f : frequencyData.get(k).keySet()) {
				dataset.addValue(frequencyData.get(k).get(f), f, k);
				
			}
		return dataset;
	}

	/**
	 * @return
	 */
	private HashMap<Integer, HashMap<String, Integer>> computeFeatureReleaseFrequency() {
		HashMap<Integer, HashMap<String, Integer>> featureFrequency = new HashMap<Integer, HashMap<String, Integer>>();
		for (int i = 1; i <= noOfReleases; i++) {
			HashMap<String, Integer> freqPlan = new HashMap<String, Integer>();
			for (ReleasePlan p : optimal) {
				HashMap<Integer, String> plan = planToString(p);
				if (plan.containsKey(i)) {
					String features[] = plan.get(i).split(",");
					for (String feature : features) {
						if (freqPlan.containsKey(feature)) {
							int oldValue = freqPlan.get(feature);
							freqPlan.put(feature, ++oldValue);
						} else {
							freqPlan.put(feature, 1);
						}
					}
				}
			}
			featureFrequency.put(i, freqPlan);
		}
		return featureFrequency;
	}

	public HashMap<Integer, HashMap<String, Integer>> getFeatureReleaseFrequency() {
		HashMap<Integer, HashMap<String, Integer>> releaseFrequency = computeFeatureReleaseFrequency();
		int totalSolution = optimal.size();
		for (Integer k : releaseFrequency.keySet())
			for (String f : releaseFrequency.get(k).keySet()) {
				HashMap<String, Integer> relfeq = releaseFrequency.get(k);
				int percent = (int) (relfeq.get(f) * 100 / totalSolution);
				relfeq.put(f, percent);
			}
		return releaseFrequency;
	}

	private HashMap<Integer, String> planToString(ReleasePlan plan) {
		HashMap<Integer, String> planString = new HashMap<Integer, String>();
		for (Map.Entry<Integer, Release> entry : plan.getPlan().entrySet()) {
			if (entry.getValue().isEmpty())
				continue;
			String s = "";
			for (WorkItem wItem : entry.getValue().getwItems()) {
				s += s.equals("") ? wItem.getItemId() : "," + wItem.getItemId();
			}
			planString.put(entry.getKey(), s);
		}
		return planString;
	}

}