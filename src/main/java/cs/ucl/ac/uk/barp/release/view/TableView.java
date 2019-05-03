package cs.ucl.ac.uk.barp.release.view;

import cs.ucl.ac.uk.barp.release.OptimalSolutions;

public class TableView extends View {
	
	private int noOfReleases;

	public TableView(OptimalSolutions solutions, int noReleases) {
		this.solutions = solutions;
		this.solutions.attachView(this);
		this.noOfReleases = noReleases;
	}

	@Override
	public void update() {
		TableGenerator generator = new TableGenerator(solutions.getSolutions(), noOfReleases);
		generator.generateLatexTable();
		generator.generateCSVTable();
	}

}
