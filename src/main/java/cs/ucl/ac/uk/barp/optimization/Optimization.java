package cs.ucl.ac.uk.barp.optimization;

import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;

import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

public class Optimization {
	
	Problem<IntegerSolution> problem;
	Algorithm<List<IntegerSolution>> algorithm;
	CrossoverOperator<IntegerSolution> crossover;
	MutationOperator<IntegerSolution> mutation;
	SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;

	public Optimization(Project project) {
		crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY, ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY, ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		selection = new BinaryTournamentSelection<IntegerSolution>();
		problem = ProblemFactory.getProblem("Barp", project);
		algorithm = AlgorithmFactory.getAlgorithm(AlgorithmType.NSGAII, crossover, mutation, selection, problem);
	}
	
	public Optimization(Project project, String probType, String algType) {
		AlgorithmType algT = getAlgorithmType(algType);
		if (algT == null) {
			System.err.println("Invalid algorithm selection");
		}
		else {
			crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY, ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
			mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY, ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
			selection = new BinaryTournamentSelection<IntegerSolution>();
			problem = ProblemFactory.getProblem(probType, project);
			algorithm = AlgorithmFactory.getAlgorithm(algT, crossover, mutation, selection, problem);
		}
		
	}
	
	public List<IntegerSolution> run(){
		AlgorithmRunner algRunner = new AlgorithmRunner.Executor(algorithm)
				.execute();
		System.out.println("Computation time: " + algRunner.getComputingTime());
		return algorithm.getResult();	
	}
	
	public AlgorithmType getAlgorithmType(String type){
		switch (type) {
		case "NSGAII":
			return AlgorithmType.NSGAII;
		case "NSGAIII":
			return AlgorithmType.NSGAIII;
		case "MOCELL":
			return AlgorithmType.MOCELL;
		case "RANDOM":
			return AlgorithmType.RANDOM;
		case "SPEA":
			return AlgorithmType.SPEA;
		case "SMPSO":
			return AlgorithmType.SMPSO;
		default:
			return null;
		}
	}

}
