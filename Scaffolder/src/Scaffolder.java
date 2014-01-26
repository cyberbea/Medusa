import graphs.MyEdge;
import graphs.MyGraph;
import graphs.MyNode;
import graphs.spanningTree.Kruskal;
import graphs.spanningTree.StEnumerator;
import hs.HSP.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.xml.sax.SAXException;

import utilities.GexfReader;
import utilities.GexfWriter;
import utilities.GraphHSPadapter;

public class Scaffolder {

	public static void main(String[] args)
			throws org.apache.commons.cli.ParseException, ParseException,
			ParserConfigurationException, SAXException, IOException,
			TransformerException {
		new Scaffolder(args);
	}

	public Scaffolder(String[] args)
			throws org.apache.commons.cli.ParseException, ParseException,
			ParserConfigurationException, SAXException, IOException,
			TransformerException {
		runOnTerminal(args);
	}

	@SuppressWarnings("static-access")
	private void runOnTerminal(String[] args)
			throws org.apache.commons.cli.ParseException, ParseException,
			ParserConfigurationException, SAXException, IOException,
			TransformerException {
		Options opts = new Options();

		Option output = OptionBuilder.withArgName("<file>").hasArgs(1)
				.withValueSeparator()
				.withDescription("Save the console output on a file")
				.create("output");
		opts.addOption(output);

		Option scaffolder = OptionBuilder
				.withArgName(
						"<originalTree> <syntenyFactor> <homologyFactor> <deduplicate> <infoFile>")
				.hasArgs(5)
				.withValueSeparator()
				.withDescription(
						"The whole process: Graph--> ST ---> COVER. He produces three more files: _ST, _COVER, _RESULTS")
				.create("scaff");
		opts.addOption(scaffolder);
		
		Option scaffolderHS = OptionBuilder
				.withArgName(
						"<originalTree> <syntenyFactor> <homologyFactor> <deduplicate> <infoFile>")
				.hasArgs(5)
				.withValueSeparator()
				.withDescription(
						"The whole process: Graph--> HS ---> MINIMAL HS AS COVER")
				.create("scaffHS");
		opts.addOption(scaffolderHS);
		
		Option multiple = OptionBuilder
				.withArgName(
						"<originalTree> <syntenyFactor> <homologyFactor> <numberTrees> <deduplicate> <infoFile>")
				.hasArgs(6)
				.withValueSeparator()
				.withDescription(
						"The whole process: Graph--> ST ---> COVER. For a given number of maxST")
				.create("multiple");
		opts.addOption(multiple);

		Option spanningTree = OptionBuilder
				.withArgName(
						"<originalTree> <syntenyFactor> <homologyFactor> <deduplicate> <orderFile>")
				.hasArgs(5)
				.withValueSeparator()
				.withDescription(
						"Produce a spanning tree of a gexf format network")
				.create("st");
		opts.addOption(spanningTree);

		Option amplifier = OptionBuilder
				.withArgName("<originalTree> <factor> <numberOfTrees> ")
				.hasArgs(3)
				.withValueSeparator()
				.withDescription(
						"Change the weights of the tree according to all the optimal st")
				.create("ampl");
		opts.addOption(amplifier);

		Option paths = OptionBuilder
				.withArgName("<STree>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"Produce a txt file with analysis and redundant scaffolds")
				.create("pt");
		opts.addOption(paths);
		Option cover = OptionBuilder
				.withArgName("<STree>")
				.hasArgs(2)
				.withValueSeparator()
				.withDescription(
						"Produce a textual file with a weighted path cover of the tree")
				.create("cover");
		opts.addOption(cover);

		Option chains = OptionBuilder
				.withArgName("<STree>")
				.hasArgs(1)
				.withValueSeparator()
				.withDescription(
						"Produce a txt file with analysis and strict subchains as scaffolds")
				.create("ch");
		opts.addOption(chains);

		Option enumerator = OptionBuilder
				.withArgName("<gexfGraph><threshold>")
				.hasArgs(6)
				.withValueSeparator()
				.withDescription(
						"Use an enumerator of ST in order to generate the trees of cost<=opt*threshold")
				.create("enum");
		opts.addOption(enumerator);

		BasicParser bp = new BasicParser();
		try {

			CommandLine cl = bp.parse(opts, args);

			if (cl.hasOption("st")) {
				/* produce the .gexf file of a maximum spanning tree */
				spanningTree(cl);
			} else if (cl.hasOption("pt")) {
				/* restituisce una lista di sottocammini */
				paths(cl);
				// } else if (cl.hasOption("ch")) {
				// /* restituisce le sottocatene */
				// chains(cl);
			} else if (cl.hasOption("enum")) {
				/* usa l'enumeratore */
				enumerator(cl);
			} else if (cl.hasOption("cover")) {
				pathCover(cl);
			} else if (cl.hasOption("ampl")) {
				// restituisce una rete dove gli archi sono ri-pesati in base al
				// numero di st in cui compaiono
				amplifier(cl);
			} else if (cl.hasOption("scaff")) {
				scaffolder(cl);
			}
			else if (cl.hasOption("scaffHS")) {
				scaffolderHS(cl);
			}

		} catch (UnrecognizedOptionException uoe) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("avalaible options", opts);
		}
	}

