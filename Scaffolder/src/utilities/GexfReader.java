package utilities;

import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GexfReader {

	private GexfReader() {

	}

	static public MyGraph read(InputStream input)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(input);
		MyGraph graph = new MyGraph();
		HashMap<String, String> attributeMap = new HashMap<String, String>();
		Element graphNode = (Element) doc.getElementsByTagName("graph").item(0);
		// Legge l'indice degli attributi.
		NodeList attributes = ((Element) graphNode.getElementsByTagName(
				"attributes").item(0)).getElementsByTagName("attribute");
		for (int i = 0; i < attributes.getLength(); ++i) {
			Element attribute = (Element) attributes.item(i);
			attributeMap.put(attribute.getAttribute("title"),
					attribute.getAttribute("id"));
		}
		// Legge i nodi.
		Element nodesElement = (Element) graphNode
				.getElementsByTagName("nodes").item(0);
		NodeList nodes = nodesElement.getElementsByTagName("node");
		for (int i = 0; i < nodes.getLength(); ++i) {
			Element current = (Element) nodes.item(i);
			String id = current.getAttribute("id");
			String label = current.getAttribute("label");
			MyNode n = new MyNode(id, label);
			NodeList attvaluesNodes = current.getElementsByTagName("attvalues");
			if (attvaluesNodes.getLength() == 1) {
				// controlla che ci sia un singolo nodo attvalue
				NodeList nodeAttributes = ((Element) attvaluesNodes.item(0))
						.getElementsByTagName("attvalue");
				for (int j = 0; j < nodeAttributes.getLength(); ++j) {
					Element na = (Element) nodeAttributes.item(j);
					if (na.getAttribute("for") != null
							&& na.getAttribute("for").equals(
									attributeMap.get("length"))) {
						n.setContiglength(Integer.parseInt(na
								.getAttribute("value")));
					}
				}
			}

			graph.addNode(n);
		}
		// Legge gli archi.
		Element edgesElement = (Element) graphNode
				.getElementsByTagName("edges").item(0);
		NodeList edges = edgesElement.getElementsByTagName("edge");
		for (int i = 0; i < edges.getLength(); ++i) {
			Element current = (Element) edges.item(i);
			String id = current.getAttribute("id");
			String source = current.getAttribute("source");
			String target = current.getAttribute("target");
			MyNode ns = graph.nodeFromId(source);
			MyNode nt = graph.nodeFromId(target);
			MyEdge e = new MyEdge(id, ns, nt);
			NodeList edgeAttributes = ((Element) current.getElementsByTagName(
					"attvalues").item(0)).getElementsByTagName("attvalue");
			for (int j = 0; j < edgeAttributes.getLength(); ++j) {
				Element ea = (Element) edgeAttributes.item(j);
				// se esiste l'attributo weight usa quello.
				if (ea.getAttribute("for") != null
						&& ea.getAttribute("for").equals(
								attributeMap.get("weight"))) {
					e.setWeight(Double.parseDouble(ea.getAttribute("value")));

				}
			}

			graph.addEdge(e);
		}
		return graph;
	}

	public static HashMap<String, String[]> readContigInfo(String fileName)
			throws IOException {
		HashMap<String, String[]> contigInfo = new HashMap<String, String[]>();
		File f = new File(fileName);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String currentLine = br.readLine();
		int i = 0;
		
		while (currentLine != null) {
			String contigPosition = String.valueOf(i);
			String[] split = currentLine.split("\t");
			String id = split[0];
			String length = split[1].replaceFirst("\t", "");
			String[] info = { contigPosition, length };
			if (contigInfo.get(id) != null) {
				//contiguo ha piu' posizioni
				String[] infoVecchio = contigInfo.get(id);
				String pos1 = infoVecchio[0];
				String pos2 = info[0];
				info[0] = pos1 + "or" + pos2;
			}
			contigInfo.put(id, info);
			currentLine = br.readLine();
			i++;
		}
		br.close();
		return contigInfo;
	}

	/*
	 * public static HashMap<String, String> readContigOrder(String fileName)
	 * throws IOException{ HashMap<String,String> contigOrder = new
	 * HashMap<String, String>(); File f = new File(fileName); FileReader fr =
	 * new FileReader(f); BufferedReader br = new BufferedReader(fr); String
	 * currentLine = br.readLine(); int i=0; while(currentLine != null){ String
	 * contigPosition= String.valueOf(i); int endIndex=
	 * currentLine.indexOf("\t"); String id =currentLine.substring(0, endIndex);
	 * contigOrder.put(id, contigPosition); currentLine=br.readLine(); i++; }
	 * br.close(); return contigOrder; }
	 */

	public static HashMap<String, Double> readBc(String fileName)
			throws IOException {
		HashMap<String, Double> bcMap = new HashMap<String, Double>();
		File f = new File(fileName);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String currentLine = br.readLine();

		while (currentLine != null) {

			String[] splitLine = currentLine.split("\t");
			String id = splitLine[0];
			Double contigBC = Double.parseDouble(splitLine[1]);
			bcMap.put(id, contigBC);
			currentLine = br.readLine();
		}
		br.close();
		return bcMap;

	}

	public static MyGraph read(InputStream input, double sigma, double omega)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(input);
		MyGraph graph = new MyGraph();
		HashMap<String, String> attributeMap = new HashMap<String, String>();//attributi archi
		HashMap<String, String> attributeMap2 = new HashMap<String, String>();//attributi nodi
		Element graphNode = (Element) doc.getElementsByTagName("graph").item(0);
		// Legge l'indice degli attributi.//ne deve leggere due gruppi 
		NodeList attributeList = graphNode.getElementsByTagName(
				"attributes");
		if(attributeList.getLength()!=0){
		NodeList attributes = ((Element) graphNode.getElementsByTagName(
				"attributes").item(0)).getElementsByTagName("attribute");
		
		for (int i = 0; i < attributes.getLength(); ++i) {
			Element attribute = (Element) attributes.item(i);
			attributeMap.put(attribute.getAttribute("title"),
					attribute.getAttribute("id"));
		}
		NodeList attributesN = ((Element) graphNode.getElementsByTagName(
				"attributes").item(1)).getElementsByTagName("attribute");
		for (int i = 0; i < attributesN.getLength(); ++i) {
			Element attribute = (Element) attributesN.item(i);
			
			attributeMap2.put(attribute.getAttribute("title"),
					attribute.getAttribute("id"));
		}
		}
		
		// Legge i nodi.
		Element nodesElement = (Element) graphNode
				.getElementsByTagName("nodes").item(0);
		NodeList nodes = nodesElement.getElementsByTagName("node");
		for (int i = 0; i < nodes.getLength(); ++i) {
			Element current = (Element) nodes.item(i);
			String id = current.getAttribute("id");
			String label = current.getAttribute("label");
			MyNode n = new MyNode(id, label);
			
			String nodelenghtString = current.getAttribute("lenght");//cerca se nel nodo c'e' l'attributo 
			if(nodelenghtString == null || nodelenghtString.equals("") ){
				//altrimenti lo cerca nel sotto albero degli attributi e pone la lenght come la media della distanza
			NodeList nodeAttributes = ((Element) current.getElementsByTagName(
					"attvalues").item(0)).getElementsByTagName("attvalue");
			for (int j = 0; j < nodeAttributes.getLength(); ++j) {
				Element ea = (Element) nodeAttributes.item(j);
				if (ea.getAttribute("for") != null
						&& ea.getAttribute("for").equals(
								attributeMap2.get("length"))) {
					n.setContiglength(Integer.parseInt(ea.getAttribute("value")));
				} else {
					System.out.println("ERROR: missing attribute lenght");
			}}
			} else{
				int l = Integer.parseInt(nodelenghtString);
				n.setContiglength(l);
			}
			graph.addNode(n);
		}
		

		
		// Legge gli archi.
		Element edgesElement = (Element) graphNode
				.getElementsByTagName("edges").item(0);
		NodeList edges = edgesElement.getElementsByTagName("edge");
		for (int i = 0; i < edges.getLength(); ++i) {
			Element current = (Element) edges.item(i);
			String id = current.getAttribute("id");
			String source = current.getAttribute("source");
			String target = current.getAttribute("target");
			MyNode ns = graph.nodeFromId(source);
			MyNode nt = graph.nodeFromId(target);
			MyEdge e = new MyEdge(id, ns, nt);
			String weightString = current.getAttribute("weight");//cerca se nel nodo c'e' l'attributo weight
			if(weightString == null || weightString.equals("") ){//altrimenti lo cerca nel sotto albero degli attributi
			NodeList edgeAttributes = ((Element) current.getElementsByTagName(
					"attvalues").item(0)).getElementsByTagName("attvalue");;
			for (int j = 0; j < edgeAttributes.getLength(); ++j) {
				Element ea = (Element) edgeAttributes.item(j);
				// se esiste l'attributo weight usa quello.
				if (ea.getAttribute("for") != null
						&& ea.getAttribute("for").equals(
								attributeMap.get("weight"))) {
					e.setWeight(Double.parseDouble(ea.getAttribute("value")));
				} else {
					System.out.println("ERROR: missing attribute weight");
			}}
			} else{
				Double weight = Double.parseDouble(weightString);
				e.setWeight(weight);
			}
			
			String distanceString = current.getAttribute("distance");//cerca se nel nodo c'e' l'attributo distance
			if(distanceString == null || distanceString.equals("") ){
				//altrimenti lo cerca nel sotto albero degli attributi e pone la lenght come la media della distanza
			NodeList edgeAttributes = ((Element) current.getElementsByTagName(
					"attvalues").item(0)).getElementsByTagName("attvalue");
			for (int j = 0; j < edgeAttributes.getLength(); ++j) {
				Element ea = (Element) edgeAttributes.item(j);
				// se esiste l'attributo weight usa quello.
				if (ea.getAttribute("for") != null
						&& ea.getAttribute("for").equals(
								attributeMap.get("distance"))) {
					e.setLenght(Integer.parseInt(ea.getAttribute("value"))/e.getWeight());
				} else {
					System.out.println("ERROR: missing attribute distance");
			}}
			} else{
				double distance = Double.parseDouble(distanceString);
				e.setLenght(distance/e.getWeight());
			}

			//System.out.println("P="+e.getWeight()+"L="+e.getLenght());//debug
			graph.addEdge(e);
		}
		return graph;
	}

}
