package cs.ucl.ac.uk.barp.optimization;

import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

public class AlgorithmFactory {

	public static Algorithm<List<IntegerSolution>> getAlgorithm(String algorithmType, CrossoverOperator<IntegerSolution> crossover, MutationOperator<IntegerSolution> mutation,
				SelectionOperator<List<IntegerSolution>, IntegerSolution> selection, Problem<IntegerSolution> problem){
		if (algorithmType == null){
			return null;
		}
		if (algorithmType.equalsIgnoreCase("NSGAII")){
			return new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
					.setSelectionOperator(selection)
					.setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
					.setPopulationSize(ConfigSetting.POPULATION_SIZE)
					.build();
		}
		
		if (algorithmType.equalsIgnoreCase("MOCELL")){
			return new MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
			        .setSelectionOperator(selection)
			        .setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
			        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
			        .setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
			        .build() ;
		}
		
		if (algorithmType.equalsIgnoreCase("SPEA2")){
			return new SPEA2Builder<IntegerSolution>(problem, crossover, mutation)
			        .setSelectionOperator(selection)
			        .setPopulationSize(ConfigSetting.POPULATION_SIZE)
			        .build();
		}
		
		if (algorithmType.equalsIgnoreCase("Random")){
			return new RandomSearchBuilder<IntegerSolution>(problem)
			        .build();
		}
		return null;
			
	}

}