	private void spanningTree(CommandLine cl)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException {
		String gexfFileName = cl.getOptionValues("st")[0];
		double sigma = Double.parseDouble(cl.getOptionValues("st")[1]);
		double omega = Double.parseDouble(cl.getOptionValues("st")[2]);
		boolean deduplicate = false;
		String orderFileName = null;
		if (cl.getOptionValues("st").length > 3) {
			deduplicate = Boolean.parseBoolean(cl.getOptionValues("st")[3]);
		}
		if (cl.getOptionValues("st").length > 4) {
			orderFileName = cl.getOptionValues("st")[4];
		}
		String outputFileName = cl.getOptionValue("output");
		MyGraph grafo = GexfReader.read(gexfFileName, sigma, omega);
		// todo leggere weight se c'e'??
		if (deduplicate) {
			grafo.deduplicateEdge();
		}
		if (orderFileName != null) {
			HashMap<String, String[]> info = GexfReader
					.readContigInfo(orderFileName);
			grafo.setInfo(info);
		}
		grafo.removeSingletons();
		MyGraph maxST = Kruskal.maxST(grafo);
		GexfWriter.write(maxST, outputFileName);

	}

	private void paths(CommandLine cl) throws ParserConfigurationException,
			SAXException, IOException, TransformerException {
		String gexfST = cl.getOptionValues("pt")[0];
		String outputFileName = cl.getOptionValue("output");
		PrintWriter output = null;
		if (outputFileName != null) {
			File outputFile = new File(outputFileName);
			output = new PrintWriter(new FileWriter(outputFile));
		} else {
			output = new PrintWriter(System.out, true);
		}

		MyGraph maxST = GexfReader.read(gexfST);
		ArrayList<String> paths = maxST.subPaths();
		Evaluation evaluation = evaluator(maxST);
		int goodPCR = evaluation.getGood();// goodPCR(paths);
		int placedNodes = maxST.notSingletons();
		int nullLabelsedges = evaluation.getNullLabel();
		int numberOfScaffolds = paths.size();
		int totalLength = computeLenght(paths);
		String breakpoints = String.valueOf(evaluation.getErrors());// badPCR(paths));
		output.println("#nodes: " + maxST.getNodes().size()
				+ "( not singletons: " + placedNodes + ")");
		output.println("#edges: " + maxST.getEdges().size() + "\n"
				+ "Good PCR: " + goodPCR + "\n" + "Breakpoints: " + breakpoints
				+ "\n" + "Nulli: " + nullLabelsedges);
		output.println("#scaffolds: " + numberOfScaffolds);
		output.println("Total length: " + totalLength);
		for (String a : paths) {
			output.println(a);
		}
		output.flush();
	}

