package TopDown;

import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import IEMLInterface.IEMLLang;
import Rules.RuleEngine;
import TopDown.Parser.Mode;

public class TopDownParser {
	
	private static int numCores = Runtime.getRuntime().availableProcessors()/2;
	private static long parsingTime;
	private static ExecutorService e = Executors.newFixedThreadPool(numCores);
	
	public static Node Parse(String input, ParserConfigurator config) {
		
		RuleEngine rules = new RuleEngine();
		
		int inputLength = input.length();
		String detectedLayer = input.substring(inputLength-1, inputLength);
		System.out.print("Based on character: " + detectedLayer);				
		System.out.println(" layer is: " + IEMLLang.LMList.indexOf(detectedLayer));
		
		Node.TotalNodes.set(0);
		long startParsing = System.nanoTime();
		
		int startingDefaultLayer = IEMLLang.LMList.indexOf(detectedLayer);		
		Node root = Node.GetNewNode(input, startingDefaultLayer);
		ParserConfigurator pc = new ParserConfigurator();
		pc._Mode = Mode.Toplevel;		
		Parser.Parse(root, startingDefaultLayer, pc);
		
		ArrayList<Node> children = root.GetNodes();
		
		if (children!=null){
			for (Node n : children){
				e.execute(new Parser(n, n.GetLayer(), config));
			}
			
			try {
				e.shutdown();
				if (!e.awaitTermination(10, TimeUnit.SECONDS)){
					System.out.println("Timed out");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		//processing
		
		root.MarkEmpty();
		
		//end processing
		
		//checks
		
		rules.RunRules(root);
		
		//end checks
		
		parsingTime = System.nanoTime() - startParsing;
		System.out.println(Node.TotalNodes.get() + " node(s) processed in " + parsingTime/1000000 + " ms. on " + numCores + " cores for length " + inputLength);
		return root;	
	}
}



