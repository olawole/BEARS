package cs.ucl.ac.uk.barp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.Release;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.model.WorkItem;
import cs.ucl.ac.uk.barp.optimization.InformationValueAnalyser;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.problem.MORP;
import cs.ucl.ac.uk.barp.project.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.ConfigSetting;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.BarChartView;
import cs.ucl.ac.uk.barp.release.view.RoadMapView;
import cs.ucl.ac.uk.barp.release.view.Scatter;
import cs.ucl.ac.uk.barp.release.view.ScatterPlotView;
import cs.ucl.ac.uk.barp.release.view.TableView;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.evolve.Feature;
public class Bears {

	public Bears() {
		// TODO Auto-generated constructor stub
	}
	@Parameter(names = "--help", description = "Help")
	private boolean help = false;
	
	@Parameter(names = "--projName", description = "Name of the project ")
	public String projName = "council";
	
	@Parameter(names = "--output", description = "Output folder where the results are saved. Input <file path>.  ")
	public String outputFolder = "MotivationExample/output/";
	
	@Parameter(names = "--data", description = "The file that contains list of features and planning parameters ")
	public String data = "MotivationExample/input_data/council.csv";

	@Parameter(names = "--budget", description = "Available budget per release")
	private String budget = null;
	
	@Parameter(names = "--capacity", description = "Available effort per release")
	public String capacity = null;
	
	@Parameter(names = "--investment", description = "Number of investment horizon")
	public Integer investment = 0;
	
	@Parameter(names = "--numberOfReleases", description = "Number of investment horizon")
	public Integer numberOfRelease = 0;
	
	@Parameter(names ="--alg", description = "Type of optimization algorithm, SPEA2 by default. Others are NSGAII, MOCell, Random")
	public String alg = "SPEA2";
	
	@Parameter(names ="--distribution", description = "Type of probability distribution input data belongs to, default is lognormal")
	public String distribution = "LogNormal";
	
	@Parameter(names ="--interest", description = "Discount rate per period")
	public String interest = null;
	
	@Parameter(names ="--scatter", description = "Displays the scatter plot.", arity=1)
	public boolean scatter = false;
	
	@Parameter(names ="--table", description = "Generates Latex and CSV tables", arity=1)
	public boolean table = false;
	
	@Parameter(names ="--roadmap", description = "Generate roadmap visualization with dot", arity=1)
	public boolean roadmap = false;
	
	@Parameter(names ="--bar", description = "Displays the bar plot containing frequency of features in different releases.", arity=1)
	public boolean bar = false;
	
	@Parameter(names ="--valueAnalyzer", description = "Displays the pareto plots.", arity=1)
	public boolean valueAnalyzer = false;
	
	@Parameter(names ="--compareEvolve", description = "Displays the pareto plots.", arity=1)
	public boolean compareEvolve = false;
	
	public static void main(String[] args) throws Exception {
		Bears command = new Bears();
		
		JCommander jcommander = new JCommander(command, args);
		jcommander.setProgramName("BEARS");
		if (command.help) {
        	jcommander.usage();
            return;
        }
        command.run(command);

	}
	private void run(Bears command) throws Exception {
		if ((data != null) && (capacity != null) && (budget != null)){
			Project project = loadProject();
			OptimalSolutions optimal = runOptimisation(project);
			if (compareEvolve){
				EvolveProject evolve = convertBearsToEvolve(project);
				List<IntegerSolution> evolveSolutions = runEvolve(evolve);
				List<ReleasePlan> evolvePlan = computeObjective(evolveSolutions, project);
				evolvePlan = removeDuplicate(evolvePlan);
				optimal.getSolutions().forEach(plan->{
					plan.setSatisfaction(computeValuePoint(plan, evolve.releaseImp));
				});
				Scatter s = new Scatter("BEARS vs EVOLVE", optimal.getSolutions(), evolvePlan);
				s.drawPlot();
				generateCSVTable(optimal.getSolutions(), "Bears");
				generateCSVTable(evolvePlan, "Evolve");
			}
		}
	}
	