	private int computeLenght(ArrayList<String> paths) {
		int l = 0;
		String current;
		for (int i = 0; i < paths.size(); i++) {
			current = paths.get(i);
			String[] currentSplit = current.split("@");
			int le = Integer.parseInt(currentSplit[1].replaceFirst("@", ""));
			l = l + le;
		}
		return l;
	}

	private Evaluation evaluator(MyGraph st) {
		Evaluation evaluation = new Evaluation();
		int good = 0;
		int bad = 0;
		int nullLabel = 0;
		for (MyEdge e : st.getEdges()) {

			String ls = e.getSource().getLabel();
			String lt = e.getTarget().getLabel();

				if (!ls.contains("label") && !lt.contains("label")) {
				boolean found = false;
				if (ls.contains("or")) {
					String[] lss = ls.split("or");
					if (lt.contains("or")) {
						// entrambe
						String[] lts = lt.split("or");
						for (String s : lss) {
							for (String l : lts) {
								int v1 = Integer.parseInt(s);
								int v2 = Integer.parseInt(l);
								int diff = Math.abs(v1 - v2);
								if (diff == 1) {
									found = true;
								}

							}
						}
					} else {
						// solo la prima
						for (String s : lss) {
							int v1 = Integer.parseInt(s);
							int v2 = Integer.parseInt(lt);
							int diff = Math.abs(v1 - v2);
							if (diff == 1) {
								found = true;
							}
						}
					}
				} else if (lt.contains("or")) {
					// solo la seconda
					String[] l2ss = lt.split("or");
					for (String l : l2ss) {
						int v1 = Integer.parseInt(ls);
						int v2 = Integer.parseInt(l);
						int diff = Math.abs(v1 - v2);
						if (diff == 1) {
							found = true;
						}
					}
				} else {
					// nessuna
					int v1 = Integer.parseInt(ls);
					int v2 = Integer.parseInt(lt);
					int diff = Math.abs(v1 - v2);
					if (diff == 1) {
						found = true;
					}
				}
				if (found == true) {
					good++;
				} else {
					bad++;
				}

			} else {
				nullLabel++;
			}
		}
		evaluation.setCost(st.cost());
		evaluation.setErrors(bad);
		evaluation.setGood(good);
		evaluation.setNullLabel(nullLabel);
		return evaluation;

	}

	private void pathCover(CommandLine cl) throws ParserConfigurationException,
			SAXException, IOException, TransformerException {
		String gexfST = cl.getOptionValues("cover")[0];
		String outputFileName = cl.getOptionValue("output");
		PrintWriter output = null;
		if (outputFileName != null) {
			File outputFile = new File(outputFileName);
			output = new PrintWriter(new FileWriter(outputFile));
		} else {
			output = new PrintWriter(System.out, true);
			output.println("FILE DI INPUT: " + gexfST);
		}
		MyGraph maxST = GexfReader.read(gexfST);

		MyGraph cover = maxST.computeCover();
		ArrayList<String> paths = cover.subPaths();
		Evaluation evaluation = evaluator(cover);
		double cost = evaluation.getCost();
		int goodPCR = evaluation.getGood();
		int placedNodes = maxST.notSingletons();
		int nullLabelsedges = evaluation.getNullLabel();

		int totalLength = computeLenght(paths);
		int finalSingletons = cover.getNodes().size() - cover.notSingletons();
		int numberOfScaffolds = paths.size() + finalSingletons;
		String breakpoints = String.valueOf(evaluation.getErrors());
		output.println("#nodes: " + maxST.getNodes().size() + "( singletons: "
				+ (maxST.getNodes().size() - placedNodes) + ")");
		output.println("#edges: " + maxST.getEdges().size() + "\n"
				+ "\nGood PCR: " + goodPCR + "\n" + "Breakpoints: "
				+ breakpoints + "\n" + "Nulli: " + nullLabelsedges);
		output.println("#scaffolds: " + numberOfScaffolds + "(singletons= "
				+ finalSingletons + ")");
		output.println("Total length: " + totalLength);
		output.println("Total weight: " + cost);
		for (String a : paths) {
			output.println(a);
		}
		output.flush();
	}

