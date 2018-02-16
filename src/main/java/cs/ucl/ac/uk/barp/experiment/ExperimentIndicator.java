package cs.ucl.ac.uk.barp.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

public class ExperimentIndicator {
	
	final static List<GenericIndicator<IntegerSolution>> indicators = Arrays.asList(
			new PISAHypervolume<IntegerSolution>(),
			new Epsilon<IntegerSolution>());
	final String referencePareto = "pareto_front";
	final String baseDir = "result";
	final String[] methodDirectory = {"bears", "evolve"};
	final String[] problems = {"b30", "b50", "b100", "b200"};
	final int[] noReleases = {1,2,3,4,5};
	final static int INDEPENDENT_RUNS = 5;
	String[] problemList;
	

	public ExperimentIndicator() {
		problemList = new String[20];
		int k = 0;
		for(int i = 0; i < problems.length; i++){
			for (int j = 0; j < noReleases.length; j ++){
				problemList[k++] = problems[i] + "_" + noReleases[j];
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		ExperimentIndicator experiment = new ExperimentIndicator();
		for (GenericIndicator<IntegerSolution> indicator : indicators){
		for (String method : experiment.methodDirectory){
			String methodDir = experiment.baseDir + "/" + method;
			
			for (int probId = 0; probId < experiment.problemList.length; probId++){
				String problemDir = methodDir + "/" + experiment.problemList[probId];
				
				String referenceFrontName = experiment.referencePareto + "/" 
						+ experiment.problemList[probId] + ".rf";
				JMetalLogger.logger.info("RF: " + referenceFrontName);
				
				Front referenceFront = new ArrayFront(referenceFrontName);
				FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
		        Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
		        
		        String qualityIndicatorFile = problemDir + "/" + indicator.getName();
		        resetFile(qualityIndicatorFile);
		        
		        indicator.setReferenceParetoFront(normalizedReferenceFront);
		        
		        for (int i = 0; i < INDEPENDENT_RUNS; i++){
		        	String frontFileName = problemDir + "/" +
		                    "FUN" + i + ".tsv";
		        	Front front = new ArrayFront(frontFileName) ;
		        	Front normalizedFront = frontNormalizer.normalize(front) ;
		        	List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront) ;
		        	Double indicatorValue = (Double)indicator.evaluate((List)normalizedPopulation);
		            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;
		            
		            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile) ;
		        }
			}
		}
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
