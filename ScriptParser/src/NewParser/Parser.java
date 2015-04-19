package NewParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import TopDown.Node;

public class Parser {
	
	private static boolean initialised = false;
	
	public static Character multiplication = '*';
	public static Character addition = '+';	
	
	public static List<Character> c_alphabet = Arrays.asList(new Character[]{'E','U','A','O','S','B','T','M','F','I'});
	public static List<Character> c_smallCap = Arrays.asList(new Character[]{'y','o','e','u','a','i','j','g','s','b','t','h','c','k','m','n','p','x','d','f','l'});
	public static List<Character> c_vowels   = Arrays.asList(new Character[]{'o','a','u','e'});
	public static List<Character> c_ignore   = Arrays.asList(new Character[]{'(',')',' '});
	public static List<Character> c_wLetter  = Arrays.asList(new Character[]{'w'});
	public static List<Character> c_addOp    = Arrays.asList(new Character[]{'+'});
	public static List<Character> c_marks    = Arrays.asList(new Character[]{':', '.', '-', '�', ',', '_', ';'});
	public static List<Character> c_star     = Arrays.asList(new Character[]{'*'});
	
	public static List<String> c_smallCapOrdered = Arrays.asList(new String[]{"wo", "wa","y","o","e","wu","we","u","a","i","j","g","s","b","t","h","c","k","m","n","p","x","d","f","l"});
	
	public enum States {
		
		state_none("invalid state"),
		state_pre("not started parsing yet"),
		state_i("initial"),		
		state_sc("small cap"),		
		state_ws("start small cap wo, wa, wu and we"),		
		state_a("primitives"),		
		state_f("node completed"),	    
		state_d("addition operation"),
		state_post("finish parsing now"),
		state_done("done");
		
	    private final String fieldDescription;

	    States(String descr) {
	        this.fieldDescription = descr;
	    }
	    
	    public String getFieldDescription() {
	        return fieldDescription;
	    }
	    
	    public static String GetKey(States a, States b){
	    	return a.getFieldDescription()+b.getFieldDescription();
	    }
	}
	
	public enum Transitions {
		t_p_i,    
		t_f_p,	  
		t_p_p,    
		t_i_sc,
		t_i_ws,
		t_i_a,
		t_f_sc,
		t_f_ws,
		t_f_a,
		t_d_sc,
		t_d_ws,
		t_d_a,
		t_a_f,    
		t_ws_sc,  
		t_sc_f,   
		t_f_d,
		t_f_f
	}
	
	public static HashMap<String, Transitions> transitionMap = new HashMap<String, Transitions>();
	static {
		transitionMap.put(States.GetKey(States.state_post, States.state_done), Transitions.t_p_p);
		transitionMap.put(States.GetKey(States.state_f, States.state_post), Transitions.t_f_p);
		transitionMap.put(States.GetKey(States.state_pre, States.state_i), Transitions.t_p_i);
		transitionMap.put(States.GetKey(States.state_i, States.state_sc), Transitions.t_i_sc);
		transitionMap.put(States.GetKey(States.state_i, States.state_ws), Transitions.t_i_ws);
		transitionMap.put(States.GetKey(States.state_i, States.state_a), Transitions.t_i_a);
		transitionMap.put(States.GetKey(States.state_f, States.state_sc), Transitions.t_f_sc);
		transitionMap.put(States.GetKey(States.state_f, States.state_ws), Transitions.t_f_ws);
		transitionMap.put(States.GetKey(States.state_f, States.state_a), Transitions.t_f_a);
		transitionMap.put(States.GetKey(States.state_d, States.state_sc), Transitions.t_d_sc);
		transitionMap.put(States.GetKey(States.state_d, States.state_ws), Transitions.t_d_ws);
		transitionMap.put(States.GetKey(States.state_d, States.state_a), Transitions.t_d_a);
		transitionMap.put(States.GetKey(States.state_a, States.state_f), Transitions.t_a_f);
		transitionMap.put(States.GetKey(States.state_ws, States.state_sc), Transitions.t_ws_sc);
		transitionMap.put(States.GetKey(States.state_sc, States.state_f), Transitions.t_sc_f);
		transitionMap.put(States.GetKey(States.state_f, States.state_d), Transitions.t_f_d);
		transitionMap.put(States.GetKey(States.state_f, States.state_f), Transitions.t_f_f);
	}
	
