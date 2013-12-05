package graphs.spanningTree;

import graphs.MyEdge;
import graphs.MyGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Partition {
	private Set<MyEdge> included;
	private Set<MyEdge> excluded;

	public Set<MyEdge> getIncluded() {
		return included;
	}

	public Set<MyEdge> getExcluded() {
		return excluded;
	}


	public Partition(Set<MyEdge> included, Set<MyEdge> excluded) {
		this.included = new HashSet<MyEdge>(included);
		this.excluded = new HashSet<MyEdge>(excluded);
	}

	public Partition() {
		included = new HashSet<MyEdge>();
		excluded = new HashSet<MyEdge>();
	}

	//MODIFICATA
	public List<Partition> subPartitions(MyGraph tree) {
		List<Partition> result = new ArrayList<Partition>();
		
		for (int i = 0; i < tree.getEdges().size(); i++) {
			if (!included.contains(tree.getEdges().get(i))) {

				Partition p = new Partition(this.included, this.excluded);
				for (int j = 0; j < i; ++j) {
					MyEdge e = tree.getEdges().get(j);
					p.included.add(e);
				}
				p.excluded.add(tree.getEdges().get(i));
				result.add(p);
			}
		}
		return result;
	}

}
