package cs.ucl.ac.uk.barp.workitem;

import java.util.List;

import cs.ucl.ac.uk.barp.distribution.Distribution;

public interface IWorkItem {

	String getItemId();

	void setItemId(String itemId);

	List<String> getPrecursors();

	void setPrecursors(List<String> precursors);

	void addPrecursor(String item);

	Distribution getValue();

	void setValue(Distribution value);

	Distribution getEffort();

	void setEffort(Distribution effort);

}