	public static HashMap<Character, Integer> m_alphabet = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_smallCap = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_vowels   = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_ignore   = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_wLetter  = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_addOp    = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_marks    = new HashMap<Character, Integer>();
	public static HashMap<Character, Integer> m_star     = new HashMap<Character, Integer>();
	
	static {
		for (Character c : c_alphabet)
			m_alphabet.put(c, c_alphabet.indexOf(c));
		for (Character c : c_smallCap)
			m_smallCap.put(c, c_smallCap.indexOf(c));
		for (Character c : c_vowels)
			m_vowels.put(c, c_vowels.indexOf(c));
		for (Character c : c_ignore)
			m_ignore.put(c, c_ignore.indexOf(c));
		for (Character c : c_wLetter)
			m_wLetter.put(c, c_wLetter.indexOf(c));
		for (Character c : c_addOp)
			m_addOp.put(c, c_addOp.indexOf(c));
		for (Character c : c_marks)
			m_marks.put(c, c_marks.indexOf(c));
		for (Character c : c_star)
			m_star.put(c, c_star.indexOf(c));
	}
	
	public static HashMap<String, Node> scLookup = new HashMap<String, Node>();
	static {
		try {		
			scLookup.put("wo.", new Parser().parse("*U:U:E:.**"));
			scLookup.put("wa.", new Parser().parse("*U:A:E:.**"));
			scLookup.put("wu.", new Parser().parse("*A:U:E:.**"));
			scLookup.put("we.", new Parser().parse("*A:A:E:.**"));
			scLookup.put("y.", new Parser().parse("*U:S:E:.**"));
			scLookup.put("o.", new Parser().parse("*U:B:E:.**"));
			scLookup.put("e.", new Parser().parse("*U:T:E:.**"));
			scLookup.put("u.", new Parser().parse("*A:S:E:.**"));
			scLookup.put("a.", new Parser().parse("*A:B:E:.**"));
			scLookup.put("i.", new Parser().parse("*A:T:E:.**"));
			scLookup.put("j.", new Parser().parse("*S:U:E:.**"));
			scLookup.put("g.", new Parser().parse("*S:A:E:.**"));
			scLookup.put("s.", new Parser().parse("*S:S:E:.**"));
			scLookup.put("b.", new Parser().parse("*S:B:E:.**"));
			scLookup.put("t.", new Parser().parse("*S:T:E:.**"));
			scLookup.put("h.", new Parser().parse("*B:U:E:.**"));
			scLookup.put("c.", new Parser().parse("*B:A:E:.**"));
			scLookup.put("k.", new Parser().parse("*B:S:E:.**"));
			scLookup.put("m.", new Parser().parse("*B:B:E:.**"));
			scLookup.put("n.", new Parser().parse("*B:T:E:.**"));
			scLookup.put("p.", new Parser().parse("*T:U:E:.**"));
			scLookup.put("x.", new Parser().parse("*T:A:E:.**"));
			scLookup.put("d.", new Parser().parse("*T:S:E:.**"));
			scLookup.put("f.", new Parser().parse("*T:B:E:.**"));
			scLookup.put("l.", new Parser().parse("*T:T:E:.**"));
		}catch (Exception e) {
			System.out.println("mapping failed: " + e.getMessage());
		}
	}
	
	public static HashMap<String, Node> aLookup = new HashMap<String, Node>();
	static {
		try {		
			aLookup.put("O:", new Parser().parse("*U:+A:**"));
			aLookup.put("M:", new Parser().parse("*S:+B:+T:**"));
			aLookup.put("F:", new Parser().parse("*U:+A:+S:+B:+T:**"));
			aLookup.put("I:", new Parser().parse("*E:+U:+A:+S:+B:+T:**"));	
		}catch (Exception e) {
			System.out.println("mapping failed: " + e.getMessage());
		}
	}
	
