package cs.ucl.ac.uk.barp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.IntegerSolution;

public class ReleasePlan {
	
	private HashMap<Integer, Release> releases;
	//private List<Objective> objectives;
	private double businessValue;
	private double investmentRisk;
	private double expectedPunctuality;
	private double satisfaction;

	public ReleasePlan() {
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
	
	/**
	 * 
	 */
	public void sortWorkItemsByPriority(){
		for(Map.Entry<Integer, Release> entry : releases.entrySet()){
			Release release = entry.getValue();
			List<WorkItem> items = release.getwItems();
			if(!items.isEmpty()){
				release.sortByPriority();
			}
		}
	}
	
	/**
	 * @return
	 */
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
	
	/**
	 * @param scenario
	 * @param workSequence
	 * @param capacity
	 * @return
	 */
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

	/**
	 * @param currentIteration
	 * @return
	 */
	public Release getRelease(int currentIteration) {
		return this.releases.get(currentIteration);
	}

	/**
	 * @param currentIteration
	 * @return
	 */
	private boolean containsKey(int currentIteration) {
		return this.releases.containsKey(currentIteration);
	}

	/**
	 * @return
	 */
	public double getInvestmentRisk() {
		return investmentRisk;
	}

	/**
	 * @param investmentRisk
	 */
	public void setInvestmentRisk(double investmentRisk) {
		this.investmentRisk = investmentRisk;
	}

	/**
	 * @return
	 */
	public double getBusinessValue() {
		return businessValue;
	}

	/**
	 * @param businessValue
	 */
	public void setBusinessValue(double businessValue) {
		this.businessValue = businessValue;
	}
	
	/**
	 * @return
	 */
	public HashMap<Integer, Release> getPlan(){
		return releases;
	}
	
	
	// Used for planning with a point estimate
	/**
	 * @param workSequence
	 * @param capacity
	 * @return
	 */
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
	
	/**
	 * @return
	 */
	public HashMap<String, Integer> featureReleaseMap(){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		releases.forEach((releaseId, release) ->{
			release.getwItems().forEach(wItem->{
				result.put(wItem.getItemId(), releaseId);
			});
		});
		return result;	
	}

	/**
	 * @return
	 */
	public double getExpectedPunctuality() {
		return expectedPunctuality;
	}

	/**
	 * @param latenessRisk
	 */
	public void setExpectedPunctuality(double punctuality) {
		this.expectedPunctuality = punctuality;
	}

	/**
	 * @return
	 */
	public String planToString(){
		String s = "";
		for (Map.Entry<Integer, Release> entry : getPlan().entrySet()) {
			if (entry.getValue().isEmpty())
				continue;
			int size = entry.getValue().getwItems().size();
			int counter = 0;
			for (WorkItem wItem : entry.getValue().getwItems()) {
				s += (counter == 0) ? "(" : ",";
//				s += s.equals("") ? wItem.getItemId() : "," + wItem.getItemId();
				s += wItem.getItemId();
				s += (counter == size - 1)? ")":"";
				counter++;
			}
			s += "->"+ entry.getKey() + " ";
		}
		return s;
	}

	public double getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(double satisfaction) {
		this.satisfaction = satisfaction;
	}
	

}
