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
import cs.ucl.ac.uk.barp.release.view.ScatterBearsRigidVSRPRisk;
import cs.ucl.ac.uk.barp.release.view.ScatterRigidVSRPRiskSRPObjective;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class Bears1VsSRPRisk {

	public static void main(String[] args) throws Exception {

		Project project = ProjectParser.parseCSVToProject(ExperimentConfiguration.FILENAME,
				ExperimentConfiguration.distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(ExperimentConfiguration.capacity);
		project.setInterestRate(ExperimentConfiguration.interestRate);
		project.setNumberOfInvestmentPeriods(ExperimentConfiguration.noOfInvestmentHorizon);
		project.setNumberOfIterations(ExperimentConfiguration.noOfReleases);
		project.setBudgetPerRelease(ExperimentConfiguration.budget);

		MCSimulator.simulate(project.getWorkItems(), ExperimentConfiguration.noOfInvestmentHorizon,
				ExperimentConfiguration.interestRate);

		SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, true);

		Optimization bearsRigid = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);

		List<IntegerSolution> bearsRigidSolutions = bearsRigid.run();

		List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBears1Objective(bearsRigidSolutions, project);
		rigidPlans = ParetoOptimalUtil.removeDuplicate(rigidPlans);
		System.out.println(rigidPlans.size());
		List<IntegerSolution> srpSolutions = ObjectiveValueUtil.runSRPRisk(srpProject);
		List<ReleasePlan> srpPlans = ObjectiveValueUtil.computeBears1Objective(srpSolutions, project);
		double maxValue = ObjectiveValueUtil.getMaxEconomicValue(project);
		ObjectiveValueUtil.computeSRPObjectives(srpPlans, ExperimentConfiguration.noOfReleases, 
				ExperimentConfiguration.capacity, maxValue);
		ObjectiveValueUtil.computeSRPObjectives(rigidPlans, ExperimentConfiguration.noOfReleases, 
				ExperimentConfiguration.capacity, maxValue);
		// srpPlans = ParetoOptimalUtil.removeDuplicate(srpPlans);
		ScatterBearsRigidVSRPRisk scatterPlot = new ScatterBearsRigidVSRPRisk("RIGID BEARS Vs SRPRisk", rigidPlans,
				srpPlans);
		ScatterRigidVSRPRiskSRPObjective scatterPlot1 = new ScatterRigidVSRPRiskSRPObjective("RIGID BEARS Vs SRPRisk", rigidPlans,
				srpPlans);
		scatterPlot1.drawPlot();
		scatterPlot.drawPlot();

	}

}
