package graphs;

public class MyEdge implements Comparable<MyEdge> {
	private String id;
	private MyNode source;
	private MyNode target;
	private double weight;
	private double lenght;
	public int[] orientations;

	public MyEdge(String id, MyNode source, MyNode target) {
		this.setId(id);
		this.weight = 0;
		this.lenght=1;
		this.source = source;
		this.target = target;
		this.orientations= new int[2];
		orientations[0]=1;
		orientations[1]=1;
	}

	public int[] getOrientations() {
		return orientations;
	}

	public void setOrientations(int[] orientations) {
		this.orientations = orientations;
	}

	public String toStringVerbose() {
		String s = id + "<" + source.getLabel() + ";" + target.getLabel() + ">"
				+ "[" + weight + "]";
		return s;
	}

	public String toString() {
		return id;
	}

	public MyNode getSource() {
		return source;
	}

	public void setSource(MyNode source) {
		this.source = source;
	}

	public MyNode getTarget() {
		return target;
	}

	public void setTarget(MyNode target) {
		this.target = target;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(MyEdge e) {
		if (weight < e.getWeight())
			return -1;
		else if (weight > e.getWeight())
			return 1;
		else
			return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyEdge other = (MyEdge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		/*
		 * if (source == null) { if (other.source != null) return false; } else
		 * if (!source.equals(other.source)) return false; if (target == null) {
		 * if (other.target != null) return false; } else if
		 * (!target.equals(other.target)) return false;
		 */
		return true;
	}

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double d) {
		this.lenght = d;
	}

	public String orientationString() {
		String sourceId = this.getSource().getId();
		String targetId = this.getTarget().getId();
		String oS=  String.valueOf(orientations[0]);
		String oT=  String.valueOf(orientations[1]);
		String s= sourceId+":"+oS+"_"+targetId+":"+oT;
		return s;
	}
}
