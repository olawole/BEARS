package cs.ucl.ac.uk.barp;

import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;
import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.SolutionFileWriterUtil;
import cs.ucl.ac.uk.barp.release.view.ScatterBears0Evolve;
import cs.ucl.ac.uk.evolve.EvolveProject;

public class Bears0VsEvolveII {

	public static void main(String[] args) throws Exception {

		Project project = ProjectParser.parseCSVToProject(ExperimentConfiguration.FILENAME,
				ExperimentConfiguration.distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(ExperimentConfiguration.capacity);
		project.setInterestRate(ExperimentConfiguration.interestRate);
		project.setNumberOfInvestmentPeriods(ExperimentConfiguration.noOfInvestmentHorizon);
		project.setNumberOfIterations(ExperimentConfiguration.noOfReleases);
		project.setBudgetPerRelease(ExperimentConfiguration.budget);
		
		MCSimulator.simulate(project.getWorkItems(), ExperimentConfiguration.noOfInvestmentHorizon, ExperimentConfiguration.interestRate);

		EvolveProject evolve = ObjectiveValueUtil.convertProjectToEvolve(project, true);
		EvolveProject bears0 = ObjectiveValueUtil.convertProjectToEvolve(project, false);

		List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolve);
		List<IntegerSolution> bears0Solutions = ObjectiveValueUtil.runBears0(bears0);
		
		List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeValueInBears0(evolveSolutions, project);
		List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeValueInBears0(bears0Solutions, project);
		
		evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
		bears0Plan = ParetoOptimalUtil.removeDuplicate(bears0Plan);
		
		ParetoOptimalUtil.sortBusinessValue(bears0Plan);
		ParetoOptimalUtil.sortBusinessValue(evolvePlan);
		
		ScatterBears0Evolve scatterPlot = new ScatterBears0Evolve("BEARS0 Vs EVOLVE II", bears0Plan, evolvePlan);
		scatterPlot.drawPlot();
		
		SolutionFileWriterUtil.generateCSVTableBears0VEvolveII(bears0Plan, evolvePlan, ExperimentConfiguration.noOfReleases);

	}

}
