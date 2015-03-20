package TopDown;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import IEMLInterface.IEMLLang;
import Rules.PostprocessorEngine;
import Rules.RuleEngine;
import TopDown.Parser.Mode;

public class TopDownParser {
	
	public static Node Parse(String input, ParserConfigurator config) {
		
		int numCores = Runtime.getRuntime().availableProcessors()/2;
		ExecutorService executor = Executors.newFixedThreadPool(numCores);
		
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
				executor.execute(new Parser(n, n.GetLayer(), config));
			}
			
			try {
				executor.shutdown();
				if (!executor.awaitTermination(10, TimeUnit.SECONDS)){
					System.out.println("Timed out");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		//processing
		
		PostprocessorEngine post = new PostprocessorEngine();
		post.RunRules(root);
		
		//end processing
		
		//checks
		
		RuleEngine rules = new RuleEngine();
		rules.RunRules(root);
		
		//end checks
		
		long parsingTime = System.nanoTime() - startParsing;
		System.out.println(Node.TotalNodes.get() + " node(s) processed in " + parsingTime/1000000 + " ms. on " + numCores + " cores for length " + inputLength);
		return root;	
	}
}