	/*
	 * private void chains(CommandLine cl) throws IOException,
	 * ParserConfigurationException, SAXException { String gexfST =
	 * cl.getOptionValues("ch")[0]; String outputFileName =
	 * cl.getOptionValue("output"); PrintWriter output = null; if
	 * (outputFileName != null) { File outputFile = new File(outputFileName);
	 * output = new PrintWriter(new FileWriter(outputFile)); } else { output =
	 * new PrintWriter(System.out, true); }
	 * 
	 * MyGraph maxST = GexfReader.read(gexfST); ArrayList<String> chains =
	 * maxST.subChains(); int goodPCR = goodPCR(chains); int placedNodes =
	 * maxST.notSingletons(); int numberOfScaffolds = chains.size(); int
	 * totalLength = computeLenght(chains); String breakpoints =
	 * String.valueOf(badPCR(chains)); output.println("#nodes: " +
	 * maxST.getNodes().size() + "( not singletons: " + placedNodes + ")");
	 * output.println("#edges: " + maxST.getEdges().size() + "\n" + "Good PCR: "
	 * + goodPCR + "\n" + "Breakpoints: " + breakpoints);
	 * output.println("#scaffolds: " + numberOfScaffolds);
	 * output.println("Total length: " + totalLength); for (String a : chains) {
	 * output.println(a); } output.flush();
	 * 
	 * }
	 * 
	 * private int badPCR(ArrayList<String> paths) { int n = 0; String current;
	 * for (int i = 0; i < paths.size(); i++) { current = paths.get(i); String[]
	 * currentSplit = current.split("-"); for (int k = 0; k <
	 * currentSplit.length - 1; k++) { String l1 = currentSplit[k]; String l2 =
	 * currentSplit[k + 1]; int v1 = 0; int v2 = 1; if (l1.length() != 0 &
	 * l2.length() != 0 & !l2.contains("@")) { boolean found = false; if
	 * (l1.contains("or")) { String[] l1ss = l1.split("or"); if
	 * (l2.contains("or")) { // entrambe String[] l2ss = l2.split("or"); for
	 * (String s : l1ss) { for (String l : l2ss) { v1 = Integer.parseInt(s); v2
	 * = Integer.parseInt(l); int diff = Math.abs(v1 - v2); if (diff == 1) {
	 * found = true; }
	 * 
	 * } } } else { // solo la prima for (String s : l1ss) { v1 =
	 * Integer.parseInt(s); v2 = Integer.parseInt(l2); int diff = Math.abs(v1 -
	 * v2); if (diff == 1) { found = true; } } } } else if (l2.contains("or")) {
	 * // solo la seconda String[] l2ss = l2.split("or"); for (String l : l2ss)
	 * { v1 = Integer.parseInt(l1); v2 = Integer.parseInt(l); int diff =
	 * Math.abs(v1 - v2); if (diff == 1) { found = true; } } } else { // nessuna
	 * v1 = Integer.parseInt(l1); v2 = Integer.parseInt(l2); int diff =
	 * Math.abs(v1 - v2); if (diff == 1) { found = true; } } if (found == false)
	 * { n = n + 1; } } } } return n; }
	 * 
	 * 
	 * private int goodPCR(ArrayList<String> paths) { int n = 0; String current;
	 * for (int i = 0; i < paths.size(); i++) { current = paths.get(i); String[]
	 * currentSplit = current.split("-"); for (int k = 0; k <
	 * currentSplit.length - 1; k++) { String l1 = currentSplit[k]; String l2 =
	 * currentSplit[k + 1]; int v1 = 0; int v2 = 100; if (l1.length() != 0 &
	 * l2.length() != 0 & !l2.contains("@")) {
	 * 
	 * if (l1.contains("or")) { String[] l1ss = l1.split("or"); if
	 * (l2.contains("or")) { // entrambe String[] l2ss = l2.split("or"); for
	 * (String s : l1ss) { for (String l : l2ss) { v1 = Integer.parseInt(s); v2
	 * = Integer.parseInt(l); int diff = Math.abs(v1 - v2); if (diff == 1) { n =
	 * n + 1; }
	 * 
	 * } } } else { // solo la prima for (String s : l1ss) { v1 =
	 * Integer.parseInt(s); v2 = Integer.parseInt(l2); int diff = Math.abs(v1 -
	 * v2); if (diff == 1) { n = n + 1; } } } } else if (l2.contains("or")) { //
	 * solo la seconda String[] l2ss = l2.split("or"); for (String l : l2ss) {
	 * v1 = Integer.parseInt(l1); v2 = Integer.parseInt(l); int diff =
	 * Math.abs(v1 - v2); if (diff == 1) { n = n + 1; } } } else { // nessuna v1
	 * = Integer.parseInt(l1); v2 = Integer.parseInt(l2); int diff = Math.abs(v1
	 * - v2); if (diff == 1) { n = n + 1; } } } } } return n; }
	 */

