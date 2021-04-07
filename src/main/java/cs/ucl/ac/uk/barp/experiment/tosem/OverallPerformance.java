package cs.ucl.ac.uk.barp.experiment.tosem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import cs.ucl.ac.uk.barp.project.utilities.StatUtil;

public class OverallPerformance {
	
//	final static List<GenericIndicator<IntegerSolution>> indicators = Arrays.asList(
//			new PISAHypervolume<IntegerSolution>(),
//			new Epsilon<IntegerSolution>());
	//final String referencePareto = "empirical_study/pareto_fronts/";
	final String baseDir = "empirical_study/TestExperiment/data";
	final String[] AlgorithmDirectory = {"MOCELL", "NSGAII","RS", "SPEA2"};
	final String[] problemList = {
			"COUNCIL-1", "COUNCIL-2", "COUNCIL-3", "COUNCIL-4", 
			"RALIC-1","RALIC-2", "RALIC-3", "RALIC-4",
			"RPlanner-1", "RPlanner-2", "RPlanner-3", "RPlanner-4",
			"Word-1", "Word-2", "Word-3", "Word-4",
			"Synthetic30-1", "Synthetic30-2", "Synthetic30-3", "Synthetic30-4",
			"Synthetic50-1", "Synthetic50-2", "Synthetic50-3", "Synthetic50-4",
			"Synthetic100-1", "Synthetic100-2", "Synthetic100-3", "Synthetic100-4",
			"Synthetic200-1", "Synthetic200-2", "Synthetic200-3", "Synthetic200-4"
			};

	final static int INDEPENDENT_RUNS = 30;	

	public OverallPerformance() {

	}
	
	public static void main(String[] args) throws IOException{
		OverallPerformance experiment = new OverallPerformance();
//		for (GenericIndicator<IntegerSolution> indicator : indicators){
		for (String method : experiment.AlgorithmDirectory){
			String methodDir = experiment.baseDir + "/" + method;
			List<Double> overall = new ArrayList<>();
			for (int probId = 0; probId < experiment.problemList.length; probId++){
				String problemDir = methodDir + "/" + experiment.problemList[probId];
		        
		        String qualityIndicatorFile = problemDir + "/IGD+";
		        Path indicatorFile = Paths.get(qualityIndicatorFile);
		        if(indicatorFile == null){
		        	throw new JMetalException("File not found");
		        }
		        List<String> data = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);
		        double[] problemHv = new double[data.size()];
		        for(int i = 0; i < problemHv.length; i++) {
		        	problemHv[i] = Double.parseDouble(data.get(i));
		        }
		        overall.addAll(DoubleStream.of(problemHv).boxed().collect(Collectors.toList()));
		        problemHv = StatUtil.sort(problemHv);
		        double median = (problemHv[14] + problemHv[15]) / 2;
		        System.out.println(method + "\t" + experiment.problemList[probId] + "\t" + median);
			}
			double[] overAll = overall.stream().mapToDouble(Double::doubleValue).toArray();
			overAll = StatUtil.sort(overAll);
			double median = (overAll[119] + overAll[120]) / 2;
			System.out.println(method + "\tOverall\t" + median);
		}
//		}
		
	}

}
