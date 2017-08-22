package cs.ucl.ac.uk.barp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.problem.MORP;
import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.project.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.Release;
import cs.ucl.ac.uk.barp.release.ReleasePlan;
import cs.ucl.ac.uk.barp.release.view.Scatter;
import cs.ucl.ac.uk.barp.workitem.WorkItem;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.evolve.Feature;

public class Example {

	static String filename;
	static int noOfReleases;
	static int noOfInvestmentHorizon;
	static double capacity[];
	static double releaseImp[];
	static double budget[];
	static double interestRate;
	static String distributionType;
	static String algorithmType;
	public static List<ReleasePlan> allPlan = new ArrayList<ReleasePlan>();

	public Example() {
		filename = "councilNew.csv";
		noOfReleases = 4;
		noOfInvestmentHorizon = 10;
		budget = new double[]{500, 350, 200, 200};
		capacity = new double[]{150,120,100, 100};
		//budget = new double[] { 200, 200, 200 };// , 70};
		//capacity = new double[] { 500, 400, 400 };// , 300};
		// capacity = new double[]{80, 70, 60};//, 50};
		releaseImp = new double[noOfReleases];
		interestRate = 0.02;
		distributionType = "LogNormal";
		algorithmType = ConfigSetting.DEFAULT_APRROX_ALGORITHM;
		
	}

	public static void main(String[] args) throws Exception {
		new Example();
		String problemType = getProblemType();
		Project project = ProjectParser.parseCSVToProject(filename, distributionType);
		project.checkTransitiveDependency();
		project.setEffortCapacity(capacity);
		project.setInterestRate(interestRate);
		project.setNumberOfInvestmentPeriods(noOfInvestmentHorizon);
		project.setNumberOfIterations(noOfReleases);
		project.setBudgetPerRelease(budget);
		if (problemType.equalsIgnoreCase("Barp")) {
			MCSimulator.simulate(project.getWorkItems(), noOfInvestmentHorizon, interestRate);
		}
		EvolveProject evolve = convertBearsToEvolve(project);
		Optimization optimisation = new Optimization(project, problemType, algorithmType);
		List<IntegerSolution> evolveSolutions = runEvolve(evolve);
		List<ReleasePlan> evolvePlan = computeObjective(evolveSolutions, project);
		List<IntegerSolution> solutions = optimisation.run();
		System.out.println(solutions.size());
		OptimalSolutions optimal = new OptimalSolutions();

		optimal.setSolutions(solutions, project);
		Scatter s = new Scatter("BEARS Vs EVOLVE", optimal.getSolutions(), evolvePlan, allPlan);
		s.drawPlot();
		System.out.println(optimal.getSolutions().size());

	}

	private static EvolveProject convertBearsToEvolve(Project project) {
		EvolveProject evolveProject = new EvolveProject();
		for (Map.Entry<String, WorkItem> entry : project.getWorkItems().entrySet()){
			Feature feature = new Feature(entry.getKey());
			feature.setEffort(entry.getValue().getAverageEffort());
			if (entry.getValue().getAverageSimulation() == null){
				feature.setValue(0);
			}
			else {
				double value = 100 * (entry.getValue().getAverageValue() / StatUtil.max(entry.getValue().getAverageSimulation()));
				feature.setValue((int) value);
			}
			feature.setPrecursors(entry.getValue().getPrecursors());
			evolveProject.addFeature(feature);
		}
		evolveProject.capacity = capacity;
		evolveProject.setParameterMatrix();
		for (int i = 0; i < noOfReleases; i++) {
			releaseImp[i] = 1 / Math.pow(1 + interestRate, i + 1);
		}
		double sum = StatUtil.sum(releaseImp);
		for (int i = 0; i < noOfReleases; i++) {
			releaseImp[i] = releaseImp[i] / sum;
		}
		evolveProject.releaseImp = releaseImp;
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
//				.setSelectionOperator(selection).setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
//				.setPopulationSize(ConfigSetting.POPULATION_SIZE)
//				.setArchive(new CrowdingDistanceArchive<IntegerSolution>(100)).build();
		
//		Algorithm<List<IntegerSolution>> algorithm = new SPEA2Builder<IntegerSolution>(problem, crossover, mutation)
//        .setSelectionOperator(selection)
//        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
//        .setMaxIterations(250)
//        .build();
		
		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(25000)
		        .setPopulationSize(100)
		        .build() ;
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}

	private static String getProblemType() {
		if (distributionType.equalsIgnoreCase("Point")) {
			return "BarpCertain";
		} else {
			return "Barp";
		}
	}

	private static List<ReleasePlan> computeObjective(List<IntegerSolution> evolveSol, Project project){
		List<ReleasePlan> optimal = new ArrayList<ReleasePlan>();
		evolveSol.forEach(solution->{
			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] lateness = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			ReleasePlan iterationPlan = new ReleasePlan(solution, project);
			iterationPlan.sortWorkItemsByPriority();
			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
			ReleasePlan actualPlan = iterationPlan.actualPlan(workSequence, project.getEffortCapacity());
			HashMap<String, Integer> actualFeatureReleaseMap = actualPlan.featureReleaseMap();
			for(int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++){
				double sumEffort = 0;
				double sanpv = 0;
				ReleasePlan rPlan = iterationPlan.actualPlan(j, workSequence, capacity);
				HashMap<String, Integer> scenarioFeatureReleaseMap = rPlan.featureReleaseMap();
				for (int i = 1; i <= noOfReleases; i++){
					Release release = rPlan.getRelease(i);
					if(release != null){
						for (WorkItem wi : release.getwItems()){
							sumEffort += wi.getEffortSimulation()[j];
							if(wi.getValue() != null)
								sanpv += wi.getSanpv()[j][i-1];
						}
						sanpv -= budget[i-1];
					}
				}
				lateness[j] = computeLatenessProbability(actualFeatureReleaseMap, scenarioFeatureReleaseMap);
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
			actualPlan.setBusinessValue(StatUtil.mean(npv));
			actualPlan.setExpectedPunctuality((1 - StatUtil.mean(lateness)) * 100);
			optimal.add(actualPlan);
		});
		return optimal;
	}
	
	private static double computeLatenessProbability(HashMap<String, Integer> actualP, HashMap<String, Integer> scenarioP){
		if (actualP.size() == 0){
			return 0;
		}
		double latenessProbability;
		double diff = 0;
		for (String item : actualP.keySet()){
			Integer actualRelease = actualP.get(item);
			Integer scenarioRelease = (scenarioP.get(item) != null) ? scenarioP.get(item) : 0;
			if(scenarioRelease == 0){
				diff += actualRelease;
			}
			if(actualRelease < scenarioRelease){
				diff += scenarioRelease - actualRelease;
			}
		}
		latenessProbability = diff / actualP.size();
		return latenessProbability;	
	}

}