	private void enumerator(CommandLine cl)
			throws ParserConfigurationException, SAXException, IOException {
		String gexfGraph = cl.getOptionValues("enum")[0];
		double sigma = Double.parseDouble(cl.getOptionValues("enum")[1]);
		double omega = Double.parseDouble(cl.getOptionValues("enum")[2]);
		Double thresholdFactor = Double.valueOf(cl.getOptionValues("enum")[3]);
		boolean deduplicate = false;
		String orderFileName = null;
		if (cl.getOptionValues("enum").length > 4) {
			deduplicate = Boolean.parseBoolean(cl.getOptionValues("enum")[4]);
		}
		if (cl.getOptionValues("enum").length > 5) {
			orderFileName = cl.getOptionValues("enum")[5];
		}
		MyGraph grafo = GexfReader.read(gexfGraph, sigma, omega);
		// todo leggere weight se c'e'??
		if (deduplicate) {
			grafo.deduplicateEdge();
		}
		if (orderFileName != null) {
			HashMap<String, String[]> info = GexfReader
					.readContigInfo(orderFileName);
			grafo.setInfo(info);
		}

		StEnumerator enumeratore = new StEnumerator(grafo);

		MyGraph st = enumeratore.next();// il primo
		int i = 1;
		double opt = st.cost();
		double currentCost = opt;
		double threshold = opt * thresholdFactor;

		while (currentCost >= threshold) {
			if (st == null) {
				break;
			} else {
				// GexfWriter.write(st, i+"_"+st.cost()+".gexf");
				System.out.println(i + "\n" + st.toString() + "\n" + "Costo: "
						+ st.cost());
				st = enumeratore.next();
				currentCost = st.cost();
				++i;
			}
		}

	}

