package cs.ucl.ac.uk.barp.experiment.tosem;

import java.io.IOException;

public class TSExperiment {

	public TSExperiment() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		
		if(args.length != 1){
			System.err.println("One argument must be specified");
			return;
		}
		
		switch(args[0]){
		case "word": 
			ExperimentWord wordExperiment = new ExperimentWord();
			wordExperiment.run();
			break;
		case "ralic": 
			ExperimentRalic ralicExperiment = new ExperimentRalic();
			ralicExperiment.run();
			break;
		case "council": 
			ExperimentCouncil councilExperiment = new ExperimentCouncil();
			councilExperiment.run();
			break;
		case "planner": 
			ExperimentRPlanner plannerExperiment = new ExperimentRPlanner();
			plannerExperiment.run();
			break;
		case "b30": 
			ExperimentSynthetic30 experiment30 = new ExperimentSynthetic30();
			experiment30.run();
			break;
		case "b50": 
			ExperimentSynthetic50 experiment50 = new ExperimentSynthetic50();
			experiment50.run();
			break;
		case "b100": 
			ExperimentSynthetic100 experiment100 = new ExperimentSynthetic100();
			experiment100.run();
			break;
		
		case "b200": 
			ExperimentSynthetic200 experiment200 = new ExperimentSynthetic200();
			experiment200.run();
			break;
		default: System.out.println("Incorrect Argument Specified");
		}

	}

}
