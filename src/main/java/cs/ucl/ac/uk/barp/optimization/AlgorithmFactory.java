package cs.ucl.ac.uk.barp.optimization;

import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

/**
 * @author olawoleoni
 * A factory object to create type of optimisation algorithm
 */
public class AlgorithmFactory {
	
	public static Algorithm<List<IntegerSolution>> getAlgorithm(AlgorithmType algorithmType, CrossoverOperator<IntegerSolution> crossover, MutationOperator<IntegerSolution> mutation,
				SelectionOperator<List<IntegerSolution>, IntegerSolution> selection, Problem<IntegerSolution> problem){
		if (algorithmType == null){
			return null;
		}
		Algorithm<List<IntegerSolution>> algorithm = null;
		switch (algorithmType) {
		case NSGAII:
			algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
					.setSelectionOperator(selection)
					.setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
					.setPopulationSize(ConfigSetting.POPULATION_SIZE)
					.build();
			break;
		case MOCELL:
			algorithm = new MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
			        .setSelectionOperator(selection)
			        .setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
			        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
			        .setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
			        .build() ;
			break;
		case SPEA: 
			algorithm = new SPEA2Builder<IntegerSolution>(problem, crossover, mutation)
			        .setSelectionOperator(selection)
			        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
			        .setMaxIterations(250)
			        .build();
		case NSGAIII:
			algorithm = new NSGAIIIBuilder<IntegerSolution>(problem)
			.setCrossoverOperator(crossover)
			.setMutationOperator(mutation)
			.setMaxIterations(250)
			.setSelectionOperator(selection)
			.setPopulationSize(ConfigSetting.POPULATION_SIZE)
			.build();
			break;
		case SMPSO:
			break;
		case RANDOM:
			return new RandomSearchBuilder<IntegerSolution>(problem)
			        .build();
		default:
			break;
		}
		return algorithm;
			
	}

}