	private void amplifier(CommandLine cl) throws ParserConfigurationException,
			SAXException, IOException, TransformerException {
		String gexfGraph = cl.getOptionValues("ampl")[0];
		MyGraph amplifiedGraph = GexfReader.read(gexfGraph);

		double amplificationFactor = Double
				.valueOf(cl.getOptionValues("ampl")[1]);
		int numberOfTrees = Integer.valueOf(cl.getOptionValues("ampl")[2]);
		ArrayList<MyGraph> trees = new ArrayList<MyGraph>();
		StEnumerator enumeratore = new StEnumerator(amplifiedGraph);
		MyGraph st = enumeratore.next();
		while (st != null && trees.size() < numberOfTrees) {
			trees.add(st);
			st = enumeratore.next();
		}
		HashMap<MyEdge, Integer> map = new HashMap<MyEdge, Integer>();
		for (MyGraph t : trees) {
			for (MyEdge e : t.getEdges()) {
				String sourceL = e.getSource().getId();
				MyNode source = amplifiedGraph.nodeFromId(sourceL);
				String targetL = e.getTarget().getId();
				MyNode target = amplifiedGraph.nodeFromId(targetL);
				MyEdge edge = amplifiedGraph.getEdgeByST(source, target);
				if (map.keySet().contains(edge)) {
					int i = map.get(edge);
					int j = i + 1;
					map.put(edge, j);
				} else {
					map.put(edge, 1);
				}
			}
		}
		System.out.println(amplifiedGraph.toStringVerbose());
		for (MyEdge current : amplifiedGraph.getEdges()) {
			if (map.containsKey(current)) {
				int i = map.get(current);

				double w = current.getWeight();
				double w1 = w * i * amplificationFactor;
				current.setWeight(w1);
			}

		}
		System.out.println("generati " + trees.size() + " alberi.");
		System.out.println(amplifiedGraph.toStringVerbose());
		GexfWriter.write(amplifiedGraph, gexfGraph + "amplified");
		MyGraph newST = Kruskal.maxST(amplifiedGraph);
		System.out.println("Spanning Tree risultante:\n"
				+ newST.toStringVerbose());
		GexfWriter.write(newST, gexfGraph + "_amp_ST");
	}

	private void scaffolder(CommandLine cl)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException {
		String gexfFileName = cl.getOptionValues("scaff")[0];
		double sigma = Double.parseDouble(cl.getOptionValues("scaff")[1]);
		double omega = Double.parseDouble(cl.getOptionValues("scaff")[2]);

		String orderFileName = null;

		if (cl.getOptionValues("scaff").length > 3) {
			orderFileName = cl.getOptionValues("scaff")[3];
		}
		String StFileName = gexfFileName + "_ST";
		MyGraph grafo = GexfReader.read(gexfFileName, sigma, omega);

		// is the deduplication of the edges still necessary??
		// boolean deduplicate = false;
		// if (cl.getOptionValues("scaff").length > 3) {
		// deduplicate = Boolean.parseBoolean(cl.getOptionValues("st")[3]);
		// }
		// if (deduplicate) {
		// grafo.deduplicateEdge();
		// }

		if (orderFileName != null) {
			HashMap<String, String[]> info = GexfReader
					.readContigInfo(orderFileName);
			grafo.setInfo(info);
		}
		grafo.removeSingletons();
		//DEBUG
		System.out.println("PESI: ");
		for(MyEdge e : grafo.getEdges()){
			System.out.println(e.toStringVerbose());
		}
		//
		GexfWriter.write(grafo, gexfFileName+"_LABELLED");
		MyGraph maxST = Kruskal.maxST(grafo);
		GexfWriter.write(maxST, StFileName);

		MyGraph cover = maxST.computeCover();
		GexfWriter.write(cover, gexfFileName + "_COVER");
		ArrayList<String> paths = cover.subPaths();

		File outputFile = new File(gexfFileName + "_RESULTS");
		PrintWriter writerOutput = new PrintWriter(new FileWriter(outputFile));
		writerOutput.write("Network: " + gexfFileName + "\n");
		writerOutput.write("Nodes: " + grafo.getNodes().size() + "\n");
		writerOutput.write("Edges: " + grafo.getEdges().size() + "\n");
		writerOutput.write("Info File: " + orderFileName + "\n");
		writerOutput.write("Synteny factor: " + sigma + "\n");
		writerOutput.write("Homology factor: " + omega + "\n");

		Evaluation evaluation = evaluator(cover);
		double cost = evaluation.getCost();
		int goodPCR = evaluation.getGood();
		int placedNodes = maxST.notSingletons();
		int nullLabelsedges = evaluation.getNullLabel();
		int totalLength = computeLenght(paths);
		int finalSingletons = cover.getNodes().size() - cover.notSingletons();
		int numberOfScaffolds = paths.size() + finalSingletons;
		String breakpoints = String.valueOf(evaluation.getErrors());
		writerOutput.println("#nodes: " + maxST.getNodes().size()
				+ "( singletons: " + (maxST.getNodes().size() - placedNodes)
				+ ")");
		writerOutput.println("#edges: " + maxST.getEdges().size() + "\n"
				+ "\nGood PCR: " + goodPCR + "\n" + "Breakpoints: "
				+ breakpoints + "\n" + "Nulli: " + nullLabelsedges);
		writerOutput.println("#scaffolds: " + numberOfScaffolds
				+ "(singletons= " + finalSingletons + ")");
		writerOutput.println("Total length: " + totalLength);
		writerOutput.println("Total weight: " + cost);
		for (String a : paths) {
			writerOutput.println(a);
		}
		writerOutput.flush();
		System.out.println("File saved: "+outputFile);

	}
	
	
	
