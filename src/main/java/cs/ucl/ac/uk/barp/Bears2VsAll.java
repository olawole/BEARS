package cs.ucl.ac.uk.barp;

import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.SolutionFileWriterUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsRigidVBears;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsVSAll;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsVSAllRigidObj;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsVSAllSRPObj;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class Bears2VsAll {

	public static void main(String[] args) throws Exception {
		Project project = ProjectParser.parseCSVToProject(ExperimentConfiguration.FILENAME,
				ExperimentConfiguration.distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(ExperimentConfiguration.capacity);
		project.setInterestRate(ExperimentConfiguration.interestRate);
		project.setNumberOfInvestmentPeriods(ExperimentConfiguration.noOfInvestmentHorizon);
		project.setNumberOfIterations(ExperimentConfiguration.noOfReleases);
		project.setBudgetPerRelease(ExperimentConfiguration.budget);
		// simulation
		MCSimulator.simulate(project.getWorkItems(), ExperimentConfiguration.noOfInvestmentHorizon,
				ExperimentConfiguration.interestRate);
		// convert project to Evolve II
		EvolveProject evolve = ObjectiveValueUtil.convertProjectToEvolve(project, true);
		EvolveProject bears0 = ObjectiveValueUtil.convertProjectToEvolve(project, false);
		// Convert project to SRPRisk
		SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, true);
		Optimization optimisation1 = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);
		Optimization optimisation = new Optimization(project, "Barp", ExperimentConfiguration.algorithmType);

		// Run Evolve II and SRPRisk
		List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolve);
		List<IntegerSolution> bears0Solutions = ObjectiveValueUtil.runEvolve(bears0);
		List<IntegerSolution> srpSolutions = ObjectiveValueUtil.runSRPRisk(srpProject);
		List<IntegerSolution> bearsSolutions = optimisation.run();
		List<IntegerSolution> bears1Solutions = optimisation1.run();

		// Compute their equivalent value in Bears
		List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsObjectives(evolveSolutions, project);
		List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeBearsObjectives(bears0Solutions, project);
		List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsObjectives(srpSolutions, project);
		List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBearsObjectives(bears1Solutions, project);

		// Execute Bears

		OptimalSolutions optimal = new OptimalSolutions();

		optimal.setSolutions(bearsSolutions, project);

		evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
		bears0Plan = ParetoOptimalUtil.removeDuplicate(bears0Plan);
		srpPlan = ParetoOptimalUtil.removeDuplicate(srpPlan);
		rigidPlans = ParetoOptimalUtil.removeDuplicate(rigidPlans);
		
		// compute objectives in BEARS 1
		ObjectiveValueUtil.computePlanBears1Objective(rigidPlans, project);
		ObjectiveValueUtil.computePlanBears1Objective(bears0Plan, project);
		ObjectiveValueUtil.computePlanBears1Objective(srpPlan, project);
		ObjectiveValueUtil.computePlanBears1Objective(evolvePlan, project);
		ObjectiveValueUtil.computePlanBears1Objective(optimal.getSolutions(), project);
		
		// compute SRP Objectives
		double maxValue = ObjectiveValueUtil.getMaxEconomicValue(project);
		ObjectiveValueUtil.computeSRPObjectives(rigidPlans, 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
		ObjectiveValueUtil.computeSRPObjectives(bears0Plan, 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
		ObjectiveValueUtil.computeSRPObjectives(srpPlan, 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
		ObjectiveValueUtil.computeSRPObjectives(evolvePlan, 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
		ObjectiveValueUtil.computeSRPObjectives(optimal.getSolutions(), 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity, maxValue);
		
//		ScatterBearsRigidVBears flexibleRigid = new ScatterBearsRigidVBears("Flexible Vs Rigid Bears", optimal.getSolutions(), rigidPlans);

		ScatterBearsVSAll s = new ScatterBearsVSAll("BEARS vs All using Bears flexible Objective", optimal.getSolutions(), evolvePlan, srpPlan,
				bears0Plan, rigidPlans);
		ScatterBearsVSAllRigidObj rigid = new ScatterBearsVSAllRigidObj("BEARS vs All using Bears Rigid Objectives", optimal.getSolutions(), evolvePlan, srpPlan,
				bears0Plan, rigidPlans);
		ScatterBearsVSAllSRPObj srp = new ScatterBearsVSAllSRPObj("BEARS vs All using SRPRisk objectives", optimal.getSolutions(), evolvePlan, srpPlan,
				bears0Plan, rigidPlans);
//		flexibleRigid.drawPlot();
		s.drawPlot();
		rigid.drawPlot();
		srp.drawPlot();

		HashMap<String, List<ReleasePlan>> allMethodPlans = new HashMap<>();
		allMethodPlans.put("EVOLVE II", evolvePlan);
		allMethodPlans.put("SRP Risk", srpPlan);
		allMethodPlans.put("BEARS", optimal.getSolutions());
		allMethodPlans.put("BEARS0", bears0Plan);
		allMethodPlans.put("RIGID Bears", rigidPlans);
		
		SolutionFileWriterUtil.writeAll(allMethodPlans, ExperimentConfiguration.noOfReleases);

		System.out.println(optimal.getSolutions().size());
	}

}
