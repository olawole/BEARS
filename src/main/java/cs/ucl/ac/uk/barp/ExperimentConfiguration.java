package cs.ucl.ac.uk.barp;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

public class ExperimentConfiguration {

	public static final String FILENAME = "councilNew2.csv";
	public static final int noOfReleases = 3;
	public static final int noOfInvestmentHorizon = 10;
	public static final double capacity[] = new double[]{400,400,400};
	public static final double releaseImp[] = new double[]{0.5, 0.45, 0.40, 0.35, 0.25};
	public static final double budget[] = new double[]{500, 350, 300};
	public static final double interestRate = 0.02;
	public static final String distributionType = "LogNormal";
	public static final String algorithmType = ConfigSetting.DEFAULT_APRROX_ALGORITHM;

}
