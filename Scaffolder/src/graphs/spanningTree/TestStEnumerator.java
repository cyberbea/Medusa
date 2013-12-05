package graphs.spanningTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;

public class TestStEnumerator {
	
	public TestStEnumerator() {
		
	}
	
	public ArrayList<Double> weights = new ArrayList<Double>();
	
	public void enumerateSpanningTreesRecursive(MyGraph g, MyGraph st) {
		
		if (!connected(g))
			return;
		if (!acyclic(st))
			return;
		
		
		if (st.getEdges().size() == st.getNodes().size() - 1) {
			//System.out.println(st.toString());
			//System.out.println("Costo: " + st.cost() + "\n");
			weights.add(st.cost());
			return;
		}
		
		MyEdge edge = new MyEdge("ERROR", g.getNodes().get(0), g.getNodes().get(0));
		
		for(MyEdge e: g.getEdges()) {
			if (st.getEdgeByST(e.getSource(), e.getTarget()) == null) {
				edge = e;
				break;
			}
		}

		MyGraph newST = new MyGraph(st);
		newST.addEdge(edge);
		enumerateSpanningTreesRecursive(g, newST);
		
		MyGraph newG = new MyGraph(g);
		newG.removeEdge(newG.getEdgeByST(edge.getSource(), edge.getTarget()));
		enumerateSpanningTreesRecursive(newG, st);
		
	}
	
	public static boolean connected(MyGraph g) {
		boolean visited[] = new boolean[g.getNodes().size()];
		
		for (int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}
		
		Queue<MyNode> q = new LinkedList<MyNode>();
		q.add(g.getNodes().get(0));
		visited[0] = true;
		
		while (!q.isEmpty()) {
			MyNode v = q.poll();
			
			for (MyNode w : v.getAdj()) {
				if (!visited[g.getNodes().indexOf(w)]) {
					visited[g.getNodes().indexOf(w)] = true;
					q.add(w);
				}
			}
		}
		
		for (int i = 0; i < visited.length; i++) {
			if (!visited[i])
				return false;
		}
		return true;
	}
	
	public static boolean acyclic(MyGraph g) {
		MyGraph h = new MyGraph(g);
		boolean doneSth = true;
		while (doneSth) {
			doneSth = false;
			ArrayList<MyNode> nodes = (ArrayList<MyNode>) h.getNodes().clone();
			for (MyNode n : nodes) {
				if (n.getAdj().size() <= 1) {
					h.removeNode(n);
					doneSth = true;
				}
			}
		}
		if (h.getNodes().size() == 0) {
			return true;
		} else
			return false;
	}

}
