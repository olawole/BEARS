package cs.ucl.ac.uk.barp.distribution;

/**
 * @author olawoleoni
 * An interface for probability distribution, interface implements sample method for sampling
 * from the distribution
 */
public interface Distribution {
//	boolean isValidParameterLength(Double[] parameters);
	double sample();
	double[] sample(int N);
}
