package cs.ucl.ac.uk.barp.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cs.ucl.ac.uk.exception.TransitiveDependencyException;
import cs.ucl.ac.uk.exception.CyclicDependencyException;

public class Project {
	private HashMap<String, WorkItem> workItems;
	private int numberOfIterations;
	private int numberOfInvestmentHorizons;
	private double interestRate;
	private String[] workItemVector;
	private double[] effortCapacity;
	private double[] budgetPerRelease;
	Set<String> terminationSet;

	public Project() {
		workItems = new HashMap<String, WorkItem>();
	}

	public void add(WorkItem wItem) {
		if (workItems.containsKey(wItem.getItemId())) {
            throw new IllegalArgumentException("This Work Item already exists in the project: "
                    + wItem);
        }
        workItems.put(wItem.getItemId(), wItem);		
	}

	public HashMap<String, WorkItem> getWorkItems() {
		return workItems;
	}

	public void setWorkItems(HashMap<String, WorkItem> workItems) {
		if (workItems == null){
			throw new IllegalArgumentException("Null Work item set");
		}
		this.workItems = workItems;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		if (numberOfIterations < 0){
			throw new IllegalArgumentException("Number of iteration must be positive");
		}
		this.numberOfIterations = numberOfIterations;
		
	}

	public int getNumberOfInvestmentPeriods() {
		return numberOfInvestmentHorizons;
	}

	public void setNumberOfInvestmentPeriods(int numberOfInvestmentPeriods) {
		this.numberOfInvestmentHorizons = numberOfInvestmentPeriods;
	}

	public double getInterestRate() {
		// TODO Auto-generated method stub
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
		
	}
	
	public double[] getEffortCapacity() {
		return effortCapacity;
	}


	public void setEffortCapacity(double[] effortCapacity) {
		this.effortCapacity = effortCapacity;
	}
	
	public String[] getWorkItemVector() {
		return workItemVector;
	}


	public void setWorkItemVector() {
		if (workItems == null){
			return;
		}
		workItemVector = new String[workItems.size()];
		int i = 0;
		for (String s : workItems.keySet()){
			workItemVector[i++] = s;
		}
	}
	
	public boolean checkCyclicDependency() throws CyclicDependencyException{
		
		return false;
	}
	
	private Set<String> getAllPrecursors(String wItemId){
		
		Set<String> preclist = new HashSet<String>();
		if (wItemId.equalsIgnoreCase("") || wItemId == null){
			return preclist;
		}
		if (terminationSet != null && terminationSet.contains(wItemId)){
			preclist.add(wItemId);
			return preclist;
		}
		if (workItems.get(wItemId).getPrecursors().get(0).equals("")){
			return preclist;
		}
		terminationSet.add(wItemId);
		preclist.addAll(workItems.get(wItemId).getPrecursors());
		Set<String> tempList = new HashSet<String>();
		for(String s : preclist){
			tempList.addAll(getAllPrecursors(s));
		}
		if (!tempList.isEmpty()){
			preclist.addAll(tempList);
		}
		
		return preclist;
	}
	
	public void checkTransitiveDependency(){
		
		workItems.forEach((k,v) -> {
			terminationSet = new HashSet<String>();
			if (v.getPrecursors().get(0).equals("")){
				// nothing
			}
			else {
			Set<String> precedence = new HashSet<String>(getAllPrecursors(k));
			
				
				try {
					if (precedence.contains(k)){
						throw new TransitiveDependencyException("Error: Transitive relationship exists with work item " + k);
					}
				} catch (TransitiveDependencyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
	
			}
			
		});
	}
	
	public void printStrands(){
		workItems.forEach((k,v) -> {
			System.out.println("Strand " + getAllPrecursors(k).toString() + "->" + k);
		});
	}

	public double[] getBudgetPerRelease() {
		return budgetPerRelease;
	}

	public void setBudgetPerRelease(double[] budgetPerRelease) {
		this.budgetPerRelease = budgetPerRelease;
	}

}
