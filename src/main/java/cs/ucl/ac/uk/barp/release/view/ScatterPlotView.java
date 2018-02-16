package cs.ucl.ac.uk.barp.release.view;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class ScatterPlotView extends View {
	ScatterPlot scatter;
	String xAxis;

	public ScatterPlotView(OptimalSolutions solutions) {
		this.solutions = solutions;
		this.solutions.attachView(this);
	}

	public ScatterPlotView(OptimalSolutions solutions, String xAxisLabel) {
		this.solutions = solutions;
		this.solutions.attachView(this);
		this.xAxis = xAxisLabel;
		
	}

	@Override
	public void update() {
		scatter = new ScatterPlot("", solutions.getSolutions(), xAxis);
		scatter.drawPlot();
	}

}
