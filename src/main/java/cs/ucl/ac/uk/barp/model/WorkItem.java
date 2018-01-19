package cs.ucl.ac.uk.barp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.ac.uk.barp.distribution.Distribution;

/**
 * 
 * @author olawoleoni
 */
public class WorkItem {

		private String itemId;
		
		private List<String> precursorids;
		
		private Distribution value;
		
		private Distribution effort;
		
		private int earliestRelease;
		
		private int latestRelease;
		
		private int valuePoint;
		
		private double priority;
		
		private double[][] valueSimulation;
		
		private double[] averageSimulation;
		
		private double[] effortSimulation;
		
		private double[][] sanpv;
		
		private double[] averagePeriodValue;
		
		private double averageEffort;
		
		private double averageValue;
		
		public WorkItem(String id, Distribution effort, Distribution value) {
			setItemId(id);
			setPrecursors(new ArrayList<String>());
			setEffort(effort);
			setValue(value);
		}

		public String getItemId() {
			return itemId;
		}

		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}

		
		public List<String> getPrecursors() {
			return Collections.unmodifiableList(precursorids);
		}

		
		public void setPrecursors(List<String> precursors) {
			this.precursorids = precursors;
		}
		
		
		public void addPrecursor(String item){
			if (item.equalsIgnoreCase(itemId)){
				return;
			}
			if (!precursorids.contains(item)){
				precursorids.add(item);
			}
		}

		
		public Distribution getValue() {
			return value;
		}

		
		public void setValue(Distribution value) {
			this.value = value;
		}

		
		public Distribution getEffort() {
			return effort;
		}

		
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

		public int getValuePoint() {
			return valuePoint;
		}

		public void setValuePoint(int valuePoint) {
			this.valuePoint = valuePoint;
		}

		public double[] getAveragePeriodValue() {
			return averagePeriodValue;
		}

		public void setAveragePeriodValue(double[] averageSanpv) {
			this.averagePeriodValue = averageSanpv;
		}

		/**
		 * @return the earliestRelease
		 */
		public int getEarliestRelease() {
			return earliestRelease;
		}

		/**
		 * @param earliestRelease the earliestRelease to set
		 */
		public void setEarliestRelease(int earliestRelease) {
			this.earliestRelease = earliestRelease;
		}

		/**
		 * @return the latestRelease
		 */
		public int getLatestRelease() {
			return latestRelease;
		}

		/**
		 * @param latestRelease the latestRelease to set
		 */
		public void setLatestRelease(int latestRelease) {
			this.latestRelease = latestRelease;
		}

}
