package NewParser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Utilities.IemlOrderComparator;


public class Token {
	
	Boolean emptyToken = null;
	public int layer = -1;
	public int taille = -1;
	public boolean isTailleComputed = false;
	public boolean expandedToken = false;
	public StringBuilder name = null;			
	public ArrayList<Token> nodes = new ArrayList<Token>();	
	public Token parent = null;
	public Character opCode = null;
	public List<String> canonicalOrder = new ArrayList<String>();
	
	public Token(Character c){
		name = new StringBuilder();
		if (c != null)
			name.append(c);
	}
	
	public Token(Character c, Character operator, int l){
		this(c);
		opCode = operator;
		layer = l;
	}
		
	public int ComputeTaille() throws Exception {
		
		if (!isTailleComputed) {
			
			if (opCode == null) {
				
				Character c = GetName().charAt(0);

				if (!Tokenizer.m_alphabet.containsKey(c))
					throw new Exception("cannot compute ordre standard");	
				
				taille = 1;
				
			}
			else if (opCode == Tokenizer.addition) {					
				for (Token child : nodes) {
					if (taille == -1)
						taille = child.ComputeTaille();
					else
						taille += child.ComputeTaille();
				}						
			}
			else if (opCode == Tokenizer.multiplication) {
				for (Token child : nodes) {
					if (taille == -1)
						taille = child.ComputeTaille();
					else
					    taille *= child.ComputeTaille();
				}						
			}
			else {
				throw new Exception("undefined opCode");
			}
							
			isTailleComputed = true;
		}
		
		return taille;
	}

	public static boolean IsSmaller(Token smaller, Token bigger) throws Exception {
		
		SetCanonical(smaller);
		SetCanonical(bigger);
		
		IemlOrderComparator comparator = new IemlOrderComparator();
		int result = comparator.compare(smaller, bigger);
		
		if (result < 0)
			return true;
		
		return false;
	}
	
	public static void SetCanonical(Token token) throws Exception {
		
		ParserImpl internal_parser = new ParserImpl();
		
		for (String _res : Token.genVariables(token, false)){			
			Token t = internal_parser.parse_internal(Tokenizer.MakeParsable(_res));
			String canon = Token.calcCanonicalString(t);		
			token.canonicalOrder.add(canon);
			internal_parser.Reset();			
		}
	}

	public boolean HasTrailingEmpty()  {
		if (nodes.size() == 1) return false;		
		return nodes.get(nodes.size()-1).IsEmpty();
	}
	
	public boolean IsEmpty() {		
		if (emptyToken == null) {			
			if (nodes == null || nodes.size() == 0)
				emptyToken = GetName().equals("E:");
			else
				for (Token t : nodes) {
					if (emptyToken == null) {
						emptyToken = t.IsEmpty();
					}						
					emptyToken &= t.IsEmpty();
				}					
		}
		return emptyToken;
	}
		
	public void AddNodes(ArrayList<Token> list) throws Exception {
		for (Token n : list){			
			AddNode(n);
		}
	}
	
	public void AddNode(Token n) throws Exception{		
		n.SetParent(this);	
		n.ComputeTaille();
		nodes.add(n);
	}
	
	public void SetParent(Token n) throws Exception{	
		parent = n;
	}
	
	public void AppendToName(Character c) throws Exception{
		name.append(c);
	}
	
	public void AppendToName(String s) throws Exception{
		name.append(s);
	}
	
	public void ReplaceName(String newName) {
		name = new StringBuilder(newName);
	}
	
	public String GetName(){
		return name.toString();
	}
	
	public Token GetParent(){
		return parent;
	}
			
	public void PrintNodes(String prepend){
		
		StringBuilder builder = new StringBuilder("[" + name.toString());
		
		if (layer >= 0)
			builder.append("|" + layer);

		builder.append("]");

		String toPrint = builder.toString();
		
		if (toPrint != null){
			System.out.println(prepend + toPrint);
		}

		if (nodes != null) {
			for (Token node : nodes){
				node.PrintNodes(prepend+"\t");
			}
		}	
	}
	
