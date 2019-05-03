package cs.ucl.ac.uk.evolve;

public class TableView {
	
	private int noOfReleases;
	Pareto solutions;

	public TableView(Pareto solutions, int noReleases) {
		this.solutions = solutions;
		this.noOfReleases = noReleases;
	}

	public void update() {
		TableGenerator generator = new TableGenerator(solutions.getSolutions(), noOfReleases);
		generator.generateLatexTable();
		generator.generateCSVTable();
	}

}
