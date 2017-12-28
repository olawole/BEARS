package cs.ucl.ac.uk.barp.release.view;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cs.ucl.ac.uk.barp.project.Project;
import cs.ucl.ac.uk.barp.project.utilities.StatUtil;
import cs.ucl.ac.uk.barp.release.Release;
import cs.ucl.ac.uk.barp.release.ReleasePlan;
import cs.ucl.ac.uk.barp.workitem.WorkItem;

public class RoadMap {

	public RoadMap(List<ReleasePlan> optimal, int releases) {
		this.optimal = optimal;
		noOfReleases = releases;
	}

	List<ReleasePlan> optimal;
	int noOfReleases;

	public Integer getKeyFromValue(HashMap<Integer, String> hm, String value) {
		for (Integer key : hm.keySet()) {
			if (hm.get(key).equals(value)) {
				return key;
			}
		}
		return null;
	}

	public void writeDot(String filename) {
		HashMap<Integer, String> rank = new HashMap<Integer, String>();
		String all = "";
		String dotString = "digraph G { \n";
		dotString += "\trankdir=LR\n";
		dotString += "\troot[shape=point]\n";
		int num = 0;
		// List<String> nodes = new ArrayList<String>();
		for (ReleasePlan p : optimal) {
			String lastNode = "";
//			if (++num > 20) {
//				break;
//			}
			HashMap<Integer, String> solution = planToString(p);
			String label = "";

			// System.out.println(solution.toString());
			Iterator<String> it = solution.values().iterator();
			if (it.hasNext()) {
				String object = it.next();
				label += object;
				if (dotString.indexOf("root -> \"" + label + "\"") < 0) {
					dotString += "\t\"" + label + "\"[shape = box]\n";
					dotString += "\troot -> \"" + label + "\"[label=\"" + object + "\"]\n";
					Integer key = getKeyFromValue(solution, object);
					if (!all.contains("\"" + label + "\"")) {
						if (rank.containsKey(key)) {
							String old = rank.get(key);
							rank.put(key, old + " \"" + label + "\"");

						} else {
							rank.put(key, "\"" + label + "\"");
						}
						all += "\"" + label + "\",";
					}
				}

				while (it.hasNext()) {
					String current = it.next();
					label += "|\\n" + current;
					if (dotString.indexOf("\"" + label + "\"[shape = box, style=rounded]\n") < 0) {
						dotString += "\t\"" + label + "\"[shape = box, style=rounded]\n";
						Integer key = getKeyFromValue(solution, current);
						if (!all.contains("\"" + label + "\"")) {
							if (rank.containsKey(key)) {
								String old = rank.get(key);
								rank.put(key, old + " \"" + label + "\"");
							} else {
								rank.put(key, "\"" + label + "\"");
							}
							all += "\"" + label + "\",";
						}
					}

					String str = "\t\"" + object + "\"" + "->" + "\"" + label + "\"";
					if (dotString.indexOf(str) < 0) {
						dotString += str + "[label=\"" + current + "\"]\n";
					}

					object = label;
					lastNode = label;
				}
				String str = "\"" + lastNode + "\"->\"ExpectedValue";
				if (dotString.indexOf(str) < 0) {
					Double value = StatUtil.round(p.getBusinessValue(), 2);
					Double puctuality = StatUtil.round(p.getExpectedPunctuality(), 2);
					String objNode = "ExpectedValue = £" + value + "\\nPunctuality = " + puctuality + "%";
					dotString += "\t\"" + lastNode + "\"->\"" + objNode + "\"\n";
				}
				
			}
			

		}
		for (Integer k : rank.keySet()) {
			dotString += "\t{ rank=same " + rank.get(k) + " }\n";
		}
		dotString += "}";
		try {
			FileWriter output = new FileWriter(filename + ".dot");
			output.write(dotString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String sort(String s) {
		String l = "";
		for (String c : s.split("")) {
			if (c.equals(",") || c.equals("|")) {
				continue;
			} else {
				l += c;
			}
		}
		char[] ar = l.toCharArray();
		Arrays.sort(ar);
		String sorted = String.valueOf(ar);
		return sorted;
	}

	public HashMap<Integer, HashMap<String, Integer>> freq() {
		HashMap<Integer, HashMap<String, Integer>> featureFrequency = new HashMap<Integer, HashMap<String, Integer>>();
		for (int i = 1; i <= noOfReleases; i++) {
			HashMap<String, Integer> freqPlan = new HashMap<String, Integer>();
			for (ReleasePlan p : optimal) {
				HashMap<Integer, String> plan = planToString(p);
				if (plan.containsKey(i)) {
					String features[] = plan.get(i).split(",");
					for (String feature : features) {
						if (freqPlan.containsKey(feature)) {
							int oldValue = freqPlan.get(feature);
							freqPlan.put(feature, ++oldValue);
						} else {
							freqPlan.put(feature, 1);
						}
					}
				}
			}
			featureFrequency.put(i, freqPlan);
		}
		return featureFrequency;
	}

	public void freqToFile(Project project, String filename) {
		HashMap<Integer, HashMap<String, Integer>> releaseFrequency = freq();
		int totalSolution = optimal.size();
		String output = "";
		for (int j = 1; j <= noOfReleases; j++) {
			output += "\t" + j;
		}
		output += "\n";
		for (String f : project.getWorkItemVector()) {
			output += f + "\t";
			for (int k = 1; k <= noOfReleases; k++) {
				if (releaseFrequency.containsKey(k)) {
					HashMap<String, Integer> relfeq = releaseFrequency.get(k);
					if (relfeq != null && relfeq.containsKey(f)) {
						int percent = (int) (relfeq.get(f) * 100 / totalSolution);
						output += percent + "\t";
					} else {
						output += "0\t";
					}
				}

			}
			output += "\n";
		}
		try {
			FileWriter outputFile = new FileWriter(filename + ".tsv");
			outputFile.write(output);
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<Integer, String> planToString(ReleasePlan plan) {
		HashMap<Integer, String> planString = new HashMap<Integer, String>();
		for (Map.Entry<Integer, Release> entry : plan.getPlan().entrySet()) {
			if (entry.getValue().isEmpty())
				continue;
			String s = "";
			for (WorkItem wItem : entry.getValue().getwItems()) {
				s += s.equals("") ? wItem.getItemId() : "," + wItem.getItemId();
			}
			planString.put(entry.getKey(), s);
		}
		return planString;
	}

}
