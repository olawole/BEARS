package cs.ucl.ac.uk.barp.distribution;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.RandomGenerator;

public class StudentTDistribution extends TDistribution implements Distribution {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudentTDistribution(double degreesOfFreedom) throws NotStrictlyPositiveException {
		super(degreesOfFreedom);
		// TODO Auto-generated constructor stub
	}

	public StudentTDistribution(double degreesOfFreedom, double inverseCumAccuracy) throws NotStrictlyPositiveException {
		super(degreesOfFreedom, inverseCumAccuracy);
		// TODO Auto-generated constructor stub
	}

	public StudentTDistribution(RandomGenerator rng, double degreesOfFreedom) throws NotStrictlyPositiveException {
		super(rng, degreesOfFreedom);
		// TODO Auto-generated constructor stub
	}

	public StudentTDistribution(RandomGenerator rng, double degreesOfFreedom, double inverseCumAccuracy)
			throws NotStrictlyPositiveException {
		super(rng, degreesOfFreedom, inverseCumAccuracy);
		// TODO Auto-generated constructor stub
	}

}
