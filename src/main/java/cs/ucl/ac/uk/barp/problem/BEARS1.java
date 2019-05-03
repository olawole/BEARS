package cs.ucl.ac.uk.barp.problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.Release;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.model.WorkItem;

/**
 * @author olawoleoni Barp - Bayesian Agile Release Planning. This is the
 *         description of the optimisation problem using jMetal
 */
public class BEARS1 extends AbstractIntegerProblem implements ConstrainedProblem<IntegerSolution> {

	/**
	 * Expected effort required by a release plan
	 */
	double effortPerRelease[];
	/**
	 * vector is an array of string thats used to interpret solutions
	 */
	String[] vector;
	
	int releaseBy[];
	
	int earliestRealease[];
	/**
	 * capacity is the available effort per release
	 */
	double[] capacity;
	/**
	 * Number of release to plan
	 */
	int noOfReleases;

	public static HashMap<String, double[]> buffer = new HashMap<>();

	double[] releaseBudget;
	Project projectId;
	private static final long serialVersionUID = 1L;
	OverallConstraintViolation<IntegerSolution> overallConstraintViolationDegree;
	NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints;

	public BEARS1(Project project) {
		capacity = project.getEffortCapacity();
		vector = project.getWorkItemVector();
		noOfReleases = project.getNumberOfIterations();
		releaseBudget = project.getBudgetPerRelease();
		projectId = project;
		setNumberOfObjectives(2);
		setNumberOfConstraints(noOfReleases);
		setNumberOfVariables(vector.length);
		setName("BEARS1");

		List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
		List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());

		overallConstraintViolationDegree = new OverallConstraintViolation<IntegerSolution>();
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<IntegerSolution>();

