package cs.ucl.ac.uk.srprisk;

import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cs.ucl.ac.uk.barp.problem.SRPRisk;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

public class Main {

	static String valuePath;
	static String urgencyPath;
	static String featurePath;
	

	public Main(String featurePath, String valuePath) {
		Main.featurePath = featurePath;
		Main.valuePath = valuePath;
		//Main.urgencyPath = urgencyPath;
	}

	public static void main(String[] args) {
		double capacity[] = new double[]{500,500,300};
		double releaseImp[] = new double[]{0.5, 0.3, 0.2};
		double stakeImp[] = new double[]{0.4, 0.3,0.3};
		new Main("srprisk/councileffort.csv", "srprisk/councilvalue.csv");
		SRPRiskProject project = new SRPRiskProject();
		SRPRiskProjectUtil.readFeatures(project, featurePath);
		SRPRiskProjectUtil.readStakeholderValue(project, valuePath, stakeImp);
//		EvolveProjectUtil.readUrgency(project, urgencyPath);
		project.capacity = capacity;
		project.releaseImp = releaseImp;
//		project.setStakeImportance();
		project.setParameterMatrix(1000);
		Problem<IntegerSolution> problem = new SRPRisk(project);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY, ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY, ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();
		Algorithm<List<IntegerSolution>> algorithm = new MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(250000)
		        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
		        .setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
		        .build();
		
//		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
//		.setSelectionOperator(selection)
//		.setMaxEvaluations(250000)
//		.setPopulationSize(ConfigSetting.POPULATION_SIZE)
//		.build();
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
		        .execute() ;

		    List<IntegerSolution> population = algorithm.getResult() ;
		    long computingTime = algorithmRunner.getComputingTime() ;
		    
		    Pareto pareto = new Pareto();
		    pareto.setSolutions(population, project);
		    TableView table = new TableView(pareto, project.noOfReleases);
		    table.update();

		    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

		    printFinalSolutionSet(population);
	}
	
	public static void printFinalSolutionSet(List<? extends Solution<?>> population) {

	    new SolutionListOutput(population)
	        .setSeparator("\t")
	        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
	        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
	        .print();

	    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
	    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
	    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
	  }

}
