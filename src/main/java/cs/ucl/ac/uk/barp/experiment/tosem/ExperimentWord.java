package cs.ucl.ac.uk.barp.experiment.tosem;

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
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class ExperimentWord {

	final static int INDEPENDENT_RUNS = 30;
	// final String dataDirectory = "data/";
	final static String referencePareto = "pareto_front";
	final static String resultDirectory = "Result-TSE";
	final double interestRate = 0.02;
	final int noOfHorizons = 12;
	final String distributionType = "LogNormal";
	HashMap<String, double[]> importance;

	public ExperimentWord() {
		importance = new HashMap<>();
		importance.put("WordProcessing-2", new double[] { 9, 8 });
		importance.put("WordProcessing-3", new double[] { 9, 8, 7 });
		importance.put("WordProcessing-4", new double[] { 9, 8, 7, 6 });
		importance.put("WordProcessing-5", new double[] { 9, 8, 7, 6, 6 });
	}

	public void run() throws IOException {
		// JMetalLogger.configureLoggers(null);
		
		HashMap<String, Project> projects = getProjects();
		createDirectory(resultDirectory);
		String VPDETERMINISTICPATH = resultDirectory + "/VP-deterministic";
		String NPVDETERMINISTICPATH = resultDirectory + "/NPV-deterministic";
		String NPVFIXEDSCOPEPATH = resultDirectory + "/NPV-fixed-scope";
		String VPFIXEDSCOPEPATH = resultDirectory + "/VP-fixed-scope";
		String BEARSPATH = resultDirectory + "/BEARS";
		createDirectory(VPDETERMINISTICPATH);
		createDirectory(BEARSPATH);
		createDirectory(NPVFIXEDSCOPEPATH);
		createDirectory(VPFIXEDSCOPEPATH);
		createDirectory(NPVDETERMINISTICPATH);
		createDirectory(referencePareto);
		for (Map.Entry<String, Project> entry : projects.entrySet()) {
			String name = entry.getKey();
			Project project = entry.getValue();
			List<ReleasePlan> allPlans = new ArrayList<>();
			List<Double> evolveRuntimes = new ArrayList<Double>();
			List<Double> rigidRuntimes = new ArrayList<Double>();
			List<Double> bears0Runtimes = new ArrayList<Double>();
			List<Double> srpRuntimes = new ArrayList<Double>();
			List<Double> bearsRuntimes = new ArrayList<Double>();
			for (int k = 0; k < INDEPENDENT_RUNS; k++) {
				JMetalLogger.logger.info("RUNNING: " + name + " Run " + k);
				SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, false);
				EvolveProject evolveProject = ObjectiveValueUtil.convertProjectToEvolve(project, false);
				EvolveProject bearsDeterministic = ObjectiveValueUtil.convertProjectToEvolve(project, false);
				srpProject.releaseImp = importance.get(name);
				evolveProject.releaseImp = importance.get(name);
				Optimization optimisation = new Optimization(project, "Barp", "NSGAII");
				Optimization optimisation1 = new Optimization(project, "Bears1", "NSGAII");

				Long startTime = System.currentTimeMillis();
				List<IntegerSolution> srpSolutions = ObjectiveValueUtil.runSRPRisk(srpProject);
				Long endTime = System.currentTimeMillis();
				Double srpRuntime = (endTime - startTime) / 1000.0;
				srpRuntimes.add(srpRuntime);

				startTime = System.currentTimeMillis();
				List<IntegerSolution> bearsSolutions = optimisation.run();
				endTime = System.currentTimeMillis();
				Double bearsRuntime = (endTime - startTime) / 1000.0;
				bearsRuntimes.add(bearsRuntime);

				startTime = System.currentTimeMillis();
				List<IntegerSolution> bears0Solutions = ObjectiveValueUtil.runBears0(bearsDeterministic);
				endTime = System.currentTimeMillis();
				Double bears0Runtime = (endTime - startTime) / 1000.0;
				bears0Runtimes.add(bears0Runtime);

				startTime = System.currentTimeMillis();
				List<IntegerSolution> rigidSolutions = optimisation1.run();
				endTime = System.currentTimeMillis();
				Double rigidRuntime = (endTime - startTime) / 1000.0;
				rigidRuntimes.add(rigidRuntime);

				startTime = System.currentTimeMillis();
				List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolveProject);
				endTime = System.currentTimeMillis();
				Double evolveRuntime = (endTime - startTime) / 1000.0;
				evolveRuntimes.add(evolveRuntime);
				
				OptimalSolutions optimal = new OptimalSolutions();
				optimal.setSolutions(bearsSolutions, project);
				int noOfBearsSolutions = optimal.getSolutions().size();
				rigidSolutions = ParetoOptimalUtil.findParetoOptimalSol(rigidSolutions);
				srpSolutions = ParetoOptimalUtil.findParetoOptimalSol(srpSolutions);
				bears0Solutions = ParetoOptimalUtil.sortValuePoint(bears0Solutions, noOfBearsSolutions);
				evolveSolutions = ParetoOptimalUtil.sortValuePoint(evolveSolutions, noOfBearsSolutions);

				List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsInObjectives(evolveSolutions, project);
				List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeBearsInObjectives(bears0Solutions, project);
				List<ReleasePlan> rigidPlan = ObjectiveValueUtil.computeBearsInObjectives(rigidSolutions, project);
				List<ReleasePlan> bearsPlan = ObjectiveValueUtil.computeBearsInObjectives(bearsSolutions, project);

				List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsInObjectives(srpSolutions, project);
				// bearsPlan = ParetoOptimalUtil.removeDuplicate(bearsPlan);
				// evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
				writeSolutions(BEARSPATH + "/" + name, bearsPlan, k);
				writeSolutions(VPFIXEDSCOPEPATH + "/" + name, srpPlan, k);
				writeSolutions(VPDETERMINISTICPATH + "/" + name, evolvePlan, k);
				writeSolutions(NPVFIXEDSCOPEPATH + "/" + name, rigidPlan, k);
				writeSolutions(NPVDETERMINISTICPATH + "/" + name, bears0Plan, k);

				allPlans.addAll(evolvePlan);
				allPlans.addAll(bearsPlan);
				allPlans.addAll(rigidPlan);
				allPlans.addAll(srpPlan);
				allPlans.addAll(bears0Plan);
			}
			JMetalLogger.logger.info("RF: Writing Pareto Front to " + name + ".rf");
			writeRuntimes(BEARSPATH + "/" + name, bearsRuntimes);
			writeRuntimes(VPDETERMINISTICPATH + "/" + name, evolveRuntimes);
			writeRuntimes(NPVFIXEDSCOPEPATH + "/" + name, rigidRuntimes);
			writeRuntimes(VPFIXEDSCOPEPATH + "/" + name, srpRuntimes);
			writeRuntimes(NPVDETERMINISTICPATH + "/" + name, bears0Runtimes);
			allPlans = ParetoOptimalUtil.removeDuplicate(allPlans);
			allPlans = ParetoOptimalUtil.findParetoOptimal(allPlans);
			writeReferencePareto(name, allPlans);

		}

	}

	private void writeRuntimes(String Path, List<Double> bearsRuntimes) {
		try {
			FileWriter output = new FileWriter(Path + "/runtime.tsv");
			for (Double runtime : bearsRuntimes) {
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
		try {
			Project wordP2 = ProjectParser.parseCSVToProjectExp("models/word-processing.csv", distributionType);
			wordP2.setInterestRate(interestRate);
			wordP2.setNumberOfInvestmentPeriods(noOfHorizons);
			wordP2.setEffortCapacity(new double[] { 725, 693});
			wordP2.setBudgetPerRelease(new double[] { 0, 0});
			wordP2.setNumberOfIterations(2);
			wordP2.checkTransitiveDependency();
			MCSimulator.simulate(wordP2.getWorkItems(), noOfHorizons, interestRate);
			
			Project wordP3 = new Project(wordP2);
			wordP3.setEffortCapacity(new double[] { 725, 693, 675 });
			wordP3.setBudgetPerRelease(new double[] { 0, 0, 0 });
			wordP3.setNumberOfIterations(3);
			
			Project wordP4 = new Project(wordP2);
			wordP4.setEffortCapacity(new double[] { 725, 693, 675, 600 });
			wordP4.setBudgetPerRelease(new double[] { 0, 0, 0, 0 });
			wordP4.setNumberOfIterations(4);
			
			Project wordP5 = new Project(wordP2);
			wordP5.setEffortCapacity(new double[] { 725, 693, 675, 600, 600 });
			wordP5.setBudgetPerRelease(new double[] { 0, 0, 0, 0, 0 });
			wordP5.setNumberOfIterations(5);

			projects.put("WordProcessing-2", wordP2);
			projects.put("WordProcessing-3", wordP3);
			projects.put("WordProcessing-4", wordP4);
			projects.put("WordProcessing-5", wordP5);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return projects;
	}

}
