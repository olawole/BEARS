package cs.ucl.ac.uk.barp.experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.evolve.EvolveProject;

public class BearsEvolveExperiment {

	final static int INDEPENDENT_RUNS = 5;
	final String dataDirectory = "data/";
	final static String referencePareto = "pareto_front";
	final static String resultDirectory = "result";
	final String[] files = { "b30", "b50", "b100", "b200" };
	final static int[] noOfReleases = { 1, 2, 3, 4, 5 };
	final double capacityPerRelease = 400.0;
	final double interestRate = 0.02;
	final int noOfHorizons = 12;
	final double budgetPerRelease = 500;
	final String distributionType = "LogNormal";

	public static void main(String[] args) {
		BearsEvolveExperiment experiment = new BearsEvolveExperiment();
		HashMap<String, Project> projects = experiment.getProjects();
		String EVOLVEPATH = resultDirectory + "/evolve";
		String BEARSPATH = resultDirectory + "/bears";
		createDirectory(EVOLVEPATH);
		createDirectory(BEARSPATH);
		createDirectory(referencePareto);
		for (Map.Entry<String, Project> entry : projects.entrySet()) {
			String name = entry.getKey();
			Project project = entry.getValue();
			for (int i = 1; i <= noOfReleases.length; i++) {
				List<ReleasePlan> allPlans = new ArrayList<>();
				List<Double> evolveRuntimes = new ArrayList<Double>();
				List<Double> bearsRuntimes = new ArrayList<Double>();
				for (int k = 0; k < INDEPENDENT_RUNS; k++) {					
					project.setEffortCapacity(experiment.setEffortCapacity(i));
					project.setBudgetPerRelease(experiment.setBudgetCapacity(i));
					project.setNumberOfIterations(i);
					EvolveProject evolveProject = ObjectiveValueUtil.convertProjectToEvolve(project, true);
					Optimization optimisation = new Optimization(project, "Barp", "NSGAII");
					Long startTime = System.currentTimeMillis();
					List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolveProject);
					Long endTime = System.currentTimeMillis();
					Double evolveRuntime = (endTime - startTime) / 1000.0;
					evolveRuntimes.add(evolveRuntime);
					startTime = System.currentTimeMillis();
					List<IntegerSolution> bearsSolutions = optimisation.run();
					endTime = System.currentTimeMillis();
					Double bearsRuntime = (endTime - startTime) / 1000.0;
					bearsRuntimes.add(bearsRuntime);
					List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsInObjectives(evolveSolutions,
							project);
					List<ReleasePlan> bearsPlan = ObjectiveValueUtil.computeBearsInObjectives(bearsSolutions, project);
					//bearsPlan = ParetoOptimalUtil.removeDuplicate(bearsPlan);
					//evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
					experiment.writeSolutions(BEARSPATH + "/" + name + "_" + i , bearsPlan, k);
					experiment.writeSolutions(EVOLVEPATH + "/" + name + "_" + i, evolvePlan, k);

					allPlans.addAll(evolvePlan);
					allPlans.addAll(bearsPlan);
				}
				experiment.writeRuntimes(BEARSPATH + "/" + name + "_" + i, bearsRuntimes);
				experiment.writeRuntimes(EVOLVEPATH + "/" + name + "_" + i, evolveRuntimes);
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