	public static HashMap<Integer, Node> emptyLookup = new HashMap<Integer, Node>();
	static {
		try {
			for (int i = 0; i < 7; i++)
				emptyLookup.put(i, new Parser().parse(getEmptySequence(i)));
		}catch (Exception e) {
			System.out.println("mapping failed: " + e.getMessage());
		}
		
		initialised = true;
	}
	
	public static String run(String input) {
		
		String result = null;
		Parser parser = new Parser();
		
		try {				
			Node n = parser.parse(input);
			//n.PrintNodes("");
			//result = n.Output();
			result = n.GetName();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(input);
			StringBuilder builder = new StringBuilder();
			for (int i = 0 ; i < parser.counter; i++)
				builder.append(" ");
			builder.append("^");
			System.out.println(builder.toString());
		}
		
		return result;
	}
	
	States currentState;
	Stack<Node> stack;
	int counter;
	
	public Parser() {
		currentState = States.state_pre;
		stack = new Stack<Node>();
		counter = 0;
	}
	
	public Node parse(String input) throws Exception {

		for (counter = 0; counter < input.length(); counter++){			
			Character charIn = new Character(input.charAt(counter));						
			nextChar(charIn);
		}
		
		//TODO: test case
		if (currentState != States.state_done) {
			throw new Exception("bad final state, missing *");
		}
		
		return stack.pop();
	}
	
	public void nextChar(Character charIn) throws Exception {		
		if (m_ignore.containsKey(charIn)) 
			return;
		stateDispatcher(charIn);			
	}
		
