package cs.ucl.ac.uk.barp.release.view;

import java.io.File;

//import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class RoadMapView extends View {
	int noOfRelease;
	String dotFilePath;

	public RoadMapView(OptimalSolutions solutions, int noRelease, String path) {
		this.solutions = solutions;
		this.solutions.attachView(this);
		this.noOfRelease = noRelease;
//		this.dotFilePath = ConfigSetting.DOT_DIRECTORY+path;
		this.dotFilePath = path;
	}

	@Override
	public void update() {
		//List<ReleasePlan> rPlans = solutions.getSolutions();
		RoadMap roadmap = new RoadMap(solutions.getSolutions(), noOfRelease);
		roadmap.writeDot(dotFilePath);
		GraphViz gv = new GraphViz();
		gv.readSource(dotFilePath+".dot");
		String type = "pdf";
		String representationType= "dot";
		File outFile = new File(dotFilePath + "." + type);
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, representationType), outFile);
	}

}
