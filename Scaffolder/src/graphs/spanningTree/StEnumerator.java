package graphs.spanningTree;

import java.util.PriorityQueue;

import graphs.MyGraph;

public class StEnumerator {

	private static class Solution implements Comparable<Solution> {

		public Partition partition;
		public Double cost;
		public MyGraph maxST;
		
		public Solution(Partition subPartition, MyGraph st, double c) {
			partition = subPartition;
			maxST = st;
			cost = c;
		}

		@Override
		public int compareTo(Solution o) {
			if (this.cost < o.cost) {
				return 1;
			} else if (this.cost > o.cost) {
				return -1;
			} else
				return 0;
		}

	}

	private MyGraph graph;
	private PriorityQueue<Solution> solutions;

	public StEnumerator(MyGraph graph) {
		this.graph = graph;
		this.solutions = null;
	}

	public MyGraph next() {
		MyGraph result = null;
		
		if (solutions == null) {
			solutions = new PriorityQueue<StEnumerator.Solution>();
			result = Kruskal.maxST(graph);
			Partition partition = new Partition();
			for (Partition subPartition : partition.subPartitions(result)) {
				MyGraph st = Kruskal.maxST(graph, subPartition);
				if (st.getEdges().size() == graph.getNodes().size() - 1) {
					double c = st.cost();
					Solution s = new Solution(subPartition, st, c);
					solutions.add(s);
				}
			}
		} else {
			Solution currentSolution = solutions.poll();
			if (currentSolution != null) {
				result = currentSolution.maxST;
				for (Partition subPartition : currentSolution.partition
						.subPartitions(result)) {
					MyGraph st = Kruskal.maxST(graph, subPartition);
					if (st.getEdges().size() == graph.getNodes().size() - 1) {
						double c = st.cost();
						Solution s = new Solution(subPartition, st, c);
						solutions.add(s);
					}
				}
			}

		}
		return result;
	}
	
}