	private States stateDispatcher(char c) throws Exception {					
		
		if (m_star.containsKey(c)){			
			if (currentState == States.state_pre)
				return StateChangeActions(States.state_i, c);
			else if (currentState == States.state_f)
				return StateChangeActions(States.state_post, c);
			else if (currentState == States.state_post)
				return StateChangeActions(States.state_done, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}
		else if (m_addOp.containsKey(c)){			
			if (currentState == States.state_f)
				return StateChangeActions(States.state_d, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}
		else if (m_wLetter.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f) 
				return StateChangeActions(States.state_ws, c);	
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}		
		else if (m_marks.containsKey(c)){
			if (currentState == States.state_a || currentState == States.state_f || currentState == States.state_sc) 
				return StateChangeActions(States.state_f, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}		
		else if (m_smallCap.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f || currentState == States.state_ws) 
				return StateChangeActions(States.state_sc, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}
		else if (m_alphabet.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f) 
				return StateChangeActions(States.state_a, c);
			throw new Exception("cannot process " + c + " in state " + currentState.getFieldDescription());
		}

		throw new Exception("unrecognized " + c);
	}	
			
	private States StateChangeActions(States next, Character c) throws Exception {
		
		String transitionKey = States.GetKey(currentState, next);		
		if (!transitionMap.containsKey(transitionKey))
			throw new Exception("missing transition from "+currentState+" to "+next);
		
		switch (transitionMap.get(transitionKey)){
	
			case t_p_p:				
				break;
			case t_f_p:				
				a_f_p(c);
				break;
			case t_p_i:				
				break;
			case t_a_f:				
				a_a_f(c);
				break;
			case t_sc_f:			
				a_sc_f(c);
				break;				
			case t_d_a:				
				a_d_a(c);
				break;
			case t_d_sc:			
				a_d_sc(c);
				break;
			case t_d_ws:			
				a_d_ws(c);
				break;
			case t_f_a:				
				a_f_a(c);			
				break;
			case t_f_sc:			
				a_f_sc(c);
				break;
			case t_f_ws:						
				a_f_ws(c);
				break;
			case t_i_a:				
				a_i_a(c);
				break;
			case t_i_sc:			
				a_i_sc(c);
				break;
			case t_i_ws:			
				a_i_ws(c);
				break;
			case t_f_d:				
				a_f_d(c);
				break;
			case t_f_f:				
				a_f_f(c);
				break;				
			case t_ws_sc:			
				a_ws_sc(c);						
				break;
			default:
				throw new Exception("undefined transition from "+currentState+" to "+next);		
		}
		
		States prev = currentState;
		currentState = next;
		
		return prev;
	}
	
	//==================================================================================
	
	private void a_i_ws(Character c) throws Exception{ 
		stack.push(new Node(c));
	}	
	private void a_i_sc(Character c) throws Exception{ 
		stack.push(new Node(c));
	}
	private void a_i_a(Character c) throws Exception{ 
		stack.push(new Node(c));
	}	
	private void a_f_ws(Character c) throws Exception{ 
		pushNodeFromFinal(c);
	}
	private void a_f_sc(Character c) throws Exception{ 
		pushNodeFromFinal(c);
	}
	private void a_f_a(Character c) throws Exception{ 
		pushNodeFromFinal(c);
	}
	private void a_d_ws(Character c) throws Exception{ 
		pushNodeFromAdd(c);
	}
	private void a_d_sc(Character c) throws Exception{ 
		pushNodeFromAdd(c);
	}
	private void a_d_a(Character c) throws Exception{ 
		pushNodeFromAdd(c);
	}
	private void a_f_d(Character c) throws Exception{ 
		stack.push(null);
	}
	private void a_a_f(Character c) throws Exception{ 
		//TODO:test case
		if (m_marks.get(c) != 0)
			throw new Exception("layer mark must be '" + c_marks.get(0) + "'");		
		
		//This is a node of layer 0.		
		Node pop = stack.pop();	
		pop.AppendToName(c);
		pop.layer = m_marks.get(c);
		
		if (initialised && aLookup.containsKey(pop.GetName())) {
			pop.opCode = addition;
			pop.AddNodes(aLookup.get(pop.GetName()).nodes);
		}
		
		pop.ComputeTaille();
		pushNode(pop);
	}
	private void a_sc_f(Character c) throws Exception{ 
		//TODO:test case
		if (m_marks.get(c) != 1)
			throw new Exception("layer mark must be '" + c_marks.get(1) + "'");
				
		Node pop = stack.pop();	
		pop.AppendToName(c);
		pop.layer = m_marks.get(c);
		pop.opCode = multiplication;
		pop.AddNodes(scLookup.get(pop.GetName()).nodes);
		pop.ComputeTaille();
		pushNode(pop);
	}
	private void a_ws_sc(Character c) throws Exception{ 		
		//TODO:test case
		if (!m_vowels.containsKey(c)) 
			throw new Exception("must use vowel");				
		stack.peek().AppendToName(c);
	}
	
	//checks if layer mark is ok
	//finds up to 3 nodes in multiplication relation (siblings)
	//creates a new parent multiplication node and ads children
	//fills with Empty as needed
	//triggers more processing if required
	private void a_f_f(Character c) throws Exception{
		
		Node tempNode = stack.peek();
		
		//TODO:test case
		if (tempNode.layer != m_marks.get(c)-1)
			throw new Exception("bad layer mark, expecting " + c_marks.get(tempNode.layer+1));
		
		Stack<Node> tempStack = new Stack<Node>();		
		for (int i = 0; i < 3; i++) {			
			if (stack.isEmpty())
				break;			
			tempNode = stack.peek();			
			if (tempNode == null)
				break;									
			if (tempNode.layer != m_marks.get(c)-1)
				break;			
			tempStack.push(stack.pop());
		}
		
		//sanity
		if (tempStack.isEmpty())
			throw new Exception("could not find nodes in multiplication relation");
				
		Node newNode = new Node(null, multiplication, m_marks.get(c));	
		while (!tempStack.isEmpty()){
			Node pop = tempStack.pop();
			newNode.AppendToName(pop.GetName());
			newNode.AddNode(pop);
		}
		
		//empties not added to his name
		while (newNode.nodes.size() < 3)	
			newNode.AddNode(emptyLookup.get(newNode.layer-1));			

		newNode.AppendToName(c);		
		newNode.ComputeTaille();		
		pushNode(newNode);
	}
	
	private void a_f_p(Character c) throws Exception{
		//TODO:test case
		if (stack.size() > 1)
			throw new Exception("missing layer mark?");
	}
	
	//==================================================================================
	
	//node will be in additive relation
	//check if layers match
	//no further processing, need layer mark
	private void pushNodeFromAdd(Character c) throws Exception{
		//sanity
		if (stack.isEmpty()) 
			throw new Exception("pushNodeFromAdd cannot be used here, stack empty");
		//sanity
		if  (stack.peek() != null)
			throw new Exception("pushNodeFromAdd cannot be used here, not an addition");
		stack.pop(); //eat
		Node tempNode = stack.peek();
		
		//TODO: test case
		if (tempNode.layer == 0 && !m_alphabet.containsKey(c))
			throw new Exception("small cap cannot be used here");
		
		stack.push(null);
		stack.push(new Node(c));
	}
	
	//node will be in multiplicative relation
	//check if layers match
	//check if not too many nodes in relation already
	//no further processing, need layer mark
	private void pushNodeFromFinal(Character c) throws Exception{	
		
		//sanity
		if (stack.isEmpty() || stack.peek() == null) 
			throw new Exception("addNodeFromFinal cannot be used");
		
		Stack<Node> tempStack = new Stack<Node>();		
		for (int i = 0; i< 3; i++){									
			tempStack.push(stack.pop());			
			if (stack.isEmpty() || stack.peek() == null)
				break;			
			if (stack.peek().layer != tempStack.peek().layer) 
				break;			
			
			//TODO: test case
			if (tempStack.peek().layer == 0 && !m_alphabet.containsKey(c))
				throw new Exception("small cap cannot be used here");
		}
		
		//TODO: test case
		if (tempStack.size() == 3)
			throw new Exception("missing layer mark");
		
		while (!tempStack.isEmpty())
			stack.push(tempStack.pop());
		
		stack.push(new Node(c));	
	}
	
	//check if we can addition
	private void pushNode(Node n) throws Exception{

		//sanity
		if (n == null)
			throw new Exception("null node");		
		//sanity
		if (n.layer < 0) 
			throw new Exception("negative node");
		
		if (stack.isEmpty() || stack.peek() != null) {
			stack.push(n);
			return;
		}

		stack.pop();
		Node popped = stack.pop();
		
		//sanity
		if (popped.layer < 0) 
			throw new Exception("negative previous node");
			
		if (n.layer != popped.layer) {
			stack.push(popped);
			stack.push(null);
			stack.push(n);
			return;
		}
		
		Node newNode;
		if (popped.opCode == addition){
			newNode = popped;
		}
		else {
			newNode = new Node(null, addition, popped.layer);
			newNode.AppendToName(popped.GetName());
			newNode.AddNode(popped);
		}
		
		newNode.AppendToName(addition);
		newNode.AppendToName(n.GetName());
		
		//check if order is respected
		ArrayList<Node> children = newNode.nodes;
		Node lastChild = children.get(children.size()-1);	
		
		//TODO:testcase
		if (!lastChild.IsLessThan(n))
			throw new Exception("standard order is not respected");
		
		newNode.AddNode(n);
		stack.push(newNode);
	}
	
	public static String getEmptySequence(int l) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("*E");
		for (int i = 0; i <= l; i++)
			builder.append(c_marks.get(i));
		builder.append("**");
		return builder.toString();		
	}
	
