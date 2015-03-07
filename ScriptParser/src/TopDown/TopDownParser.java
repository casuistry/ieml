package TopDown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import IEMLInterface.IEMLLang;
import IEMLInterface.StructureRule;
import TopDown.Parser.Mode;

public class TopDownParser {
	
	private static int numCores = Runtime.getRuntime().availableProcessors()/2;
	private static long parsingTime;
	private static ExecutorService e = Executors.newFixedThreadPool(numCores);
	
	public static Pattern[] patternDetector = new Pattern[] { 
		Pattern.compile("(\\w+)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[0]+"(\\+.+?"+IEMLLang.LM_R[0]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[1]+"(\\+.+?"+IEMLLang.LM_R[1]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[2]+"(\\+.+?"+IEMLLang.LM_R[2]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[3]+"(\\+.+?"+IEMLLang.LM_R[3]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[4]+"(\\+.+?"+IEMLLang.LM_R[4]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[5]+"(\\+.+?"+IEMLLang.LM_R[5]+")*)"),
		Pattern.compile("(.+?"+IEMLLang.LM_R[6]+"(\\+.+?"+IEMLLang.LM_R[6]+")*)")
	};
	public static Pattern[] layerMarkDetectors = new Pattern[] { 
		Pattern.compile("(\\w+"+IEMLLang.LM_R[0]+")"),
		Pattern.compile(".+?"+IEMLLang.LM_R[1]),
		Pattern.compile(".+?"+IEMLLang.LM_R[2]),
		Pattern.compile(".+?"+IEMLLang.LM_R[3]),
		Pattern.compile(".+?"+IEMLLang.LM_R[4]),
		Pattern.compile(".+?"+IEMLLang.LM_R[5]),
		Pattern.compile(".+?"+IEMLLang.LM_R[6])
	};

	public static Node Parse(String input) {
		
		int inputLength = input.length();
		String detectedLayer = input.substring(inputLength-1, inputLength);
		System.out.print("Based on character: " + detectedLayer);				
		System.out.println(" layer is: " + IEMLLang.LMList.indexOf(detectedLayer));
		
		Node.TotalNodes.set(0);
		long startParsing = System.nanoTime();
		
		int startingDefaultLayer = IEMLLang.LMList.indexOf(detectedLayer);		
		Node result = new Node(input, Node.ROOT, Integer.toString(startingDefaultLayer));
		Parser.Parse(result, startingDefaultLayer, Parser.Mode.Toplevel);
		
		ArrayList<Node> children = result.GetNodes();
		
		if (children!=null){
			for (Node n : children){
				e.execute(new Parser(n, n.GetLayerInt(), Mode.Full));
			}
			
			try {
				e.shutdown();
				if (!e.awaitTermination(10, TimeUnit.SECONDS)){
					System.out.println("Timed out");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		StructureRule rule = new StructureRule();
		String error = rule.ApplyRule(result);
		
		if (error != null)
			System.out.println(error);
		else 
			System.out.println("Passed " + rule.GetName());
		
		parsingTime = System.nanoTime() - startParsing;
		System.out.println(Node.TotalNodes.get() + " node(s) processed in " + parsingTime/1000000 + " ms. on " + numCores + " cores for length " + inputLength);
		return result;	
	}
}



