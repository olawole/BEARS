package cs.ucl.ac.uk.barp.release.view;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class RoadMapView extends View {
	int noOfRelease;

	public RoadMapView(OptimalSolutions solutions, int noRelease) {
		this.solutions = solutions;
		this.solutions.attachView(this);
		this.noOfRelease = noRelease;
	}

	@Override
	public void update() {
		//List<ReleasePlan> rPlans = solutions.getSolutions();
		RoadMap roadmap = new RoadMap(solutions.getSolutions(), noOfRelease);
		roadmap.writeDot1("result");
		//roadmap.writeDot2("result2");
	}

}
