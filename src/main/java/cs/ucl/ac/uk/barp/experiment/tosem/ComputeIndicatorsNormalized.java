package cs.ucl.ac.uk.barp.experiment.tosem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

public class ComputeIndicatorsNormalized {
	
//	final static List<GenericIndicator<IntegerSolution>> indicators = Arrays.asList(
//			new PISAHypervolume<IntegerSolution>(),
//			new Epsilon<IntegerSolution>());
	final String referencePareto = "pareto_front/";
	final String baseDir = "Result-TSE";
	final String[] methodDirectory = {"NPV-deterministic","NPV-fixed-scope", "VP-deterministic","VP-fixed-scope"};
	final String[] problemList = {
			"COUNCIL-2", "COUNCIL-3", "COUNCIL-4", "COUNCIL-5", 
			"RALIC-2","RALIC-3", "RALIC-4", "RALIC-5",
			"RPlanner-2", "RPlanner-3", "RPlanner-4", "RPlanner-5",
			"WordProcessing-2", "WordProcessing-3", "WordProcessing-4", "WordProcessing-5",
			"Synthetic30-2", "Synthetic30-3", "Synthetic30-4", "Synthetic30-5",
			"Synthetic50-2", "Synthetic50-3", "Synthetic50-4", "Synthetic50-5",
			"Synthetic100-2", "Synthetic100-3", "Synthetic100-4", "Synthetic100-5",
			"Synthetic200-2", "Synthetic200-3", "Synthetic200-4", "Synthetic200-5"
			};

	final static int INDEPENDENT_RUNS = 30;	

	public ComputeIndicatorsNormalized() {

	}
	
	public static void main(String[] args) throws FileNotFoundException{
		ComputeIndicatorsNormalized experiment = new ComputeIndicatorsNormalized();
		PISAHypervolume<IntegerSolution> indicator = new PISAHypervolume<>();
//		for (GenericIndicator<IntegerSolution> indicator : indicators){
		for (String method : experiment.methodDirectory){
			String methodDir = experiment.baseDir + "/" + method;
			
			for (int probId = 0; probId < experiment.problemList.length; probId++){
				String problemDir = methodDir + "/" + experiment.problemList[probId];
		        
		        String qualityIndicatorFile = problemDir + "/" + indicator.getName() + "-Norm";
		        resetFile(qualityIndicatorFile);
		        String reference = experiment.referencePareto + experiment.problemList[probId] + ".rf";
//		        indicator.setReferenceParetoFront(normalizedReferenceFront);
		        Front referenceFront = new ArrayFront(reference);
		        double[] bestObj = FrontUtils.getMinimumValues(referenceFront);
		        
		        for (int i = 0; i < INDEPENDENT_RUNS; i++){
		        	String frontFileName = problemDir + "/" +
		                    "FUN" + i + ".tsv";
		        	Front front = new ArrayFront(frontFileName) ;
//		        	Front normalizedFront = frontNormalizer.normalize(front) ;
		        	double[][] arrayFront = FrontUtils.convertFrontToArray(front);
		        	swap(arrayFront);
		        	maximNormal(arrayFront, bestObj);
		        	Double indicatorValue = (Double)indicator.calculateHypervolume(arrayFront, arrayFront[0].length, 2);
		            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;
		            
		            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile) ;
		        }
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
