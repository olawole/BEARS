package cs.ucl.ac.uk.barp.problem;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.evolve.EvolveProject;


@SuppressWarnings("serial")
public class MORP extends AbstractIntegerProblem implements ConstrainedProblem<IntegerSolution>{
	int noOfFeatures;
	
	int noOfReleases;
	
	double[] releaseImportance;
	
	int[] values;
	
	double[] effort; //effort per feature in hours - given
		
//	int urgency[][];
	
	double[] effortCapacity; //effort capacity per release - given
		
//	double[] stakeholderWeight;
	
	int noOfStakeholders;
	
	double[] budgetPerRelease;
	double[] effortNeededPerRelease;
	EvolveProject project;
	
	public OverallConstraintViolation<IntegerSolution> overallConstraintViolationDegree;
	public NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints;
	
	public MORP(EvolveProject project){
		this.project = project;
		this.values = project.valueMatrix;
//		this.urgency = project.urgencyMatrix;
		this.effort = project.effortMatrix;
		this.releaseImportance = project.releaseImp;
		this.noOfReleases = project.capacity.length;
		this.effortCapacity = project.capacity;
//		this.stakeholderWeight = project.stakeImp;
//		noOfStakeholders = stakeholderWeight.length;
		noOfFeatures = effort.length;
		
		setNumberOfVariables(noOfFeatures);
		setNumberOfConstraints(noOfReleases);
	    setNumberOfObjectives(1);
	    setName("EVOLVERP");
	    
	    overallConstraintViolationDegree = new OverallConstraintViolation<IntegerSolution>();
	    numberOfViolatedConstraints = new NumberOfViolatedConstraints<IntegerSolution>();
	    
	    List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
	    List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());
	    
	    for(int i = 0; i < getNumberOfVariables(); i++){
	    	lowerLimit.add(0);
	    	upperLimit.add(noOfReleases);
	    }
	    
	    setLowerLimit(lowerLimit);
	    setUpperLimit(upperLimit);
	}

	
	
	@Override
	public void evaluate(IntegerSolution solution) {
		boolean isValid = isValid(repairSolution(solution));
		effortNeededPerRelease = new double[noOfReleases];
		if (isValid){
			double totalSatisfaction = 0.0;
			double sumEffort = 0.0;
			
			
			for (int k = 1; k <= noOfReleases; k++){
				double satisfaction = 0;
				double effort = 0;
				for (int i = 0; i < noOfFeatures; i++){
					int xi = solution.getVariableValue(i);
					if (k == xi){
						satisfaction += WAS(i,k-1);
						effort += this.effort[i];
						sumEffort += this.effort[i];
					}
				}
				effortNeededPerRelease[k-1] = effort;
				totalSatisfaction += satisfaction;
			}
			
			double utilization = StatUtil.sum(effortCapacity) - sumEffort;
			if (utilization < 0){
				solution.setObjective(0, 0);
				//solution.setObjective(1, 1000000);
			}
			else {
				solution.setObjective(0, -totalSatisfaction);
				//solution.setObjective(1, utilization);
			}	
		}
		else {
			solution.setObjective(0, 0);
			//solution.setObjective(1, 1000000);
		}
		
		
	}
	
	public double WAS(int featureIndex, int releaseId){
		double was;
		was = releaseImportance[releaseId] * values[featureIndex];
		return was;
//		double sum = 0.0;
//		int i = featureIndex; int k = releaseId;
//		for(int p = 0; p < noOfStakeholders; p++){
//			sum += stakeholderWeight[p] * values[i][p] * urgency[i][noOfStakeholders * p + k];
//		}
//		was = releaseImportance[k] * sum;
		
//		return was;
	}

	@Override
	public void evaluateConstraints(IntegerSolution solution) {
		//double[] constraints = new double[getNumberOfConstraints()];
		int noOfViolation = 0;
		double total = 0.0;
		for(int k = 0; k < noOfReleases; k++){
			double constraint2 = effortCapacity[k] - effortNeededPerRelease[k];
			if (constraint2 < 0){
				noOfViolation++;
				total += constraint2;
				
			}
		}
		numberOfViolatedConstraints.setAttribute(solution, noOfViolation);
		overallConstraintViolationDegree.setAttribute(solution, total);
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
			String featureId = project.getFeatureIds().get(i);
			for (String feature : project.getFeature(featureId).getPrecursors()){
				if (feature.equals("")){
					continue;
				}
				int precursorIndex = project.getFeatureIds().indexOf(feature);			
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
			String featureId = project.getFeatureIds().get(i);
			for (String feature : project.getFeature(featureId).getPrecursors()){
				if (feature.equals("")){
					continue;
				}
				int precursorIndex = project.getFeatureIds().indexOf(feature);	
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

}
