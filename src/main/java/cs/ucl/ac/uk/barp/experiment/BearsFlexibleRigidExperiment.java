package cs.ucl.ac.uk.barp.experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalLogger;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;

public class BearsFlexibleRigidExperiment {

	final static int INDEPENDENT_RUNS = 30;
	final String dataDirectory = "data/";
	final static String referencePareto = "pareto_front3";
	final static String resultDirectory = "result3";
	final String[] files = { "councilNew2"};
	final static int[] noOfReleases = { 1, 2, 3 };
	final double capacityPerRelease = 500.0;
	final double interestRate = 0.02;
	final int noOfHorizons = 12;
	final double budgetPerRelease = 500;
	final String distributionType = "LogNormal";
	

	public static void main(String[] args) throws IOException {
		//JMetalLogger.configureLoggers(null);
		BearsFlexibleRigidExperiment experiment = new BearsFlexibleRigidExperiment();
		HashMap<String, Project> projects = experiment.getProjects();
		String RIGIDPATH = resultDirectory + "/rigid";
		String BEARSPATH = resultDirectory + "/bears";
		createDirectory(RIGIDPATH);
		createDirectory(BEARSPATH);
		createDirectory(referencePareto);
		for (Map.Entry<String, Project> entry : projects.entrySet()) {
			String name = entry.getKey();
			Project project = entry.getValue();
			for (int i = 3; i <= noOfReleases.length; i++) {
				List<ReleasePlan> allPlans = new ArrayList<>();
				List<Double> rigidRuntimes = new ArrayList<Double>();
				List<Double> bearsRuntimes = new ArrayList<Double>();
				for (int k = 0; k < INDEPENDENT_RUNS; k++) {	
					JMetalLogger.logger.info("RUNNING: " + name + " Run " + k);
					project.setEffortCapacity(experiment.setEffortCapacity(i));
					project.setBudgetPerRelease(experiment.setBudgetCapacity(i));
					project.setNumberOfIterations(i);
					Optimization optimisation = new Optimization(project, "Barp", "NSGAII");
					Optimization optimisation1 = new Optimization(project, "Bears1", "NSGAII");
					Long startTime = System.currentTimeMillis();
					List<IntegerSolution> rigidSolutions = optimisation1.run();
					Long endTime = System.currentTimeMillis();
					Double rigidRuntime = (endTime - startTime) / 1000.0;
					rigidRuntimes.add(rigidRuntime);
					startTime = System.currentTimeMillis();
					List<IntegerSolution> bearsSolutions = optimisation.run();
					endTime = System.currentTimeMillis();
					Double bearsRuntime = (endTime - startTime) / 1000.0;
					bearsRuntimes.add(bearsRuntime);
					List<ReleasePlan> rigidPlan = ObjectiveValueUtil.computeBearsInObjectives(rigidSolutions,
							project);
					List<ReleasePlan> bearsPlan = ObjectiveValueUtil.computeBearsInObjectives(bearsSolutions, project);
					//bearsPlan = ParetoOptimalUtil.removeDuplicate(bearsPlan);
					//evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
					experiment.writeSolutions(BEARSPATH + "/" + name + "_" + i , bearsPlan, k);
					experiment.writeSolutions(RIGIDPATH + "/" + name + "_" + i, rigidPlan, k);

					allPlans.addAll(rigidPlan);
					allPlans.addAll(bearsPlan);
				}
				JMetalLogger.logger.info("RF: Writing Pareto Front to " + name + "_" + i + ".rf");
				experiment.writeRuntimes(BEARSPATH + "/" + name + "_" + i, bearsRuntimes);
				experiment.writeRuntimes(RIGIDPATH + "/" + name + "_" + i, rigidRuntimes);
				//allPlans = ParetoOptimalUtil.removeDuplicate(allPlans);
				allPlans = ParetoOptimalUtil.findParetoOptimal(allPlans);
				experiment.writeReferencePareto(name + "_" + i, allPlans);

			}
		}

	}

	private void writeRuntimes(String Path, List<Double> bearsRuntimes) {
		try {
			FileWriter output = new FileWriter(Path + "/runtime.tsv");
			for (Double runtime : bearsRuntimes){
				output.write(runtime.toString());
				output.write("\n");
			}
			output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param PATH
	 */
	private static void createDirectory(String PATH) {
		File directory = new File(PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	private void writeSolutions(String Path, List<ReleasePlan> solutions, int run) {
		createDirectory(Path);
		String writeString = "";
		for (ReleasePlan plan : solutions) {
			writeString += plan.getBusinessValue() + "\t" + plan.getExpectedPunctuality();
			writeString += "\n";
		}
		try {
			FileWriter output = new FileWriter(Path + "/FUN" + run + ".tsv");
			output.write(writeString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeReferencePareto(String name, List<ReleasePlan> plans) {
		String writeString = "";
		for (ReleasePlan plan : plans) {
			writeString += plan.getBusinessValue() + "\t" + plan.getExpectedPunctuality();
			writeString += "\n";
		}
		try {
			FileWriter output = new FileWriter(referencePareto + "/" + name + ".rf");
			output.write(writeString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private HashMap<String, Project> getProjects() {
		HashMap<String, Project> projects = new HashMap<>();
		for (int i = 0; i < files.length; i++) {
			String filePath = dataDirectory + files[i] + ".csv";
			try {
				Project project = ProjectParser.parseCSVToProjectExp(filePath, distributionType);
				project.setInterestRate(interestRate);
				project.setNumberOfInvestmentPeriods(noOfHorizons);
				project.checkTransitiveDependency();
				MCSimulator.simulate(project.getWorkItems(), noOfHorizons, interestRate);
				projects.put(files[i], project);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return projects;
	}

	private double[] setEffortCapacity(int noOfRelease) {
		double[] capacity = new double[noOfRelease];
		for (int i = 0; i < noOfRelease; i++) {
			capacity[i] = capacityPerRelease;
		}
		return capacity;
	}

	private double[] setBudgetCapacity(int noOfRelease) {
		double[] budget = new double[noOfRelease];
		for (int i = 0; i < noOfRelease; i++) {
			budget[i] = budgetPerRelease;
		}
		return budget;
	}

}
