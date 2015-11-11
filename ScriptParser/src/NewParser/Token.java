package NewParser;

import java.util.ArrayList;


public class Token {
	
	Boolean emptyToken = null;
	public int layer = -1;
	public int taille = -1;
	public boolean isTailleComputed = false;
	
	public StringBuilder name = null;			
	public ArrayList<Token> nodes = new ArrayList<Token>();	
	public Token parent = null;
	public Character opCode = null;
	
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
			
			if (nodes.isEmpty()){
				Character c = GetName().charAt(0);
				
				//sanity
				if (!Tokenizer.m_alphabet.containsKey(c))
					throw new Exception("cannot compute ordre standard");	
				
				taille = 1;
				
				if (c.equals('O'))
					taille = 2;
				if (c.equals('M'))
					taille = 3;
				if (c.equals('F'))
					taille = 5;
				if (c.equals('I'))
					taille = 6;
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
			//sanity
			else 
				throw new Exception("undefined opCode");
			
			isTailleComputed = true;
		}
		
		return taille;
	}
	
	//=============================================
		
	private Boolean n_n(Token bigger) throws Exception{
		int a = Tokenizer.m_alphabet.get(this.GetName().charAt(0));
		int b = Tokenizer.m_alphabet.get(bigger.GetName().charAt(0));
		if (a == b)
			return null;
		if (a < b)
			return true;			
		//TODO: test case
		throw new Exception("alphanumeric mismatch between " + this.GetName() + " and " + bigger.GetName());
	}
	
	private Boolean n_m(Token bigger) throws Exception{
		throw new Exception("not viable");
	}
	
	private Boolean n_a(Token bigger) throws Exception{
		return this.IsProperOrder(bigger.nodes.get(0));
	}
	
	private Boolean m_n(Token bigger) throws Exception{
		throw new Exception("not viable");
	}
	
	private Boolean m_m(Token bigger) throws Exception{
		for (int i = 0; i < 3; i++ ){
			Boolean res = this.nodes.get(i).IsProperOrder(bigger.nodes.get(i));
			if (res != null)
				return res;
		}
		return null;
	}
	
	private Boolean m_a(Token bigger) throws Exception{
		throw new Exception("not real");
	}
	
	private Boolean a_n(Token bigger) throws Exception{		
		return this.nodes.get(0).IsProperOrder(bigger);
	}
	
	private Boolean a_m(Token bigger) throws Exception{
		throw new Exception("not real");
	}
	
	private Boolean a_a(Token bigger) throws Exception{
		
		for (int i = 0; i < this.nodes.size() && i < bigger.nodes.size(); i++ ){
			Boolean res = this.nodes.get(i).IsProperOrder(bigger.nodes.get(i));
			if (res != null)
				return res;
		}
		
		if (this.nodes.size() == bigger.nodes.size())
			return null;
		
		return this.nodes.size() < bigger.nodes.size();
	}
	
	public boolean EvaluateOrder(Token n) throws Exception {
		Boolean res = IsProperOrder(n);
		if (res == null)
			throw new Exception("duplicate found " + GetName() + " and " + n.GetName());
		return res;
	}
	
	public Boolean IsProperOrder(Token n) throws Exception {
		
		if (this.layer < 0 || n.layer < 0)
			//sanity
			throw new Exception("negative layer values");
		
		if (!this.isTailleComputed || !n.isTailleComputed)
			//sanity
			throw new Exception("taille not computed");
		
		if (this.layer < n.layer)
			return true;
		if (this.layer > n.layer)
			//TODO: test case
			throw new Exception("layer mismatch between " + GetName() + " and " + n.GetName());
		if (this.taille < n.taille)
			return true;
		if (this.taille > n.taille)
			//TODO: test case
			throw new Exception("taille mismatch between " + GetName() + " and " + n.GetName());
		
		if (this.opCode == null) {
			if (n.opCode == null) {
				return this.n_n(n);
			}
			else if (n.opCode.equals(Tokenizer.multiplication)) {
				return this.n_m(n);
			}
			else if (n.opCode.equals(Tokenizer.addition)) {
				return this.n_a(n);
			}			
			//sanity
			throw new Exception("unrecognized opCode");
		}
		else if (this.opCode.equals(Tokenizer.multiplication)) {
			if (n.opCode == null) {
				return this.m_n(n);
			}
			else if (n.opCode.equals(Tokenizer.multiplication)) {
				return this.m_m(n);
			}
			else if (n.opCode.equals(Tokenizer.addition)) {
				return this.m_a(n);
			}			
			//sanity
			throw new Exception("unrecognized opCode");
		}
		else if (this.opCode.equals(Tokenizer.addition)) {
			if (n.opCode == null) {
				return this.a_n(n);
			}
			else if (n.opCode.equals(Tokenizer.multiplication)) {
				return this.a_m(n);
			}
			else if (n.opCode.equals(Tokenizer.addition)) {
				return this.a_a(n);
			}			
			//sanity
			throw new Exception("unrecognized opCode");
		}
		
		//sanity
		throw new Exception("unhandled case");
	}

	public boolean HasTrailingEmpty() throws Exception {
		if (nodes.size() == 1) return false;		
		return nodes.get(nodes.size()-1).IsEmpty();
	}
	
	public boolean IsEmpty() throws Exception {		
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
	//=============================================
		
	public void AddNodes(ArrayList<Token> list) throws Exception {
		for (Token n : list){
			AddNode(n);
		}
	}
	
	public void AddNode(Token n) throws Exception{		
		n.SetParent(this);		
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
	
	//=============================================
	
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
	
	public String Output(){
		
		StringBuilder builder = new StringBuilder("[" + name.toString());			
		if (layer >= 0)
			builder.append("|" + layer);
		builder.append("]");			
		
		for (Token node : nodes){
			builder.append(node.Output());
		}
		
		return builder.toString();
	}
}
