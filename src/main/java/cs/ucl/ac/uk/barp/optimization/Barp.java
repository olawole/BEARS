package cs.ucl.ac.uk.barp.optimization;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.Release;
import cs.ucl.ac.uk.barp.release.ReleasePlan;
import cs.ucl.ac.uk.barp.workitem.WorkItem;
import cs.ucl.ac.uk.barp.project.Project;


public class Barp extends AbstractIntegerProblem {

	/**
	 * 
	 */
	//double expectedNPV;
	//double investmentRisk;
	double effort;
	String[] vector;
	double[] capacity;
	int noOfReleases;
	int noSims;
	Project projectId;
	private static final long serialVersionUID = 1L;

	public Barp(Project project) {
		capacity = project.getEffortCapacity();
		vector = project.getWorkItemVector();
		noOfReleases = project.getNumberOfIterations();
		projectId = project;
		setNumberOfObjectives(2);
		setNumberOfVariables(vector.length);
		setName("Barp");
		
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
		boolean isValid = isValid(solution);
		if(isValid){
			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			ReleasePlan iterationPlan = new ReleasePlan(solution, projectId);
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
		}
		else {
			solution.setObjective(0, 0);
			solution.setObjective(1, 1);
			this.effort = 0;
		}	
		
	}

	private void expectedValue(IntegerSolution solution, double[] effort, double[] npv) {
		double enpv = StatUtil.mean(npv);
		double eEffort = StatUtil.mean(effort);
		double invRisk = StatUtil.round(StatUtil.stdev(npv) / enpv, 4);
		double sumCapacity = StatUtil.sum(capacity); //
		if (eEffort > sumCapacity){
			solution.setObjective(0, 0);
			solution.setObjective(1, 1);
			this.effort = eEffort;
		}
		else {
			solution.setObjective(0, -enpv);
			solution.setObjective(1, invRisk);
			this.effort = eEffort;
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
				if (precursorIndex < 0 || (solution.getVariableValue(i) < solution.getVariableValue(precursorIndex))){
					return false;
				}
				else if ((solution.getVariableValue(i) == solution.getVariableValue(precursorIndex)) && solution.getVariableValue(i) != 0 ){
						return false;
				}
			}
		}
		return true;
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
