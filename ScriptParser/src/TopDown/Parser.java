package TopDown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import IEMLInterface.IEMLLang;
import IEMLInterface.TermInterface;

public class Parser implements Runnable {

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
		
		if (index < 0 || index >= IEMLLang.LM_R.length) 
			return; 
		if (index >= TopDownParser.patternDetector.length || index >= TopDownParser.layerMarkDetectors.length){
			throw new Exception("==> "+ "missing regex for index " + index);
		}
									
		ArrayList<String> substrings = new ArrayList<String>();	
		
		Matcher matcher = TopDownParser.layerMarkDetectors[index].matcher(input.GetName());			
		while (matcher.find())	
			substrings.add(matcher.group());
				
		if (substrings.size() < 1) {
			//nothing at this layer, go down
			if (input.GetSize() > 0){
				throw new Exception("==> "+ "missing layer " + IEMLLang.LM_R[index]);
			}
			input.SetLayer((index - 1));
			parse(input, index - 1, mode);
		}			
		else if (substrings.size() == 1){
			// multiplication
			substrings.clear();
			matcher = TopDownParser.patternDetector[index].matcher(input.GetName());	
			
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
			input.AddNode(new Node("+", Node.OPCODE));
			for (String str : substrings) 
			{	
				Node newNode = new Node(str, Node.NODE, Integer.toString(index));
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
