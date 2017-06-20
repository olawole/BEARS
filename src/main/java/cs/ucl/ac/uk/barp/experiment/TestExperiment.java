package cs.ucl.ac.uk.barp.experiment;

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoFront;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import cs.ucl.ac.uk.barp.optimization.Barp;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.project.ProjectParser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Example of experimental study based on solving the ZDT1 problem but using five different
* number of variables. This can be interesting to study the behaviour of the algorithms when solving
* an scalable problem (in the number of variables). The used algorithms are NSGA-II, SPEA2 and
* SMPSO.
*
* This experiment assumes that the reference Pareto front is of problem ZDT1 is known,
* so the name of file containing it and the directory where it are located must be specified. Note
* that the name of the file must be replicated to be equal to the number of problem variants.
*
* Six quality indicators are used for performance assessment.
*
* The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the algorithms
* 3. Generate the reference Pareto fronts 4. Compute the quality indicators 5. Generate Latex
* tables reporting means and medians 6. Generate Latex tables with the result of applying the
* Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by applying the
* Friedman test 8. Generate R scripts to obtain boxplots
*
* @author Antonio J. Nebro <antonio@lcc.uma.es>
*/
public class TestExperiment {

private static final int INDEPENDENT_RUNS = 1;

public static void main(String[] args) throws Exception {
if (args.length != 1) {
  throw new JMetalException("Needed arguments: experimentBaseDirectory");
}
String experimentBaseDirectory = args[0];
String filename[] = new String[]{"usample.csv","council.csv"};
int numberOfReleases[] = new int[]{5,3};
int numberOfInvestment = 8;
double[] capacity = new double[]{80, 70, 60, 50, 40};
double[] capacity2 = new double[]{800,700,600};
//double[] capacity = new double[]{40, 40, 30, 30, 30};

Project project = ProjectParser.parseCSVToProject(filename[0], "Normal");
project.setEffortCapacity(capacity);
project.setInterestRate(0.02);
project.setNumberOfInvestmentPeriods(numberOfInvestment);
project.setNumberOfIterations(numberOfReleases[0]);
Project project1 = ProjectParser.parseCSVToProject(filename[1], "Normal");
project1.setEffortCapacity(capacity2);
project1.setInterestRate(0.02);
project1.setNumberOfInvestmentPeriods(numberOfInvestment);
project1.setNumberOfIterations(numberOfReleases[1]);


try {
	MCSimulator.simulate(project.getWorkItems(), numberOfInvestment, 0.02);
	MCSimulator.simulate(project1.getWorkItems(), numberOfInvestment, 0.02);

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
List<ExperimentProblem<IntegerSolution>> problemList = new ArrayList<>();
problemList.add(new ExperimentProblem<>(new Barp(project), "Sample"));
problemList.add(new ExperimentProblem<>(new Barp(project1), "Council"));

List<ExperimentAlgorithm<IntegerSolution, List<IntegerSolution>>> algorithmList =
        configureAlgorithmList(problemList);

//List<String> referenceFrontFileNames = Arrays.asList("sampleproject.pf");

Experiment<IntegerSolution, List<IntegerSolution>> experiment =
        new ExperimentBuilder<IntegerSolution, List<IntegerSolution>>("TestExperiment")
                .setAlgorithmList(algorithmList)
                .setProblemList(problemList)
                .setExperimentBaseDirectory(experimentBaseDirectory)
                .setOutputParetoFrontFileName("FUN")
                .setOutputParetoSetFileName("VAR")
                .setReferenceFrontDirectory(experimentBaseDirectory+"/pareto_fronts")
                //.setReferenceFrontFileNames(referenceFrontFileNames)
                .setIndicatorList(Arrays.asList(
                        new Epsilon<IntegerSolution>(),
                        new Spread<IntegerSolution>(),
                        new GenerationalDistance<IntegerSolution>(),
                        new PISAHypervolume<IntegerSolution>(),
                        new InvertedGenerationalDistance<IntegerSolution>(),
                        new InvertedGenerationalDistancePlus<IntegerSolution>()))
                .setIndependentRuns(INDEPENDENT_RUNS)
                .setNumberOfCores(8)
                .build();

new ExecuteAlgorithms<>(experiment).run();
new GenerateReferenceParetoFront(experiment).run();
new ComputeQualityIndicators<>(experiment).run();
new GenerateLatexTablesWithStatistics(experiment).run();
new GenerateWilcoxonTestTablesWithR<>(experiment).run();
new GenerateFriedmanTestTables<>(experiment).run();
new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
}

/**
* The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
* a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
* ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this example,
* where four variants of a same algorithm are defined.
*/
static List<ExperimentAlgorithm<IntegerSolution, List<IntegerSolution>>> configureAlgorithmList(
      List<ExperimentProblem<IntegerSolution>> problemList) {
List<ExperimentAlgorithm<IntegerSolution, List<IntegerSolution>>> algorithms = new ArrayList<>();

for (int i = 0; i < problemList.size(); i++) {
  Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(
          problemList.get(i).getProblem(),
          new IntegerSBXCrossover(1.0, 20.0),
          new IntegerPolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
          .build();
  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
}

for (int i = 0; i < problemList.size(); i++) {
  Algorithm<List<IntegerSolution>> algorithm = new SPEA2Builder<IntegerSolution>(
          problemList.get(i).getProblem(),
          new IntegerSBXCrossover(1.0, 10.0),
          new IntegerPolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
          .build();
  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
}

for (int i = 0; i < problemList.size(); i++) {
	  Algorithm<List<IntegerSolution>> algorithm = new MOCellBuilder<IntegerSolution>(
	          problemList.get(i).getProblem(),
	          new IntegerSBXCrossover(1.0, 10.0),
	          new IntegerPolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
	          .build();
	  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
}

for (int i = 0; i < problemList.size(); i++) {
	  Algorithm<List<IntegerSolution>> algorithm = new RandomSearchBuilder<IntegerSolution>(problemList.get(i).getProblem())
	          .build();
	  algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
}

return algorithms ;
}
}
