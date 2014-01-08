package utilities;

import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

	static public MyGraph read(String fileName)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(fileName));
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

	public static MyGraph read(String fileName, double sigma, double omega)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(fileName));
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
			Double weight = Double.parseDouble(current.getAttribute("weight"));
			MyNode ns = graph.nodeFromId(source);
			MyNode nt = graph.nodeFromId(target);
			MyEdge e = new MyEdge(id, ns, nt);
			if(weight == null){
			NodeList edgeAttributes = ((Element) current.getElementsByTagName(
					"attvalues").item(0)).getElementsByTagName("attvalue");
			double s = 0;
			double h = 0;
			boolean findW = false;
			for (int j = 0; j < edgeAttributes.getLength(); ++j) {
				Element ea = (Element) edgeAttributes.item(j);
				// se esiste l'attributo weight usa quello.
				if (ea.getAttribute("for") != null
						&& ea.getAttribute("for").equals(
								attributeMap.get("weight"))) {
					e.setWeight(Double.parseDouble(ea.getAttribute("value")));
					findW = true;
				} else {// altrimenti calcola il peso combinando synteny e
						// homology con coefficienti sigma e omega
					if (ea.getAttribute("for") != null
							&& ea.getAttribute("for").equals(
									attributeMap.get("synteny")) & !findW) {
						s = Double.parseDouble(ea.getAttribute("value"));
						double w = e.getWeight();
						e.setWeight((sigma * s) + w);
					}
					if (ea.getAttribute("for") != null
							&& ea.getAttribute("for").equals(
									attributeMap.get("homology")) & !findW) {
						h = Double.parseDouble(ea.getAttribute("value"));
						double w = e.getWeight();
						e.setWeight((omega * h) + w);
					}	
			}
			

				}
			} else{
				e.setWeight(weight);
			}

			graph.addEdge(e);
		}
		return graph;
	}

}
