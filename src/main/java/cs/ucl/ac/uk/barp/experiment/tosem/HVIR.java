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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;

public class HVIR {
	
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

	public HVIR() {
	}
	
	public static void main(String[] args) throws IOException{
		HVIR experiment = new HVIR();
		//Map<String, Double> dom = new HashMap<>();
		for (String method : experiment.methodDirectory){
			String methodDir = experiment.baseDir + "/" + method;
			Map<String, Double[]> result = new HashMap<>();
			List<Double> allRatio = new ArrayList<>();
			for (int probId = 0; probId < experiment.problemList.length; probId++){
				String problemDir = methodDir + "/" + experiment.problemList[probId];
				String bearsHvFile = experiment.baseDir + "/BEARS/" + experiment.problemList[probId] +"/HV";
		        Path hvFileOther = Paths.get(problemDir + "/HV");
		        Path hvFileBears = Paths.get(bearsHvFile);
		        if (hvFileBears == null) {
		            throw new JMetalException("Indicator file BearsHV doesn't exist") ;
		          }
		        if (hvFileOther == null) {
		            throw new JMetalException("Indicator file HV doesn't exist") ;
		          }
		        List<String> fileArray;
		        List<String> bearsFileArray;
		        fileArray = Files.readAllLines(hvFileOther, StandardCharsets.UTF_8);
		        bearsFileArray = Files.readAllLines(hvFileBears, StandardCharsets.UTF_8);
		        double[] values = new double[fileArray.size()];
		        double[] bearsValues = new double[bearsFileArray.size()];
		        for(int i = 0; i < fileArray.size(); i++){
		        	double value = Double.parseDouble(fileArray.get(i));
		        	double bearsValue = Double.parseDouble(bearsFileArray.get(i));
		        	values[i] = value;
		        	bearsValues[i] = bearsValue;
		        }
		        double[] impRatio = new double[bearsValues.length];
		        for(int j = 0; j < bearsValues.length; j++){
		        	impRatio[j] = bearsValues[j] / values[j];
		        }
		        allRatio.addAll(DoubleStream.of(impRatio).boxed().collect(Collectors.toList()));
		        double mean = StatUtil.mean(impRatio);
		        double fifthPercentile = StatUtil.min(impRatio); //.percentile(impRatio, 5.0);
		        double nintyFifthPercentile = StatUtil.max(impRatio); //.percentile(impRatio, 95.0);
		        result.put(experiment.problemList[probId], new Double[]{mean, fifthPercentile, nintyFifthPercentile});
		        System.out.println(method  + "\t" + experiment.problemList[probId] + "\t"
		        		+ StatUtil.round(mean,3) + "\t[" + StatUtil.round(fifthPercentile,3) + "," + StatUtil.round(nintyFifthPercentile,3) + "]");
			}
			writeToFile(result, methodDir);
			double[] overallRatio = allRatio.stream().mapToDouble(Double::doubleValue).toArray();
			double overallMean = StatUtil.mean(overallRatio);
	        double overallFifthPercentile = StatUtil.percentile(overallRatio, 5);
	        double overallNintyFifthPercentile = StatUtil.percentile(overallRatio, 95);
	        
	        System.out.println(method  + "\t" 
	        		+ StatUtil.round(overallMean,3) + "\t[" + StatUtil.round(overallFifthPercentile,3) + "," + StatUtil.round(overallNintyFifthPercentile,3) + "]");
		}
		
//		}
		
	}
	
	private static void writeToFile(Map<String, Double[]> domSet, String dir) {
	    FileWriter os;
	    try {
	      os = new FileWriter(dir + "/hvir.tsv");
	      for(Map.Entry<String, Double[]> entry: domSet.entrySet()){
	    	  os.write(entry.getKey() + "\t" + StatUtil.round(entry.getValue()[0],3) + "\t[" + 
	      StatUtil.round(entry.getValue()[1],3) + "," + StatUtil.round(entry.getValue()[2],3) + "]" + "\n");
	      }	      
	      os.close();
	    } catch (IOException ex) {
	      throw new JMetalException("Error writing indicator file" + ex) ;
	    }
	  }
	

}
