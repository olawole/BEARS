package cs.ucl.ac.uk.barp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.IntegerSolution;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import cs.ucl.ac.uk.barp.experiment.ComputeIndicators;
import cs.ucl.ac.uk.barp.experiment.Experiment;
import cs.ucl.ac.uk.barp.model.Project;
import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.optimization.InformationValueAnalyser;
import cs.ucl.ac.uk.barp.optimization.MCSimulator;
import cs.ucl.ac.uk.barp.optimization.Optimization;
import cs.ucl.ac.uk.barp.project.utilities.ObjectiveValueUtil;
import cs.ucl.ac.uk.barp.project.utilities.ParetoOptimalUtil;
import cs.ucl.ac.uk.barp.project.utilities.ProjectParser;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.OptimalSolutions;
import cs.ucl.ac.uk.barp.release.view.BarChartView;
import cs.ucl.ac.uk.barp.release.view.RoadMapView;
import cs.ucl.ac.uk.barp.release.view.Scatter;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsRigidVBears;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsRigidVBearsRigidObj;
import cs.ucl.ac.uk.barp.release.view.ScatterBearsVSAll;
import cs.ucl.ac.uk.barp.release.view.ScatterPlotView;
import cs.ucl.ac.uk.barp.release.view.TableView;
import cs.ucl.ac.uk.evolve.EvolveProject;
import cs.ucl.ac.uk.srprisk.SRPRiskProject;
public class BearsTool {

	public BearsTool() {
		// TODO Auto-generated constructor stub
	}
	@Parameter(names = "--help", description = "Help")
	private boolean help = false;
	
	@Parameter(names = "--projName", description = "Name of the project ")
	public String projName = "council";
	
	@Parameter(names = "--output", description = "Output folder where the results are saved. Input <file path>.  ")
	public String outputFolder = "";
	
	@Parameter(names = "--data", description = "The file that contains list of features and planning parameters ")
	public String data = "input_data/councilNew2.csv";

	@Parameter(names = "--budget", description = "Available budget per release")
	private String budget = null;
	
	@Parameter(names = "--capacity", description = "Available effort per release")
	public String capacity = null;
	
	@Parameter(names = "--investment", description = "Number of investment horizon")
	public Integer investment = 10;
	
	@Parameter(names = "--numberOfReleases", description = "Number of investment horizon")
	public Integer numberOfRelease = 0;
	
	@Parameter(names ="--alg", description = "Type of optimization algorithm, SPEA2 by default. Others are NSGAII, MOCell, Random")
	public String alg = "SPEA2";
	
	@Parameter(names ="--interest", description = "Discount rate per period")
	public String interest = "2";
	
	@Parameter(names ="--scatter", description = "Displays the scatter plot.", arity=1)
	public boolean scatter = true;
	
	@Parameter(names ="--table", description = "Generates Latex and CSV tables", arity=1)
	public boolean table = false;
	
	@Parameter(names ="--roadmap", description = "Generate roadmap visualization with dot", arity=1)
	public boolean roadmap = false;
	
	@Parameter(names ="--bar", description = "Displays the bar plot containing frequency of features in different releases.", arity=1)
	public boolean bar = false;
	
	@Parameter(names ="--valueAnalyzer", description = "Displays the pareto plots.", arity=1)
	public boolean valueAnalyzer = false;
	
	@Parameter(names ="--compare", description = "Compare BEARS output with other methods", arity=1)
	public String compareWith = "";
	
	@Parameter(names ="--exp", description = "Set to true to run experiment", arity=1)
	public boolean exp = false;
	
	
	public static void main(String[] args) throws Exception {
		BearsTool command = new BearsTool();
		
		JCommander jcommander = new JCommander(command, args);
		jcommander.setProgramName("BEARS");
		if (command.help) {
        	jcommander.usage();
            return;
        }
        command.run(command);

	}
	
	private void run(BearsTool command) throws Exception {
		if (exp) {
			runExperiment();
		}
		else {
			runBears();
		}
	}
	
	private void runExperiment() throws IOException {
		Experiment.run();
		ComputeIndicators.run();	
	}

