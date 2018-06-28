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
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class BearsSPRiskExperimentReal {

	final static int INDEPENDENT_RUNS = 30;
	//final String dataDirectory = "data/";
	final static String referencePareto = "pareto_front";
	final static String resultDirectory = "result";
	final double interestRate = 0.02;
	final int noOfHorizons = 12;
	final String distributionType = "LogNormal";
	HashMap<String, double[]> importance;
	
	public BearsSPRiskExperimentReal(){
		importance = new HashMap<>();
		importance.put("council", new double[]{9,8,7});
		importance.put("RALICR", new double[]{9,8,7});
		importance.put("RALICP", new double[]{9,8,7});
		importance.put("ReleasePlanner", new double[]{9,5,3});
		importance.put("WordProcessing", new double[]{9,8,8});
	}

	public static void main(String[] args) throws IOException {
		//JMetalLogger.configureLoggers(null);
		BearsSPRiskExperimentReal experiment = new BearsSPRiskExperimentReal();
		HashMap<String, Project> projects = experiment.getProjects();
		createDirectory(resultDirectory);
		String SRPRISKPATH = resultDirectory + "/sprisk";
		String BEARSPATH = resultDirectory + "/bears";
		createDirectory(SRPRISKPATH);
		createDirectory(BEARSPATH);
		createDirectory(referencePareto);
		for (Map.Entry<String, Project> entry : projects.entrySet()) {
			String name = entry.getKey();
			Project project = entry.getValue();
				List<ReleasePlan> allPlans = new ArrayList<>();
				List<Double> srpRuntimes = new ArrayList<Double>();
				List<Double> bearsRuntimes = new ArrayList<Double>();
				for (int k = 0; k < INDEPENDENT_RUNS; k++) {	
					JMetalLogger.logger.info("RUNNING: " + name + " Run " + k);
					SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, false);
					srpProject.releaseImp = experiment.importance.get(name);
					Optimization optimisation = new Optimization(project, "Barp", "NSGAII");
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
					List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsInObjectives(srpSolutions,
							project);
					List<ReleasePlan> bearsPlan = ObjectiveValueUtil.computeBearsInObjectives(bearsSolutions, project);
					//bearsPlan = ParetoOptimalUtil.removeDuplicate(bearsPlan);
					//evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
					experiment.writeSolutions(BEARSPATH + "/" + name, bearsPlan, k);
					experiment.writeSolutions(SRPRISKPATH + "/" + name, srpPlan, k);

					allPlans.addAll(srpPlan);
					allPlans.addAll(bearsPlan);
				}
				JMetalLogger.logger.info("RF: Writing Pareto Front to " + name + ".rf");
				experiment.writeRuntimes(BEARSPATH + "/" + name, bearsRuntimes);
				experiment.writeRuntimes(SRPRISKPATH + "/" + name, srpRuntimes);
				//allPlans = ParetoOptimalUtil.removeDuplicate(allPlans);
				allPlans = ParetoOptimalUtil.findParetoOptimal(allPlans);
				experiment.writeReferencePareto(name, allPlans);

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
		try {
			Project councilProject = ProjectParser.parseCSVToProjectExp("data/councilNew2.csv", distributionType);
			councilProject.setInterestRate(interestRate);
			councilProject.setNumberOfInvestmentPeriods(noOfHorizons);
			councilProject.setEffortCapacity(new double[]{500, 500, 500});
			councilProject.setBudgetPerRelease(new double[]{0,0,0});
			councilProject.setNumberOfIterations(3);
			councilProject.checkTransitiveDependency();
			MCSimulator.simulate(councilProject.getWorkItems(), noOfHorizons, interestRate);
			
			Project ralicR = ProjectParser.parseCSVToProjectExp("data/ralic-rate.csv", distributionType);
			ralicR.setInterestRate(interestRate);
			ralicR.setNumberOfInvestmentPeriods(noOfHorizons);
			ralicR.setEffortCapacity(new double[]{1000, 1000, 1000, 1000, 1000});
			ralicR.setBudgetPerRelease(new double[]{0,0,0,0,0});
			ralicR.setNumberOfIterations(5);
			ralicR.checkTransitiveDependency();
			MCSimulator.simulate(ralicR.getWorkItems(), noOfHorizons, interestRate);
			
			Project ralicP = ProjectParser.parseCSVToProjectExp("data/ralic-point.csv", distributionType);
			ralicP.setInterestRate(interestRate);
			ralicP.setNumberOfInvestmentPeriods(noOfHorizons);
			ralicP.setEffortCapacity(new double[]{1000, 1000, 1000, 1000, 1000});
			ralicP.setBudgetPerRelease(new double[]{0,0,0,0,0});
			ralicP.setNumberOfIterations(5);
			ralicP.checkTransitiveDependency();
			MCSimulator.simulate(ralicP.getWorkItems(), noOfHorizons, interestRate);
			
			Project wordProcessing = ProjectParser.parseCSVToProjectExp("data/word-processing.csv", distributionType);
			wordProcessing.setInterestRate(interestRate);
			wordProcessing.setNumberOfInvestmentPeriods(noOfHorizons);
			wordProcessing.setEffortCapacity(new double[]{725, 693, 675});
			wordProcessing.setBudgetPerRelease(new double[]{0, 0, 0});
			wordProcessing.setNumberOfIterations(3);
			wordProcessing.checkTransitiveDependency();
			MCSimulator.simulate(wordProcessing.getWorkItems(), noOfHorizons, interestRate);
			
			Project releasePlanner = ProjectParser.parseCSVToProjectExp("data/releaseplanner-data.csv", distributionType);
			releasePlanner.setInterestRate(interestRate);
			releasePlanner.setNumberOfInvestmentPeriods(noOfHorizons);
			releasePlanner.setEffortCapacity(new double[]{8604, 6960, 7420});
			releasePlanner.setBudgetPerRelease(new double[]{0,0,0});
			releasePlanner.setNumberOfIterations(3);
			releasePlanner.checkTransitiveDependency();
			MCSimulator.simulate(releasePlanner.getWorkItems(), noOfHorizons, interestRate);
			
			projects.put("council", councilProject);
			projects.put("RALICR", ralicR);
			projects.put("RALICP", ralicP);
			projects.put("WordProcessing", wordProcessing);
			projects.put("ReleasePlanner", releasePlanner);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return projects;
	}

}
