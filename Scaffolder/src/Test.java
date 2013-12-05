

import graphs.MyGraph;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import utilities.GexfReader;
import utilities.GexfWriter;

public class Test {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, TransformerException {
	/*
		System.out.println("RETE PERFETTA");
		MyGraph reteCorretta = new MyGraph();
		MyNode node = new MyNode(Integer.toString(0),Integer.toString(0));
		reteCorretta.addNode(node);
		//archi corretti hanno peso da 100 a 150
		for(int i=1; i<100; i++){
			MyNode node1 = new MyNode(Integer.toString(i),Integer.toString(i));
			MyNode node2 = reteCorretta.nodeFromId(Integer.toString(i-1));			
			reteCorretta.addNode(node1);
			MyEdge edge = new MyEdge(Integer.toString(i-1)+"/"+Integer.toString(i), node2, node1);
			int w = (int) (Math.random()*100)+100;
			edge.setWeight(w);
			reteCorretta.addEdge(edge);
			System.out.println(edge.toStringVerbose());
		}
		//archi fasulli random hanno peso da 0 a 100
		for(int i=1; i<100; i++){
			int i1 = (int) (Math.random()*100);
			int i2 = (int) (Math.random()*100);
			if(!(i1==i2)){ 
				MyNode node1 = reteCorretta.nodeFromId(Integer.toString(i1));
				MyNode node2 = reteCorretta.nodeFromId(Integer.toString(i2));
				MyEdge edge = new MyEdge(Integer.toString(i1)+"/"+Integer.toString(i2), node1, node2);
				int w = (int) (Math.random()*100);
				edge.setWeight(w);
				reteCorretta.addEdge(edge);
				System.out.println(edge.toStringVerbose());
			}
			
		}
		GexfWriter.write(reteCorretta, "reteOttima.gexf");
		*/
		MyGraph tree =  GexfReader.read("reteCorrettaST.gexf");
		MyGraph cover = tree.computeCover();
		System.out.println(cover.toStringVerbose());
		GexfWriter.write(cover, "reteOttimaCover");
		
		
		
		/*------------LEGGERE GRAFO---------------*/
	

		// System.out.println(grafo.toStringVerbose());
		/*-----------LEGGERE ORDINE -----------*/
		// HashMap<String, String[]> info =
		// GexfReader.readContigInfo("REF_SCAFFOLD_chr_1_mio.txt");
		// grafo.setInfo(info);
		// System.out.println(order.toString());

		/*----------RIMUOVI RUMORE-------------*/
		// HashMap<String, Double> bcmap = GexfReader.readBc("outputBC");
		// grafo.cleanBC(bcmap, 100);

		/*------------DEDUPLICARE ARCHI---------------*/
		// grafo.deduplicateEdge();

		/*------------FARE SPANNING TREE---------*/
		// MyGraph maxST1 = grafo1.maxST();
		// System.out.println(grafo.toString());

	
		/*----------------CONTROLLO NODI NON PRESENTI---------------
		 for(MyNode n : grafo.getNodes()){
		 if(n.getLabel()==null){
		 System.out.println(n.getId().split("!")[1]+" non presente nell'ordine");
		 }	
		 }*/
		/*------------ENUMERATORE--------*/
		
		//MyGraph grafo = GexfReader.read("noZero.gexf" );
		//MyGraph maxSt = Kruskal.maxST(grafo);
		//MyGraph cover = grafo.computeCover();
		//grafo.deleteZeroEdges();
		/*-----------CREARE GEXF--------*/

		// GexfWriter.write(grafo, "noZero.gexf");
		// MyGraph st = Kruskal.maxST(grafo);
		// GexfWriter.write(st, "new2_0_100_ST.gexf");
		
		/*StEnumerator enumeratore = new StEnumerator(new MyGraph(grafo));
		
		while (true) {
			MyGraph st = enumeratore.next();
			
			if (st == null) {
				break;
			} else {
				//GexfWriter.write(st, i+"_"+st.cost()+".gexf");
				System.out.println(st.toString() + "\nCosto: " + st.cost() + "\n");
			}
		}*/
		
		/*-----------TESTER PROBLEM--------*/
//		MyGraph grafo = GexfReader.read("problem.gexf");
//		if (TestGraph(grafo)) {
//			System.out.println("All OK!");
//		} else {
//			System.out.println("HOUSTON, WE HAVE A PROBLEM!");
//		}
		
		/*-----------TESTER------------
		MyGraph grafo;
		while (true) {
			grafo = RandomGraphGenerator.GenerateRandomGraph(9, 0.3);
			if (!TestStEnumerator.connected(grafo)) {
				System.out.println("Not connected input graph!");
			}
			else if (TestGraph(grafo)) {
				System.out.println("All OK!");
			} else {
				System.out.println("HOUSTON, WE HAVE A PROBLEM! THE PROBLEMATIC GRAPH WAS SAVED AS problem.gexf");
				GexfWriter.write(grafo, "problem.gexf");
				break;
			}
		}
		
	}
	public static boolean TestGraph (MyGraph grafo) {
		StEnumerator enumeratore = new StEnumerator(new MyGraph(grafo));
		ArrayList<Double> weights = new ArrayList<Double>();
		
		while (true) {
			MyGraph st = enumeratore.next();
			
			if (st == null) {
				break;
			} else {
				weights.add(st.cost());
				// GexfWriter.write(st, i+"_"+st.cost()+".gexf");
//				System.out.println(st.toString() + "\nCosto: " + st.cost() + "\n");
			}
		}
		
		MyGraph st = new MyGraph(grafo.getNodes(), new ArrayList<MyEdge>());
		
		TestStEnumerator en = new TestStEnumerator();
		en.enumerateSpanningTreesRecursive(grafo, st);
		Collections.sort(en.weights);
		
		if (en.weights.size() == weights.size()) {
			for (int i = 0; i < weights.size(); i++) {
				if (en.weights.get(i) == weights.get(i)) {
					return false;
				}
			}
		} else
			return false;
		
		System.out.print(en.weights);
		return true;
	}*/
		
	}
}