		for (int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(0);
			upperLimit.add(noOfReleases);
		}

		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}

	/**
	 * Computes fitness function of a solution
	 * 
	 * @param solution
	 */
	@Override
	public void evaluate(IntegerSolution solution) {
		String solutionString = solution.toSolutionString();
		if (buffer.containsKey(solutionString)) {
			solution.setObjective(0, buffer.get(solutionString)[0]);
			solution.setObjective(1, buffer.get(solutionString)[1]);
		} else {
			// long start = System.currentTimeMillis();
			// check repair a solution and check for its validity
			boolean isValid = isValid(solution);
			// boolean isValid = isValid(solution);
			if (isValid) {
				computeFitness(solution);
			} else {
				computeFitness(repairSolution(solution));
			} // if solution is invalid, assign bad fitness
			
			buffer.put(solution.toSolutionString(),
					new double[] { solution.getObjective(0), solution.getObjective(1) });
			// long stop = System.currentTimeMillis();
			// System.out.println(stop - start);
		}

	}

	/**
	 * @param solution
	 */
	private void computeFitness(IntegerSolution solution) {
		double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
		double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
		// iteration plan represents the allocation of work items to releases
		ReleasePlan rPlan = new ReleasePlan(solution, projectId);
		effortPerRelease = new double[noOfReleases];
		for (int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++) {
			double sanpv = 0;
			double sumEffort = 0;
			for (int i = 1; i <= noOfReleases; i++) {
				Release release = rPlan.getRelease(i);
				if (release != null) {
					double releaseEffort = computeEffort(release, j);
					sumEffort += releaseEffort;
					effortPerRelease[i-1] += releaseEffort;
					sanpv += computeValue(release, j, i);
					sanpv -= releaseBudget[i - 1];
				}
			}
			npv[j] = sanpv;
			effort[j] = sumEffort;
		}
		double enpv = StatUtil.mean(npv);
		double expLateness = computeEffortOverrunProbability(effort);
		for (int i = 0; i < noOfReleases; i++){
			effortPerRelease[i] = effortPerRelease[i] / ConfigSetting.NUMBER_OF_SIMULATION;
		}
		solution.setObjective(0, -enpv);
		// solution.setObjective(1, invRisk);
		solution.setObjective(1, expLateness);
	}

	private double computeValue(Release release, int simNumber, int releaseId) {
		double value = 0;
		for (WorkItem wi : release.getwItems()) {
			if (wi.getValue() != null)
				value += wi.getSanpv()[simNumber][releaseId - 1];
		}

		return value;
	}

	private double computeEffort(Release release, int simNumber) {
		double sumEffort = 0;
		if (release == null)
			return sumEffort;

		for (WorkItem wi : release.getwItems()) {
			sumEffort += wi.getEffortSimulation()[simNumber];
		}

		return sumEffort;
	}

	/**
	 * checks for validity of a solution. Returns true if valid and false
	 * otherwise
	 * 
	 * @param solution
	 * @return
	 */
	public boolean isValid(IntegerSolution solution) {
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			if (solution.getVariableValue(i) == 0) {
				continue;
			}
			if (solution.getVariableValue(i) > noOfReleases) {
				return false;
			}
			String featureId = vector[i];
			for (String wItemId : projectId.getWorkItems().get(featureId).getPrecursors()) {
				if (wItemId.equals("")) {
					continue;
				}
				int precursorIndex = getIndex(wItemId);
				if ((precursorIndex > 0 && (solution.getVariableValue(i) < solution.getVariableValue(precursorIndex)))
						|| solution.getVariableValue(precursorIndex) == 0) {
					return false;
				} else if ((solution.getVariableValue(i) == solution.getVariableValue(precursorIndex))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Repair a given solution to form valid solution
	 * 
	 * @param solution
	 * @return
	 */
	private IntegerSolution repairSolution(IntegerSolution solution) {
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			if (solution.getVariableValue(i) == 0) {
				continue;
			}
			String featureId = vector[i];
			for (String wItemId : projectId.getWorkItems().get(featureId).getPrecursors()) {
				if (wItemId.equals("")) {
					continue;
				}
				int precursorIndex = getIndex(wItemId);
				if (solution.getVariableValue(precursorIndex) == 0) {
					if (i < precursorIndex) {
						if ((solution.getVariableValue(i) - 1) > 0) {
							solution.setVariableValue(precursorIndex, solution.getVariableValue(i) - 1);
						} else {
							solution.setVariableValue(i, 0);
						}
					} else {
						solution.setVariableValue(i, 0);
					}
				} else if (solution.getVariableValue(i) <= solution.getVariableValue(precursorIndex)) {
					if (i < precursorIndex) {
						if ((solution.getVariableValue(i) - 1) > 0) {
							solution.setVariableValue(precursorIndex, solution.getVariableValue(i) - 1);
						} else {
							solution.setVariableValue(i, 0);
						}
					} else {
						if ((solution.getVariableValue(precursorIndex) + 1) <= noOfReleases) {
							solution.setVariableValue(i, solution.getVariableValue(precursorIndex) + 1);
						} else {
							solution.setVariableValue(i, 0);
						}
					}

				}
			}
		}

		return solution;

	}

	public int getIndex(String wItemId) {
		for (int i = 0; i < vector.length; i++) {
			if (vector[i].equals(wItemId)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void evaluateConstraints(IntegerSolution solution) {
		int noOfViolation = 0;
		double threshold = 0;
		for (int i = 0; i < noOfReleases; i++){
			if (effortPerRelease[i] > capacity[i]){
				++noOfViolation;
				threshold += capacity[i] - effortPerRelease[i];
			}
		}
		numberOfViolatedConstraints.setAttribute(solution, noOfViolation);
		overallConstraintViolationDegree.setAttribute(solution, threshold);

	}
//
//	private double computeLatenessProbability(ReleasePlan plan) {
//		double latenessProbability = 0;
//		double numberOfViolation = 0;
//		for (int i = 0; i < ConfigSetting.NUMBER_OF_SIMULATION; i++){
//			for (int j = 0; j < noOfReleases; j++){
//				double effortInRelease = 0;
//				effortInRelease = computeEffort(plan.getPlan().get(j), i);
//				if (effortInRelease > capacity[j]){
//					numberOfViolation++;
//					break;
//				}
//			}
//		}
//		latenessProbability = numberOfViolation / ConfigSetting.NUMBER_OF_SIMULATION;
//		return latenessProbability;
//	}
	
	private double computeEffortOverrunProbability(double[] effort) {
		double cumulativeOverrun = 0;
		double capacitySum = StatUtil.sum(capacity);
		for (int i = 0; i < effort.length; i++){
			double effortDiffernce = effort[i] - capacitySum;
			if (effortDiffernce > 0)
				cumulativeOverrun += effortDiffernce / capacitySum;
		}
		double effortOverrun = (cumulativeOverrun / effort.length) * 100;
		return effortOverrun;
	}
	

}
