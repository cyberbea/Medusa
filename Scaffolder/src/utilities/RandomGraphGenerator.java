package utilities;

import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;

public class RandomGraphGenerator {
	//aggiungo un commento solo per provare...
	public static MyGraph GenerateRandomGraph(int n, double p) {
		MyGraph g = new MyGraph();
		for (int i = 0; i < n; i++) {
			g.addNode(new MyNode(String.valueOf(i), String.valueOf(i)));
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				if (Math.random() < p) {
					MyEdge e = new MyEdge("e" + j + i, g.nodeFromId("" + j),  g.nodeFromId("" + i));
					e.setWeight(Math.random());
					g.addEdge(e);
				}
			}
		}
		
		return g;
	}
}