	private void scaffolderHS(CommandLine cl)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException {
		String gexfFileName = cl.getOptionValues("scaffHS")[0];
		double sigma = Double.parseDouble(cl.getOptionValues("scaffHS")[1]);
		double omega = Double.parseDouble(cl.getOptionValues("scaffHS")[2]);

		String orderFileName = null;

		if (cl.getOptionValues("scaffHS").length > 3) {
			orderFileName = cl.getOptionValues("scaffHS")[3];
		}
		MyGraph grafo = GexfReader.read(gexfFileName, sigma, omega);

		if (orderFileName != null) {
			HashMap<String, String[]> info = GexfReader
					.readContigInfo(orderFileName);
			grafo.setInfo(info);
		}
		grafo.removeSingletons();
		//DEBUG
		System.out.println("PESI: ");
		for(MyEdge e : grafo.getEdges()){
			System.out.println(e.toStringVerbose());
		}
		//
		GexfWriter.write(grafo, gexfFileName+"_LABELLED");
		GraphHSPadapter structure = new GraphHSPadapter(grafo);
		MyGraph cover = structure.createGraphFromSet(structure.getHs().findMinimalHs());
	
		
		GexfWriter.write(cover, gexfFileName + "_COVER");
		//debug
		for(MyNode n :cover.getNodes()){
		System.out.println(n.getDegree());
		}
		//
		ArrayList<String> paths = cover.subPaths();

		File outputFile = new File(gexfFileName + "_RESULTS");
		PrintWriter writerOutput = new PrintWriter(new FileWriter(outputFile));
		writerOutput.write("Network: " + gexfFileName + "\n");
		writerOutput.write("Nodes: " + grafo.getNodes().size() + "\n");
		writerOutput.write("Edges: " + grafo.getEdges().size() + "\n");
		writerOutput.write("Info File: " + orderFileName + "\n");
		writerOutput.write("Synteny factor: " + sigma + "\n");
		writerOutput.write("Homology factor: " + omega + "\n");

		Evaluation evaluation = evaluator(cover);
		double cost = evaluation.getCost();
		int goodPCR = evaluation.getGood();
		int placedNodes = cover.notSingletons();
		int nullLabelsedges = evaluation.getNullLabel();
		int totalLength = computeLenght(paths);
		int finalSingletons = cover.getNodes().size() - cover.notSingletons();
		int numberOfScaffolds = paths.size() + finalSingletons;
		String breakpoints = String.valueOf(evaluation.getErrors());
		writerOutput.println("#nodes: " + grafo.getNodes().size()
				+ "( singletons: " + (grafo.getNodes().size() - placedNodes)
				+ ")");
		writerOutput.println("#edges: " + grafo.getEdges().size() + "\n"
				+ "\nGood PCR: " + goodPCR + "\n" + "Breakpoints: "
				+ breakpoints + "\n" + "Nulli: " + nullLabelsedges);
		writerOutput.println("#scaffolds: " + numberOfScaffolds
				+ "(singletons= " + finalSingletons + ")");
		writerOutput.println("Total length: " + totalLength);
		writerOutput.println("Total weight: " + cost);
		for (String a : paths) {
			writerOutput.println(a);
		}
		writerOutput.flush();
		System.out.println("File saved: "+outputFile);

	}

}
