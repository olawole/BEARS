package cs.ucl.ac.uk.barp.distribution;

import org.apache.commons.math3.distribution.LogNormalDistribution;

@SuppressWarnings("serial")
public class LogNDistribution extends LogNormalDistribution implements Distribution {

	private double mean;

	private double standardDeviation;

	public LogNDistribution(double mean, double sd) {
		super(mean, sd);
	}

	public LogNDistribution() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double sample() {
		// TODO Auto-generated method stub
		return super.sample();
	}

	@Override
	public double[] sample(int N) {
		double[] result = new double[N];
		for (int i = 0; i < N; i++){
			result[i] = sample();
		}
		return result;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

//	@Override
//	public boolean isValidParameterLength(Double[] parameters) {
//		if (parameters.length != 2) {
//			return false;
//		}
//		return true;
//	}

}
