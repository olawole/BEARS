package cs.ucl.ac.uk.barp.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;

@SuppressWarnings("serial")
public class NDistribution extends NormalDistribution implements Distribution {
	
	private double mean;
	
	private double standardDeviation;
	
	public NDistribution(double mean, double sd){
		super(mean, sd);
	}
	
	public NDistribution(){
		super();
	}

//	@Override
//	public double sample() {
//		// TODO Auto-generated method stub
//		return this.sample();
//	}
//
//	@Override
//	public double[] sample(int N) {
//		// TODO Auto-generated method stub
//		return this.sample(N);
//	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	
//	@Override
//	public boolean isValidParameterLength(Double[] parameters) {
//		if (parameters.length != 2) {
//			return false;
//		}
//		return true;
//	}

}
