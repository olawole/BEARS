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
import cs.ucl.ac.uk.barp.release.view.ScatterUncertainty;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class BearsVsUncertaintyAll {

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
		
		// Convert project to SRPRisk
		SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, true);
		Optimization optimisation1 = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);
		Optimization optimisation = new Optimization(project, "Barp", ExperimentConfiguration.algorithmType);

		// Run Evolve II and SRPRisk
		List<IntegerSolution> srpSolutions = ObjectiveValueUtil.runSRPRisk(srpProject);
		List<IntegerSolution> bearsSolutions = optimisation.run();
		List<IntegerSolution> bears1Solutions = optimisation1.run();

		// Compute their equivalent value in Bears
		List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsObjectives(srpSolutions, project);
		List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBearsObjectives(bears1Solutions, project);

		// Execute Bears

		OptimalSolutions optimal = new OptimalSolutions();

		optimal.setSolutions(bearsSolutions, project);

		srpPlan = ParetoOptimalUtil.removeDuplicate(srpPlan);
		rigidPlans = ParetoOptimalUtil.removeDuplicate(rigidPlans);

		ScatterUncertainty s = new ScatterUncertainty("", optimal.getSolutions(), rigidPlans, srpPlan);
		s.drawPlot();
		
		System.out.println(optimal.getSolutions().size());
	}

}
