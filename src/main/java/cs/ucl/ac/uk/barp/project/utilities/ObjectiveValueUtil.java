package cs.ucl.ac.uk.barp.project.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.uma.jmetal.util.AlgorithmRunner;

import cs.ucl.ac.uk.barp.ExperimentConfiguration;
import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.Release;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.model.WorkItem;
import cs.ucl.ac.uk.barp.problem.BEARS0;
import cs.ucl.ac.uk.barp.problem.MORP;
import cs.ucl.ac.uk.barp.problem.SRPRisk;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.evolve.Feature;
import cs.ucl.ac.uk.srprisk.SRPFeature;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;

public class ObjectiveValueUtil {

	public ObjectiveValueUtil() {
	}

	public static List<ReleasePlan> computeBearsObjectives(List<IntegerSolution> evolveSol, Project project) {
		List<ReleasePlan> optimal = new ArrayList<ReleasePlan>();
		double[] releaseBudget = project.getBudgetPerRelease();
		double[] capacity = project.getEffortCapacity();
		int noOfReleases = project.getNumberOfIterations();
		evolveSol.forEach(solution -> {
			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] lateness = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			ReleasePlan iterationPlan = new ReleasePlan(solution, project);
			iterationPlan.sortWorkItemsByPriority();
			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
			ReleasePlan actualPlan = iterationPlan.actualPlan(workSequence, project.getEffortCapacity());
			HashMap<String, Integer> actualFeatureReleaseMap = actualPlan.featureReleaseMap();
			for (int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++) {
				double sumEffort = 0;
				double sanpv = 0;
				ReleasePlan rPlan = iterationPlan.actualPlan(j, workSequence, capacity);
				HashMap<String, Integer> scenarioFeatureReleaseMap = rPlan.featureReleaseMap();
				for (int i = 1; i <= noOfReleases; i++) {
					Release release = rPlan.getRelease(i);
					if (release != null) {
						sumEffort += computeEffort(release, j);
						sanpv += computeValue(release, j, i);
						sanpv -= releaseBudget[i - 1];
					}
				}
				lateness[j] = computeLatenessProbability(actualFeatureReleaseMap, scenarioFeatureReleaseMap);
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
			actualPlan.setBusinessValue(StatUtil.mean(npv));
			actualPlan.setExpectedPunctuality((1 - StatUtil.mean(lateness)) * 100);
			actualPlan.setSatisfaction(-solution.getObjective(0));
			optimal.add(actualPlan);
		});
		return optimal;
	}
	
	public static List<ReleasePlan> computeBearsInObjectives(List<IntegerSolution> evolveSol, Project project) {
		List<ReleasePlan> optimal = new ArrayList<ReleasePlan>();
		double[] releaseBudget = project.getBudgetPerRelease();
		double[] capacity = project.getEffortCapacity();
		int noOfReleases = project.getNumberOfIterations();
		evolveSol.forEach(solution -> {
			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] lateness = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			ReleasePlan iterationPlan = new ReleasePlan(solution, project);
			iterationPlan.sortWorkItemsByPriority();
			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
			ReleasePlan actualPlan = iterationPlan.actualPlan(workSequence, project.getEffortCapacity());
			HashMap<String, Integer> actualFeatureReleaseMap = actualPlan.featureReleaseMap();
			for (int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++) {
				double sumEffort = 0;
				double sanpv = 0;
				ReleasePlan rPlan = iterationPlan.actualPlan(j, workSequence, capacity);
				HashMap<String, Integer> scenarioFeatureReleaseMap = rPlan.featureReleaseMap();
				for (int i = 1; i <= noOfReleases; i++) {
					Release release = rPlan.getRelease(i);
					if (release != null) {
						sumEffort += computeEffort(release, j);
						sanpv += computeValue(release, j, i);
						sanpv -= releaseBudget[i - 1];
					}
				}
				lateness[j] = computeLatenessProbability(actualFeatureReleaseMap, scenarioFeatureReleaseMap);
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
			actualPlan.setBusinessValue(-StatUtil.mean(npv));
			actualPlan.setExpectedPunctuality(StatUtil.mean(lateness));
			optimal.add(actualPlan);
		});
		return optimal;
	}

	private static double computeValue(Release release, int simNumber, int releaseId) {
		double value = 0;
		for (WorkItem wi : release.getwItems()) {
			if (wi.getValue() != null)
				value += wi.getSanpv()[simNumber][releaseId - 1];
		}

		return value;
	}

	private static double computeEffort(Release release, int simNumber) {
		double sumEffort = 0;

		for (WorkItem wi : release.getwItems()) {
			sumEffort += wi.getEffortSimulation()[simNumber];
		}

		return sumEffort;
	}

	private static double computeLatenessProbability(HashMap<String, Integer> actualP,
			HashMap<String, Integer> scenarioP) {
		if (actualP.size() == 0) {
			return 0;
		}
		double latenessProbability;
		double diff = 0;
		for (String item : actualP.keySet()) {
			Integer actualRelease = actualP.get(item);
			Integer scenarioRelease = (scenarioP.get(item) != null) ? scenarioP.get(item) : 0;
			if (scenarioRelease == 0) {
				diff += actualRelease;
			}
			if (actualRelease < scenarioRelease) {
				diff += scenarioRelease - actualRelease;
			}
		}
		latenessProbability = diff / actualP.size();
		return latenessProbability;
	}

