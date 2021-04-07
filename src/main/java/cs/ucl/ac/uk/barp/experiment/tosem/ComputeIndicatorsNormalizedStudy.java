package cs.ucl.ac.uk.barp.experiment.tosem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

public class ComputeIndicatorsNormalizedStudy {
	
//	final static List<GenericIndicator<IntegerSolution>> indicators = Arrays.asList(
//			new PISAHypervolume<IntegerSolution>(),
//			new Epsilon<IntegerSolution>());
	final String referencePareto = "empirical_study/pareto_fronts/";
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

	public ComputeIndicatorsNormalizedStudy() {

	}
	
	public static void main(String[] args) throws FileNotFoundException{
		ComputeIndicatorsNormalizedStudy experiment = new ComputeIndicatorsNormalizedStudy();
		PISAHypervolume<IntegerSolution> indicator = new PISAHypervolume<>();
//		for (GenericIndicator<IntegerSolution> indicator : indicators){
		for (String method : experiment.AlgorithmDirectory){
			String methodDir = experiment.baseDir + "/" + method;
			
			for (int probId = 0; probId < experiment.problemList.length; probId++){
				String problemDir = methodDir + "/" + experiment.problemList[probId];
		        
		        String qualityIndicatorFile = problemDir + "/" + indicator.getName();
		        resetFile(qualityIndicatorFile);
		        String reference = experiment.referencePareto + experiment.problemList[probId] + ".rf";
//		        indicator.setReferenceParetoFront(normalizedReferenceFront);
		        Front referenceFront = new ArrayFront(reference);
		        double[] bestObj = FrontUtils.getMinimumValues(referenceFront);
		        double[] problemHv = new double[INDEPENDENT_RUNS];
		        for (int i = 0; i < INDEPENDENT_RUNS; i++){
		        	String frontFileName = problemDir + "/" +
		                    "FUN" + i + ".tsv";
		        	Front front = new ArrayFront(frontFileName) ;
//		        	Front normalizedFront = frontNormalizer.normalize(front) ;
		        	double[][] arrayFront = FrontUtils.convertFrontToArray(front);
		        	swap(arrayFront);
		        	maximNormal(arrayFront, bestObj);
		        	Double indicatorValue = (Double)indicator.calculateHypervolume(arrayFront, arrayFront[0].length, 2);
		        	problemHv[i] = indicatorValue;
		            //JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;
		            
		            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile) ;
		        }
		        problemHv = StatUtil.sort(problemHv);
		        double median = (problemHv[14] + problemHv[15]) / 2;
		        System.out.println(method + "\t" + experiment.problemList[probId] + "\t" + median);
			}
		}
//		}
		
	}
	
	private static void maximNormal(double[][] array, double[] bestObj){
		for(int i = 0; i < array.length; i++){
			array[i][1] = array[i][1] / bestObj[0];
			array[i][0] = (1 - array[i][0]) / (1 - bestObj[1]);
		}
	}
	
	private static void swap(double[][] array){
		for(int i = 0; i < array.length; i++){
			double temp = array[i][0];
			array[i][0] = array[i][1];
			array[i][1] = temp;
		}
	}
	
	private static void writeQualityIndicatorValueToFile(Double indicatorValue, String qualityIndicatorFile) {
	    FileWriter os;
	    try {
	      os = new FileWriter(qualityIndicatorFile, true);
	      os.write("" + indicatorValue + "\n");
	      os.close();
	    } catch (IOException ex) {
	      throw new JMetalException("Error writing indicator file" + ex) ;
	    }
	  }
	
	/**
	   * Deletes a file or directory if it does exist
	   * @param file
	   */
	  private static void resetFile(String file) {
	    File f = new File(file);
	    if (f.exists()) {
	      JMetalLogger.logger.info("File " + file + " exist.");

	      if (f.isDirectory()) {
	        JMetalLogger.logger.info("File " + file + " is a directory. Deleting directory.");
	        if (f.delete()) {
	          JMetalLogger.logger.info("Directory successfully deleted.");
	        } else {
	          JMetalLogger.logger.info("Error deleting directory.");
	        }
	      } else {
	        JMetalLogger.logger.info("File " + file + " is a file. Deleting file.");
	        if (f.delete()) {
	          JMetalLogger.logger.info("File succesfully deleted.");
	        } else {
	          JMetalLogger.logger.info("Error deleting file.");
	        }
	      }
	    } else {
	      JMetalLogger.logger.info("File " + file + " does NOT exist.");
	    }
	  }

}
