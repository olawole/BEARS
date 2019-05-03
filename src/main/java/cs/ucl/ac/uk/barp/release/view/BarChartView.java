package cs.ucl.ac.uk.barp.release.view;

import org.jfree.ui.RefineryUtilities;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class BarChartView extends View {

	private int noOfReleases;

	public BarChartView(OptimalSolutions solutions, int noReleases) {
		this.solutions = solutions;
		this.solutions.attachView(this);
		this.noOfReleases = noReleases;
	}

	@Override
	public void update() {
		BarChart chart = new BarChart(solutions.getSolutions(), noOfReleases);
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}

}