	public static void computeValuePoint(List<ReleasePlan> plans, int noOfReleases, double[] releaseImp, double max) {
		plans.forEach(plan -> {
			double satisfaction = 0;
			for (int i = 1; i <= noOfReleases; i++) {
				Release release = plan.getRelease(i);
				if (release != null) {
					for (WorkItem wi : release.getwItems()) {
						satisfaction += (9 * wi.getAverageValue() / max) * releaseImp[i - 1];
					}
				}
			}
			plan.setSatisfaction(satisfaction);
		});
	}

	public static List<ReleasePlan> computeValueInBears0(List<IntegerSolution> solutions, Project project) {
		List<ReleasePlan> plans = new ArrayList<>();
		solutions.forEach(solution -> {
			ReleasePlan rPlan = new ReleasePlan(solution, project);
			double value = 0;
			double effort = 0;
			for (int i = 1; i <= project.getNumberOfIterations(); i++) {
				Release release = rPlan.getRelease(i);
				if (release != null) {
					for (WorkItem wi : release.getwItems()) {
						value += discountValue(wi.getAverageValue(), i, project.getInterestRate());
						effort += wi.getAverageEffort();
					}
				}
			}
			rPlan.setBusinessValue(value);
			rPlan.setRiskMeasure(effort);
			plans.add(rPlan);
		});
		return plans;
	}

	private static double discountValue(double averageValue, int i, double intRate) {
		return averageValue / Math.pow((1 + intRate), i);
	}

	// public static void exceedProbability(List<ReleasePlan> plans, int
	// noOfReleases, double[] capacity) {
	// plans.forEach(plan -> {
	// double sumEffort[] = new double[ConfigSetting.NUMBER_OF_SIMULATION];
	// for (int k = 1; k <= noOfReleases; k++) {
	// Release release = plan.getRelease(k);
	// if (release != null) {
	// for (WorkItem wi : release.getwItems()) {
	// for (int j = 0; j < sumEffort.length; j++) {
	// sumEffort[j] += wi.getEffortSimulation()[j];
	// }
	// }
	// }
	// }
	// plan.setExceedProbability((1 - computeRisk(sumEffort, capacity)) * 100);
	// });
	// }

	public static void exceedProbability(List<ReleasePlan> plans, int noOfReleases, double[] capacity) {
		plans.forEach(plan -> {
			double[][] effortNeededPerRelease = new double[noOfReleases][ConfigSetting.NUMBER_OF_SIMULATION];
			for (int k = 1; k <= noOfReleases; k++) {
				Release release = plan.getRelease(k);
				double effort[] = new double[ConfigSetting.NUMBER_OF_SIMULATION];
				if (release != null) {
					for (WorkItem wi : release.getwItems()) {
						for (int j = 0; j < effort.length; j++) {
							effort[j] += wi.getEffortSimulation()[j];
							// sumEffort[j] += wi.getEffortSimulation()[j];
						}
					}
				}
				effortNeededPerRelease[k - 1] = effort;
			}
			plan.setExceedProbability((1 - computeRisk(effortNeededPerRelease, capacity)) * 100);
		});
	}

	public static void computeSRPObjectives(List<ReleasePlan> plans, int noOfReleases, double[] capacity, double max) {
		computeValuePoint(plans, noOfReleases, ExperimentConfiguration.releaseImp, max);
		exceedProbability(plans, noOfReleases, capacity);
	}