	public JSONObject buildTree (JSONObject parent) throws JSONException, Exception{
		
		JSONObject _this=new JSONObject();
		
		_this.put("name", GetName());
		_this.put("children", new JSONArray());
		_this.put("op", opCode!=null?opCode:"none");
		
		if (nodes != null) {
			for (Token node : nodes){
				_this.optJSONArray("children").put(node.buildTree(_this));
			}
			return _this;
		}	else {
			return _this;
		}
		
	}
	
	public String buildCanonical() {
		
		StringBuilder builder = new StringBuilder("[");

		String comma = "";
		
		for (String canon : canonicalOrder){
			builder.append(comma);
			builder.append(canon);
			comma = ",";
		}
		
		builder.append("]");
		return builder.toString();
	}	  
	
	public Token GetFirstToken() {
		
		if (nodes != null && nodes.size() > 0) {
			return nodes.get(0).GetFirstToken();
		}
		
		return this;
	}
	
	// calculates class of Token
	public int GetTokenClass() throws Exception {

		ArrayList<Token> firstTokens = new ArrayList<Token>();
		
		if (opCode != null && opCode.equals(Tokenizer.addition)) {
			for (Token n : nodes) {
				firstTokens.add(n.GetFirstToken());
			}
		}
		else {
			firstTokens.add(this.GetFirstToken());
		}
		
		int result = 0;
		
		System.out.print(GetName());
		StringBuilder builder = new StringBuilder();
		for (int i=0;i<50-GetName().length();i++)
			builder.append(" ");
		System.out.print(builder.toString());
		
		for (Token t : firstTokens){
			
			char c = t.GetName().charAt(0);
			
			if (Tokenizer.c_verb.contains(c)) {
				result |= Tokenizer.GrammaticalClass.verb.getNumVal();
			}
			
			if (Tokenizer.c_noun.contains(c)) {
				result |= Tokenizer.GrammaticalClass.noun.getNumVal();
			}
			
			if (Tokenizer.c_aux.contains(c)) {
				result |= Tokenizer.GrammaticalClass.auxiliary.getNumVal();
			}
			
			if (Tokenizer.c_full.contains(c)) {
				result |= Tokenizer.GrammaticalClass.full.getNumVal();
			}
			
			if (Tokenizer.c_inf.contains(c)) {
				result |= Tokenizer.GrammaticalClass.info.getNumVal();
			}
			
			System.out.print(t.GetName().charAt(0) + " " + result + "\t");
		}
		
		System.out.println();
		
		if (result < 1 || result > 7)
			throw new Exception("bad grammatical class");
		
		return result;
	}	
		
	// converts Token into its string representation
	public String GenerateSequenceForTable(boolean useParanthesis) {
		
		StringBuilder builder = new StringBuilder();
		
		if (nodes != null && opCode != null) {
							
			ArrayList<String> accumulator = new ArrayList<String>();
			
			for (Token t : nodes){					
				String child = t.GenerateSequenceForTable(useParanthesis);
				if (child != null)
					accumulator.add(child);					
			}
			
			String op = opCode.equals(Tokenizer.addition) ? "+" : null;
			
			if (useParanthesis && op != null)
				builder.append("(");
				
			for (int i = 0; i < accumulator.size(); i++) {
				builder.append(accumulator.get(i));
				if (op != null && (i < accumulator.size() - 1))
					builder.append(op);						
			}	
			
			if (useParanthesis && op != null)
				builder.append(")");
			
			if (op == null)
				builder.append(Tokenizer.c_marks.get(layer));
		}
		else {
			builder.append(GetName());
		}
					
		return builder.toString();
	}
	
	// writes out an input as a list of substrings to add
	public static String calcCanonicalString(Token n) throws Exception {
			
		StringBuilder builder = new StringBuilder();
		
		if (n.opCode == null) {
			int index = Tokenizer.c_alphabet.indexOf(n.GetName().charAt(0));
			builder.append(Tokenizer.natural_alphabet.charAt(index));
		}
		else if (n.opCode.equals(Tokenizer.addition)){
			if (Tokenizer.c_exp.contains(n.GetName().charAt(0))) {
				int index = Tokenizer.c_alphabet.indexOf(n.GetName().charAt(0));
				builder.append(Tokenizer.natural_alphabet.charAt(index));
			} 	
			else {
				throw new Exception("additive relation is invalid here");
			}
		}
		else {
			for (int i = 0; i < 3; i++){
				builder.append(calcCanonicalString(n.nodes.get(i)));
			}
		}
		
		return builder.toString();
	}
	