	private void runBears() throws Exception {
		if ((data != null) && (capacity != null)){
			Project project = loadProject();
			OptimalSolutions optimal = runOptimisation(project);
			switch (compareWith) {
			case "evolve": {
				EvolveProject evolve = ObjectiveValueUtil.convertProjectToEvolve(project, true);
				List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolve);
				List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsObjectives(evolveSolutions, project);
				Scatter s = new Scatter("BEARS vs EVOLVE", optimal.getSolutions(), evolvePlan);
				s.drawPlot();
				generateCSVTable(optimal.getSolutions(), "Bears");
				generateCSVTable(evolvePlan, "Evolve");
			} break;
			case "bearsR": {
				Optimization bearsRigid = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);
				List<IntegerSolution> bearsRigidSolutions = bearsRigid.run();
				List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBearsObjectives(bearsRigidSolutions, project);
				List<ReleasePlan> rigidPlansRigidObj = ObjectiveValueUtil.computeBears1Objective(bearsRigidSolutions, project);
				List<ReleasePlan> bearsPlansRigidObj = new ArrayList<>(optimal.getSolutions());
				ObjectiveValueUtil.computePlanBears1Objective(bearsPlansRigidObj, project);
				
				ScatterBearsRigidVBearsRigidObj plot1 = new ScatterBearsRigidVBearsRigidObj("Flexible Vs Rigid Bears with Rigid Objectives", bearsPlansRigidObj, rigidPlansRigidObj);
				ScatterBearsRigidVBears plot = new ScatterBearsRigidVBears("Flexible Vs Rigid Bears", optimal.getSolutions(), rigidPlans);
				plot1.drawPlot();
				plot.drawPlot(); }
				break;
			case "all": {
				EvolveProject evolve = ObjectiveValueUtil.convertProjectToEvolve(project, true);
				EvolveProject bears0 = ObjectiveValueUtil.convertProjectToEvolve(project, false);
				SRPRiskProject srpProject = ObjectiveValueUtil.convertProjectToSRPRisk(project, true);
				Optimization optimisation1 = new Optimization(project, "Bears1", ExperimentConfiguration.algorithmType);
				List<IntegerSolution> evolveSolutions = ObjectiveValueUtil.runEvolve(evolve);
				List<IntegerSolution> bears0Solutions = ObjectiveValueUtil.runEvolve(bears0);
				List<IntegerSolution> srpSolutions = ObjectiveValueUtil.runSRPRisk(srpProject);
				List<IntegerSolution> bears1Solutions = optimisation1.run();
				List<ReleasePlan> evolvePlan = ObjectiveValueUtil.computeBearsObjectives(evolveSolutions, project);
				List<ReleasePlan> bears0Plan = ObjectiveValueUtil.computeBearsObjectives(bears0Solutions, project);
				List<ReleasePlan> srpPlan = ObjectiveValueUtil.computeBearsObjectives(srpSolutions, project);
				List<ReleasePlan> rigidPlans = ObjectiveValueUtil.computeBearsObjectives(bears1Solutions, project);
				
				evolvePlan = ParetoOptimalUtil.removeDuplicate(evolvePlan);
				bears0Plan = ParetoOptimalUtil.removeDuplicate(bears0Plan);
				srpPlan = ParetoOptimalUtil.removeDuplicate(srpPlan);
				rigidPlans = ParetoOptimalUtil.removeDuplicate(rigidPlans);
				
				ScatterBearsVSAll s = new ScatterBearsVSAll("BEARS vs All using Bears flexible Objective", optimal.getSolutions(), evolvePlan, srpPlan,
						bears0Plan, rigidPlans);
				
				s.drawPlot(); } break;
				
			default: {
				if (valueAnalyzer){
					InformationValueAnalyser.computeInformationValue(optimal, project.getWorkItems());
					optimal.printEvppi();
				}
			}
				
			}
			
		}
	}
	
	Project loadProject() throws Exception{
		Project project = ProjectParser.parseCSVToProject(data, ExperimentConfiguration.distributionType);
		project.checkTransitiveDependency();
		double[] cap = parseDouble(capacity.split(","));
		if (budget != null){
			double[] budg = parseDouble(budget.split(","));
			if (cap.length != budg.length){
				throw new Exception("Budget and Capacity must have the same dimension. The size of the array must also be equal to the number of releases");
			}
			project.setBudgetPerRelease(budg);
		}
		else {
			project.setBudgetPerRelease(new double[cap.length]);
		}
		
		project.setEffortCapacity(cap);
		
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
		
		return optimal;
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
	

}
