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
import cs.ucl.ac.uk.barp.release.view.CashAnalysisView;
import cs.ucl.ac.uk.barp.release.view.RoadMapView;
import cs.ucl.ac.uk.barp.release.view.ScatterPlotView;
import cs.ucl.ac.uk.barp.release.view.TableView;

public class Main {
	
	static String filename;
	static int noOfReleases;
	static int noOfInvestmentHorizon;
	static double capacity[];
	static double budget[];
	static double interestRate;
	static String distributionType;
	static String algorithmType;
	
	public Main() {
		filename = "usample.csv";
		noOfReleases = 4;
		noOfInvestmentHorizon = 10;
		budget = new double[]{100, 80, 70, 70};
//		capacity = new double[]{500,400,400, 300};
		capacity = new double[]{80, 70, 60, 50};

		interestRate = 0.02;
		distributionType = "Normal";
		algorithmType = ConfigSetting.DEFAULT_APRROX_ALGORITHM;
	}
	
	public static void main(String[] args) throws Exception{
		new Main();
		String problemType = getProblemType();
		Project project = ProjectParser.parseCSVToProject(filename, distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(capacity);
		project.setInterestRate(interestRate);
		project.setNumberOfInvestmentPeriods(noOfInvestmentHorizon);
		project.setNumberOfIterations(noOfReleases);
		project.setBudgetPerRelease(budget);
		if (problemType.equalsIgnoreCase("Barp")){
			MCSimulator.simulate(project.getWorkItems(), noOfInvestmentHorizon, interestRate);
		}
		Optimization optimisation = new Optimization(project, problemType, algorithmType);
		List<IntegerSolution> solutions = optimisation.run();
		System.out.println(solutions.size());
		OptimalSolutions optimal = new OptimalSolutions();
		new ScatterPlotView(optimal);
		new RoadMapView(optimal, noOfReleases, filename);
		new BarChartView(optimal, noOfReleases);
		new TableView(optimal, noOfReleases);
		new CashAnalysisView(optimal, project.getNumberOfInvestmentPeriods(), project.getBudgetPerRelease(), project.getInterestRate());
		optimal.setSolutions(solutions, project);
		System.out.println(optimal.getSolutions().size());
//		sol = optimal.getSolutions().size();
		InformationValueAnalyser.computeInformationValue(optimal, project.getWorkItems());	
		optimal.printEvppi();
//		} while (sol < 2);
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
