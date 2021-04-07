package cs.ucl.ac.uk.barp.experiment.tosem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.util.PointSolution;

import cs.ucl.ac.uk.barp.model.ReleasePlan;

public class BetterThanRelation {
	
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

	public BetterThanRelation() {
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		BetterThanRelation experiment = new BetterThanRelation();
		Map<String, Double> dom = new HashMap<>();
		for (String method : experiment.methodDirectory){
			String methodDir = experiment.baseDir + "/" + method;
			for (int probId = 0; probId < experiment.problemList.length; probId++){
				String problemDir = methodDir + "/" + experiment.problemList[probId];
				int domCount = 0;
		        for (int i = 0; i < INDEPENDENT_RUNS; i++){
		        	String bearsName = experiment.baseDir + "/BEARS/" + experiment.problemList[probId] +"/FUN" + i + ".tsv";
		        	Front bearsFront = new ArrayFront(bearsName) ;
		        	String otherMethodName = problemDir + "/" +
		                    "FUN" + i + ".tsv";
	        		Front otherFront = new ArrayFront(otherMethodName);
	        		if(dominates(bearsFront, otherFront)){
	        			domCount++;
	        		}
//		        	for(int j = 0; j < INDEPENDENT_RUNS; j++){
//		        		String otherMethodName = problemDir + "/" +
//			                    "FUN" + j + ".tsv";
//		        		Front otherFront = new ArrayFront(otherMethodName);
//		        		if(dominates(bearsFront, otherFront)){
//		        			domCount++;
//		        		}
//		        	}
//		        	Front normalizedFront = frontNormalizer.normalize(front) ;
		        	//double[][] arrayFront = FrontUtils.convertFrontToArray(front);
		            //JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;
		        }
		        String key = method  + "_" + experiment.problemList[probId];
		        dom.put(key, Math.round(domCount) / 30.0 * 100);
			}
		}
		writeToFile(dom);
//		}
		
	}
	
	private static boolean dominates(Front bearsFront, Front otherFront){
		boolean dominated = false;
		for(int i = 0; i < otherFront.getNumberOfPoints(); i++){
			Point q = otherFront.getPoint(i);
			dominated = false;
			for(int j = 0; j < bearsFront.getNumberOfPoints(); j++){
				Point p = bearsFront.getPoint(j);
				if(dominates(p,q)){
					dominated = true;
					break;
				}
			}
			if(!dominated){
				return dominated;
			}
		}
		return dominated;
	}
	
	private static boolean dominates(Point p, Point q) {
		return (all(p,q) && any(p,q));
	}
	
	private static boolean all(Point p, Point q) {
		boolean value = false;
		if (p.getDimensionValue(0) <= q.getDimensionValue(0)
				&& p.getDimensionValue(1) <= q.getDimensionValue(1)) {
			value = true;
		}
		return value;
	}

	private static boolean any(Point p, Point q) {
		boolean value = false;
		if (p.getDimensionValue(0) < q.getDimensionValue(0)
				|| p.getDimensionValue(1) < q.getDimensionValue(1)) {
			value = true;
		}
		return value;
	}

	
	private static void writeToFile(Map<String, Double> domSet) {
	    FileWriter os;
	    try {
	      os = new FileWriter("DOM.tsv");
	      for(Map.Entry<String, Double> entry: domSet.entrySet()){
	    	  os.write("" + entry.getKey() + "\t" + entry.getValue() + "\n");
	      }	      
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
