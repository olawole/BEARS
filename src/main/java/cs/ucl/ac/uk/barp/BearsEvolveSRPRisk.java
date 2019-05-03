package cs.ucl.ac.uk.barp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.problem.BEARS;
import cs.ucl.ac.uk.barp.problem.MORP;
import cs.ucl.ac.uk.barp.problem.SRPRisk;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.SolutionFileWriterUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.Scatter;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsVSRPRisk;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.evolve.Feature;
import cs.ucl.ac.uk.srprisk.SRPFeature;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class BearsEvolveSRPRisk {

	public static void main(String[] args) throws Exception {

		String problemType = getProblemType();
		Project project = ProjectParser.parseCSVToProject(ExperimentConfiguration.FILENAME, ExperimentConfiguration.distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(ExperimentConfiguration.capacity);
		project.setInterestRate(ExperimentConfiguration.interestRate);
		project.setNumberOfInvestmentPeriods(ExperimentConfiguration.noOfInvestmentHorizon);
		project.setNumberOfIterations(ExperimentConfiguration.noOfReleases);
		project.setBudgetPerRelease(ExperimentConfiguration.budget);
		// simulation
		if (problemType.equalsIgnoreCase("Barp")) {
			MCSimulator.simulate(project.getWorkItems(), ExperimentConfiguration.noOfInvestmentHorizon, ExperimentConfiguration.interestRate);
		}
		// convert project to Evolve II
		EvolveProject evolve = convertProjectToEvolve(project);
		//Convert project to SRPRisk
		SRPRiskProject srpProject = convertProjectToSRPRisk(project);
		Optimization optimisation = new Optimization(project, problemType, ExperimentConfiguration.algorithmType);
		
		// Run Evolve II and SRPRisk
		List<IntegerSolution> evolveSolutions = runEvolve(evolve);
		List<IntegerSolution> srpSolutions = runSRPRisk(srpProject);
		
		// Compute their equivalent value in Bears
		List<ReleasePlan> evolvePlan = ObjectiveValueUtil
				.computeBearsObjectives(evolveSolutions, project);
		List<ReleasePlan> srpPlan = ObjectiveValueUtil
				.computeBearsObjectives(srpSolutions, project);
//		evolvePlan = removeDuplicate(evolvePlan);
//		srpPlan = removeDuplicate(srpPlan);
		
		// Execute Bears
		List<IntegerSolution> solutions = optimisation.run();
		System.out.println(solutions.size());
		OptimalSolutions optimal = new OptimalSolutions();

		optimal.setSolutions(solutions, project);
		double maxValue = ObjectiveValueUtil.getMaxEconomicValue(project);
		// Compute value point of each solution in the three methods
		ObjectiveValueUtil.computeValuePoint(optimal.getSolutions(), 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.releaseImp, maxValue);
		ObjectiveValueUtil.computeValuePoint(evolvePlan, 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.releaseImp, maxValue);
		ObjectiveValueUtil.computeValuePoint(srpPlan, 
				ExperimentConfiguration.noOfReleases, ExperimentConfiguration.releaseImp, maxValue);
		
		// Compute probability that plan exceed effort capacity
		ObjectiveValueUtil.exceedProbability(srpPlan, ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity);
		ObjectiveValueUtil.exceedProbability(evolvePlan, ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity);
		ObjectiveValueUtil.exceedProbability(optimal.getSolutions(), ExperimentConfiguration.noOfReleases, ExperimentConfiguration.capacity);
		
		List<ReleasePlan> evolvePlanBears = new ArrayList<>(evolvePlan);
		List<ReleasePlan> evolvePlanSRP = new ArrayList<>(evolvePlan);
		
		List<ReleasePlan> srpPlanBears = new ArrayList<>(srpPlan);
		List<ReleasePlan> srpPlanSRP = new ArrayList<>(srpPlan);
		
		List<ReleasePlan> bearsPlanSRP = new ArrayList<>(optimal.getSolutions());
		
//		evolvePlanBears = findParetoOptimal(evolvePlanBears);
//		srpPlanBears = findParetoOptimal(srpPlanBears);
//		
//		evolvePlanSRP = findParetoOptimalSRP(evolvePlanSRP);
//		srpPlanSRP = findParetoOptimalSRP(srpPlanSRP);
//		bearsPlanSRP = findParetoOptimalSRP(bearsPlanSRP);
		
		
//		optimal.getSolutions().forEach(plan->{
//			plan.setSatisfaction(computeValuePoint(plan));
//		});
//		List<ReleasePlan> releasePlanner = getReleasePlannerSolutions(project, evolve);
//		List<ReleasePlan> releasePlanner = getFlexibleSolutions(project);
		Scatter s = new Scatter("BEARS vs EVOLVE vs SRPRisk", optimal.getSolutions(), evolvePlanBears, srpPlanBears);
//		Scatter s = new Scatter("BEARS vs EVOLVE", optimal.getSolutions(), evolvePlan);
		s.drawPlot();
		ScatterBearsVSRPRisk s1 = new ScatterBearsVSRPRisk("BEARS vs EVOLVE vs SRPRisk", bearsPlanSRP, evolvePlanSRP, srpPlanSRP);
		s1.drawPlot();
		HashMap<String, List<ReleasePlan>> allMethodPlans = new HashMap<>();
		allMethodPlans.put("EVOLVE II", evolvePlan);
		allMethodPlans.put("SRP Risk", srpPlan);
		allMethodPlans.put("BEARS", optimal.getSolutions());
		SolutionFileWriterUtil.writeAll(allMethodPlans, ExperimentConfiguration.noOfReleases);
//		generateCSVTable(optimal.getSolutions(), "Bears");
//		generateCSVTable(evolvePlan, "Evolve");
//		generateCSVTable(srpPlan, "SRPRisk");
//		generateCSVTable(releasePlanner, "releasePlanner");
		System.out.println(optimal.getSolutions().size());
	}

	private static SRPRiskProject convertProjectToSRPRisk(Project project){
		
		SRPRiskProject srpProject = new SRPRiskProject();
		project.getWorkItems().forEach((k,v) ->{
			SRPFeature f = new SRPFeature(k);
			if (v.getValue() == null){
				f.setValue(0);
			}
			else {
				f.setValue((int) v.getAverageValue());
			}
				
			f.setEffortSim(v.getEffortSimulation());
			f.setPrecursors(v.getPrecursors());
			srpProject.addFeature(f);
		});
		srpProject.capacity = project.getEffortCapacity();
		srpProject.setParameter(ConfigSetting.NUMBER_OF_SIMULATION);
		srpProject.releaseImp = ExperimentConfiguration.releaseImp;
		
		return srpProject;
	}
	
	private static EvolveProject convertProjectToEvolve(Project project){
		
		EvolveProject evolveProject = new EvolveProject();
		project.getWorkItems().forEach((k,v) ->{
			Feature f = new Feature(k);
			if (v.getValue() == null){
				f.setValue(0);
			}
			else {
				f.setValue((int) v.getAverageValue());
			}
				
			f.setEffort(v.getAverageEffort());
			f.setPrecursors(v.getPrecursors());
			evolveProject.addFeature(f);
		});
		evolveProject.capacity = project.getEffortCapacity();
		evolveProject.setParameterMatrix();
		evolveProject.releaseImp = ExperimentConfiguration.releaseImp;
		
		return evolveProject;
	}

	private static List<IntegerSolution> runEvolve(EvolveProject evolve) {
		Problem<IntegerSolution> problem = new MORP(evolve);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY,
				ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY,
				ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();
//		Algorithm<List<IntegerSolution>> algorithm = new MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
//				.setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
//				.setSelectionOperator(selection)
//				.setPopulationSize(ConfigSetting.POPULATION_SIZE)
//				.setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
//				.build();
				
		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(25000)
		        .setPopulationSize(100)
		        .build() ;
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}
	
	private static List<IntegerSolution> runSRPRisk(SRPRiskProject srpRisk) {
		Problem<IntegerSolution> problem = new SRPRisk(srpRisk);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY,
				ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY,
				ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();		
		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(25000)
		        .setPopulationSize(100)
		        .build() ;
		
//		Algorithm<List<IntegerSolution>> algorithm = new SPEA2Builder<IntegerSolution>(problem, crossover, mutation)
//		        .setSelectionOperator(selection)
//		        .setMaxIterations(250)
//		        .setPopulationSize(100)
//		        .build() ;
		
//		Algorithm<List<IntegerSolution>> algorithm = new MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
//				.setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
//				.setSelectionOperator(selection)
//				.setPopulationSize(ConfigSetting.POPULATION_SIZE)
//				.setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
//				.build();
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}

	private static String getProblemType() {
		if (ExperimentConfiguration.distributionType.equalsIgnoreCase("Point")) {
			return "BarpCertain";
		} else {
			return "Barp";
		}
	}

//	private static List<ReleasePlan> computeObjective(List<IntegerSolution> evolveSol, Project project){
//		List<ReleasePlan> optimal = new ArrayList<ReleasePlan>();
//		evolveSol.forEach(solution->{
//			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
//			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
//			double[] lateness = new double[ConfigSetting.NUMBER_OF_SIMULATION];
//			ReleasePlan iterationPlan = new ReleasePlan(solution, project);
//			iterationPlan.sortWorkItemsByPriority();
//			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
//			ReleasePlan actualPlan = iterationPlan.actualPlan(workSequence, project.getEffortCapacity());
//			HashMap<String, Integer> actualFeatureReleaseMap = actualPlan.featureReleaseMap();
//			for(int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++){
//				double sumEffort = 0;
//				double sanpv = 0;
//				ReleasePlan rPlan = iterationPlan.actualPlan(j, workSequence, capacity);
//				HashMap<String, Integer> scenarioFeatureReleaseMap = rPlan.featureReleaseMap();
//				for (int i = 1; i <= noOfReleases; i++){
//					Release release = rPlan.getRelease(i);
//					if(release != null){
//						for (WorkItem wi : release.getwItems()){
//							sumEffort += wi.getEffortSimulation()[j];
//							if(wi.getValue() != null)
//								sanpv += wi.getSanpv()[j][i-1];
//						}
//						sanpv -= budget[i-1];
//					}
//				}
//				lateness[j] = computeLatenessProbability(actualFeatureReleaseMap, scenarioFeatureReleaseMap);
//				npv[j] = sanpv;
//				effort[j] = sumEffort;
//			}
//			actualPlan.setBusinessValue(StatUtil.mean(npv));
//			actualPlan.setExpectedPunctuality((1 - StatUtil.mean(lateness)) * 100);
//			actualPlan.setSatisfaction(-solution.getObjective(0));
//			optimal.add(actualPlan);
//		});
//		return optimal;
//	}
	
//	private static double computeLatenessProbability(HashMap<String, Integer> actualP, HashMap<String, Integer> scenarioP){
//		if (actualP.size() == 0){
//			return 0;
//		}
//		double latenessProbability;
//		double diff = 0;
//		for (String item : actualP.keySet()){
//			Integer actualRelease = actualP.get(item);
//			Integer scenarioRelease = (scenarioP.get(item) != null) ? scenarioP.get(item) : 0;
//			if(scenarioRelease == 0){
//				diff += actualRelease;
//			}
//			if(actualRelease < scenarioRelease){
//				diff += scenarioRelease - actualRelease;
//			}
//		}
//		latenessProbability = diff / actualP.size();
//		return latenessProbability;	
//	}
	
//	private static void computeValuePoint(List<ReleasePlan> plans){
//		plans.forEach(plan -> {
//			double satisfaction = 0;
//			for (int i = 1; i <= noOfReleases; i++){
//				Release release = plan.getRelease(i);
//				if(release != null){
//					for (WorkItem wi : release.getwItems()){
//						satisfaction += wi.getAverageValue() * releaseImp[i-1];
//					}
//				}
//			}
//			plan.setSatisfaction(satisfaction);
//		});
//	}
	
//	private static void exceedProbability(List<ReleasePlan> plans){
//		plans.forEach(plan ->{
//			double sumEffort[] = new double[ConfigSetting.NUMBER_OF_SIMULATION];
//			for (int k = 1; k <= noOfReleases; k++) {
//				Release release = plan.getRelease(k);
//				if (release != null){
//					for (WorkItem wi : release.getwItems()){
//						for (int j = 0; j < sumEffort.length; j++) {
//							sumEffort[j] += wi.getEffortSimulation()[j];
//						}
//					}
//				}
//			}
//			plan.setExceedProbability((1 -computeRisk(sumEffort)) * 100);
//		});
//	}
	
//	private static double computeRisk(double[] effortNeededByPlan) {
//		double riskProbability;
//		double sumCapacity = StatUtil.sum(capacity);
//		double N = effortNeededByPlan.length;
//		double noEffortExceedCapacity = 0;
//		for (int j = 0; j < N; j++) {
//			if (effortNeededByPlan[j] > sumCapacity) {
//				noEffortExceedCapacity++;
//			}
//		}
//		riskProbability = noEffortExceedCapacity / N;
//
//		return riskProbability;
//	}
	
//	public static void generateCSVTable(List<ReleasePlan> rPlans, String type){
//		String csvString = "";
//		String heading = "S/N" + COMMA_SEP;
//		for (int i = 0; i < noOfReleases; i++){
//			heading += "Release " + (i+1) + COMMA_SEP;
//		}
//		heading += "ENPV('000£)" + COMMA_SEP  + "Expected Punctuality (%)" +  COMMA_SEP + "Satisfaction Score" +  COMMA_SEP + "Prob. Effort Exceed Capacity(%)";
//		csvString += heading + "\n";
//		int counter = 0;
//		for (ReleasePlan plan : rPlans){
//			String row = ++counter + COMMA_SEP;
//			for (int i = 1; i <= noOfReleases; i++){
//				if (plan.getPlan().get(i) == null){
//					row += "" + COMMA_SEP;
//					continue;
//				}
//					
//				String s = plan.getPlan().get(i).toString();
//				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
//			}
//			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + StatUtil.round(plan.getExpectedPunctuality(), 2) + COMMA_SEP +  
//					plan.getSatisfaction() + COMMA_SEP + plan.getExceedProbability();
//			csvString += row + "\n";
//		}
//		try {
//			FileWriter output = new FileWriter(type + ".csv");
//			output.write(csvString);
//			output.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private static void writeAll(List<ReleasePlan> solutions, List<ReleasePlan> evolvePlan, List<ReleasePlan> srpPlan) {
//		String csvString = "";
//		String heading = "S/N" + COMMA_SEP;
//		for (int i = 0; i < noOfReleases; i++){
//			heading += "Release " + (i+1) + COMMA_SEP;
//		}
//		heading += "ENPV('000£)" + COMMA_SEP  + "Expected Punctuality (%)" +  COMMA_SEP + "Satisfaction Score" +  COMMA_SEP + "Prob. Effort Exceed Capacity(%)";
//		csvString += heading + "\n";
//		int counter = 0;
//		for (ReleasePlan plan : solutions){
//			String row = ++counter + COMMA_SEP;
//			for (int i = 1; i <= noOfReleases; i++){
//				if (plan.getPlan().get(i) == null){
//					row += "" + COMMA_SEP;
//					continue;
//				}
//					
//				String s = plan.getPlan().get(i).toString();
//				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
//			}
//			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + StatUtil.round(plan.getExpectedPunctuality(), 2) + COMMA_SEP +  
//					plan.getSatisfaction() + COMMA_SEP + plan.getExceedProbability();
//			csvString += row + "\n";
//		}
//		csvString += "\n\n";
//		for (ReleasePlan plan : evolvePlan){
//			String row = ++counter + COMMA_SEP;
//			for (int i = 1; i <= noOfReleases; i++){
//				if (plan.getPlan().get(i) == null){
//					row += "" + COMMA_SEP;
//					continue;
//				}
//					
//				String s = plan.getPlan().get(i).toString();
//				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
//			}
//			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + StatUtil.round(plan.getExpectedPunctuality(), 2) + COMMA_SEP +  
//					plan.getSatisfaction() + COMMA_SEP + plan.getExceedProbability();
//			csvString += row + "\n";
//		}
//		csvString += "\n\n";
//		for (ReleasePlan plan : srpPlan){
//			String row = ++counter + COMMA_SEP;
//			for (int i = 1; i <= noOfReleases; i++){
//				if (plan.getPlan().get(i) == null){
//					row += "" + COMMA_SEP;
//					continue;
//				}
//					
//				String s = plan.getPlan().get(i).toString();
//				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
//			}
//			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + StatUtil.round(plan.getExpectedPunctuality(), 2) + COMMA_SEP +  
//					plan.getSatisfaction() + COMMA_SEP + plan.getExceedProbability();
//			csvString += row + "\n";
//		}
//		try {
//			FileWriter output = new FileWriter("result.csv");
//			output.write(csvString);
//			output.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
//	public static List<ReleasePlan> removeDuplicate(List<ReleasePlan> solutions){
//		List<ReleasePlan> sol = new ArrayList<ReleasePlan>(solutions);
//		solutions = new ArrayList<ReleasePlan>();
//		for (ReleasePlan plan : sol){
//			if (!contains(solutions, plan)){
//				solutions.add(plan);
//			}
//		}
//		return solutions;
//	}
	
//	public static boolean contains(List<ReleasePlan> sol, ReleasePlan plan){
//		for (ReleasePlan p : sol){
//			if (p.getBusinessValue() == plan.getBusinessValue()){
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public static List<ReleasePlan> getReleasePlannerSolutions(Project project, EvolveProject proj){
//		int[][] seed = new int[][]{{0,0,0,0,0,0,1,0,4,0,4,2,2,0,4,0,1,3,0,0,1,0,3,1}, 
//			{0,0,0,0,0,0,1,0,4,0,4,2,3,0,4,0,1,2,0,0,1,0,3,1}, 
//			{0,0,0,0,0,0,1,0,2,4,3,4,0,0,2,0,1,1,0,3,3,0,1,2},
//			{0,0,0,0,0,0,1,0,0,4,4,2,2,0,4,0,0,3,0,3,1,0,1,1},
//			{0,0,0,0,0,0,1,0,4,4,0,2,2,0,0,4,1,0,0,3,1,0,3,1}};
//		List<IntegerSolution> solutions = new ArrayList<IntegerSolution>();
//		MORP problem = new MORP(proj);
//		for (int i = 0; i < 5; i++){
//			IntegerSolution solution = new DefaultIntegerSolution(problem);
//			for (int j = 0; j < seed[i].length; j++){
//				solution.setVariableValue(j, seed[i][j]);
//			}
//			problem.evaluate(solution);
//			solutions.add(solution);
//		}
//		List<ReleasePlan> rp = computeObjective(solutions, project);
//		
//		return rp;
//	}
	
	public static List<ReleasePlan> getFlexibleSolutions(Project project){
		int[][] seed = new int[][]{{2,2,3,0,0,3,1,0,1,2,4,1,0,3,4,1,1,2}, 
			{2,2,3,0,4,3,1,3,1,2,4,1,3,3,2,1,1,2}};
		List<IntegerSolution> solutions = new ArrayList<IntegerSolution>();
		BEARS problem = new BEARS(project);
		for (int i = 0; i < 2; i++){
			IntegerSolution solution = new DefaultIntegerSolution(problem);
			for (int j = 0; j < seed[i].length; j++){
				solution.setVariableValue(j, seed[i][j]);
			}
			problem.evaluate(solution);
			solutions.add(solution);
		}
		OptimalSolutions rp = new OptimalSolutions();
		rp.setSolutions(solutions, project);
		List<ReleasePlan> p = rp.getSolutions();
		
		return p;
	}
	
//	public static List<ReleasePlan> findParetoOptimal(List<ReleasePlan> solutions) {
//		List<ReleasePlan> rPlans = new ArrayList<ReleasePlan>(solutions);
//		solutions = new ArrayList<ReleasePlan>();
//		for (int i = 0; i < rPlans.size(); i++) {
//			boolean pareto = true;
//			for (int j = 0; j < rPlans.size(); j++) {
//				if (i == j)
//					continue;
//				if (dominates(rPlans.get(j), rPlans.get(i))) {
//					pareto = false;
//					break;
//				}
//			}
//			if (pareto) {
//				solutions.add(rPlans.get(i));
//			}
//		}
//		return solutions;
//	}
//	
//	public static boolean all(ReleasePlan plan1, ReleasePlan plan2) {
//		boolean value = false;
//		if (plan1.getBusinessValue() >= plan2.getBusinessValue()
//				&& plan1.getExpectedPunctuality() >= plan2.getExpectedPunctuality()) {
//			value = true;
//		}
//		return value;
//	}
//
//	public static boolean any(ReleasePlan plan1, ReleasePlan plan2) {
//		boolean value = false;
//		if (plan1.getBusinessValue() > plan2.getBusinessValue()
//				|| plan1.getExpectedPunctuality() > plan2.getExpectedPunctuality()) {
//			value = true;
//		}
//		return value;
//	}
//
//	public static boolean dominates(ReleasePlan plan1, ReleasePlan plan2) {
//		boolean dominate = false;
//
//		if (all(plan1, plan2) && any(plan1, plan2)) {
//			dominate = true;
//		}
//
//		return dominate;
//	}
//	
//	public static List<ReleasePlan> findParetoOptimalSRP(List<ReleasePlan> solutions) {
//		List<ReleasePlan> rPlans = new ArrayList<ReleasePlan>(solutions);
//		solutions = new ArrayList<ReleasePlan>();
//		for (int i = 0; i < rPlans.size(); i++) {
//			boolean pareto = true;
//			for (int j = 0; j < rPlans.size(); j++) {
//				if (i == j)
//					continue;
//				if (dominatesSRP(rPlans.get(j), rPlans.get(i))) {
//					pareto = false;
//					break;
//				}
//			}
//			if (pareto) {
//				solutions.add(rPlans.get(i));
//			}
//		}
//		return solutions;
//	}
//	
//	public static boolean allSRP(ReleasePlan plan1, ReleasePlan plan2) {
//		boolean value = false;
//		if (plan1.getSatisfaction() >= plan2.getSatisfaction()
//				&& plan1.getExceedProbability() >= plan2.getExceedProbability()) {
//			value = true;
//		}
//		return value;
//	}
//
//	public static boolean anySRP(ReleasePlan plan1, ReleasePlan plan2) {
//		boolean value = false;
//		if (plan1.getSatisfaction() > plan2.getSatisfaction()
//				|| plan1.getExceedProbability() > plan2.getExceedProbability()) {
//			value = true;
//		}
//		return value;
//	}
//
//	public static boolean dominatesSRP(ReleasePlan plan1, ReleasePlan plan2) {
//		boolean dominate = false;
//
//		if (allSRP(plan1, plan2) && anySRP(plan1, plan2)) {
//			dominate = true;
//		}
//
//		return dominate;
//	}
	



}
