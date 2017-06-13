package cs.ucl.ac.uk.barp.project;

import java.util.HashMap;

import cs.ucl.ac.uk.barp.workitem.WorkItem;

public interface IProject {
	public void add(WorkItem wItem);
	
	public HashMap<String, WorkItem> getWorkItems();
	
	public void setWorkItems(HashMap<String, WorkItem> workItems);
	
	public int getNumberOfIterations();

	public void setNumberOfIterations(int numberOfIterations);

	public int getNumberOfInvestmentPeriods();

	public void setNumberOfInvestmentPeriods(int numberOfInvestmentPeriods);
	
	public double getInterestRate();

	public void setInterestRate(double interestRate);
}
