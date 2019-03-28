package cs.ucl.ac.uk.barp;

import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.SolutionFileWriterUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsVSAll;
import cs.ucl.ac.uk.barp.release.view.ScatterDeterministic;
import cs.ucl.ac.uk.barp.release.view.ScatterPlotView;
import cs.ucl.ac.uk.barp.release.view.ScatterUncertainty;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class Bears2VsAll {

	public static void main(String[] args) throws Exception {
		Project project = ProjectParser.parseCSVToProjectExp("data/councilNew2.csv",
				ExperimentConfiguration.distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(new double[]{40, 40, 40});
		project.setInterestRate(ExperimentConfiguration.interestRate);
		project.setNumberOfInvestmentPeriods(ExperimentConfiguration.noOfInvestmentHorizon);
		project.setNumberOfIterations(3);
		project.setBudgetPerRelease(new double[]{0, 0, 0});
		// simulation
		MCSimulator.simulate(project.getWorkItems(), ExperimentConfiguration.noOfInvestmentHorizon,
				ExperimentConfiguration.interestRate);
		// convert project to Evolve II
		EvolveProject evolve = ObjectiveValueUtil.convertProjectToEvolve(project, false);
		evolve.releaseImp = new double[]{9,8,7};
		EvolveProject bears0 = ObjectiveValueUtil.convertProjectToEvolve(project, false);
		// Convert project to SRPRisk
		SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, false);
		Optimization optimisation1 = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);
		Optimization optimisation = new Optimization(project, "Barp", ExperimentConfiguration.algorithmType);

		// Run Evolve II and SRPRisk
		List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolve);
		List<IntegerSolution> bears0Solutions = ObjectiveValueUtil.runBears0(bears0);
		List<IntegerSolution> srpSolutions = ObjectiveValueUtil.runSRPRisk(srpProject);
		List<IntegerSolution> bearsSolutions = optimisation.run();
		List<IntegerSolution> bears1Solutions = optimisation1.run();
		
//		printFinalSolutionSet(bears1Solutions, "bears1");
//		printFinalSolutionSet(bearsSolutions, "bears");
//		printFinalSolutionSet(srpSolutions, "srp");
//		printFinalSolutionSet(evolveSolutions, "evolve");
//		printFinalSolutionSet(bears0Solutions, "cub");

		// Compute their equivalent value in Bears
		List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsObjectives(evolveSolutions, project);
		List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeBearsObjectives(bears0Solutions, project);
		List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsObjectives(srpSolutions, project);
		List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBearsObjectives(bears1Solutions, project);

		// Execute Bears

		OptimalSolutions optimal = new OptimalSolutions();
		new ScatterPlotView(optimal, "Expected Punctuality (%)");
		optimal.setSolutions(bearsSolutions, project);

		evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
		bears0Plan = ParetoOptimalUtil.removeDuplicate(bears0Plan);
		srpPlan = ParetoOptimalUtil.removeDuplicate(srpPlan);
		rigidPlans = ParetoOptimalUtil.removeDuplicate(rigidPlans);
		
		// compute objectives in BEARS 1
//		ObjectiveValueUtil.computePlanBears1Objective(rigidPlans, project);
//		ObjectiveValueUtil.computePlanBears1Objective(bears0Plan, project);
//		ObjectiveValueUtil.computePlanBears1Objective(srpPlan, project);
//		ObjectiveValueUtil.computePlanBears1Objective(evolvePlan, project);
//		ObjectiveValueUtil.computePlanBears1Objective(optimal.getSolutions(), project);
		
		// compute SRP Objectives
//		double maxValue = ObjectiveValueUtil.getMaxEconomicValue(project);
//		ObjectiveValueUtil.computeSRPObjectives(rigidPlans, 
//				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
//		ObjectiveValueUtil.computeSRPObjectives(bears0Plan, 
//				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
//		ObjectiveValueUtil.computeSRPObjectives(srpPlan, 
//				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
//		ObjectiveValueUtil.computeSRPObjectives(evolvePlan, 
//				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
//		ObjectiveValueUtil.computeSRPObjectives(optimal.getSolutions(), 
//				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
		
//		ScatterBearsRigidVBears flexibleRigid = new ScatterBearsRigidVBears("Flexible Vs Rigid Bears", optimal.getSolutions(), rigidPlans);

//		ScatterBearsVSAll s = new ScatterBearsVSAll("BEARS vs All using Bears flexible Objective", optimal.getSolutions(), evolvePlan, srpPlan,
//				bears0Plan, rigidPlans);
//		ScatterBearsVSAllRigidObj rigid = new ScatterBearsVSAllRigidObj("BEARS vs All using Bears Rigid Objectives", optimal.getSolutions(), evolvePlan, srpPlan,
//				bears0Plan, rigidPlans);
//		ScatterBearsVSAllSRPObj srp = new ScatterBearsVSAllSRPObj("BEARS vs All using SRPRisk objectives", optimal.getSolutions(), evolvePlan, srpPlan,
//				bears0Plan, rigidPlans);
//		flexibleRigid.drawPlot();
		ScatterDeterministic s = new ScatterDeterministic("", optimal.getSolutions(), bears0Plan, evolvePlan);
		s.drawPlot();
		
		ScatterUncertainty s1 = new ScatterUncertainty("", optimal.getSolutions(), rigidPlans, srpPlan);
		s1.drawPlot();
		
		ScatterBearsVSAll sAll = new ScatterBearsVSAll("", optimal.getSolutions(), evolvePlan, srpPlan, bears0Plan, rigidPlans);
		sAll.drawPlot();
//		rigid.drawPlot();
//		srp.drawPlot();

		HashMap<String, List<ReleasePlan>> allMethodPlans = new HashMap<>();
		allMethodPlans.put("EVOLVE II", evolvePlan);
		allMethodPlans.put("SRP Risk", srpPlan);
		allMethodPlans.put("BEARS", optimal.getSolutions());
		allMethodPlans.put("BEARS0", bears0Plan);
		allMethodPlans.put("RIGID Bears", rigidPlans);
		
		SolutionFileWriterUtil.writeAll(allMethodPlans, ExperimentConfiguration.noOfReleases);

		System.out.println(optimal.getSolutions().size());
	}
	
	public static void printFinalSolutionSet(List<? extends Solution<?>> population, String name) {

	    new SolutionListOutput(population)
	        .setSeparator("\t")
	        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
	        .setFunFileOutputContext(new DefaultFileOutputContext(name + ".tsv"))
	        .print();

	    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
	    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
	    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
	  }

}
