package cs.ucl.ac.uk.barp.optimization;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.Release;
import cs.ucl.ac.uk.barp.release.ReleasePlan;
import cs.ucl.ac.uk.barp.workitem.WorkItem;

public class BarpCertain extends AbstractIntegerProblem {

	/**
	 * 
	 */
	//double expectedNPV;
	//double investmentRisk;
	String[] vector;
	double[] capacity;
	int noOfReleases;
	Project projectId;
	private static final long serialVersionUID = 1L;

	public BarpCertain(Project project) {
		capacity = project.getEffortCapacity();
		vector = project.getWorkItemVector();
		noOfReleases = project.getNumberOfIterations();
		projectId = project;
		setNumberOfObjectives(2);
		setNumberOfVariables(vector.length);
		setName("BarpCertain");
		
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
		if(isValid){
			double effort = 0;
			double npv = 0;
			ReleasePlan iterationPlan = new ReleasePlan(solution, projectId);
			iterationPlan.sortWorkItemsByPriority();
			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
			ReleasePlan rPlan = iterationPlan.actualPlan(workSequence, capacity);
			for (int i = 1; i <= noOfReleases; i++){
				Release release = rPlan.getRelease(i);
				if(release != null){
					for (WorkItem wi : release.getwItems()){
						effort += wi.getEffort().sample();
						if(wi.getValue() != null)
							npv += wi.getValue().sample();
					}
				}
			}
			double sumCapacity = StatUtil.sum(capacity); //
			if (effort > sumCapacity){
				solution.setObjective(0, 0);
				solution.setObjective(1, 0);
			}
			else {
				solution.setObjective(0, -npv);
				solution.setObjective(1, sumCapacity - effort);
			}
				
				
		}
		
		else {
			solution.setObjective(0, 0);
			solution.setObjective(1, 0);
		}	
		
	}
	
	
	public boolean isValid(IntegerSolution solution){
		//System.out.println(encodedPlan);
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
						//solution.setVariableValue(precursorIndex, solution.getVariableValue(i) - 1);
				}
				else if (solution.getVariableValue(i) <= solution.getVariableValue(precursorIndex)){
//					if ((solution.getVariableValue(precursorIndex) + 1) <= noOfReleases){
//						solution.setVariableValue(i, solution.getVariableValue(precursorIndex) + 1);
//					}
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

}
