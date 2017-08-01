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
	
	int[][] values;
	
	double[] effort; //effort per feature in hours - given
		
	int urgency[][];
	
	double[] effortCapacity; //effort capacity per release - given
		
	double[] stakeholderWeight;
	
	int noOfStakeholders;
	
	double[] budgetPerRelease;
	double[] effortNeededPerRelease;
	EvolveProject project;
	
	public OverallConstraintViolation<IntegerSolution> overallConstraintViolationDegree;
	public NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints;
	
//	public MORP(){
//		this(new int[][] {{4,4,5,4,5},{2,4,3,5,4},{1,2,3,2,2},{2,2,3,3,4},{5,4,4,3,5},{5,5,5,4,4},
//		{2,1,2,2,2},{4,4,4,4,4}, {4,4,4,2,5},{4,5,4,3,2},{2,2,2,5,4},{3,3,4,2,5},{4,2,1,3,3},{2,4,5,2,4},
//		{4,4,4,4,4},{4,2,1,3,1},{4,3,2,5,1},{1,2,3,4,2},{3,3,3,3,4},{2,1,2,2,1}}, new int[] {4,4,5,4,5,2,4,3,5,4,1,2,3,2,2,2,2,3,3,4},10,new double[]{0.1,0.25,0.2,0.15,0.3});
//	}
	
	public MORP(){
//		this(new int[][]{{6,2},{7,5},{9,3},{5,7},{3,2},{9,3},{5,3},{7,1},{6,5},
//			{2,1},{1,5},{3,7},{7,9},{8,3},{1,5}}, 
//			new int[][]{{5,4,0,0,3,6},{5,0,4,9,0,0},{9,0,0,2,7,0},{2,7,0,7,2,0},{7,2,0,9,0,0},{7,2,0,5,4,0},{9,0,0,2,7,0},
//			{8,1,0,0,0,9},{9,0,0,0,8,1},{5,4,0,0,0,9},{8,1,0,0,7,2},{9,0,0,0,6,3},
//			{9,0,0,9,0,0},{9,0,0,6,3,0},{0,0,9,3,6,0}}, 
//			new int[]{290, 93,520,590,640,625,320,199,470,540,675,425,370,115,360},
//			new int[] {1600, 1500}, new int[]{1000,200,200,0,0,25,500,200,1500,500,150,50,50,0,0},
//			new int[]{1800,1750}, new int[]{4,6}, new double[]{0.7,0.3});
		
//		this(new int[][]{{6,2},{7,5},{9,3},{5,7},{3,2},{9,3},{5,3},{7,1},{6,5},
//			{2,1},{1,5},{3,7},{7,9},{8,3},{1,5}}, 
//			new int[][]{{5,4,0,0,3,6},{5,0,4,9,0,0},{9,0,0,2,7,0},{2,7,0,7,2,0},{7,2,0,9,0,0},{7,2,0,5,4,0},{9,0,0,2,7,0},
//			{8,1,0,0,0,9},{9,0,0,0,8,1},{5,4,0,0,0,9},{8,1,0,0,7,2},{9,0,0,0,6,3},
//			{9,0,0,9,0,0},{9,0,0,6,3,0},{0,0,9,3,6,0}}, 
//			new int[]{290, 93,520,590,640,625,320,199,470,540,675,425,370,115,360},
//			new int[] {800, 1000,1000}, new int[]{1000,200,200,0,0,25,500,200,1500,500,150,50,50,0,0},
//			new int[]{1800,1750,900}, new int[]{4,6}, new double[]{0.45,0.3,0.25});
//		double[][] val = new double[][]{{6,2},{7,5},{9,3},{5,7},{3,2},{9,3},{5,3},{7,1},{6,5},{2,1},{1,5},{3,7},{7,9},{8,3},{1,5}};
//		double[][] urg = new double[][]{{5,4,0,0,3,6},{5,0,4,9,0,0},{9,0,0,2,7,0},{2,7,0,7,2,0},{7,2,0,9,0,0},{7,2,0,5,4,0},{9,0,0,2,7,0},
//			{8,1,0,0,0,9},{9,0,0,0,8,1},{5,4,0,0,0,9},{8,1,0,0,7,2},{9,0,0,0,6,3},{9,0,0,9,0,0},{9,0,0,6,3,0},{0,0,9,3,6,0}};
//		double[] eff = new double[]{290, 93,520,590,640,625,320,199,470,540,675,425,370,115,360};
//		double[] effcap = new double[] {2908, 2401};
//		double[] budg = new double[]{1000,200,200,0,0,25,500,200,1500,500,150,50,50,0,0};
//		double[] budgcap = new double[]{2200,1750};
//		double[] Simp = new double[]{4,6};
//		double[] Rimp = new double[]{0.7,0.3};
	}
	
	public MORP(EvolveProject project){
		this.project = project;
		this.values = project.valueMatrix;
		this.urgency = project.urgencyMatrix;
		this.effort = project.effortMatrix;
		this.releaseImportance = project.releaseImp;
		this.noOfReleases = releaseImportance.length;
		this.effortCapacity = project.capacity;
		this.stakeholderWeight = project.stakeImp;
		noOfStakeholders = stakeholderWeight.length;
		noOfFeatures = effort.length;
		
		setNumberOfVariables(noOfFeatures);
		setNumberOfConstraints(noOfReleases);
	    setNumberOfObjectives(2);
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

	public MORP(int[][] values, int[][] urgency, double[] effort, double[] capacity, 
			double[] budget, double[] budgetCap, double[] weight, double[] releaseImportance) {
		noOfFeatures = effort.length;
		this.values = values;
		this.urgency = urgency;
		this.effort = effort;
		this.releaseImportance = releaseImportance;
		this.noOfReleases = releaseImportance.length;
		this.effortCapacity = capacity;
		this.stakeholderWeight = weight;
		noOfStakeholders = weight.length;
		
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
		if (isValid){
			double totalSatisfaction = 0.0;
			double sumEffort = 0.0;
			effortNeededPerRelease = new double[noOfReleases];
			
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
				solution.setObjective(1, 1000000);
			}
			else {
				solution.setObjective(0, -totalSatisfaction);
				solution.setObjective(1, utilization);
			}	
		}
		else {
			solution.setObjective(0, 0);
			solution.setObjective(1, 1000000);
		}
		
		
	}
	
	public double WAS(int featureIndex, int releaseId){
		double was;
		double sum = 0.0;
		int i = featureIndex; int k = releaseId;
		for(int p = 0; p < noOfStakeholders; p++){
			sum += stakeholderWeight[p] * values[i][p] * urgency[i][noOfStakeholders * p + k];
		}
		was = releaseImportance[k] * sum;
		
		return was;
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
