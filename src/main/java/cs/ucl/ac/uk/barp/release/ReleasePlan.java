package cs.ucl.ac.uk.barp.release;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.IntegerSolution;

//import cs.ucl.ac.uk.barp.objective.Objective;
import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.workitem.WorkItem;

public class ReleasePlan {
	
	private HashMap<Integer, Release> releases;
	private double businessValue;
	private double investmentRisk;

	public ReleasePlan() {
		// TODO Auto-generated constructor stub
		releases = new HashMap<Integer, Release>();
	}
	
	public void addRelease(int releaseId, Release newRelease){
		releases.put(releaseId, newRelease);
	}
	
	/**
	 * Copy constructor to generate iteration plan from a solution
	 * @param solution
	 * @param projectId
	 */
	public ReleasePlan (IntegerSolution solution, Project projectId){
		releases = new HashMap<Integer, Release>();
		String[] vector = projectId.getWorkItemVector();
		for (int j = 1; j <= projectId.getNumberOfIterations(); j++){
			Release release = new Release();
			for (int i = 0; i < solution.getNumberOfVariables(); i++){
				if(solution.getVariableValue(i) == j){
					String featureId = vector[i];
					release.addItemToRelease(projectId.getWorkItems().get(featureId));
				}
			}
			addRelease(j, release);
		}
	}
	
	public void sortWorkItemsByPriority(){
		for(Map.Entry<Integer, Release> entry : releases.entrySet()){
			Release release = entry.getValue();
			List<WorkItem> items = release.getwItems();
			if(!items.isEmpty()){
				release.sortByPriority();
			}
		}
	}
	
	public List<WorkItem> getWorkSequence() {
		List<WorkItem> workSequence = new ArrayList<WorkItem>();
		for(Map.Entry<Integer, Release> entry : releases.entrySet()){
			List<WorkItem> wItemsInRelease = entry.getValue().getwItems();
			for(WorkItem item : wItemsInRelease){
				workSequence.add(item);
			}
		}
		return workSequence;
	}
	
	public ReleasePlan actualPlan(int scenario, List<WorkItem> workSequence, double[] capacity){
		ReleasePlan myPlan = new ReleasePlan();
		int noOfReleases = capacity.length;
		double cumulativeEffort = 0;
		int currentIteration = 1;
		double cumulativeCapacity = capacity[currentIteration - 1];
		for(int i = 0; i < workSequence.size(); i++){
			cumulativeEffort += workSequence.get(i).getEffortSimulation()[scenario];
			
			while(cumulativeEffort > cumulativeCapacity && currentIteration <= noOfReleases ){
				if (currentIteration == noOfReleases){
					currentIteration++;
				}
				else {
					currentIteration++;
					cumulativeCapacity += capacity[currentIteration - 1];
				}
				
			}
			if (currentIteration > noOfReleases){
				break;
			}
			else {
				if (myPlan.containsKey(currentIteration)){
					Release value = myPlan.getRelease(currentIteration);
					value.addItemToRelease(workSequence.get(i));
					myPlan.addRelease(currentIteration, value);
				}
				else {
					Release value = new Release();
					value.addItemToRelease(workSequence.get(i));
					myPlan.addRelease(currentIteration, value);
				}
			}		
		}
		
		return myPlan;
	}

	public Release getRelease(int currentIteration) {
		// TODO Auto-generated method stub
		return this.releases.get(currentIteration);
	}

	private boolean containsKey(int currentIteration) {
		// TODO Auto-generated method stub
		return this.releases.containsKey(currentIteration);
	}

	public double getInvestmentRisk() {
		return investmentRisk;
	}

	public void setInvestmentRisk(double investmentRisk) {
		this.investmentRisk = investmentRisk;
	}

	public double getBusinessValue() {
		return businessValue;
	}

	public void setBusinessValue(double businessValue) {
		this.businessValue = businessValue;
	}
	
	public HashMap<Integer, Release> getPlan(){
		return releases;
	}
	
	
	// Used for planning with a point estimate
	public ReleasePlan actualPlan(List<WorkItem> workSequence, double[] capacity) {
		ReleasePlan myPlan = new ReleasePlan();
		int noOfReleases = capacity.length;
		double cumulativeEffort = 0;
		int currentIteration = 1;
		double cumulativeCapacity = capacity[currentIteration - 1];
		for(int i = 0; i < workSequence.size(); i++){
			cumulativeEffort += workSequence.get(i).getAverageEffort();
			
			while(cumulativeEffort > cumulativeCapacity && currentIteration <= noOfReleases ){
				if (currentIteration == noOfReleases){
					currentIteration++;
				}
				else {
					currentIteration++;
					cumulativeCapacity += capacity[currentIteration - 1];
				}
				
			}
			if (currentIteration > noOfReleases){
				break;
			}
			else {
				if (myPlan.containsKey(currentIteration)){
					Release value = myPlan.getRelease(currentIteration);
					value.addItemToRelease(workSequence.get(i));
					myPlan.addRelease(currentIteration, value);
				}
				else {
					Release value = new Release();
					value.addItemToRelease(workSequence.get(i));
					myPlan.addRelease(currentIteration, value);
				}
			}
			
		}
		
		return myPlan;
	}

}