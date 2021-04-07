package cs.ucl.ac.uk.barp.experiment.tosem;

import java.io.IOException;

public class TSEExperiment {

	public TSEExperiment() {
		// TODO Auto-generated constructor stub
	}

	public static void run() throws IOException {
		ExperimentWord wordExperiment = new ExperimentWord();
		wordExperiment.run();
		ExperimentRalic ralicExperiment = new ExperimentRalic();
		ralicExperiment.run();
		ExperimentCouncil councilExperiment = new ExperimentCouncil();
		councilExperiment.run();
		ExperimentRPlanner plannerExperiment = new ExperimentRPlanner();
		plannerExperiment.run();
		ExperimentSynthetic30 experiment30 = new ExperimentSynthetic30();
		experiment30.run();
		ExperimentSynthetic50 experiment50 = new ExperimentSynthetic50();
		experiment50.run();
		ExperimentSynthetic100 experiment100 = new ExperimentSynthetic100();
		experiment100.run();
		ExperimentSynthetic200 experiment200 = new ExperimentSynthetic200();
		experiment200.run();
	}

}