	public class Node {
		
		private int layer = -1;
		private int taille = -1;
		private boolean isTailleComputed = false;
		
		private StringBuilder name = null;			
		public ArrayList<Node> nodes = new ArrayList<Node>();	
		public Node parent = null;
		public Character opCode = null;
		
		public Node(Character c){
			name = new StringBuilder();
			if (c != null)
				name.append(c);
		}
		
		public Node(Character c, Character operator, int l){
			this(c);
			opCode = operator;
			layer = l;
		}
		
		public int ComputeTaille() throws Exception {
			
			if (!isTailleComputed) {
				
				if (nodes.isEmpty()){
					Character c = GetName().charAt(0);
					
					//sanity
					if (!m_alphabet.containsKey(c))
						throw new Exception("cannot compute ordre standard");					
					taille = 1;				
				}
				else if (opCode == addition) {					
					for (Node child : nodes) {
						if (taille == -1)
							taille = child.ComputeTaille();
						else
							taille += child.ComputeTaille();
					}						
				}
				else if (opCode == multiplication) {
					for (Node child : nodes) {
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
			
		public boolean IsLessThan(Node n) throws Exception {
			Boolean res = isLessThan(n);
			if (res == null)
				throw new Exception("duplicate found " + GetName() + " and " + n.GetName());
			return res;
		}
		
		public Boolean isLessThan(Node n) throws Exception{
			
			//sanity
			if (this.layer < 0 || n.layer < 0)
				throw new Exception("negative layer values");
			
			if (this.layer < n.layer)
				return true;
			if (this.layer > n.layer)
				throw new Exception("layer mismatch between " + GetName() + " and " + n.GetName());
			if (this.taille < n.taille)
				return true;
			if (this.taille > n.taille)
				throw new Exception("taille mismatch between " + GetName() + " and " + n.GetName());
			
			if (this.opCode == null && n.opCode == null) {
				int a = m_alphabet.get(GetName().charAt(0));
				int b = m_alphabet.get(n.GetName().charAt(0));
				if (a == b)
					return null;
				if (a < b)
					return true;
				
				//TODO: test case
				throw new Exception("alphanumeric mismatch between " + GetName() + " and " + n.GetName());
			}
			else if (this.opCode == null && n.opCode != null) {
				if (n.layer != 0)
					//sanity
					throw new Exception("layer mismatch");
				
				throw new Exception("not implemented yet null not null");
			}
			else if (this.opCode != null && n.opCode == null) {
				if (this.layer != 0)
					//sanity
					throw new Exception("layer mismatch");
				
				throw new Exception("not implemented yet not null null");
			}
			else if (this.opCode.equals(multiplication) && n.opCode.equals(multiplication)) {				
				for (int i = 0; i < 3; i++ ){
					Boolean res = this.nodes.get(i).isLessThan(n.nodes.get(i));
					if (res != null)
						return res;
				}
				return null;
			}
			else if (this.opCode.equals(multiplication) && n.opCode.equals(addition)) {
				
				throw new Exception("not implemented yet m a");
			}
			else if (this.opCode.equals(addition) && n.opCode.equals(multiplication)) {
				
				throw new Exception("not implemented yet a m");
			}
			else if (this.opCode.equals(addition) && n.opCode.equals(addition)) {
				
				if (this.nodes.size() != n.nodes.size())
					//TODO: test case
					throw new Exception("not implemented yet size mismatch");
				
				for (int i = 0; i < nodes.size(); i++ ){
					Boolean res = this.nodes.get(i).isLessThan(n.nodes.get(i));
					if (res != null)
						return res;
				}				
				return null;
			}
			
			//sanity
			throw new Exception("not implemented yet");
		}
		
	
		//=============================================
			
		public void AddNodes(ArrayList<Node> list) throws Exception {
			for (Node n : list){
				AddNode(n);
			}
		}
		
		public void AddNode(Node n) throws Exception{		
			n.SetParent(this);		
			nodes.add(n);
		}
		
		public void SetParent(Node n) throws Exception{	
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
		
		public Node GetParent(){
			return parent;
		}
				
		//Readable representation of a node and its children in a list-form
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
				for(Node node : nodes){
					node.PrintNodes(prepend+"\t");
				}
			}	
		}
		
		public String Output(){
			
			StringBuilder builder = new StringBuilder("[" + name.toString());			
			if (layer >= 0)
				builder.append("|" + layer);
			builder.append("]");			
			
			for(Node node : nodes){
				builder.append(node.Output());
			}
			
			return builder.toString();
		}
	}
}
