package cs.ucl.ac.uk.barp.project.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class StatUtil {
	
	public static double round(double value, int places){
		if (places < 0) throw new IllegalArgumentException();
		if (Double.isNaN(value)){
			return 0;
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static double mean(double[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return mydata.getMean();
	}
	
	public static double[] sort(double[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return mydata.getSortedValues();
	}
	
	public static double stdev(double[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		
		return mydata.getStandardDeviation();
	}
	
	public static double sum(double[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return mydata.getSum();
	}
	
	public static double max(double[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return mydata.getMax();
	}
	
	public static double percentile(double[] data, double n){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return mydata.getPercentile(n);
	}
	
	public static double min(double[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		double size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return mydata.getMin();
	}
	
	public static int sum(Integer[] data){
		DescriptiveStatistics mydata = new DescriptiveStatistics();
		int size = data.length;
		
		for (int i = 0; i < size; i++){
			mydata.addValue(data[i]);
		}
		return (int) mydata.getSum();
	}
}
