package cs.ucl.ac.uk.barp.distribution;

public class PointDistribution implements Distribution {
	private Double pointEstimate;
	
	public PointDistribution() {
		// TODO Auto-generated constructor stub
	}

	public PointDistribution(double estimate) {
		setPointEstimate(estimate);
	}

	@Override
	public double sample() {
		// TODO Auto-generated method stub
		return pointEstimate;
	}

	@Override
	public double[] sample(int N) {
		// TODO Auto-generated method stub
		return null;
	}

	public Double getPointEstimate() {
		return pointEstimate;
	}

	public void setPointEstimate(Double pointEstimate) {
		this.pointEstimate = pointEstimate;
	}

}