	public static String canonize(String input) throws Exception {
				
		StringBuilder descriptor = new StringBuilder();
		String[] tok = input.split(Tokenizer.token_regex);
		
		for (String s : tok) {
			if (s.isEmpty())
				continue;
			
			String expanded = s+".";
			if (Tokenizer.scLookup.containsKey(expanded)){
				Token t = Tokenizer.scLookup.get(expanded);						
				descriptor.append(canonize(t.GetName()));
			}			
			else if (s.length() == 1 && Tokenizer.c_alphabet.contains(s.charAt(0))) {
				int index = Tokenizer.c_alphabet.indexOf(s.charAt(0));
				descriptor.append(Tokenizer.natural_alphabet.charAt(index));
			}
			else {
				throw new Exception("Cannot canonize");
			}	
		}
		
		return descriptor.toString();		
	}
	
	// to preserve backward compatibility with other code (in Table generation)
	public static ArrayList<String> genVariables(Token parent) {
		return genVariables(parent, true);
	}
	
	public static ArrayList<String> genVariables(Token parent, boolean expandPrimitives) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> temp = recursiveGenVariables(parent, expandPrimitives);
		for (String s : temp){
			
			// kludge: if this is an empty sequence, MakeParsable will abbreviate it
			// and it will not be possible to remove trailing empties afterwards by
			// running MakeParsable again. But we need to run it now in order to find
			// all the abbreviations, etc. So, if after MakeParsable the first letter 
			// is 'E', it means it was an empty sequence and we will not use the output
			// of MakeParsable.
			String intermediate = Tokenizer.MakeParsable(s);
			if ('E' == intermediate.charAt(0))
				result.add(s);
			else 
				result.add(intermediate);
		}
		return result;
	}
		
	private static ArrayList<String> recursiveGenVariables(Token parent, boolean expandPrimitives) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		// TODO: refactor this. Originally, O,M, I and F had no children and their opcode was null
		// the first 'if' meant to handle those cases. Now these primitives have children in a '+' 
		// relation, so they are handled in the 'else'. U, A, S, B, T cannot be expanded so this
		// first 'if' is doing work for nothing. Leaving this now as it needs testing.
		if (parent.opCode == null || 
				parent.GetName().equals("O:") || 
				parent.GetName().equals("M:") ||
				parent.GetName().equals("I:") ||
				parent.GetName().equals("F:")) { // primitive layer: check if we can expand, if yes, do it, otherwise use parent
			String pName = parent.GetName();
			if (expandPrimitives && Tokenizer.primitiveLookup.containsKey(pName)) {
				for (Token sub : Tokenizer.primitiveLookup.get(pName).nodes){
					result.add(sub.GetName());
				}
			}
			else {
				result.add(pName);
			}
		}
		else {
			
			if (parent.opCode.equals(Tokenizer.addition)) { // addition
				for (Token child : parent.nodes) {					
					result.addAll(recursiveGenVariables(child, expandPrimitives));					
				}
			}
			else { // multiplication
				
				if (parent.IsEmpty()) {
					result.add(parent.GenerateSequenceForTable(false));
				}
				else {
					for (Token child : parent.nodes) {	
						if (result.isEmpty()) {
							result.addAll(recursiveGenVariables(child, expandPrimitives));
						}
						else {
							ArrayList<String> temp = new ArrayList<String>();
							for (String prefix : result) {
								for (String postfix : recursiveGenVariables(child, expandPrimitives)) {
									temp.add(prefix+postfix);
								}
							}
							result = temp;
						}
					}
					
					ArrayList<String> temp = new ArrayList<String>();
					for (String prefix : result) {
						temp.add(prefix+Tokenizer.c_marks.get(parent.layer));
					}
					
					result = temp;
				}
			}
		}
		
		return result;
	}
}
