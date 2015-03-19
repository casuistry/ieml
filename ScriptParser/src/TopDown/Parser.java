package TopDown;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import IEMLInterface.IEMLLang;
import IEMLInterface.TermInterface;

public class Parser implements Runnable {

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
	
	private Node startNode; // root node
	private int startIndex; // estimate of layer
	private ParserConfigurator config; 
	
	private TermInterface terms;
	
	public enum Mode {
		Toplevel, // non-recursive
		Full,     // recursive
		TermOnly, // stops at terms
	}
	
	public Parser(Node n, int i, ParserConfigurator config){
		this.startNode = n;
		this.startIndex = i;
		this.config = config;				
		this.terms = TermInterface.getInstance(config._TermFilePath);		
	}
	
	public void run() {
		try {	
			parse(startNode, startIndex);
			System.out.println(Thread.currentThread().getName());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void Parse(Node n, int i, ParserConfigurator config){
		try {	
			Parser p = new Parser(n, i, config);
			p.parse();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void parse() throws Exception {
		parse(startNode, startIndex);
	}
	
	private void parse(Node input, int index) throws Exception {		
		
		Mode mode = config._Mode;
		
		if (!IEMLLang.IsLayerValid(index)) 
			return; 
		
		if (index >= patternDetector.length || index >= layerMarkDetectors.length){
			throw new Exception("==> "+ "missing regex for index " + index);
		}
									
		ArrayList<String> substrings = new ArrayList<String>();	
		
		Matcher matcher = layerMarkDetectors[index].matcher(input.GetName());			
		while (matcher.find())	
			substrings.add(matcher.group());
			
		if (substrings.size() < 1) {
		    throw new Exception("missing layer " + IEMLLang.LM_R[index]);
		}			
		else if (substrings.size() == 1){
			// multiplication
			substrings.clear();
			matcher = patternDetector[index].matcher(input.GetName());	
			
			while (matcher.find())
				substrings.add(matcher.group());
			
			if (index == 0 && substrings.size() == 1) {
				//atom
				Node newNode = Node.GetNewNode(input.GetName(), index);
				if (terms.IsTerm(newNode))
					newNode.isTerm = true;				
				input.AddNode(newNode);
				return;
			}
			else {
				input.SetOpCode(IEMLLang.Multiplication);
				
				for (String str : substrings) 
				{	
					Node newNode = Node.GetNewNode(str, index-1);				
					input.AddNode(newNode);	
					
					if (mode == Mode.Full)
						parse(newNode, index - 1);
					else if (mode == Mode.TermOnly){
						if (terms.IsTerm(newNode))
							newNode.isTerm = true;
						else
							parse(newNode, index - 1);						
					}
				}
			}
		}	
		else {
			// addition
			//input.AddNode(Node.GetNewOpcodeNode(IEMLLang.Addition));
			input.SetOpCode(IEMLLang.Addition);
			
			for (String str : substrings) 
			{	
				Node newNode = Node.GetNewNode(str, index);
				input.AddNode(newNode);	
				
				if (mode == Mode.Full)
					parse(newNode, index - 1);
				else if (mode == Mode.TermOnly){
					if (terms.IsTerm(newNode))
						newNode.isTerm = true;
					else
						parse(newNode, index - 1);	
				}
			}
		}
	}
}
