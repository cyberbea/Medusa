package graphs;

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
		return (int) (arg0.getWeight()-arg1.getWeight());
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
		}
		for (MyEdge e : g.getEdges()) {
			MyEdge e1 = new MyEdge(e.getId(), this.nodeFromId(e.getSource()
					.getId()), this.nodeFromId(e.getTarget().getId()));
			this.addEdge(e1);
			e1.setWeight(e.getWeight());
		}
	}

	public double cost() {
		double c = 0;
		for (MyEdge e : this.getEdges()) {
			//System.out.println(e.getId()+" ha peso "+e.getWeight());
			c = c + e.getWeight();
			
		}
		//System.out.println("Total:"+c);
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
			MyNode root = null;
			for (MyNode r : copy.nodes) {
				if (r.getDegree() == 1) {// cerca una foglia di copy da cui
											// partire.
					root = r;
					break;
				}
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
			}
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
		int j=0;
		for (MyNode n : nodes) {
			String[] nodeInfo = info.get(n.getId());
			if (nodeInfo == null) {
				//n.setLabel(null);
				n.setLabel("label"+String.valueOf(j));
				j++;
				//just debug--------
				System.out.println(n.getId()+"--->"+n.getLabel());
				//-------------
			} else {
				n.setLabel(nodeInfo[0]);
				n.setContiglength(Integer.valueOf(nodeInfo[1]));
				//just debug--------
				System.out.println(n.getId()+"--->"+nodeInfo[0]);
				//-------------
			}

		}

	}

	public  MyGraph computeCover() {
		//minor numero di componenti e, tra queste, una di massimo peso.
		MyGraph cover = new MyGraph(this);
		ArrayList<MyNode> leaves = cover.getLeaves();
		while(!cover.isPaths()){
			ArrayList<MyEdge> cuts=null;
			while(cuts==null && !leaves.isEmpty()){
				MyNode leaf = leaves.get(0);
				cuts = cover.followTheVine(leaf);
				leaves.remove(0);
			}
			if(!leaves.isEmpty()){
			for(MyEdge e : cuts ){
				cover.removeEdge(e);
			}
			}
		}		
		return cover;
	}
	
	/*private  ArrayList<MyEdge> computeCuts(MyNode n) {
		ArrayList<MyEdge> notToCut = new ArrayList<MyEdge>(outEdges(n));
		ArrayList<MyEdge> cuts = new ArrayList<MyEdge>();
		Collections.sort(notToCut, new WeightComparator());
		while(notToCut.size()==2){
			MyEdge e= notToCut.get(0);
			cuts.add(e);
			notToCut.remove(e);
		}
		return cuts;

	}*/
	
	public   MyGraph computeCover3() {
		//minor numero di componenti.
		MyGraph cover = new MyGraph(this);
		ArrayList<MyNode> leaves = cover.getLeaves();
		while(!cover.isPaths()){
			ArrayList<MyEdge> cuts=null;
			while(cuts==null && !leaves.isEmpty()){
				MyNode leaf = leaves.get(0);
				cuts = cover.followTheVine(leaf);
				leaves.remove(0);
			}
			if(!leaves.isEmpty()){
			for(MyEdge e : cuts ){
				cover.removeEdge(e);
			}
			}
		}		
		return cover;
	}
	
	public   MyGraph computeCover4() {
		//euristica. Tiene i piu' pesanti ma il peso di quelli condivisi viene dimezzato
		MyGraph cover = new MyGraph(this);
		ArrayList<MyNode> hightDegreeNodes = new ArrayList<MyNode>();
		for(MyNode n : cover.getNodes()){
			if(n.getDegree()>2){
				hightDegreeNodes.add(n);
			}
		}
		for(MyNode node: hightDegreeNodes){
			//dimezza il peso degli archi che non sono vine
			for(MyEdge edge : cover.outEdges(node)){
				if(cover.isNotVine(node, edge)){
					double oldW = edge.getWeight();
					edge.setWeight(oldW/2);
				}
			}
			
		}
		while(!cover.isPaths()){
			ArrayList<MyEdge> cuts=null;
			while(cuts==null && !hightDegreeNodes.isEmpty()){
				MyNode node = hightDegreeNodes.get(0);
				cuts = cover.computeWorstEdges(node);
				hightDegreeNodes.remove(0);
			}
			
			for(MyEdge e : cuts ){
				cover.removeEdge(e);
			}
			
		}		
		return cover;
	}
	
	
	
	
