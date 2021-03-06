package cs.ucl.ac.uk.barp.project.utilities;

public class ConfigSetting {

	//Graphviz
	public static String MACEXE = "/usr/local/bin/dot";
	public static String WINEXE = "c:/Program Files (x86)/Graphviz 2.28/bin/dot.exe";
	public static String LINUXEXE = "/usr/bin/dot";

	//Algorithm based Configs
	public static boolean USE_DEFAULT_PARAMETER_SETTINGS= true;
	public static String APROXIMATE_ALGORITHM_LIST= "NSGAII,SPEA2,IBEA";
	public static int NUMBER_OF_SIMULATION=10000;
	public static double MUTATION_PROBABILITY = 0.05;
	public static int THREADS=1;
	public static int MAX_EVALUATIONS=25000;
	public static String DEFAULT_APRROX_ALGORITHM= "nsgaii";
	public static int POPULATION_SIZE=100;
	public static double CROSSOVER_PROBABILITY=0.9;
	public static double CROSSOVER_DISTRIBUTION_INDEX=20;
	public static double MUTATION_DISTRIBUTION_INDEX=20;
	public static String BARCHART_TITLE="Frequency Bar Chart";
	public static String BARCHART_SUBTITLE= "";//"In what release is a feature more likely?";
	public static String DOT_DIRECTORY = "";
	public static String OUTPUT_DIRECTORY= "";
}
