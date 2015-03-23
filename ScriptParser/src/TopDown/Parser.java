package TopDown;

import java.util.ArrayList;
import java.util.regex.Matcher;

import IEMLInterface.IEMLLang;
import IEMLInterface.TermInterface;

public class Parser implements Runnable {
	
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
		
		if (!IEMLLang.IsLayerValid(index)) 
			return; 
		
		if (index >= IEMLLang.patternDetector.length || index >= IEMLLang.layerMarkDetectors.length)
			throw new Exception("missing regex for index " + index);
				
		Mode mode = config._Mode;
		
		Matcher matcher = IEMLLang.layerMarkDetectors[index].matcher(input.GetName());	
		ArrayList<String> substrings = new ArrayList<String>();
		while (matcher.find())	
			substrings.add(matcher.group());
			
		if (substrings.size() < 1) {
		    throw new Exception("missing layer " + IEMLLang.LM_R[index]);
		}			
		
		// multiplication (only one layer mark found)
		if (substrings.size() == 1){

			ArrayList<String> mults = new ArrayList<String>();
			matcher = IEMLLang.patternDetector[index].matcher(input.GetName());				
			while (matcher.find())
				mults.add(matcher.group());
				
			if (mults.size() < 1){
				if (index == 1)
					throw new Exception("small cap " + substrings.get(0));
				else
					throw new Exception("unrecognized " + substrings.get(0));
			}
			
			if (mults.size() == 1){ 
				if (index == 0) {//atom
					input.SetTerm(terms.IsTerm(input));
					return;	
				}
			}			
			
			input.SetOpCode(IEMLLang.Multiplication);
			
			for (String str : mults) 
			{	
				Node newNode = Node.GetNewNode(str, index-1);				
				input.AddNode(newNode);	
				
				if (mode == Mode.Full)
					parse(newNode, index - 1);
				else if (mode == Mode.TermOnly)
					newNode.SetTerm(terms.IsTerm(newNode));
					else
						parse(newNode, index - 1);										
			}
		}	
		else { // addition (multiple layer marks)
			
			input.SetOpCode(IEMLLang.Addition);
			
			for (String str : substrings) 
			{	
				Node newNode = Node.GetNewNode(str, index);
				input.AddNode(newNode);	
				
				if (mode == Mode.Full)
					parse(newNode, index - 1);
				else if (mode == Mode.TermOnly){
					if (terms.IsTerm(newNode))
						newNode.SetTerm(true);
					else
						parse(newNode, index - 1);	
				}
			}
		}
	}
}
