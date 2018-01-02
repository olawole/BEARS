package cs.ucl.ac.uk.barp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Release {
	
	private List<WorkItem> wItems;
	
	//private double capacity;

	public Release() {
		wItems = new ArrayList<WorkItem>();
		
	}
	
	public boolean isEmpty(){
		return wItems.isEmpty();
	}

	public List<WorkItem> getwItems() {
		return wItems;
	}

	public void setwItems(List<WorkItem> wItems) {
		this.wItems = wItems;
	}
	
	public void sortByPriority(){
		for (int i = 1; i < wItems.size(); i++){
			Double index = wItems.get(i).getPriority();
			int j = i;
			while (j > 0 && wItems.get(j-1).getPriority() < index){
				Collections.swap(wItems, j, j-1);
				j--;
			}
		}
	}
	
	public  void addItemToRelease(WorkItem wItem) {
        if (wItems.contains(wItem.getItemId())) {
            throw new IllegalArgumentException("This Work Item already exists in the release "
                    + wItem);
        }
        wItems.add(wItem);
    }
	
	public String toString(){
		String output = "";
		for (WorkItem wItem : wItems){
			output += (output == "") ? wItem.getItemId() : "," + wItem.getItemId();
		}
		return output;
	}

//	public double getCapacity() {
//		return capacity;
//	}
//
//	public void setCapacity(double capacity) {
//		this.capacity = capacity;
//	}

}
