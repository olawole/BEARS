package cs.ucl.ac.uk.barp.release.view;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import cs.ucl.ac.uk.barp.model.ReleasePlan;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;

public class TableGenerator {
	
	int noReleases;
	List<ReleasePlan> rPlans;
	static final String OPEN_TAG = "\\begin{tabular}";
	static final String CLOSE_TAG = "\\end{tabular}";
	static final String HORIZONTAL_LINE = "\t\\hline\n";
	static final String COL_SEP = " & ";
	static final String COMMA_SEP = " , ";

	public TableGenerator(List<ReleasePlan> plans, int numberOfReleases) {
		noReleases = numberOfReleases;
		rPlans = plans;
	}
	
	public void generateLatexTable(){
		String latexString = OPEN_TAG;
		String columnStructure = "{|c|c|c|c|";
		for (int i = 0; i < noReleases; i++){
			columnStructure += "c|";
		}
		latexString += columnStructure + "}\n";
		latexString += HORIZONTAL_LINE;
		String heading = "S/N" + COL_SEP;
		for (int i = 0; i < noReleases; i++){
			heading += "Release " + (i+1) + COL_SEP;
		}
		heading += "EV('000\\pounds)" + COL_SEP + "Risk(\\%)" + COL_SEP + "LatenessProb";
		latexString += heading + "\\\\ \n";
		latexString += HORIZONTAL_LINE;
		int counter = 0;
		for (ReleasePlan plan : rPlans){
			String row = ++counter + COL_SEP;
			for (int i = 1; i <= noReleases; i++){
				if (plan.getPlan().get(i) != null){
					row += plan.getPlan().get(i).toString() + COL_SEP;
				}
				else {
					row += "" + COL_SEP;
				}
			}
			row += StatUtil.round(plan.getBusinessValue(), 2) + COL_SEP + 
					StatUtil.round(plan.getInvestmentRisk()*100, 2) + COL_SEP +
					StatUtil.round(plan.getExpectedPunctuality(), 3);
			latexString += row + "\\\\ \n";
			latexString += HORIZONTAL_LINE;
		}
		latexString += CLOSE_TAG;
		try {
			FileWriter output = new FileWriter("table.tex");
			output.write(latexString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void generateCSVTable(){
		String csvString = "";
		String heading = "S/N" + COMMA_SEP;
		for (int i = 0; i < noReleases; i++){
			heading += "Release " + (i+1) + COMMA_SEP;
		}
		heading += "EV('000£)" + COMMA_SEP + "Risk(%)";
		csvString += heading + "\n";
		int counter = 0;
		for (ReleasePlan plan : rPlans){
			String row = ++counter + COMMA_SEP;
			for (int i = 1; i <= plan.getPlan().size(); i++){
				String s = plan.getPlan().get(i).toString();
				row += "\""+ s.replace(",", "->")+ "\"" + COMMA_SEP;
			}
			row += StatUtil.round(plan.getBusinessValue(), 2) + COMMA_SEP + 
					StatUtil.round(plan.getInvestmentRisk()*100, 2);
			csvString += row + "\n";
		}
		try {
			FileWriter output = new FileWriter("table.csv");
			output.write(csvString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
