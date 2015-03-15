package TopDown;

import java.util.ArrayList;
import java.util.HashMap;
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
	private Mode startMode; // parsing mode
	
	private HashMap<String, String> terms;
	
	public enum Mode {
		Toplevel, // non-recursive
		Full,     // recursive
		TermOnly, // stops at terms
	}
	
	public Parser(Node n, int i, Mode m){
		this.startNode = n;
		this.startIndex = i;
		this.startMode = m;		
		
		if (startMode == Mode.TermOnly)
			terms = TermInterface.LoadDictionary(null);
	}
	
	public void run() {
		try {	
			parse(startNode, startIndex, startMode);
			System.out.println(Thread.currentThread().getName());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void Parse(Node n, int i, Mode m){
		try {	
			Parser p = new Parser(n, i, m);
			p.parse();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void parse() throws Exception {
		parse(startNode, startIndex, startMode);
	}
	
	private void parse(Node input, int index, Mode mode) throws Exception {		
		
		if (!IEMLLang.IsLayerValid(index)) 
			return; 
		
		if (index >= patternDetector.length || index >= layerMarkDetectors.length){
			throw new Exception("==> "+ "missing regex for index " + index);
		}
									
		ArrayList<String> substrings = new ArrayList<String>();	
		
		Matcher matcher = layerMarkDetectors[index].matcher(input.GetName());			
		while (matcher.find())	
			substrings.add(matcher.group());
			
		//discover the highest layer
		//TODO: run if 'mode' says it should
		if (substrings.size() < 1) {
			//nothing at this layer, go down
			//if (input.GetSize() > 0){
				throw new Exception("==> "+ "missing layer " + IEMLLang.LM_R[index]);
			//}
			//input.SetLayer((index - 1));
			//parse(input, index - 1, mode);
		}			
		else if (substrings.size() == 1){
			// multiplication
			substrings.clear();
			matcher = patternDetector[index].matcher(input.GetName());	
			
			while (matcher.find())
				substrings.add(matcher.group());
			
			if (index == 0 && substrings.size() == 1) {
				//atom
				input.AddNode(Node.GetNewNode(substrings.get(0), index));
				return;
			}
			
			input.AddNode(Node.GetNewOpcodeNode(IEMLLang.Multiplication));
			
			for (String str : substrings) 
			{	
				Node newNode = Node.GetNewNode(str, index-1);				
				input.AddNode(newNode);	
				
				if (mode == Mode.Full)
					parse(newNode, index - 1, mode);
				else if (mode == Mode.TermOnly){
					if (!terms.containsKey(str))
						parse(newNode, index - 1, mode);
				}
			}
		}	
		else {
			// addition
			input.AddNode(Node.GetNewOpcodeNode(IEMLLang.Addition));
			
			for (String str : substrings) 
			{	
				Node newNode = Node.GetNewNode(str, index);
				input.AddNode(newNode);	
				
				if (mode == Mode.Full)
					parse(newNode, index - 1, mode);
				else if (mode == Mode.TermOnly){
					if (!terms.containsKey(str))
						parse(newNode, index - 1, mode);
				}
			}
		}
	}
}
