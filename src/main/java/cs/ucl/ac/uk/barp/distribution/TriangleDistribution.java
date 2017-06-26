package cs.ucl.ac.uk.barp.distribution;

import org.apache.commons.math3.distribution.TriangularDistribution;


/*
 * Describes a triangular distribution for the cash elements.
 */
@SuppressWarnings("serial")
public class TriangleDistribution extends TriangularDistribution implements Distribution {

	/*
	 * least is the minimum value of the cash element
	 */
	private double least;
	
	/*
	 * most is the maximum value of the cash element
	 */
	private double most;
	
	private double mode;
	
	public TriangleDistribution(){
		super(0,0,0);
		least = mode = most = 0;
	}
	public TriangleDistribution(double least, double mode, double most){
		super(least, mode, most);
		this.setMode(mode);
		this.setLeast(least);
		this.setMost(most);
	}
	

	private void setMode(double mode) {
		this.mode = mode;	
	}


	public double getMode() {
		return this.mode;
	}

	public double getMost() {
		return most;
	}

	public void setMost(double most) {
		this.most = most;
	}

	public double getLeast() {
		return least;
	}

	public void setLeast(double least) {
		this.least = least;
	}


	@Override
	public double sample() {
		// TODO Auto-generated method stub
		return super.sample();
	}


	@Override
	public double[] sample(int N) {
		// TODO Auto-generated method stub
		return super.sample(N);
	}
	
//	@Override
//	public boolean isValidParameterLength(Double[] parameters) {
//		if (parameters.length != 3) {
//			return false;
//		}
//		return true;
//	}
	
	

}