/*	private ArrayList<MyEdge> findCuts(MyNode current) {
			ArrayList<MyEdge>  cuts= new ArrayList<MyEdge>(outEdges(current));
			ArrayList<MyEdge> notToCut  = new ArrayList<MyEdge>();
			int i =0;
			while(notToCut.size()!=2){
				MyEdge candidate = cuts.get(i);
				if(!isNotVine(current, candidate)){
				 notToCut.add(candidate);
				}
				i=i+1;
			}
			return cuts;

	}*/

	private ArrayList<MyEdge> followTheVine(MyNode current) {

		if(current.getDegree()==0){
			return null;
		}
		MyNode first;
		MyEdge e= this.outEdges(current).get(0);//unico in una foglia
		if(current.equals(e.getSource())){
			first=e.getTarget();
		}else{
			first=e.getSource();
		}
		MyNode next = first;
		MyNode previous=current;
		while(next.getDegree()==2){
		ArrayList<MyNode> tmp = new ArrayList<MyNode>(next.getAdj());
		tmp.remove(previous);
		previous=next;
		next=tmp.get(0); 
		}
		ArrayList<MyEdge> cuts;
		if(next.getDegree()==1){
			//ero in un path
			cuts =null;	
		}else{
		//ho trovato un nodo di incrocio
		cuts = new ArrayList<MyEdge>();
		 ArrayList<MyEdge> save = new ArrayList<MyEdge>(this.outEdges(next));
		 ArrayList<MyEdge> toRemove = new ArrayList<MyEdge>();
		 for(MyEdge candidate : save){		
		if(isNotVine(next,candidate)){
				cuts.add(candidate);
				toRemove.add(candidate);
			}
		}
		save.removeAll(toRemove);
		if(save.size()>=2){
		Collections.sort(save, new WeightComparator());
		save.remove(save.size()-1);
		save.remove(save.size()-1);
		cuts.addAll(save);	
		}else{
			Collections.sort(cuts, new WeightComparator());
			cuts.remove(cuts.size()-1);
		}
		
		}
		return cuts;
	}

	private boolean isNotVine(MyNode n, MyEdge e) {
		MyNode first;
		if(n==e.getSource()){
			first=e.getTarget();
		}else{
			first=e.getSource();
		}
		MyNode next = first;
		MyNode previous=n;
		while(next.getDegree()==2){
		ArrayList<MyNode> tmp = new ArrayList<MyNode>(next.getAdj());
		tmp.remove(previous);
		previous=next;
		next=tmp.get(0); 
		}
		if(next.getDegree()>2){
			return true;
		}
		return false;
	}

	private boolean isPaths() {
		for(MyNode n : this.nodes){
			if( n.getDegree()>2){
				return false;
			}
		}
		return true;
	}
	 

	public   MyGraph computeCover2() {
		MyGraph cover = new MyGraph(this);
		for(MyNode n : cover.getNodes()){
			if(n.getDegree()>= 3){
				ArrayList<MyNode> mates= findMates(n) ;
				ArrayList<MyEdge> cuts = cover.computeWorstEdgesForGroup(mates);
				for(MyEdge e : cuts){
				cover.removeEdge(e);
			}	
			}
		}
		
		return cover;
	}


	private ArrayList<MyEdge> computeWorstEdgesForGroup(ArrayList<MyNode> mates) {
		ArrayList<MyEdge> cuts = new ArrayList<MyEdge>( );
		ArrayList<MyEdge> candidateCuts = new ArrayList<MyEdge>( );
		HashMap<MyNode, Integer> degrees =new HashMap<MyNode, Integer>();
		for(MyNode n : mates){
			candidateCuts.addAll(this.outEdges(n));
			degrees.put(n, n.getDegree());
					}
		Collections.sort(candidateCuts, new WeightComparator());
		while(!goodDegree(degrees)){
			MyEdge e = candidateCuts.get(0);
			MyNode s = e.getSource();
			MyNode t = e.getTarget();
			int sD = degrees.get(s);
			int tD = degrees.get(s);
			if(sD>2 || tD>2){
			cuts.add(e);
			degrees.put(t, tD-1);
			degrees.put(s, sD-1);	
			}
			candidateCuts.remove(e);
		}
		return cuts;
	}


	private boolean goodDegree(HashMap<MyNode, Integer> degrees) {
		boolean b = true;
		for( Entry<MyNode, Integer> entry : degrees.entrySet()){
			if(entry.getValue() >2){
				b=false;
			}
		}
		return b;
	}

	private ArrayList<MyNode> findMates(MyNode n) {
		ArrayList<MyEdge> directions= this.outEdges(n);
		MyNode previous = n;
		MyNode next;
		ArrayList<MyNode> mates = new ArrayList<MyNode>();
		for(MyEdge d : directions){
			if(previous==d.getSource()){
				next= d.getTarget();
			} else{
				next=d.getSource();
			}
			while(next.getDegree()==2){
				ArrayList<MyNode> adj = new ArrayList<MyNode>(n.getAdj());
				adj.remove(previous);
				previous= next;
				next = adj.get(0);
				
			}
			if(next.getDegree()>2){
			 mates.add(next);
			}
		}
		return mates;
	}

	private ArrayList<MyEdge> computeWorstEdges(MyNode n) {
		ArrayList<MyEdge> cuts = new ArrayList<MyEdge>( this.outEdges(n));
		Collections.sort(cuts, new WeightComparator());
		cuts.remove(cuts.size()-1);
		cuts.remove(cuts.size()-1);
		return cuts;
	}

	/*private  ArrayList<MyEdge> maximumVinePathCuts(MyNode n) {
		ArrayList<MyEdge> toBeEliminate = new ArrayList<MyEdge>(this.outEdges(n));
		MyEdge one=null;
		MyEdge two=null;
		double best=0;
		for(MyEdge e1 : this.outEdges(n)){
			for(MyEdge e2 : this.outEdges(n)){
				if(e1!=e2){
					double weight1 = this.pathToLeaf(e1,n);
					double weight2 = this.pathToLeaf(e2,n);
					double w=weight1+weight2;
					//nota, i pareggi vanno a caso;
					if(w>best){
						best=w;
						one=e1;
						two=e2;
					}
					}			
				}
			}
		toBeEliminate.remove(one);
		toBeEliminate.remove(two);	
		return toBeEliminate;
	}

	private double pathToLeaf(MyEdge e, MyNode n) {		
		MyNode first;
		if(n==e.getSource()){
			first=e.getTarget();
		}else{
			first=e.getSource();
		}
		MyNode next = first;
		MyNode previous=n;
		double weight=e.getWeight();
		while(next.getDegree()==2){
		ArrayList<MyNode> tmp = new ArrayList<MyNode>(next.getAdj());
		tmp.remove(previous);
		MyEdge nextE = this.getEdgeByST(next, tmp.get(0));
		weight=weight + nextE.getWeight();
		previous=next;
		next=tmp.get(0); 
		}
		if(next.getDegree()>2){
			weight= 0;
		}
		return weight;
	}
*/
	public ArrayList<MyEdge> outEdges(MyNode n) {
		ArrayList<MyEdge> outEdges = new ArrayList<MyEdge>();
		for(MyNode a : n.getAdj()){
			MyEdge e = getEdgeByST(n, a);
			outEdges.add(e);
		}
		return outEdges;
	}
	
	public void deleteZeroEdges(){
		ArrayList<MyEdge> toDelete= new ArrayList<MyEdge>();
		for(MyEdge e : edges){
			if(e.getWeight()==0){
				toDelete.add(e);
			}
		}
		for(MyEdge d : toDelete){
			this.removeEdge(d);
		}
	}
	
	public ArrayList<MyNode> getLeaves(){
		ArrayList<MyNode> leaves = new ArrayList<MyNode>();
		for(MyNode n : this.nodes){
			if(n.getDegree()==1){
				leaves.add(n);
			}
		}
		return leaves;
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public void labelsAsOrder(HashMap<String,String> order){ for(MyNode n :
	 * nodes){ String newLabel = order.get(n.getId()); n.setLabel(newLabel); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * public ArrayList<ArrayList<String>> enumeratePaths(){ MyNode root = null;
	 * 
	 * for(MyNode r : nodes){ if(r.getDegree()==1){//cerca una foglia. root=r;
	 * break; } } ArrayList<ArrayList<String>> paths
	 * =computePaths(root,root.getAdj().get(0)); return paths; } public
	 * ArrayList<ArrayList<String>> computePaths(MyNode root, MyNode next){
	 * ArrayList<ArrayList<String>> ps = new ArrayList<ArrayList<String>>();
	 * 
	 * if(next.getDegree()==1){ ArrayList<String> p = new ArrayList<String>();
	 * p.add(root.getLabel()); p.add(next.getLabel()); ps.add(p); }else{
	 * ArrayList<MyNode> follow = new ArrayList<MyNode>(next.getAdj());
	 * follow.remove(root); for(MyNode f : follow){ for(ArrayList<String> a :
	 * computePaths(next,f)){ ArrayList<String> a1 = new ArrayList<String>(a);
	 * a1.add(0, root.getLabel()); ps.add(a1); } } }
	 * 
	 * return ps; }
	 * 
	 * 
	 *  questa stampa gli label. Mettere gli id // TODO
	private String clearPrintSB2(MyNode root,
			HashMap<MyNode, Integer> originalDegrees) {
		StringBuilder sb = new StringBuilder();
		sb.append(root.getLabel() + "-");
		MyNode current = root.getAdj().get(0);
		MyEdge start = this.getEdgeByST(root, current);
		this.removeEdge(start);
		this.removeNode(root);
		while (originalDegrees.get(current) == 2) {
			sb.append(current.getLabel() + "-");
			MyNode next = current.getAdj().get(0);
			MyEdge e = this.getEdgeByST(current, next);
			this.removeEdge(e);
			this.removeNode(current);
			current = next;
		}
		sb.append(current.getLabel());// l'ultimo lo lascia
		String p = sb.toString();
		return p;
	}
	 * 
	 */


}
