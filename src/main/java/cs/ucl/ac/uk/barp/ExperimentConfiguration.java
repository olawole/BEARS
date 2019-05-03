package cs.ucl.ac.uk.barp;

import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;

public class ExperimentConfiguration {

	public static final String FILENAME = "councilNew2.csv";
	public static final String WORD = "data/word-processing.csv";
	public static final int noOfReleases = 4;
	public static final int noOfInvestmentHorizon = 12;
	public static final double capacity[] = new double[]{725,693,675};
	public static final double releaseImp[] = new double[]{9,8,7,6};
	public static final double budget[] = new double[]{500, 500, 500};
	public static final double interestRate = 0.02;
	public static final String distributionType = "LogNormal";
	public static final String algorithmType = ConfigSetting.DEFAULT_APRROX_ALGORITHM;

}
