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
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class ExperimentFull {

	final static int INDEPENDENT_RUNS = 30;
	// final String dataDirectory = "data/";
	final static String referencePareto = "pareto_front";
	final static String resultDirectory = "result";
	final double interestRate = 0.02;
	final int noOfHorizons = 12;
	final String distributionType = "LogNormal";
	HashMap<String, double[]> importance;

	public ExperimentFull() {
		importance = new HashMap<>();
		importance.put("council2", new double[] { 9, 8 });
		importance.put("council3", new double[] { 9, 8, 7 });
		importance.put("council4", new double[] { 9, 8, 7, 6 });
		importance.put("council5", new double[] { 9, 8, 7, 6, 5 });
		importance.put("RALICR5", new double[] { 9, 8, 7, 6, 6 });
		importance.put("RALICR2", new double[] { 9, 8 });
		importance.put("RALICR4", new double[]{9,8,7,6});
		importance.put("RALICR3", new double[]{9,8,7});
		importance.put("B30_5", new double[] { 9, 8, 7, 6, 6 });
		importance.put("B30_4", new double[] { 9, 8, 7, 6 });
		importance.put("B30_3", new double[] { 9, 8, 7 });
		importance.put("B30_2", new double[] { 9, 8 });
		importance.put("B50_5", new double[] { 9, 8, 7, 6, 6 });
		importance.put("B50_4", new double[] { 9, 8, 7, 6 });
		importance.put("B50_3", new double[] { 9, 8, 7 });
		importance.put("B50_2", new double[] { 9, 8 });
		importance.put("RALICP2", new double[]{9,8});
		importance.put("RALICP4", new double[]{9,8,7,6});
		importance.put("RALICP3", new double[]{9,8,7});
		importance.put("ReleasePlanner", new double[] { 9, 5, 3 });
		importance.put("WordProcessing", new double[]{9,8,8});
	}

	public static void main(String[] args) throws IOException {
		// JMetalLogger.configureLoggers(null);
		ExperimentFull experiment = new ExperimentFull();
		HashMap<String, Project> projects = experiment.getProjects();
		createDirectory(resultDirectory);
		String EVOLVEPATH = resultDirectory + "/evolve";
		String BEARS0PATH = resultDirectory + "/deterministicBears";
		String RIGIDPATH = resultDirectory + "/fixed";
		String SRPRISKPATH = resultDirectory + "/evolveUncertainty";
		String BEARSPATH = resultDirectory + "/bears";
		createDirectory(EVOLVEPATH);
		createDirectory(BEARSPATH);
		createDirectory(RIGIDPATH);
		createDirectory(SRPRISKPATH);
		createDirectory(BEARS0PATH);
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
				srpProject.releaseImp = experiment.importance.get(name);
				evolveProject.releaseImp = experiment.importance.get(name);
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

				List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsInObjectives(evolveSolutions, project);
				List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeBearsInObjectives(bears0Solutions, project);
				List<ReleasePlan> rigidPlan = ObjectiveValueUtil.computeBearsInObjectives(rigidSolutions, project);
				List<ReleasePlan> bearsPlan = ObjectiveValueUtil.computeBearsInObjectives(bearsSolutions, project);

				List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsInObjectives(srpSolutions, project);
				// bearsPlan = ParetoOptimalUtil.removeDuplicate(bearsPlan);
				// evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
				experiment.writeSolutions(BEARSPATH + "/" + name, bearsPlan, k);
				experiment.writeSolutions(SRPRISKPATH + "/" + name, srpPlan, k);
				experiment.writeSolutions(EVOLVEPATH + "/" + name, evolvePlan, k);
				experiment.writeSolutions(RIGIDPATH + "/" + name, rigidPlan, k);
				experiment.writeSolutions(BEARS0PATH + "/" + name, bears0Plan, k);

				allPlans.addAll(evolvePlan);
				allPlans.addAll(bearsPlan);
				allPlans.addAll(rigidPlan);
				allPlans.addAll(srpPlan);
				allPlans.addAll(bears0Plan);
			}
			JMetalLogger.logger.info("RF: Writing Pareto Front to " + name + ".rf");
			experiment.writeRuntimes(BEARSPATH + "/" + name, bearsRuntimes);
			experiment.writeRuntimes(EVOLVEPATH + "/" + name, evolveRuntimes);
			experiment.writeRuntimes(RIGIDPATH + "/" + name, rigidRuntimes);
			experiment.writeRuntimes(SRPRISKPATH + "/" + name, srpRuntimes);
			experiment.writeRuntimes(BEARS0PATH + "/" + name, bears0Runtimes);
			allPlans = ParetoOptimalUtil.removeDuplicate(allPlans);
			allPlans = ParetoOptimalUtil.findParetoOptimal(allPlans);
			experiment.writeReferencePareto(name, allPlans);

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
//			Project councilProject = ProjectParser.parseCSVToProjectExp("data/councilNew2.csv", distributionType);
//			councilProject.setInterestRate(interestRate);
//			councilProject.setNumberOfInvestmentPeriods(noOfHorizons);
//			councilProject.setEffortCapacity(new double[] { 500, 500, 500 });
//			councilProject.setBudgetPerRelease(new double[] { 0, 0, 0 });
//			councilProject.setNumberOfIterations(3);
//			councilProject.checkTransitiveDependency();
//			MCSimulator.simulate(councilProject.getWorkItems(), noOfHorizons, interestRate);
//
//			Project ralicR = ProjectParser.parseCSVToProjectExp("data/ralic-rate.csv", distributionType);
//			ralicR.setInterestRate(interestRate);
//			ralicR.setNumberOfInvestmentPeriods(noOfHorizons);
//			ralicR.setEffortCapacity(new double[] { 1000, 1000, 1000, 1000, 1000 });
//			ralicR.setBudgetPerRelease(new double[] { 0, 0, 0, 0, 0 });
//			ralicR.setNumberOfIterations(5);
//			ralicR.checkTransitiveDependency();
//			MCSimulator.simulate(ralicR.getWorkItems(), noOfHorizons, interestRate);
//
//			Project ralicR2 = ProjectParser.parseCSVToProjectExp("data/ralic-rate.csv", distributionType);
//			ralicR2.setInterestRate(interestRate);
//			ralicR2.setNumberOfInvestmentPeriods(noOfHorizons);
//			ralicR2.setEffortCapacity(new double[] { 1000, 1000 });
//			ralicR2.setBudgetPerRelease(new double[] { 0, 0 });
//			ralicR2.setNumberOfIterations(2);
//			ralicR2.checkTransitiveDependency();
//			MCSimulator.simulate(ralicR2.getWorkItems(), noOfHorizons, interestRate);

//			 Project ralicR4 =
//			 ProjectParser.parseCSVToProjectExp("data/ralic-rate.csv",
//			 distributionType);
//			 ralicR4.setInterestRate(interestRate);
//			 ralicR4.setNumberOfInvestmentPeriods(noOfHorizons);
//			 ralicR4.setEffortCapacity(new double[]{1000, 1000, 1000, 1000});
//			 ralicR4.setBudgetPerRelease(new double[]{0,0,0,0});
//			 ralicR4.setNumberOfIterations(4);
//			 ralicR4.checkTransitiveDependency();
//			 MCSimulator.simulate(ralicR4.getWorkItems(), noOfHorizons,
//			 interestRate);

//			Project ralicP = ProjectParser.parseCSVToProjectExp("data/ralic-point.csv", distributionType);
//			ralicP.setInterestRate(interestRate);
//			ralicP.setNumberOfInvestmentPeriods(noOfHorizons);
//			ralicP.setEffortCapacity(new double[] { 1000, 1000, 1000, 1000, 1000 });
//			ralicP.setBudgetPerRelease(new double[] { 0, 0, 0, 0, 0 });
//			ralicP.setNumberOfIterations(5);
//			ralicP.checkTransitiveDependency();
//			MCSimulator.simulate(ralicP.getWorkItems(), noOfHorizons, interestRate);

//			 Project ralicP2 =
//			 ProjectParser.parseCSVToProjectExp("data/ralic-point.csv",
//			 distributionType);
//			 ralicP2.setInterestRate(interestRate);
//			 ralicP2.setNumberOfInvestmentPeriods(noOfHorizons);
//			 ralicP2.setEffortCapacity(new double[]{1000, 1000});
//			 ralicP2.setBudgetPerRelease(new double[]{0,0});
//			 ralicP2.setNumberOfIterations(2);
//			 ralicP2.checkTransitiveDependency();
//			 MCSimulator.simulate(ralicP2.getWorkItems(), noOfHorizons,
//			 interestRate);

//			 Project ralicP4 =
//			 ProjectParser.parseCSVToProjectExp("data/ralic-point.csv",
//			 distributionType);
//			 ralicP4.setInterestRate(interestRate);
//			 ralicP4.setNumberOfInvestmentPeriods(noOfHorizons);
//			 ralicP4.setEffortCapacity(new double[]{1000, 1000, 1000, 1000});
//			 ralicP4.setBudgetPerRelease(new double[]{0,0,0,0});
//			 ralicP4.setNumberOfIterations(4);
//			 ralicP4.checkTransitiveDependency();
//			 MCSimulator.simulate(ralicP4.getWorkItems(), noOfHorizons,
//			 interestRate);

//			 Project wordProcessing =
//			 ProjectParser.parseCSVToProjectExp("data/word-processing.csv",
//			 distributionType);
//			 wordProcessing.setInterestRate(interestRate);
//			 wordProcessing.setNumberOfInvestmentPeriods(noOfHorizons);
//			 wordProcessing.setEffortCapacity(new double[]{725, 693, 675});
//			 wordProcessing.setBudgetPerRelease(new double[]{0, 0, 0});
//			 wordProcessing.setNumberOfIterations(3);
//			 wordProcessing.checkTransitiveDependency();
//			 MCSimulator.simulate(wordProcessing.getWorkItems(), noOfHorizons,
//			 interestRate);

			Project releasePlanner = ProjectParser.parseCSVToProjectExp("data/releaseplanner-data.csv",
					distributionType);
			releasePlanner.setInterestRate(interestRate);
			releasePlanner.setNumberOfInvestmentPeriods(noOfHorizons);
			releasePlanner.setEffortCapacity(new double[] { 8604, 6960, 7420 });
			releasePlanner.setBudgetPerRelease(new double[] { 0, 0, 0 });
			releasePlanner.setNumberOfIterations(3);
			releasePlanner.checkTransitiveDependency();
			MCSimulator.simulate(releasePlanner.getWorkItems(), noOfHorizons, interestRate);

//			projects.put("council", councilProject);
//			projects.put("RALICR", ralicR);
//			projects.put("RALICR2", ralicR2);
//			 projects.put("RALICR4", ralicR4);
//			projects.put("RALICP", ralicP);
//			 projects.put("RALICP2", ralicP2);
//			 projects.put("RALICP4", ralicP4);
//			 projects.put("WordProcessing", wordProcessing);
			projects.put("ReleasePlanner", releasePlanner);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return projects;
	}

}
