package graphs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

class IdComparator implements Comparator<MyEdge> {
	@Override
	public int compare(MyEdge arg0, MyEdge arg1) {
		return arg0.getId().compareTo(arg1.getId());
	}
}

class WeightComparator implements Comparator<MyEdge> {
	@Override
	public int compare(MyEdge arg0, MyEdge arg1) {
		return (int) (arg0.getWeight() - arg1.getWeight());
	}
}

public class MyGraph {
	protected ArrayList<MyNode> nodes;
	protected ArrayList<MyEdge> edges;

	public MyGraph(ArrayList<MyNode> nodes, ArrayList<MyEdge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public MyGraph() {
		nodes = new ArrayList<MyNode>();
		edges = new ArrayList<MyEdge>();
	}

	public MyGraph(MyGraph g) {
		nodes = new ArrayList<MyNode>();
		edges = new ArrayList<MyEdge>();
		for (MyNode n : g.getNodes()) {
			MyNode n1 = new MyNode(n.getId(), n.getLabel());
			this.addNode(n1);
			n1.setContiglength(n.getContiglength());
			n1.setOrientation(n.getOrientation());
		}
		for (MyEdge e : g.getEdges()) {
			MyEdge e1 = new MyEdge(e.getId(), this.nodeFromId(e.getSource()
					.getId()), this.nodeFromId(e.getTarget().getId()));
			this.addEdge(e1);
			e1.setWeight(e.getWeight());
			e1.setLenght(e.getLenght());
			e1.setOrientations(e.getOrientations());
		}
	}

	public double cost() {
		double c = 0;
		for (MyEdge e : this.getEdges()) {
			// System.out.println(e.getId()+" ha peso "+e.getWeight());
			c = c + e.getWeight();

		}
		// System.out.println("Total:"+c);
		return c;
	}

	public void cleanBC(HashMap<String, Double> bcMap, int threshold) {
		for (MyNode n : nodes) {
			Double bc = bcMap.get(n.getId());
			if (bc > threshold) {
				removeNode(n);
			}
		}
	}

	public void addEdge(MyEdge e) {
		this.edges.add(e);
		e.getSource().addAdjacentNode(e.getTarget());
		e.getTarget().addAdjacentNode(e.getSource());
	}

	public void addNode(MyNode n) {
		this.nodes.add(n);
	}

	public MyNode nodeFromId(String Id) {
		MyNode node = null;
		for (MyNode n : nodes) {
			if (n.getId().equals(Id)) {
				node = n;
			}
		}
		return node;
	}

	public ArrayList<MyNode> getNodes() {
		return nodes;
	}

	public ArrayList<MyEdge> getEdges() {
		return edges;
	}

	public void removeEdge(MyEdge e) {
		this.edges.remove(e);
		ArrayList<MyNode> adjS = e.getSource().getAdj();
		adjS.remove(e.getTarget());
		e.getSource().setAdj(adjS);
		ArrayList<MyNode> adjT = e.getTarget().getAdj();
		adjT.remove(e.getSource());
		e.getTarget().setAdj(adjT);
	}

	public void removeNode(MyNode n) {
		@SuppressWarnings("unchecked")
		ArrayList<MyNode> l = (ArrayList<MyNode>) n.getAdj().clone();
		for (MyNode a : l) {
			MyEdge e = getEdgeByST(n, a);
			this.removeEdge(e);
		}
		nodes.remove(n);
	}

	public int sumDegree() {
		int i = 0;
		for (MyNode n : nodes) {
			i = i + n.getDegree();
		}
		return i;

	}

	public MyEdge getEdgeByST(MyNode root, MyNode a) {
		MyEdge edge = null;
		for (MyEdge e : edges) {
			if (e.getSource().getId() == root.getId()
					& e.getTarget().getId() == a.getId()
					|| e.getSource().getId() == a.getId()
					& e.getTarget().getId() == root.getId()) {
				edge = e;
			}
		}
		return edge;
	}

	public String toStringVerbose() {
		StringBuilder br = new StringBuilder();
		br.append("Nodes(" + nodes.size() + ")=[");
		for (MyNode n : nodes) {
			br.append(n.toStringVErbose());
			br.append(", ");
		}
		br.append("]\n" + "Edges(" + edges.size() + ")=[");
		for (MyEdge e : edges) {
			br.append(e.toStringVerbose());
			br.append(", ");
		}
		br.append("]");
		return br.toString();
	}

	public String toString() {
		String s = "Nodes(" + nodes.size() + ")"// + nodes.toString() + "\n"
				+ "Edges(" + edges.size() + ")=" + edges.toString();
		return s;

	}

	public double meanDegree() {
		int sum = 0;
		double mean = 0;
		for (MyNode n : nodes) {
			sum = sum + n.getDegree();
		}
		mean = sum / nodes.size();

		return mean;

	}

	public void deduplicateEdge() {
		Collections.sort(edges, new IdComparator());
		int repeated = 0;
		for (int i = 1; i < edges.size(); ++i) {
			if (edges.get(i).getId().equals(edges.get(i - 1).getId())) {
				edges.get(i).setId(i + "_" + repeated++);
			} else {
				repeated = 0;
			}
		}
	}

	public ArrayList<String> subPaths() {
		MyGraph copy = new MyGraph(this);
		ArrayList<String> subPaths = new ArrayList<String>();
		HashMap<MyNode, Integer> originalDegrees = new HashMap<MyNode, Integer>();
		for (MyNode n : copy.nodes) {
			originalDegrees.put(n, n.getDegree());
		}
		while (copy.nodes.size() >= 2) {
			copy.removeSingletons();
			if (copy.nodes.size() <= 1) {
				throw new RuntimeException(
						"Nella cover c'erano solo singoletti"
								+ copy.toStringVerbose());// TODO
			}
			MyNode root = null;
			for (MyNode r : copy.nodes) {
				if (r.getDegree() == 1) {// cerca una foglia di copy da cui
											// partire.
					root = r;
					break;
				}
			}
			if (root == null) {
				throw new RuntimeException("non c'e' una root in "
						+ copy.toStringVerbose());// TODO
			}
			String p = copy.clearPrintSB(root, originalDegrees);
			subPaths.add(p);

		}

		return subPaths;

	}

	public void removeSingletons() {
		ArrayList<MyNode> toRemove = new ArrayList<MyNode>();
		for (MyNode n : nodes) {
			if (n.getDegree() == 0) {
				toRemove.add(n);
			}
		}
		for (MyNode r : toRemove) {
			this.removeNode(r);
		}
	}

	/*
	 * Serve per eliminare da una serie di paths quelli ciclici rimuovendo
	 * l'arco piu' leggero
	 */
	public void removeRings() {
		ArrayList<MyEdge> toRemove = new ArrayList<MyEdge>();
		ArrayList<MyNode> toVisit = new ArrayList<MyNode>(this.nodes);
		while (!toVisit.isEmpty()) {
			MyNode n = toVisit.remove(0);
			if (n.getDegree() > 1) {
				MyEdge candidate = findCycle(n);
				if (n.getDegree() == 2 && candidate != null) {
					toRemove.add(candidate);
				}
			}

		}
		for (MyEdge e : toRemove) {
			this.removeEdge(e);
		}
	}

	/* controlla se il path e' un ciclo e ritorna l'arco di peso minore */
	private MyEdge findCycle(MyNode n) {
		MyNode current = n;
		MyNode next;

		MyEdge e = this.outEdges(current).get(0);// sceglie una direzione a caso
													// da seguire.
		MyEdge min = e;
		MyEdge candidateEdge = e;
		if (current.equals(e.getSource())) {
			next = e.getTarget();
		} else {
			next = e.getSource();
		}
		MyNode previous = current;

		while (next.getDegree() == 2 && next != n) {
			ArrayList<MyNode> tmp = new ArrayList<MyNode>(next.getAdj());
			tmp.remove(previous);
			previous = next;
			next = tmp.get(0);
			candidateEdge = this.getEdgeByST(previous, next);
			if (candidateEdge.getWeight() < min.getWeight()) {
				min = candidateEdge;
			}
		}
		if (next.getDegree() == 1) {
			// ero in un path semplice
			return null;
		} else {
			// ero in un circolo
			return min;
		}

	}

	/* questa versione stampa le label e la lunghezza */
	private String clearPrintSB(MyNode root,
			HashMap<MyNode, Integer> originalDegrees) {
		StringBuilder sb = new StringBuilder();
		int length = 0;
		sb.append(root.getLabel() + "-");
		length = length + root.getContiglength();
		MyNode current = root.getAdj().get(0);
		MyEdge start = this.getEdgeByST(root, current);
		this.removeEdge(start);
		this.removeNode(root);
		while (originalDegrees.get(current) == 2) {
			sb.append(current.getLabel() + "-");
			length = length + current.getContiglength();
			MyNode next = current.getAdj().get(0);
			MyEdge e = this.getEdgeByST(current, next);
			this.removeEdge(e);
			this.removeNode(current);
			current = next;
		}
		sb.append(current.getLabel());// l'ultimo lo lascia
		length = length + current.getContiglength();
		sb.append("-@" + length);
		String p = sb.toString();
		return p;
	}

	public int notSingletons() {
		int n = 0;
		for (MyNode v : nodes) {
			if (v.getDegree() != 0) {
				n = n + 1;
			}// debug-------
				// else{
				// System.out.println(v.toStringVErbose()+ " nuovo singoletto");

			// }//----------------
		}

		return n;
	}

	public ArrayList<String> subChains() {
		MyGraph copy = new MyGraph(this);
		ArrayList<MyNode> toRemove = new ArrayList<MyNode>();
		for (MyNode n : copy.nodes) {
			if (n.getDegree() > 2) {
				toRemove.add(n);
			}
		}
		for (MyNode r : toRemove) {
			copy.removeNode(r);
		}
		return copy.subPaths();
	}

	public void setInfo(HashMap<String, String[]> info) {
		// System.out.println("TRADUZIONE LABEL");//DEBUG
		int j = 0;
		for (MyNode n : nodes) {
			String[] nodeInfo = info.get(n.getId());
			if (nodeInfo == null) {
				// n.setLabel(null);
				n.setLabel("label" + String.valueOf(j));
				j++;
				// just debug--------
				// System.out.println(n.getId()+"--->"+n.getLabel());
				// -------------
			} else {
				n.setLabel(nodeInfo[0]);
				n.setContiglength(Integer.valueOf(nodeInfo[1]));
				// just debug--------
				// System.out.println(n.getId()+"--->"+nodeInfo[0]);
				// -------------
			}

		}

	}

	public MyGraph computeCover() {
		// minor numero di componenti e, tra queste, una di massimo peso.
		MyGraph cover = new MyGraph(this);
		ArrayList<MyNode> leaves = cover.getLeaves();
		while (!cover.isPaths()) {
			ArrayList<MyEdge> cuts = null;
			while (cuts == null && !leaves.isEmpty()) {
				MyNode leaf = leaves.get(0);
				cuts = cover.followTheVine(leaf);
				leaves.remove(0);
			}
			if (!leaves.isEmpty()) {
				for (MyEdge e : cuts) {
					cover.removeEdge(e);
				}
			}
		}
		return cover;
	}

	/*
	 * private ArrayList<MyEdge> computeCuts(MyNode n) { ArrayList<MyEdge>
	 * notToCut = new ArrayList<MyEdge>(outEdges(n)); ArrayList<MyEdge> cuts =
	 * new ArrayList<MyEdge>(); Collections.sort(notToCut, new
	 * WeightComparator()); while(notToCut.size()==2){ MyEdge e=
	 * notToCut.get(0); cuts.add(e); notToCut.remove(e); } return cuts;
	 * 
	 * }
	 */

	public MyGraph computeCover3() {
		// minor numero di componenti.
		MyGraph cover = new MyGraph(this);
		ArrayList<MyNode> leaves = cover.getLeaves();
		while (!cover.isPaths()) {
			ArrayList<MyEdge> cuts = null;
			while (cuts == null && !leaves.isEmpty()) {
				MyNode leaf = leaves.get(0);
				cuts = cover.followTheVine(leaf);
				leaves.remove(0);
			}
			if (!leaves.isEmpty()) {
				for (MyEdge e : cuts) {
					cover.removeEdge(e);
				}
			}
		}
		return cover;
	}

	public MyGraph computeCover4() {
		// euristica. Tiene i piu' pesanti ma il peso di quelli condivisi viene
		// dimezzato
		MyGraph cover = new MyGraph(this);
		ArrayList<MyNode> hightDegreeNodes = new ArrayList<MyNode>();
		for (MyNode n : cover.getNodes()) {
			if (n.getDegree() > 2) {
				hightDegreeNodes.add(n);
			}
		}
		for (MyNode node : hightDegreeNodes) {
			// dimezza il peso degli archi che non sono vine
			for (MyEdge edge : cover.outEdges(node)) {
				if (cover.isNotVine(node, edge)) {
					double oldW = edge.getWeight();
					edge.setWeight(oldW / 2);
				}
			}

		}
		while (!cover.isPaths()) {
			ArrayList<MyEdge> cuts = null;
			while (cuts == null && !hightDegreeNodes.isEmpty()) {
				MyNode node = hightDegreeNodes.get(0);
				cuts = cover.computeWorstEdges(node);
				hightDegreeNodes.remove(0);
			}

			for (MyEdge e : cuts) {
				cover.removeEdge(e);
			}

		}
		return cover;
	}

	/*
	 * private ArrayList<MyEdge> findCuts(MyNode current) { ArrayList<MyEdge>
	 * cuts= new ArrayList<MyEdge>(outEdges(current)); ArrayList<MyEdge>
	 * notToCut = new ArrayList<MyEdge>(); int i =0; while(notToCut.size()!=2){
	 * MyEdge candidate = cuts.get(i); if(!isNotVine(current, candidate)){
	 * notToCut.add(candidate); } i=i+1; } return cuts;
	 * 
	 * }
	 */

	private ArrayList<MyEdge> followTheVine(MyNode current) {

		if (current.getDegree() == 0) {
			return null;
		}
		MyNode first;
		MyEdge e = this.outEdges(current).get(0);// unico in una foglia
		if (current.equals(e.getSource())) {
			first = e.getTarget();
		} else {
			first = e.getSource();
		}
		MyNode next = first;
		MyNode previous = current;
		while (next.getDegree() == 2) {
			ArrayList<MyNode> tmp = new ArrayList<MyNode>(next.getAdj());
			tmp.remove(previous);
			previous = next;
			next = tmp.get(0);
		}
		ArrayList<MyEdge> cuts;
		if (next.getDegree() == 1) {
			// ero in un path
			cuts = null;
		} else {
			// ho trovato un nodo di incrocio
			cuts = new ArrayList<MyEdge>();
			ArrayList<MyEdge> save = new ArrayList<MyEdge>(this.outEdges(next));
			ArrayList<MyEdge> toRemove = new ArrayList<MyEdge>();
			for (MyEdge candidate : save) {
				if (isNotVine(next, candidate)) {
					cuts.add(candidate);
					toRemove.add(candidate);
				}
			}
			save.removeAll(toRemove);
			if (save.size() >= 2) {
				Collections.sort(save, new WeightComparator());
				save.remove(save.size() - 1);
				save.remove(save.size() - 1);
				cuts.addAll(save);
			} else {
				Collections.sort(cuts, new WeightComparator());
				cuts.remove(cuts.size() - 1);
			}

		}
		return cuts;
	}

	private boolean isNotVine(MyNode n, MyEdge e) {
		MyNode first;
		if (n == e.getSource()) {
			first = e.getTarget();
		} else {
			first = e.getSource();
		}
		MyNode next = first;
		MyNode previous = n;
		while (next.getDegree() == 2) {
			ArrayList<MyNode> tmp = new ArrayList<MyNode>(next.getAdj());
			tmp.remove(previous);
			previous = next;
			next = tmp.get(0);
		}
		if (next.getDegree() > 2) {
			return true;
		}
		return false;
	}

	private boolean isPaths() {
		for (MyNode n : this.nodes) {
			if (n.getDegree() > 2) {
				return false;
			}
		}
		return true;
	}

	public MyGraph computeCover2() {
		MyGraph cover = new MyGraph(this);
		for (MyNode n : cover.getNodes()) {
			if (n.getDegree() >= 3) {
				ArrayList<MyNode> mates = findMates(n);
				ArrayList<MyEdge> cuts = cover.computeWorstEdgesForGroup(mates);
				for (MyEdge e : cuts) {
					cover.removeEdge(e);
				}
			}
		}

		return cover;
	}

	private ArrayList<MyEdge> computeWorstEdgesForGroup(ArrayList<MyNode> mates) {
		ArrayList<MyEdge> cuts = new ArrayList<MyEdge>();
		ArrayList<MyEdge> candidateCuts = new ArrayList<MyEdge>();
		HashMap<MyNode, Integer> degrees = new HashMap<MyNode, Integer>();
		for (MyNode n : mates) {
			candidateCuts.addAll(this.outEdges(n));
			degrees.put(n, n.getDegree());
		}
		Collections.sort(candidateCuts, new WeightComparator());
		while (!goodDegree(degrees)) {
			MyEdge e = candidateCuts.get(0);
			MyNode s = e.getSource();
			MyNode t = e.getTarget();
			int sD = degrees.get(s);
			int tD = degrees.get(s);
			if (sD > 2 || tD > 2) {
				cuts.add(e);
				degrees.put(t, tD - 1);
				degrees.put(s, sD - 1);
			}
			candidateCuts.remove(e);
		}
		return cuts;
	}

	private boolean goodDegree(HashMap<MyNode, Integer> degrees) {
		boolean b = true;
		for (Entry<MyNode, Integer> entry : degrees.entrySet()) {
			if (entry.getValue() > 2) {
				b = false;
			}
		}
		return b;
	}

	private ArrayList<MyNode> findMates(MyNode n) {
		ArrayList<MyEdge> directions = this.outEdges(n);
		MyNode previous = n;
		MyNode next;
		ArrayList<MyNode> mates = new ArrayList<MyNode>();
		for (MyEdge d : directions) {
			if (previous == d.getSource()) {
				next = d.getTarget();
			} else {
				next = d.getSource();
			}
			while (next.getDegree() == 2) {
				ArrayList<MyNode> adj = new ArrayList<MyNode>(n.getAdj());
				adj.remove(previous);
				previous = next;
				next = adj.get(0);

			}
			if (next.getDegree() > 2) {
				mates.add(next);
			}
		}
		return mates;
	}

	private ArrayList<MyEdge> computeWorstEdges(MyNode n) {
		ArrayList<MyEdge> cuts = new ArrayList<MyEdge>(this.outEdges(n));
		Collections.sort(cuts, new WeightComparator());
		cuts.remove(cuts.size() - 1);
		cuts.remove(cuts.size() - 1);
		return cuts;
	}

	/*
	 * private ArrayList<MyEdge> maximumVinePathCuts(MyNode n) {
	 * ArrayList<MyEdge> toBeEliminate = new
	 * ArrayList<MyEdge>(this.outEdges(n)); MyEdge one=null; MyEdge two=null;
	 * double best=0; for(MyEdge e1 : this.outEdges(n)){ for(MyEdge e2 :
	 * this.outEdges(n)){ if(e1!=e2){ double weight1 = this.pathToLeaf(e1,n);
	 * double weight2 = this.pathToLeaf(e2,n); double w=weight1+weight2; //nota,
	 * i pareggi vanno a caso; if(w>best){ best=w; one=e1; two=e2; } } } }
	 * toBeEliminate.remove(one); toBeEliminate.remove(two); return
	 * toBeEliminate; }
	 * 
	 * private double pathToLeaf(MyEdge e, MyNode n) { MyNode first;
	 * if(n==e.getSource()){ first=e.getTarget(); }else{ first=e.getSource(); }
	 * MyNode next = first; MyNode previous=n; double weight=e.getWeight();
	 * while(next.getDegree()==2){ ArrayList<MyNode> tmp = new
	 * ArrayList<MyNode>(next.getAdj()); tmp.remove(previous); MyEdge nextE =
	 * this.getEdgeByST(next, tmp.get(0)); weight=weight + nextE.getWeight();
	 * previous=next; next=tmp.get(0); } if(next.getDegree()>2){ weight= 0; }
	 * return weight; }
	 */
	public ArrayList<MyEdge> outEdges(MyNode n) {
		ArrayList<MyEdge> outEdges = new ArrayList<MyEdge>();
		for (MyNode a : n.getAdj()) {
			MyEdge e = getEdgeByST(n, a);
			outEdges.add(e);
		}
		return outEdges;
	}

	public void deleteZeroEdges() {
		ArrayList<MyEdge> toDelete = new ArrayList<MyEdge>();
		for (MyEdge e : edges) {
			if (e.getWeight() == 0) {
				toDelete.add(e);
			}
		}
		for (MyEdge d : toDelete) {
			this.removeEdge(d);
		}
	}

	public ArrayList<MyNode> getLeaves() {
		ArrayList<MyNode> leaves = new ArrayList<MyNode>();
		for (MyNode n : this.nodes) {
			if (n.getDegree() == 1) {
				leaves.add(n);
			}
		}
		return leaves;
	}

	public ArrayList<String> readScaffolds(String input) throws IOException {
		HashMap<String, String> sequences = parseSequences(input);
		ArrayList<String> scaffolds = new ArrayList<String>();
		MyGraph copy = new MyGraph(this);
		HashMap<MyNode, Integer> originalDegrees = new HashMap<MyNode, Integer>();
		// ----legge i singoletti e aggiorna la mappa dei gradi---//
		for (MyNode n : copy.nodes) {
			originalDegrees.put(n, n.getDegree());
			if (n.getDegree() == 0) {
				scaffolds.add(sequences.get(">" + n.getId()));
			}
		}
		// --legge i veri scaffolds---//
		while (copy.nodes.size() >= 2) {
			copy.removeSingletons();
			MyNode root = null;
			for (MyNode r : copy.nodes) {
				if (r.getDegree() == 1) {// cerca una foglia di copy da cui
											// partire.
					root = r;
					break;
				}
			}
			if (root == null) {
				System.out.println("ERROR: no root " + copy.toStringVerbose());
			}
			String p = copy.scaffoldSeq(root, originalDegrees, sequences);
			scaffolds.add(p);
		}

		return scaffolds;
	}

	private HashMap<String, String> parseSequences(String input)
			throws IOException {
		HashMap<String, String> sequences = new HashMap<String, String>();
		File f = new File(input);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String currentLine = br.readLine();
		while (currentLine != null) {
			if (currentLine.contains(">")) {
				String[] s = currentLine.split(" ");
				String id = s[0];
				String seq = br.readLine();
				sequences.put(id, seq);
				currentLine = br.readLine();

			} else {
				currentLine = br.readLine();
			}

		}
		return sequences;
	}

	private String scaffoldSeq(MyNode root,
			HashMap<MyNode, Integer> originalDegrees,
			HashMap<String, String> sequences) {
		StringBuilder sb = new StringBuilder();

		String rootSeq = sequences.get(">" + root.getId());
		if (root.getOrientation() == -1) {
			reverseComplement(rootSeq);
		}
		sb.append(rootSeq);
		MyNode current = root.getAdj().get(0);
		MyEdge start = this.getEdgeByST(root, current);
		for (int i = 1; i <= 100; i++) {// aggiunge N tra un contiguo e l'altro.
			sb.append("N");
		}
		this.removeEdge(start);
		this.removeNode(root);
		while (originalDegrees.get(current) == 2) {
			String currentSeq = sequences.get(">" + current.getId());
			if (current.getOrientation() == -1) {
				reverseComplement(currentSeq);
			}
			sb.append(currentSeq);
			MyNode next = current.getAdj().get(0);
			MyEdge e = this.getEdgeByST(current, next);
			for (int i = 1; i <= 100; i++) {// aggiunge N tra un contiguo e l'altro.
				sb.append("N");
			}
			this.removeEdge(e);
			this.removeNode(current);
			current = next;
		}
		String currentSeq = sequences.get(">" + current.getId());
		if (current.getOrientation() == -1) {
			reverseComplement(currentSeq);
		}
		sb.append(currentSeq);
		String p = sb.toString();
		return p;
	}

	public static String reverseComplement(String currentSeq) {
		String s = currentSeq.replace("A", "a");
		s = s.replace("G", "g");
		s = s.replace("T", "A");
		s = s.replace("C", "G");
		s = s.replace("g", "C");
		s = s.replace("a", "T");
		String rev = new StringBuffer(s).reverse().toString();
		return rev;

	}

	public void cleanOrinetation() {
		System.out.println("Removed edges for inconsistent orientation: ");// TODO
		ArrayList<MyEdge> toBeRemoved = new ArrayList<MyEdge>();
		MyGraph copy = new MyGraph(this);
		/*//TODO
		 * for(MyEdge e : edges){ MyNode s = e.getSource(); MyNode t =
		 * e.getTarget(); if(s.getOrientation()==100){
		 * s.setOrientation(e.orientations[0]); }else{
		 * if(e.orientations[0]!=s.getOrientation()){ toBeRemoved.add(e);
		 * System.
		 * out.println(s.getId()+": source orientation ="+s.getOrientation()+
		 * " new proposal "+e.getSource().getId()+"="+e.orientations[0]); } }
		 * if(t.getOrientation()==100){ t.setOrientation(e.orientations[1]);
		 * }else{ if(e.orientations[1]!=t.getOrientation()){ toBeRemoved.add(e);
		 * System
		 * .out.println(t.getId()+": target orientation ="+t.getOrientation()+
		 * " new proposal "+e.getTarget().getId()+"="+e.orientations[1]);
		 * 
		 * } }
		 */
		ArrayList<MyNode> leaves = copy.getLeaves();
		MyNode current = leaves.remove(0);
		while (!leaves.isEmpty()) {
			if (current == null) {
				current = leaves.remove(0);
			}
			MyEdge e = copy.outEdges(current).get(0);
			MyNode next = current.getAdj().get(0);
			if (current.equals(e.getSource())) {
				int o0 = e.orientations[0];
				int o1 = e.orientations[1];
				if (o0 == current.getOrientation()
						|| current.getOrientation() == 100) {
					current.setOrientation(o0);
					next.setOrientation(o1);
				} else {
					MyEdge eg = this.getEdgeByST(current, next);
					toBeRemoved.add(eg);
					System.out.println(eg.toStringVerbose()
									//+ "= {"//TODO
									//+ e.getOrientations()[0] + ","
									//+ e.getOrientations()[1] + "}//{"
									//+ e.getSource().getOrientation() + ","
									//+ e.getTarget().getOrientation() + "}"
									);
				}
			} else {
				int o0 = e.orientations[1] * (-1);
				int o1 = e.orientations[0] * (-1);
				if (o0 == current.getOrientation()
						|| current.getOrientation() == 100) {
					current.setOrientation(o0);
					next.setOrientation(o1);
				} else {
					MyEdge eg = this.getEdgeByST(current, next);
					toBeRemoved.add(eg);
					System.out.println(eg.toStringVerbose() 
							//+ "= {"//TODO
							//+ e.getOrientations()[0] + ","
							//+ e.getOrientations()[1] + "}//{"
							//+ e.getSource().getOrientation() + ","
							//+ e.getTarget().getOrientation() + "}"
							);
				}

			}
			current = next;
			copy.removeEdge(e);
			if (current.getDegree() == 0) {
				leaves.remove(current);

				current = null;
			}

		}
		for (MyEdge e : toBeRemoved) {
			removeEdge(e);
		}
		for (MyNode n : copy.nodes) {
			MyNode node = this.nodeFromId(n.getId());
			node.setOrientation(n.getOrientation());
		}
	}

	public ArrayList<String> readNodeOrder(String input) throws IOException {
		ArrayList<String> scaffolds = new ArrayList<String>();
		MyGraph copy = new MyGraph(this);
		HashMap<MyNode, Integer> originalDegrees = new HashMap<MyNode, Integer>();
		// ----legge i singoletti e aggiorna la mappa dei gradi---//
		for (MyNode n : copy.nodes) {
			originalDegrees.put(n, n.getDegree());
			if (n.getDegree() == 0) {
				scaffolds.add(n.getId());
			}
		}
		// --legge i veri scaffolds---//
		while (copy.nodes.size() >= 2) {
			copy.removeSingletons();
			MyNode root = null;
			for (MyNode r : copy.nodes) {
				if (r.getDegree() == 1) {// cerca una foglia di copy da cui
											// partire.
					root = r;
					break;
				}
			}
			if (root == null) {
				System.out.println("ERROR: no root " + copy.toStringVerbose());
			}
			String p = copy.scaffoldString(root, originalDegrees);
			scaffolds.add(p);
		}

		return scaffolds;
	}

	private String scaffoldString(MyNode root,
			HashMap<MyNode, Integer> originalDegrees) {
		StringBuilder sb = new StringBuilder();

		String rootSeq=root.getId()+":"+root.getOrientation();
		sb.append(rootSeq);
		MyNode current = root.getAdj().get(0);
		MyEdge start = this.getEdgeByST(root, current);
		sb.append("_");
		this.removeEdge(start);
		this.removeNode(root);
		while (originalDegrees.get(current) == 2) {
			String currentSeq = current.getId()+":"+current.getOrientation();
			sb.append(currentSeq);
			MyNode next = current.getAdj().get(0);
			MyEdge e = this.getEdgeByST(current, next);
			sb.append("_");
			this.removeEdge(e);
			this.removeNode(current);
			current = next;
		}
		String currentSeq = current.getId()+":"+current.getOrientation();
		sb.append(currentSeq);
		String p = sb.toString();
		return p;
	}

}
