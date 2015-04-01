package NewParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import TopDown.Node;

public class Parser {
	
	public static List<Character> c_alphabet = Arrays.asList(new Character[]{'S','B','T','U','A','O','M','I','E','F'});
	public static List<Character> c_smallCap = Arrays.asList(new Character[]{'y','o','e','u','a','i','j','g','s','b','t','h','c','k','m','n','p','x','d','f','l'});
	public static List<Character> c_vowels   = Arrays.asList(new Character[]{'o','a','u','e'});
	public static List<Character> c_ignore   = Arrays.asList(new Character[]{'(',')',' '});
	public static List<Character> c_wLetter  = Arrays.asList(new Character[]{'w'});
	public static List<Character> c_addOp    = Arrays.asList(new Character[]{'+'});
	public static List<Character> c_marks    = Arrays.asList(new Character[]{':', '.', '-', '�', ',', '_', ';'});
	public static List<Character> c_star     = Arrays.asList(new Character[]{'*'});
	
	public enum States {
		
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
	
	public static Character multiplication = '*';
	public static Character addition = '+';	
	static int counter;
	
	public static String run(String input) {
		
		String result = null;
		
		try {
			counter = 0;
			Parser parser = new Parser();			
			Node n = parser.parse(input);
			n.PrintNodes("");
			result = n.Output();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(input);
			StringBuilder builder = new StringBuilder();
			for (int i = 0 ; i < counter; i++)
				builder.append(" ");
			builder.append("^");
			result = builder.toString();
		}
		
		return result;
	}
	
	States currentState;
	Character previousChar;
	Stack<Node> stack;
	
	public Parser() {
		currentState = States.state_pre;
		previousChar = null;
		stack = new Stack<Node>();
	}
	
