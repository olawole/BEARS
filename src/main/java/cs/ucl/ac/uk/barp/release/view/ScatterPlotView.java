package cs.ucl.ac.uk.barp.release.view;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class ScatterPlotView extends View {
	ScatterPlot scatter;

	public ScatterPlotView(OptimalSolutions solutions) {
		this.solutions = solutions;
		this.solutions.attachView(this);
	}

	@Override
	public void update() {
		scatter = new ScatterPlot("Scatter plot of optimal solutions", solutions.getSolutions());
		scatter.drawPlot();
	}

}
