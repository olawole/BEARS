package cs.ucl.ac.uk.barp.problem;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.Release;
import cs.ucl.ac.uk.barp.release.ReleasePlan;
import cs.ucl.ac.uk.barp.workitem.WorkItem;
import cs.ucl.ac.uk.barp.project.Project;


/**
 * @author olawoleoni
 * Barp - Bayesian Agile Release Planning. This is the description of the optimisation problem using
 * jMetal
 */
public class Barp extends AbstractIntegerProblem implements ConstrainedProblem<IntegerSolution> {

	/**
	 * Expected effort required by a release plan
	 */
	double effort;
	/**
	 * vector is an array of string thats used to interpret solutions
	 */
	String[] vector;
	/**
	 * capacity is the available effort per release
	 */
	double[] capacity;
	/**
	 * Number of release to plan
	 */
	int noOfReleases;
	Project projectId;
	private static final long serialVersionUID = 1L;
	OverallConstraintViolation<IntegerSolution> overallConstraintViolationDegree;
	NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints;

	public Barp(Project project) {
		capacity = project.getEffortCapacity();
		vector = project.getWorkItemVector();
		noOfReleases = project.getNumberOfIterations();
		projectId = project;
		setNumberOfObjectives(2);
		setNumberOfConstraints(1);
		setNumberOfVariables(vector.length);
		setName("Barp");
		
		List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
	    List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());
	    
	    overallConstraintViolationDegree = new OverallConstraintViolation<IntegerSolution>();
	    numberOfViolatedConstraints = new NumberOfViolatedConstraints<IntegerSolution>();
	    
	    for(int i = 0; i < getNumberOfVariables(); i++){
	    	lowerLimit.add(0);
	    	upperLimit.add(noOfReleases);
	    }
	    
	    setLowerLimit(lowerLimit);
	    setUpperLimit(upperLimit);
	}

	/**
	 * Computes fitness function of a solution
	 * @param solution 
	 */
	@Override
	public void evaluate(IntegerSolution solution) {
		//check repair a solution and check for its validity
		boolean isValid = isValid(repairSolution(solution));
		if(isValid){
			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			//iteration plan represents the allocation of work items to releases
			ReleasePlan iterationPlan = new ReleasePlan(solution, projectId);
			//sort work items in releases by their priority
			iterationPlan.sortWorkItemsByPriority();
			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
			for(int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++){
				double sumEffort = 0;
				double sanpv = 0;
				ReleasePlan rPlan = iterationPlan.actualPlan(j, workSequence, capacity);
				for (int i = 1; i <= noOfReleases; i++){
					Release release = rPlan.getRelease(i);
					if(release != null){
						for (WorkItem wi : release.getwItems()){
							sumEffort += wi.getEffortSimulation()[j];
							if(wi.getValue() != null)
								sanpv += wi.getSanpv()[j][i];
						}
					}
				}
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
			expectedValue(solution, effort, npv);
		} // if solution is invalid, assign bad fitness
		else {
			solution.setObjective(0, 0);
			solution.setObjective(1, 1);
			this.effort = 0;
		}	
		
	}

	/**
	 * Compute expected value of the objectives
	 * @param solution
	 * @param effort
	 * @param npv
	 */
	private void expectedValue(IntegerSolution solution, double[] effort, double[] npv) {
		double enpv = StatUtil.mean(npv);
		double eEffort = StatUtil.mean(effort);
		double invRisk = StatUtil.round(StatUtil.stdev(npv) / enpv, 4);
//		double sumCapacity = StatUtil.sum(capacity); //
//		if (eEffort > sumCapacity){
//			solution.setObjective(0, 0);
//			solution.setObjective(1, 1);
//			this.effort = eEffort;
//		}
//		else {
			solution.setObjective(0, -enpv);
			solution.setObjective(1, invRisk);
			this.effort = eEffort;
//		}
	}
	
	
	/**
	 * checks for validity of a solution. Returns true if valid and false otherwise
	 * @param solution
	 * @return
	 */
	public boolean isValid(IntegerSolution solution){
//		System.out.println(solution.toString());
		for (int i = 0; i < solution.getNumberOfVariables(); i++){
			if (solution.getVariableValue(i) == 0){
				continue;
			}
			if (solution.getVariableValue(i) > noOfReleases){
				return false;
			}
			String featureId = vector[i];
			for (String wItemId : projectId.getWorkItems().get(featureId).getPrecursors()){
				if (wItemId.equals("")){
					continue;
				}
				int precursorIndex = getIndex(wItemId);			
				if ((precursorIndex > 0 && (solution.getVariableValue(i) < solution.getVariableValue(precursorIndex)))
						|| solution.getVariableValue(precursorIndex) == 0){
					return false;
				}
				else if ((solution.getVariableValue(i) == solution.getVariableValue(precursorIndex))){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Repair a given solution to form valid solution
	 * @param solution
	 * @return
	 */
	private IntegerSolution repairSolution(IntegerSolution solution){
		for (int i = 0; i < solution.getNumberOfVariables(); i++){
			if (solution.getVariableValue(i) == 0){
				continue;
			}
			String featureId = vector[i];
			for (String wItemId : projectId.getWorkItems().get(featureId).getPrecursors()){
				if (wItemId.equals("")){
					continue;
				}
				int precursorIndex = getIndex(wItemId);	
				if (solution.getVariableValue(precursorIndex) == 0){
					if (i < precursorIndex){
						if((solution.getVariableValue(i) - 1) > 0){
							solution.setVariableValue(precursorIndex, solution.getVariableValue(i) - 1);
						}
						else {
							solution.setVariableValue(i, 0);
						}
					}
					else {
						solution.setVariableValue(i, 0);
					}
				}
				else if (solution.getVariableValue(i) <= solution.getVariableValue(precursorIndex)){
					if (i < precursorIndex){
						if((solution.getVariableValue(i) - 1) > 0){
							solution.setVariableValue(precursorIndex, solution.getVariableValue(i) - 1);
						}
						else {
							solution.setVariableValue(i, 0);
						}
					}
					else {
						if ((solution.getVariableValue(precursorIndex) + 1) <= noOfReleases){
							solution.setVariableValue(i, solution.getVariableValue(precursorIndex) + 1);
						}
						else {
							solution.setVariableValue(i, 0);
						}
					}
					
				}
				else;
			}
		}
		
		return solution;
		
	}
	
	public int getIndex(String wItemId) {
		for(int i = 0; i < vector.length; i++){
			if(vector[i].equals(wItemId)){
				return i;
			}
		}
		return -1;
	}

	@Override
	public void evaluateConstraints(IntegerSolution solution) {
		double sumCapacity = StatUtil.sum(capacity);
		int noOfViolation = 0;
		double threshold = 0;
		if (this.effort > sumCapacity){
			++noOfViolation;
			threshold += sumCapacity - this.effort;
		}
		numberOfViolatedConstraints.setAttribute(solution, noOfViolation);
		overallConstraintViolationDegree.setAttribute(solution, threshold);
		
	}
	
}