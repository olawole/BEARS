package cs.ucl.ac.uk.barp.project;

import java.util.HashMap;

import cs.ucl.ac.uk.barp.workitem.WorkItem;

public class Project implements IProject {
	private HashMap<String, WorkItem> workItems;
	private int numberOfIterations;
	private double interestRate;
	private String[] workItemVector;
	private double[] effortCapacity;

	public Project() {
		workItems = new HashMap<String, WorkItem>();
	}

	@Override
	public void add(WorkItem wItem) {
		if (workItems.containsKey(wItem.getItemId())) {
            throw new IllegalArgumentException("This Work Item already exists: "
                    + wItem);
        }
        workItems.put(wItem.getItemId(), wItem);		
	}

	@Override
	public HashMap<String, WorkItem> getWorkItems() {
		return workItems;
	}

	@Override
	public void setWorkItems(HashMap<String, WorkItem> workItems) {
		this.workItems = workItems;
	}

	@Override
	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	@Override
	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
		
	}

	@Override
	public int getNumberOfInvestmentPeriods() {
		return 0;
	}

	@Override
	public void setNumberOfInvestmentPeriods(int numberOfInvestmentPeriods) {
		
	}

	@Override
	public double getInterestRate() {
		// TODO Auto-generated method stub
		return interestRate;
	}

	@Override
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

}
