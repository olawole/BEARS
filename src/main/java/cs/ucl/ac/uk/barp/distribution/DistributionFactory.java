package cs.ucl.ac.uk.barp.distribution;

public class DistributionFactory {
	public static Distribution getDistribution(String distributionType, Double[] value) throws Exception{
		if (distributionType == null){
			return null;
		}
		if (distributionType.equalsIgnoreCase("Triangular")){
			return makeTriangularDistribution(value);			
		}
		if (distributionType.equalsIgnoreCase("NormalCI")){
			return makeNormalCIDistribution(value);
		}
		if (distributionType.equalsIgnoreCase("Normal")){
			return makeNormalDistribution(value);
		}
		if (distributionType.equalsIgnoreCase("LogNormal")){
			return makeLogNormalDistribution(value);
		}
		if (distributionType.equalsIgnoreCase("Point")){
			return makePointDistribution(value);
		}
		return null;
	}

	private static Distribution makeTriangularDistribution(Double[] value) throws Exception {
		if (value.length == 3){
			double least = value[0];
			double mode = value[1];
			double most = value[2];
			return new TDistribution(least, mode, most);
		}
		else {
			throw new Exception("The number of parameters provided for triangular distribution must be equal to 3");
		}
	}
	
	private static Distribution makeNormalCIDistribution(Double[] value) throws Exception {
		if (value.length == 2){
			double lower = value[0];
			double upper = value[1];
			return new NormalCIDistribution(lower, upper);
		}
		else {
			throw new Exception("The number of parameters provided for normal CI distribution must be equal to 2");
		}
	}
	
	private static Distribution makeNormalDistribution(Double[] value) throws Exception {
		if (value[0] == 0){
			return null;
		}
		if (value.length == 2){
			double mean = value[0];
			double sd = value[1];
			return new NDistribution(mean, sd);
		}
		else {
			throw new Exception("The number of parameters provided for normal distribution must be equal to 2");
		}
	}
	
	private static Distribution makeLogNormalDistribution(Double[] value) throws Exception {
		if (value.length == 2){
			double lower = value[0];
			double upper = value[1];
			return new LogNDistribution(lower, upper);
		}
		else {
			throw new Exception("The number of parameters provided for log normal distribution must be equal to 2");
		}
	}
	
	private static Distribution makePointDistribution(Double[] value) throws Exception {
		if (value.length == 1){
			double estimate = value[0];
			return new PointDistribution(estimate);
		}
		else {
			throw new Exception("The number of parameters provided for point distribution must be equal to 1");
		}
	}
}
