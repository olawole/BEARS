package cs.ucl.ac.uk.barp;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsRigidVBears;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsRigidVBearsRigidObj;

public class Bears1VsBears2 {

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
		
		Optimization bearsRigid = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);
		Optimization bears = new Optimization(project, "Barp", ExperimentConfiguration.algorithmType);
		
		List<IntegerSolution> bearsRigidSolutions = bearsRigid.run();
		List<IntegerSolution> bearsSolutions = bears.run();
		OptimalSolutions bearsOptimal = new OptimalSolutions();
		
		bearsOptimal.setSolutions(bearsSolutions, project);
		
		List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBearsObjectives(bearsRigidSolutions, project);
		
//		rigidPlans = ParetoOptimalUtil.removeDuplicate(rigidPlans);
//		System.out.println(rigidPlans.size());
//		ParetoOptimalUtil.removeDuplicate(bearsOptimal.getSolutions());
		List<ReleasePlan> rigidPlansRigidObj = ObjectiveValueUtil.computeBears1Objective(bearsRigidSolutions, project);
		List<ReleasePlan> bearsPlansRigidObj = new ArrayList<>(bearsOptimal.getSolutions());
		
		ObjectiveValueUtil.computePlanBears1Objective(bearsPlansRigidObj, project);
		
		ScatterBearsRigidVBearsRigidObj plot1 = new ScatterBearsRigidVBearsRigidObj("", bearsPlansRigidObj, rigidPlansRigidObj);
		ScatterBearsRigidVBears plot = new ScatterBearsRigidVBears("Flexible Vs Rigid Bears", bearsOptimal.getSolutions(), rigidPlans);
		plot1.drawPlot();
		plot.drawPlot();
	}

}
