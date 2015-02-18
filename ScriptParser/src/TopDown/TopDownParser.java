package TopDown;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ScriptGenerator.Generator;

public class TopDownParser {
	
	private static Pattern[] patternDetector = new Pattern[] { 
		Pattern.compile("(\\w+)"),
		Pattern.compile("(.+?"+Generator.LM_R[0]+"(\\+.+?"+Generator.LM_R[0]+")*)"),
		Pattern.compile("(.+?"+Generator.LM_R[1]+"(\\+.+?"+Generator.LM_R[1]+")*)"),
		Pattern.compile("(.+?"+Generator.LM_R[2]+"(\\+.+?"+Generator.LM_R[2]+")*)"),
		Pattern.compile("(.+?"+Generator.LM_R[3]+"(\\+.+?"+Generator.LM_R[3]+")*)"),
		Pattern.compile("(.+?"+Generator.LM_R[4]+"(\\+.+?"+Generator.LM_R[4]+")*)"),
		Pattern.compile("(.+?"+Generator.LM_R[5]+"(\\+.+?"+Generator.LM_R[5]+")*)"),
		Pattern.compile("(.+?"+Generator.LM_R[6]+"(\\+.+?"+Generator.LM_R[6]+")*)")
	};
	private static Pattern[] layerMarkDetectors = new Pattern[] { 
		Pattern.compile("(\\w+"+Generator.LM_R[0]+")"),
		Pattern.compile(".+?"+Generator.LM_R[1]),
		Pattern.compile(".+?"+Generator.LM_R[2]),
		Pattern.compile(".+?"+Generator.LM_R[3]),
		Pattern.compile(".+?"+Generator.LM_R[4]),
		Pattern.compile(".+?"+Generator.LM_R[5]),
		Pattern.compile(".+?"+Generator.LM_R[6])
	};
		
	private static Matcher matcher;
	
	public static Node Parse(String input) {
		Node result = new Node(input, Node.ROOT);
		result.SetLayer(Generator.LM_R.length-1);
		try {	
			parse(result, Generator.LM_R.length-1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;	
	}
	
	private static void parse(Node input, int index) throws Exception {		
				
		if (index < 0 || index >= Generator.LM_R.length) 
			return; 
		if (index >= patternDetector.length || index >= layerMarkDetectors.length)
			missingRegexException(index);
									
		// store in substrings all substrings ending with specified layer mark
		ArrayList<String> substrings = new ArrayList<String>();		
		matcher = layerMarkDetectors[index].matcher(input.GetName());	
		while (matcher.find())	
			substrings.add(matcher.group());
				
		if (substrings.size() < 1) {
			if (input.GetSize() > 0)
				missingLayerException(index);
			input.SetLayer((index - 1));
			parse(input, index - 1);
		}	
		// multiplication
		else if (substrings.size() == 1){
			substrings.clear();
			matcher = patternDetector[index].matcher(input.GetName());	
			
			while (matcher.find())
				substrings.add(matcher.group());
			
			if (index == 0 && substrings.size() == 1) {
				Node newNode = new Node(substrings.get(0), Node.NODE, Node.ATOM);
				input.AddNode(newNode);	
				return;
			}
			
			input.AddNode(new Node("*", Node.OPCODE));
			
			for (String str : substrings) 
			{	
				Node newNode = new Node(str, Node.NODE, Integer.toString(index-1));
				input.AddNode(newNode);	
				parse(newNode, index - 1);
			}
		}
		// addition
		else {
			input.AddNode(new Node("+", Node.OPCODE));
			for (String str : substrings) 
			{	
				Node newNode = new Node(str, Node.NODE, Integer.toString(index));
				input.AddNode(newNode);	
				parse(newNode, index);
			}
		}
	}
	
	private static void missingLayerException(int index) throws Exception{
		throw new Exception("==> "+ "missing layer " + Generator.LM_R[index]);
	}

	private static void missingRegexException(int index) throws Exception{
		throw new Exception("==> "+ "missing regex for index " + index);
	}
}