	Project loadProject() throws Exception{
		Project project = ProjectParser.parseCSVToProject(data, distribution);
		project.checkTransitiveDependency();
		double[] cap = parseDouble(capacity.split(","));
		double[] budg = parseDouble(budget.split(","));
		if (cap.length != budg.length || cap.length != numberOfRelease){
			throw new Exception("Budget and Capacity must have the same dimension. The size of the array must also be equal to the number of releases");
		}
		project.setEffortCapacity(cap);
		project.setBudgetPerRelease(budg);
		project.setInterestRate(Double.parseDouble(interest) / 100);
		project.setNumberOfInvestmentPeriods(investment);
		project.setNumberOfIterations(cap.length);
		MCSimulator.simulate(project.getWorkItems(), investment, project.getInterestRate());
		return project;	
	}
	
	private double[] parseDouble(String[] stringValue){
		double[] values = new double[stringValue.length];
		for(int i = 0; i < values.length; i++){
			values[i] = Double.parseDouble(stringValue[i]);
		}
		return values;
	}
	
	private OptimalSolutions runOptimisation(Project project) throws Exception{
		Optimization optimisation = new Optimization(project, "Barp", alg);
		List<IntegerSolution> solutions = optimisation.run();
		OptimalSolutions optimal = new OptimalSolutions();
		if (scatter){
			new ScatterPlotView(optimal);
		}
		if (roadmap){
			new RoadMapView(optimal, numberOfRelease, outputFolder + projName);
		}
		if (bar){
			new BarChartView(optimal, numberOfRelease);
		}
		if (table) {
			new TableView(optimal, numberOfRelease);
		}
		optimal.setSolutions(solutions, project);
		if (valueAnalyzer){
			InformationValueAnalyser.computeInformationValue(optimal, project.getWorkItems());
			optimal.printEvppi();
		}
		return optimal;
	}
	
	private double computeValuePoint(ReleasePlan plan, double[] releaseImp){
		double satisfaction = 0;
		for (int i = 1; i <= numberOfRelease; i++){
			Release release = plan.getRelease(i);
			if(release != null){
				for (WorkItem wi : release.getwItems()){
					satisfaction += wi.getValuePoint() * releaseImp[i-1];
				}
			}
		}
		return satisfaction;
	}
	
	private EvolveProject convertBearsToEvolve(Project project) {
		EvolveProject evolveProject = new EvolveProject();
		double[] releaseImp = new double[numberOfRelease];
		int k = 0;
		double avgValues[] = new double[project.getWorkItems().size()];
		for (Map.Entry<String, WorkItem> keyValue : project.getWorkItems().entrySet()){
			avgValues[k++] = keyValue.getValue().getAverageValue();
		}
		for (Map.Entry<String, WorkItem> entry : project.getWorkItems().entrySet()){
			Feature feature = new Feature(entry.getKey());
			feature.setEffort(entry.getValue().getAverageEffort());
			if (entry.getValue().getAverageSimulation() == null){
				feature.setValue(0);
			}
			else {
				double value = 9 * (entry.getValue().getAverageValue() / StatUtil.max(avgValues));
				feature.setValue((int) Math.round(value));
				entry.getValue().setValuePoint(feature.getValue());
			}
			feature.setPrecursors(entry.getValue().getPrecursors());
			evolveProject.addFeature(feature);
			//System.out.println(feature.getFeatureId() + "\t" + feature.getEffort() + "\t" + feature.getValue() + "\t" + entry.getValue().getAverageValue());
		}
		evolveProject.capacity = project.getEffortCapacity();
		evolveProject.setParameterMatrix();

		for (int i = 0; i < releaseImp.length; i++) {
			releaseImp[i] = 1 / Math.pow(1 + project.getInterestRate(), i + 1);
		}
		double sum = StatUtil.sum(releaseImp);
		for (int i = 0; i < releaseImp.length; i++) {
			releaseImp[i] = releaseImp[i] / sum;
		}
		evolveProject.releaseImp = releaseImp;
		return evolveProject;

	}
	
	private List<IntegerSolution> runEvolve(EvolveProject evolve) {
		Problem<IntegerSolution> problem = new MORP(evolve);
		CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(ConfigSetting.CROSSOVER_PROBABILITY,
				ConfigSetting.CROSSOVER_DISTRIBUTION_INDEX);
		MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(ConfigSetting.MUTATION_PROBABILITY,
				ConfigSetting.MUTATION_DISTRIBUTION_INDEX);
		SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<IntegerSolution>();		
		Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(25000)
		        .setPopulationSize(100)
		        .build() ;
		@SuppressWarnings("unused")
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		return algorithm.getResult();
	}
	