	private static double computeRisk(double[][] effortNeededPerRelease, double[] capacity) {
		double riskProbability;
		double N = ConfigSetting.NUMBER_OF_SIMULATION;
		double noEffortExceedCapacity = 0;
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < capacity.length; i++) {
				if (effortNeededPerRelease[i][j] > capacity[i]) {
					noEffortExceedCapacity++;
					//break;
				}
			}
		}
		riskProbability = noEffortExceedCapacity / (capacity.length * N);

		return riskProbability;
	}

	public static List<ReleasePlan> computeBears1Objective(List<IntegerSolution> solutions, Project project) {
		double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
		double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
		List<ReleasePlan> plans = new ArrayList<>();
		// iteration plan represents the allocation of work items to releases
		for (IntegerSolution solution : solutions) {
			ReleasePlan rPlan = new ReleasePlan(solution, project);
			for (int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++) {
				double sanpv = 0;
				double sumEffort = 0;
				for (int i = 1; i <= project.getNumberOfIterations(); i++) {
					Release release = rPlan.getRelease(i);
					if (release != null) {
						double releaseEffort = computeEffort(release, j);
						sumEffort += releaseEffort;
						sanpv += computeValue(release, j, i);
						sanpv -= project.getBudgetPerRelease()[i - 1];
					}
				}
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
			rPlan.setBusinessValue(StatUtil.mean(npv));
			rPlan.setRiskMeasure(computeEffortOverrunProbability(effort, project.getEffortCapacity()));
			plans.add(rPlan);
		}

		return plans;
	}

	public static void computePlanBears1Objective(List<ReleasePlan> solutions, Project project) {
		double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
		double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
		// iteration plan represents the allocation of work items to releases
		for (ReleasePlan rPlan : solutions) {
			for (int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++) {
				double sanpv = 0;
				double sumEffort = 0;
				for (int i = 1; i <= project.getNumberOfIterations(); i++) {
					Release release = rPlan.getRelease(i);
					if (release != null) {
						double releaseEffort = computeEffort(release, j);
						sumEffort += releaseEffort;
						sanpv += computeValue(release, j, i);
						sanpv -= project.getBudgetPerRelease()[i - 1];
					}
				}
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
//			rPlan.setBusinessValue(StatUtil.mean(npv));
			rPlan.setRiskMeasure(computeEffortOverrunProbability(effort, project.getEffortCapacity()));
		}
	}

	private static double computeEffortOverrunProbability(double[] effort, double[] capacity) {
		double cumulativeOverrun = 0;
		double capacitySum = StatUtil.sum(capacity);
		for (int i = 0; i < effort.length; i++) {
			double effortDiffernce = effort[i] - capacitySum;
			if (effortDiffernce > 0)
				cumulativeOverrun += effortDiffernce / capacitySum;
		}
		double effortOverrun = (cumulativeOverrun / effort.length) * 100;
		return effortOverrun;
	}

	public static double getMaxEconomicValue(Project project) {
		double[] value = new double[project.getWorkItems().size()];
		int k = 0;
		for (Map.Entry<String, WorkItem> keyValue : project.getWorkItems().entrySet()) {
			value[k++] = keyValue.getValue().getAverageValue();
		}
		return StatUtil.max(value);
	}

	public static SRPRiskProject convertProjectToSRPRisk(Project project, boolean valuePoint) {
		double maxValue = getMaxEconomicValue(project);
		SRPRiskProject srpProject = new SRPRiskProject();
		project.getWorkItems().forEach((k, v) -> {
			SRPFeature f = new SRPFeature(k);
			if (v.getValue() == null) {
				f.setValue(0);
			} else {
				if (!valuePoint) {
					f.setValue((int) v.getAverageValue());
				} else {
					double value = 9 * (v.getAverageValue() / maxValue);
					f.setValue((int) value);
				}
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

	public static List<IntegerSolution> runSRPRisk(SRPRiskProject srpRisk) {
		Problem<IntegerSolution> problem = new SRPRisk(srpRisk);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY,
				ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(1.0 / srpRisk.getFeatures().size(),
				ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();
		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
				.setSelectionOperator(selection).setMaxEvaluations(10000).setPopulationSize(100).build();

		// Algorithm<List<IntegerSolution>> algorithm = new
		// SPEA2Builder<IntegerSolution>(problem, crossover, mutation)
		// .setSelectionOperator(selection)
		// .setMaxIterations(250)
		// .setPopulationSize(100)
		// .build() ;

		// Algorithm<List<IntegerSolution>> algorithm = new
		// MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
		// .setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
		// .setSelectionOperator(selection)
		// .setPopulationSize(ConfigSetting.POPULATION_SIZE)
		// .setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
		// .build();
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}

	public static EvolveProject convertProjectToEvolve(Project project, boolean valuePoint) {
		double maxValue = getMaxEconomicValue(project);
		EvolveProject evolveProject = new EvolveProject();
		project.getWorkItems().forEach((k, v) -> {
			Feature f = new Feature(k);
			if (v.getValue() == null) {
				f.setValue(0);
			} else {
				if (!valuePoint) {
					f.setValue((int) v.getAverageValue());
				} else {
					double value = 9 * (v.getAverageValue() / maxValue);
					f.setValue((int) value);
				}
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

	public static List<IntegerSolution> runEvolve(EvolveProject evolve) {
		Problem<IntegerSolution> problem = new MORP(evolve);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY,
				ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(1.0 / evolve.getFeatures().size(),
				ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();
		// Algorithm<List<IntegerSolution>> algorithm = new
		// MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
		// .setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
		// .setSelectionOperator(selection)
		// .setPopulationSize(ConfigSetting.POPULATION_SIZE)
		// .setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
		// .build();

		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
				.setSelectionOperator(selection).setMaxEvaluations(50000).setPopulationSize(100).build();
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}

	public static List<IntegerSolution> runBears0(EvolveProject evolve) {
		Problem<IntegerSolution> problem = new BEARS0(evolve);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(1.0 / evolve.getFeatures().size(),
				ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY,
				ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();
		// Algorithm<List<IntegerSolution>> algorithm = new
		// MOCellBuilder<IntegerSolution>(problem, crossover, mutation)
		// .setMaxEvaluations(ConfigSetting.MAX_EVALUATIONS)
		// .setSelectionOperator(selection)
		// .setPopulationSize(ConfigSetting.POPULATION_SIZE)
		// .setArchive(new CrowdingDistanceArchive<IntegerSolution>(100))
		// .build();

		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
				.setSelectionOperator(selection).setMaxEvaluations(50000).setPopulationSize(100).build();
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}

}
