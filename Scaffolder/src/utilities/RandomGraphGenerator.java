package utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;
import graphs.spanningTree.StEnumerator;

public class RandomGraphGenerator {
	
	
	
	
	public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException{
		String name= "grafoProvaBuono"; 
		int nodes = 100;
		double p = 0.05;//probability to have an edge
		MyGraph g = GenerateRandomGraph(nodes, name, 5, 10, 0, 0);//nodes ,fileName, minGood, maxGood, minBad, maxBad
		GexfWriter.write(g, name);
		System.out.println("Create a graph ("+name+") with "+g.getNodes().size()+ " nodes and "+g.getEdges().size()+" edges");
		CreateInfoFile(g, "info_"+name,false);
		System.out.println("Create a file (info_"+name+")");
		
	}
	
	
	
	
	public static MyGraph GenerateRandomGraph2(int n, double p) {
		MyGraph g = new MyGraph();
		for (int i = 0; i < n; i++) {
			g.addNode(new MyNode("id"+String.valueOf(i), ""));
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				if (Math.random() < p) {
					MyEdge e = new MyEdge("e" + j + i, g.nodeFromId("id"+j),  g.nodeFromId("id" + i));
					//e.setWeight(Math.random());//PESI RANDOM
					e.setWeight(1);
					g.addEdge(e);
				}
			}
		}
		
		return g;
	}
	
	
	private static void CreateInfoFile(MyGraph g, String fileName, Boolean random) throws IOException{ //for a fake net where the id are integers from 0 to k-1. 
	File file = new File(fileName);
	FileWriter writer = new FileWriter(file);
	ArrayList<String> v = new ArrayList<String>();
	for(MyNode n : g.getNodes()){
		v.add(n.getId());
	}
	if(random){
	Collections.shuffle(v);// random order.	
	}
	for(int i=0;i<v.size();++i){
		writer.write(v.get(i)+"\t"+"1"+"\n");//crea un file con gli id dei nodi e la lenght.
	}
	writer.flush();	
	}
	
	
	private static MyGraph GenerateRandomGraph(int n, String name, int m1, int m2, int m3, int m4){
		MyGraph g = new MyGraph();
		MyNode node0 = new MyNode("id"+Integer.toString(0),"");
		g.addNode(node0);
		//archi corretti hanno peso da m1 a m2
	for(int i=1; i<n; i++){
		MyNode node1 = new MyNode("id"+Integer.toString(i),"");
		MyNode node2 = g.nodeFromId("id"+Integer.toString(i-1));			
		g.addNode(node1);
		MyEdge edge = new MyEdge(Integer.toString(i-1)+"/"+Integer.toString(i), node2, node1);
		int w = (int) (Math.random()*m2)+m1;
		if(w!=0){
		edge.setWeight(w);
		g.addEdge(edge);	
		}
	}
	//archi sabagliati  hanno peso da m3 a m4
	for(int i=1; i<100; i++){
		int i1 = (int) (Math.random()*n);
		int i2 = (int) (Math.random()*n);
		if(!(i1==i2)){ 
			MyNode node1 = g.nodeFromId("id"+Integer.toString(i1));
			MyNode node2 = g.nodeFromId("id"+Integer.toString(i2));
			MyEdge edge = new MyEdge(Integer.toString(i1)+"/"+Integer.toString(i2), node1, node2);
			int w = (int) (Math.random()*m4)+m3;
			edge.setWeight(w);
			g.addEdge(edge);
			System.out.println(edge.toStringVerbose());
		}
		
	}
	return g;
	
	}
	
	
}