	private List<ReleasePlan> computeObjective(List<IntegerSolution> evolveSol, Project project){
		List<ReleasePlan> optimal = new ArrayList<ReleasePlan>();
		int noOfReleases = project.getNumberOfIterations();
		double[] budget = project.getBudgetPerRelease();
		double[] capacity = project.getEffortCapacity();
		evolveSol.forEach(solution->{
			double[] effort = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] npv = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			double[] lateness = new double[ConfigSetting.NUMBER_OF_SIMULATION];
			ReleasePlan iterationPlan = new ReleasePlan(solution, project);
			iterationPlan.sortWorkItemsByPriority();
			List<WorkItem> workSequence = iterationPlan.getWorkSequence();
			ReleasePlan actualPlan = iterationPlan.actualPlan(workSequence, project.getEffortCapacity());
			HashMap<String, Integer> actualFeatureReleaseMap = actualPlan.featureReleaseMap();
			for(int j = 0; j < ConfigSetting.NUMBER_OF_SIMULATION; j++){
				double sumEffort = 0;
				double sanpv = 0;
				ReleasePlan rPlan = iterationPlan.actualPlan(j, workSequence, capacity);
				HashMap<String, Integer> scenarioFeatureReleaseMap = rPlan.featureReleaseMap();
				for (int i = 1; i <= noOfReleases; i++){
					Release release = rPlan.getRelease(i);
					if(release != null){
						for (WorkItem wi : release.getwItems()){
							sumEffort += wi.getEffortSimulation()[j];
							if(wi.getValue() != null)
								sanpv += wi.getSanpv()[j][i-1];
						}
						sanpv -= budget[i-1];
					}
				}
				lateness[j] = computeLatenessProbability(actualFeatureReleaseMap, scenarioFeatureReleaseMap);
				npv[j] = sanpv;
				effort[j] = sumEffort;
			}
			actualPlan.setBusinessValue(StatUtil.mean(npv));
			actualPlan.setExpectedPunctuality((1 - StatUtil.mean(lateness)) * 100);
			actualPlan.setSatisfaction(-solution.getObjective(0));
			optimal.add(actualPlan);
		});
		return optimal;
	}
	
	private double computeLatenessProbability(HashMap<String, Integer> actualP, HashMap<String, Integer> scenarioP){
		if (actualP.size() == 0){
			return 0;
		}
		double latenessProbability;
		double diff = 0;
		for (String item : actualP.keySet()){
			Integer actualRelease = actualP.get(item);
			Integer scenarioRelease = (scenarioP.get(item) != null) ? scenarioP.get(item) : 0;
			if(scenarioRelease == 0){
				diff += actualRelease;
			}
			if(actualRelease < scenarioRelease){
				diff += scenarioRelease - actualRelease;
			}
		}
		latenessProbability = diff / actualP.size();
		return latenessProbability;	
	}
	
	public void generateCSVTable(List<ReleasePlan> rPlans, String type){
		String csvString = "";
		final String COMMA_SEP = ",";
		String heading = "S/N" + COMMA_SEP;
		for (int i = 0; i < numberOfRelease; i++){
			heading += "Release " + (i+1) + COMMA_SEP;
		}
		heading += "ENPV('000£)" + COMMA_SEP + "Satisfaction Score";
		csvString += heading + "\n";
		int counter = 0;
		for (ReleasePlan plan : rPlans){
			String row = ++counter + COMMA_SEP;
			for (int i = 1; i <= plan.getPlan().size(); i++){
				String s = plan.getPlan().get(i).toString();
				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
			}
			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + 
					plan.getSatisfaction();
			csvString += row + "\n";
		}
		try {
			FileWriter output1 = new FileWriter(outputFolder + projName + type + ".csv");
			output1.write(csvString);
			output1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<ReleasePlan> removeDuplicate(List<ReleasePlan> solutions){
		List<ReleasePlan> sol = new ArrayList<ReleasePlan>(solutions);
		solutions = new ArrayList<ReleasePlan>();
		for (ReleasePlan plan : sol){
			if (!contains(solutions, plan)){
				solutions.add(plan);
			}
		}
		return solutions;
	}
	
	public static boolean contains(List<ReleasePlan> sol, ReleasePlan plan){
		for (ReleasePlan p : sol){
			if (p.getBusinessValue() == plan.getBusinessValue()){
				return true;
			}
		}
		return false;
	}
	
	

}
