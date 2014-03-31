public class Evaluation {
	private double cost;
	private int good;
	private int errors;
	private int nullLabel;
	private int orientationConflicts;

	Evaluation() {
		good =0;
		errors= 0;
		nullLabel=0;
		setOrientationConflicts(0);
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

}
