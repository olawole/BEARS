package cs.ucl.ac.uk.barp.release.view;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class GanttChartView extends View {

	public GanttChartView(OptimalSolutions solutions) {
		this.solutions = solutions;
		this.solutions.attachView(this);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
