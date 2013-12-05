package graphs.spanningTree;

import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;
import graphs.UnionFind;

import java.util.ArrayList;
import java.util.Collections;

public class Kruskal {
	static public MyGraph maxST(MyGraph graph) {
		MyGraph maxST = new MyGraph();

		for (MyNode n : graph.getNodes()) {
			MyNode m = new MyNode(n.getId(), n.getLabel());
			m.setContiglength(n.getContiglength());
			maxST.addNode(m);
		}
		UnionFind uf = new UnionFind(maxST.getNodes());
		ArrayList<MyEdge> pq = new ArrayList<MyEdge>(graph.getNodes().size());
		for (MyEdge e : graph.getEdges()) {
			MyNode source = maxST.nodeFromId(e.getSource().getId());
			MyNode target = maxST.nodeFromId(e.getTarget().getId());
			MyEdge e1 = new MyEdge(e.getId(), source, target);
			e1.setWeight(e.getWeight());
			pq.add(e1);
		}
		Collections.sort(pq);
		while (!pq.isEmpty()) {
			MyEdge e = pq.remove(pq.size() - 1);
			String Id1 = e.getSource().getId();
			String Id2 = e.getTarget().getId();
			if (!uf.find(Id1, Id2)) {
				maxST.addEdge(e);
				uf.unite(Id1, Id2);
			}

		}
		return maxST;
	}

	static public MyGraph maxST(MyGraph graph, Partition partition) {
		/*
		 * idea: qui e' inutile copiare tutto il grafo e cancellare. Per
		 * migliorare mettere semplicemente un if dentro al ciclo
		 * dell'algortimo.
		 */
		MyGraph reducedGraph = new MyGraph(graph);
		for (MyEdge e : partition.getExcluded()) {
			MyNode source = reducedGraph.nodeFromId(e.getSource().getId());
			MyNode target = reducedGraph.nodeFromId(e.getTarget().getId());
			MyEdge e1 = reducedGraph.getEdgeByST(source, target);
			reducedGraph.removeEdge(e1);
		}
		for (MyEdge e : partition.getIncluded()) {
			MyNode source = reducedGraph.nodeFromId(e.getSource().getId());
			MyNode target = reducedGraph.nodeFromId(e.getTarget().getId());
			MyEdge e1 = reducedGraph.getEdgeByST(source, target);
			reducedGraph.removeEdge(e1);
		}
		MyGraph maxST = new MyGraph();

		for (MyNode n : reducedGraph.getNodes()) {
			MyNode m = new MyNode(n.getId(), n.getLabel());
			m.setContiglength(n.getContiglength());
			maxST.addNode(m);
		}
		UnionFind uf = new UnionFind(maxST.getNodes());

		for (MyEdge e : partition.getIncluded()) {
			MyNode source = maxST.nodeFromId(e.getSource().getId());
			MyNode target = maxST.nodeFromId(e.getTarget().getId());
			MyEdge e1 = new MyEdge(e.getId(), source, target);
			e1.setWeight(e.getWeight());
			maxST.addEdge(e1);
			uf.unite(e.getSource().getId(), e.getTarget().getId());
		}

		ArrayList<MyEdge> pq = new ArrayList<MyEdge>(reducedGraph.getNodes()
				.size());
		for (MyEdge e : reducedGraph.getEdges()) {
			MyNode source = maxST.nodeFromId(e.getSource().getId());
			MyNode target = maxST.nodeFromId(e.getTarget().getId());
			MyEdge e1 = new MyEdge(e.getId(), source, target);
			//TODO: aggiunto e1.setweight
			e1.setWeight(e.getWeight());
			pq.add(e1);
		}
		Collections.sort(pq);
		while (!pq.isEmpty()) {
			MyEdge e = pq.remove(pq.size() - 1);
			String Id1 = e.getSource().getId();
			String Id2 = e.getTarget().getId();
			if (!uf.find(Id1, Id2)) {
				maxST.addEdge(e);
				uf.unite(Id1, Id2);
			}
		}
		return maxST;
	}
}
