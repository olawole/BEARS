package cs.ucl.ac.uk.barp.distribution;

public interface Distribution {
//	boolean isValidParameterLength(Double[] parameters);
	double sample();
	double[] sample(int N);
}
