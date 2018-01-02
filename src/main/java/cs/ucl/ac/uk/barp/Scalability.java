package cs.ucl.ac.uk.barp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.WorkItem;
import cs.ucl.ac.uk.barp.optimization.InformationValueAnalyser;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.problem.MORP;
import cs.ucl.ac.uk.barp.project.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.evolve.Feature;

public class Scalability {
	
	static String filename;
	static String data[];
	static int noOfReleases;
	static int noOfInvestmentHorizon;
	static double capacity[];
	static double budget[];
	static double interestRate;
	static String distributionType;
	static String algorithmType;
	static String algorithms[];
	
	public Scalability() {
		filename = "data/barp100.csv";
		
		data = new String[] {"barp30.csv", "barp50.csv", "barp100.csv", "barp200.csv"};
		noOfReleases = 5;
		noOfInvestmentHorizon = 10;
		budget = new double[]{100, 80, 70, 70, 60};
//		capacity = new double[]{500,400,400, 300};
		capacity = new double[]{80, 70, 60, 50, 50};
		algorithms = new String[] {"NSGAII", "MOCELL", "SPEA2"};
		interestRate = 0.02;
		distributionType = "Normal";
		algorithmType = ConfigSetting.DEFAULT_APRROX_ALGORITHM;
	}
	
	public static void main(String[] args) throws Exception{
		new Scalability();
		String problemType = getProblemType();
		for (String name : data) {
			String entry = "SN," + algorithms[0] + "," + algorithms[1] + "," + algorithms[2];
			String entryEvolve = "SN," + algorithms[0] + "," + algorithms[1] + "," + algorithms[2];
			for(int i = 0; i < noOfReleases; i++) {
				Project project = ProjectParser.parseCSVToProject("data/"+name, distributionType);
				project.checkTransitiveDependency();
				project.setInterestRate(interestRate);
				project.setNumberOfInvestmentPeriods(noOfInvestmentHorizon);
				if (problemType.equalsIgnoreCase("Barp")){
					MCSimulator.simulate(project.getWorkItems(), noOfInvestmentHorizon, interestRate);
				}
				project.setEffortCapacity(Arrays.copyOfRange(capacity, 0, i+1));
				project.setNumberOfIterations(i+1);
				project.setBudgetPerRelease(Arrays.copyOfRange(budget, 0, i+1));
				entry += "\n" + (i + 1);
				for (int j = 0; j < algorithms.length; j++) {
					long start = System.currentTimeMillis();
					Optimization optimisation = new Optimization(project, problemType, algorithms[j]);
					List<IntegerSolution> solutions = optimisation.run();
//					OptimalSolutions optimal = new OptimalSolutions();
//					optimal.setSolutions(solutions, project);
					//InformationValueAnalyser.computeInformationValue(optimal, project.getWorkItems());
					int runtime = (int)(System.currentTimeMillis() - start) / 1000;
					entry += "," + runtime;
				}
				EvolveProject evolve = convertBearsToEvolve(project);
				Problem<IntegerSolution> problem = new MORP(evolve);
				entryEvolve += "\n" + (i + 1);
				for (int j = 0; j < algorithms.length; j++) {
					long start = System.currentTimeMillis();
					CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY,
							ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
					MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY,
							ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
					SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();
					Algorithm<List<IntegerSolution>> algorithm = null;
					switch(algorithms[i]) {
					case "MOCELL": algorithm = new MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
							.setSelectionOperator(selection).setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
							.setPopulationSize(ConfigSetting.POPULATION_SIZE)
							.setArchive(new CrowdingDistanceArchive<IntegerSolution>(100)).build(); break;
					case "NSGAII" : algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
					        .setSelectionOperator(selection)
					        .setMaxEvaluations(25000)
					        .setPopulationSize(100)
					        .build() ; break;
					case "SPEA2" : algorithm = new SPEA2Builder<IntegerSolution>(problem, crossover, mutation)
					        .setSelectionOperator(selection)
					        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
					        .setMaxIterations(250)
					        .build(); break;
					default: break;
					}
					new AlgorithmRunner.Executor(algorithm).execute();
					long runtime = System.currentTimeMillis() - start;
					entryEvolve += "," + runtime;
				}
				
			}
			try {
				FileWriter output = new FileWriter("scalability/"+name);
				FileWriter output1 = new FileWriter("scalability/"+name+"Evolve");
				output.write(entry);
				output1.write(entryEvolve);
				output.close();
				output1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}		
		//System.out.println(solutions.size());
	}

	private static String getProblemType() {
		if (distributionType.equalsIgnoreCase("Point")){
			return "BarpCertain";
		}
		else {
			return "Barp";
		}
	}
	
	private static EvolveProject convertBearsToEvolve(Project project) {
		EvolveProject evolveProject = new EvolveProject();
		double releaseImp[] = new double[project.getEffortCapacity().length];
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
		evolveProject.capacity = project.getEffortCapacity();
		evolveProject.setParameterMatrix();
		for (int i = 0; i < releaseImp.length; i++) {
			releaseImp[i] = 1 / Math.pow(1 + interestRate, i + 1);
		}
		double sum = StatUtil.sum(releaseImp);
		for (int i = 0; i < releaseImp.length; i++) {
			releaseImp[i] = releaseImp[i] / sum;
		}
		evolveProject.releaseImp = releaseImp;
		return evolveProject;

	}

}
