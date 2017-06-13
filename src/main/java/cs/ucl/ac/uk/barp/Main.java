package cs.ucl.ac.uk.barp;

import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.optimization.InformationValueAnalyser;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.project.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.BarChartView;
import cs.ucl.ac.uk.barp.release.view.RoadMapView;
import cs.ucl.ac.uk.barp.release.view.ScatterPlotView;

public class Main {
	
	static String filename;
	static int noOfReleases;
	static int noOfInvestmentHorizon;
	static double capacity[];
	static double interestRate;
	static String distributionType;
	static String algorithmType;
	
	public Main() {
		filename = "usample.csv";
		noOfReleases = 5;
		noOfInvestmentHorizon = 8;
		capacity = new double[]{50,50,50,40,40};
		interestRate = 0.002;
		distributionType = "Normal";
		algorithmType = ConfigSetting.DEFAULT_APRROX_ALGORITHM;
	}
	
	public static void main(String[] args) throws Exception{
		new Main();
		String problemType = getProblemType();
		Project project = ProjectParser.parseCSVToProject(filename, distributionType);
		project.setEffortCapacity(capacity);
		project.setInterestRate(interestRate);
		project.setNumberOfInvestmentPeriods(noOfInvestmentHorizon);
		project.setNumberOfIterations(noOfReleases);
		if (problemType.equalsIgnoreCase("Barp")){
			MCSimulator.simulate(project.getWorkItems(), noOfInvestmentHorizon, interestRate);
		}
		Optimization optimisation = new Optimization(project, problemType, algorithmType);
		List<IntegerSolution> solutions = optimisation.run();
		OptimalSolutions optimal = new OptimalSolutions();
		new ScatterPlotView(optimal);
		new RoadMapView(optimal, noOfReleases);
		new BarChartView(optimal, noOfReleases);
		optimal.setSolutions(solutions, project);
		InformationValueAnalyser.computeInformationValue(optimal, project.getWorkItems());	
		optimal.printEvppi();
	}

	private static String getProblemType() {
		if (distributionType.equalsIgnoreCase("Point")){
			return "BarpCertain";
		}
		else {
			return "Barp";
		}
	}

}