	public Node parse(String input) throws Exception {

		for (counter = 0; counter < input.length(); counter++){			
			Character charIn = new Character(input.charAt(counter));						
			if (m_ignore.containsKey(charIn)) 
				continue;			
			stateDispatcher(charIn);			
			previousChar = charIn;
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
		previousChar = charIn;
	}
	
	public States GetCurrentState() {
		return currentState;
	}
	
	public Integer getPreviousLM() {
		if (stack.isEmpty())
			return null;		
		Node temp = stack.peek();
		return temp.layer;
	}
	
	public boolean canMovePost(){
		return stack.size() == 1;
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
		
	//==================================================================================
	
	private void stackAppendLayer(Character c) throws Exception{
		Node n = stack.pop();	
		n.AppendToName(c);
		n.layer = m_marks.get(c);
		stackAdd(n);
	}
	
	private void stackAppend(Character c) throws Exception{
		Node n = stack.pop();	
		n.AppendToName(c);
		stackAdd(n);
	}
	
	private void stackMultiply(Character c) throws Exception{
		Stack<Node> multStack = new Stack<Node>();		
		for (int i = 0; i < 3; i++) {
			if (stack.isEmpty())
				break;
			Node temp = stack.peek();
			if (temp == null || temp.layer != m_marks.get(c)-1)
				break;			
			multStack.push(stack.pop());
		}
		
		Node newNode = new Node(null);
		newNode.opCode = multiplication;
		while (!multStack.isEmpty()){
			Node popedNode = multStack.pop();
			newNode.AppendToName(popedNode.GetName());
			newNode.AddNode(popedNode);
		}
		stackAdd(newNode);
		stackAppendLayer(c);
	}
	
	private void stackAdd(Node n) throws Exception{
		
		if (n != null){			
			if (n.layer < 0) {
				stack.push(n);
			}
			else {
				if (!stack.isEmpty() && stack.peek() == null) {
					stack.pop(); //eat
					Node prev = stack.pop();
					if (n.layer == prev.layer){
						Node addNode;
						if (prev.opCode == addition){
							addNode = prev;
						}
						else {
							addNode = new Node(null);
							addNode.layer = n.layer;
							addNode.opCode = addition;
							addNode.AppendToName(prev.GetName());
							addNode.AddNode(prev);
						}
						
						addNode.AppendToName(addition);
						addNode.AppendToName(n.GetName());
						addNode.AddNode(n);
						stackAdd(addNode);
					}
					else {
						stack.push(prev);
						stack.push(null);
						stack.push(n);
					}
				}
				else {
					stack.push(n);
				}
			}
		}
		else {
			stack.push(null);
		}
	}
	
	//==================================================================================
	
	private void a_i_ws(Character c) throws Exception{
		stackAdd(new Node(c));
	}	
	private void a_i_sc(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_i_a(Character c) throws Exception{
		stackAdd(new Node(c));
	}	
	private void a_f_ws(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_f_sc(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_f_a(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_d_ws(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_d_sc(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_d_a(Character c) throws Exception{
		stackAdd(new Node(c));
	}
	private void a_f_d(char c) throws Exception{		
		stackAdd(null);
	}
	
	//==================================================================================
	
	private void a_a_f(Character c) throws Exception{		
		//TODO:test case
		if (m_marks.get(c) != 0)
			throw new Exception("layer mark must be '" + c_marks.get(0) + "'");		
		stackAppendLayer(c);
	}
	private void a_sc_f(Character c) throws Exception {
		//TODO:test case
		if (m_marks.get(c) != 1)
			throw new Exception("layer mark must be '" + c_marks.get(1) + "'");
		stackAppendLayer(c);
	}
	private void a_ws_sc(Character c) throws Exception{
		//TODO:test case
		if (!m_vowels.containsKey(c)) 
			throw new Exception(c + " cannot follow " + previousChar);		
		stackAppend(c);
	}

	//==================================================================================
	
	private void a_f_f(Character c) throws Exception{	
		Node temp = stack.peek();
		//TODO:test case
		if (temp.layer != m_marks.get(c)-1)
			throw new Exception("bad layer mark, expecting " + c_marks.get(temp.layer+1));
		stackMultiply(c);
	}
	
	//==================================================================================
	
	private void a_f_p(Character c) throws Exception{	
		//TODO:test case
		if (stack.size() > 1)
			throw new Exception("missing layer mark");
	}
	
	//==================================================================================
		
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
	
	private String getEmptySequence(int l) {
		
		if (l == 0)
			return "E" + c_marks.get(0);
		else 
		{
			StringBuilder builder = new StringBuilder();
			builder.append(getEmptySequence(l-1));
			builder.append(getEmptySequence(l-1));
			builder.append(getEmptySequence(l-1));
			builder.append(c_marks.get(l));
			return builder.toString();
		}				
	}
	
	public class Node {
		
		private boolean completed = false;
		private StringBuilder name = null;	
		private int layer = -1;
		public ArrayList<Node> nodes = new ArrayList<Node>();	
		public Node parent = null;
		public Character opCode = null;
		
		public Node(Character c){
			name = new StringBuilder();
			if (c != null)
				name.append(c);
		}
		
		//=============================================
		
		public void AddNode(Node n) throws Exception{
			
			if (completed)
				throw new Exception("already completed, cannot modify me");
			
			n.SetParent(this);		
			nodes.add(n);
		}
		
		public void SetParent(Node n) throws Exception{
			
			//this node can be finalized but then we find out it is in a * relation, hence the check for parent != null
			if (completed && parent != null) 
				throw new Exception("already completed, cannot modify me");
			
			parent = n;
		}
		
		public void AppendToName(Character c) throws Exception{
			
			if (completed)
				throw new Exception("already completed, cannot modify me");
			
			name.append(c);
		}
		
		public void AppendToName(String s) throws Exception{
			
			if (completed)
				throw new Exception("already completed, cannot modify me");
			
			name.append(s);
		}
		
		public void CompleteNode(boolean completed){
			this.completed = completed;
		}
		
		//=============================================
		
		public String GetName(){
			return name.toString();
		}
		
		public Node GetParent(){
			return parent;
		}
		
		public boolean IsCompleted(){
			return completed;
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
