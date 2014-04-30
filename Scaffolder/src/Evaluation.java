public class Evaluation {
	private double cost;
	private int good;
	private int errors;
	private int nullLabel;
	private int orientationConflicts;
	private int conflictsGood;
	private int conflictsBad;
	private int incorrectScaffolds;

	Evaluation() {
		good =0;
		errors= 0;
		nullLabel=0;
		setOrientationConflicts(0);
		setConflictsBad(0);
		setConflictsGood(0);
		setIncorrectScaffolds(0);
	}
	public int getIncorrectScaffolds() {
		return incorrectScaffolds;
	}

	public void setIncorrectScaffolds(int incorrectScaffolds) {
		this.incorrectScaffolds = incorrectScaffolds;
	}



	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public int getNullLabel() {
		return nullLabel;
	}

	public void setNullLabel(int nullLabel) {
		this.nullLabel = nullLabel;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getOrientationConflicts() {
		return orientationConflicts;
	}

	public void setOrientationConflicts(int orientationConflicts) {
		this.orientationConflicts = orientationConflicts;
	}

	public int getConflictsBad() {
		return conflictsBad;
	}

	public void setConflictsBad(int conflictsBad) {
		this.conflictsBad = conflictsBad;
	}

	public int getConflictsGood() {
		return conflictsGood;
	}

	public void setConflictsGood(int conflictsGood) {
		this.conflictsGood = conflictsGood;
	}

}
