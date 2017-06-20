package cs.ucl.ac.uk.barp.workitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.ac.uk.barp.distribution.Distribution;
import cs.ucl.ac.uk.elicitation.Expert;

public class WorkItemM implements IWorkItem {
	private String itemId;
	
	private List<String> precursorids;
	
	private List<Expert> expertEstimates;
	
	private List<Distribution> value;
	
	private Distribution effort;
	
	private double priority;
	
	private double[][] valueSimulation;
	
	private double[] averageSimulation;
	
	private double[] effortSimulation;
	
	private double[][] sanpv;
	
	private double averageEffort;
	
	private double averageValue;

	public WorkItemM(String id, Distribution effort, List<Distribution> value) {
		setItemId(id);
		setPrecursors(new ArrayList<String>());
		setEffort(effort);
		this.value = value;;
	}

	@Override
	public String getItemId() {
		return itemId;
	}

	
	@Override
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	
	@Override
	public List<String> getPrecursors() {
		return Collections.unmodifiableList(precursorids);
	}

	
	@Override
	public void setPrecursors(List<String> precursors) {
		this.precursorids = precursors;
	}
	
	
	@Override
	public void addPrecursor(String item){
		if (!precursorids.contains(item)){
			precursorids.add(item);
		}
	}

	
	@Override
	public Distribution getValue() {
		return null;
	}

	
	@Override
	public void setValue(Distribution value) {
		//this.value = value;
	}

	
	@Override
	public Distribution getEffort() {
		return effort;
	}

	
	@Override
	public void setEffort(Distribution effort) {
		this.effort = effort;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}
	
	public double[][] getValueSimulation() {
		return valueSimulation;
	}

	public void setValueSimulation(double[][] valueSimulation) {
		this.valueSimulation = valueSimulation;
	}

	public double[] getEffortSimulation() {
		return effortSimulation;
	}

	public void setEffortSimulation(double[] effortSimulation) {
		this.effortSimulation = effortSimulation;
	}
	
	public double[][] getSanpv() {
		return sanpv;
	}

	public void setSanpv(double[][] sanpv) {
		this.sanpv = sanpv;
	}

	public double getAverageEffort() {
		return averageEffort;
	}

	public void setAverageEffort(double averageEffort) {
		this.averageEffort = averageEffort;
	}

	public double getAverageValue() {
		return averageValue;
	}

	public void setAverageValue(double averageValue) {
		this.averageValue = averageValue;
	}

	public double[] getAverageSimulation() {
		return averageSimulation;
	}

	public void setAverageSimulation(double[] averageSimulation) {
		this.averageSimulation = averageSimulation;
	}

}
