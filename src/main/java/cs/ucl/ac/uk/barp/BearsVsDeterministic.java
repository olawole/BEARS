package cs.ucl.ac.uk.barp;

import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.ScatterDeterministic;
import cs.ucl.ac.uk.evolve.EvolveProject;

public class BearsVsDeterministic {

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
		Optimization optimisation = new Optimization(project, "Barp", ExperimentConfiguration.algorithmType);

		// Run Evolve II and SRPRisk
		List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolve);
		List<IntegerSolution> bears0Solutions = ObjectiveValueUtil.runEvolve(bears0);
		List<IntegerSolution> bearsSolutions = optimisation.run();

		// Compute their equivalent value in Bears
		List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsObjectives(evolveSolutions, project);
		List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeBearsObjectives(bears0Solutions, project);

		// Execute Bears

		OptimalSolutions optimal = new OptimalSolutions();

		optimal.setSolutions(bearsSolutions, project);

		evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
		bears0Plan = ParetoOptimalUtil.removeDuplicate(bears0Plan);

		
		// compute objectives in BEARS 1
		ObjectiveValueUtil.computePlanBears1Objective(bears0Plan, project);
		ObjectiveValueUtil.computePlanBears1Objective(evolvePlan, project);
		ObjectiveValueUtil.computePlanBears1Objective(optimal.getSolutions(), project);
		
		ScatterDeterministic s = new ScatterDeterministic("", optimal.getSolutions(), bears0Plan, evolvePlan);
		s.drawPlot();

		System.out.println(optimal.getSolutions().size());
	}

}
