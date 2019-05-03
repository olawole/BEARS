package cs.ucl.ac.uk.barp.distribution;

import org.apache.commons.math3.distribution.NormalDistribution;

public class NormalCIDistribution implements Distribution{
	
	private static final double CI_AT_90 = 3.29;
	private double upper;
	private double lower;
	private double mean;
	private double sd;
	private NormalDistribution normal;
	
	public NormalCIDistribution(){
		upper = lower = mean = sd = 0.0;
	}
	
	public NormalCIDistribution(double lower, double upper) throws Exception{
		this.lower = lower;
		this.upper = upper;
		this.mean = calculateMean();
		this.sd = calculateSd();
		normal = new NormalDistribution(mean, sd);
	}
	public double getUpper() {
		return upper;
	}
	public void setUpper(double upper) {
		this.upper = upper;
	}
	public double getLower() {
		return lower;
	}
	public void setLower(double lower) {
		this.lower = lower;
	}
	
	
	public double getMeanV(double lower, double upper) throws Exception{
		return mean;
	}
	
	public double calculateMean() throws Exception{
		if (upper < 0 || lower < 0 || upper < lower){
			throw new Exception("upper and lower value must be positive and lower must be less or"
					+ "equal to upper");
		}
		return (upper + lower) / 2;
	}
	
	public double getSd() throws Exception {
		return sd;
	}
	public double calculateSd() throws Exception {
		if (upper < 0 || lower < 0 || upper < lower){
			throw new Exception("upper and lower value must be positive and lower must be less or"
					+ "equal to upper");
		}
		return Math.abs((upper - lower) / CI_AT_90);
	}
	@Override
	public double sample() {
		// TODO Auto-generated method stub
		return normal.sample();
	}
	@Override
	public double[] sample(int N) {
		// TODO Auto-generated method stub
		return normal.sample(N);
	}
	
//	@Override
//	public boolean isValidParameterLength(Double[] parameters) {
//		if (parameters.length != 2) {
//			return false;
//		}
//		return true;
//	}
	
}
