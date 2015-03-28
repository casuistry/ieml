package NewParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import TopDown.Node;

public class Parser {
	
	public static List<Character> c_alphabet = Arrays.asList(new Character[]{'S','B','T','U','A','O','M','I','E','F'});
	public static List<Character> c_smallCap = Arrays.asList(new Character[]{'y','o','e','u','a','i','j','g','s','b','t','h','c','k','m','n','p','x','d','f','l'});
	public static List<Character> c_vowels   = Arrays.asList(new Character[]{'o','a','u','e'});
	public static List<Character> c_ignore   = Arrays.asList(new Character[]{'(',')','*',' '});
	public static List<Character> c_wLetter  = Arrays.asList(new Character[]{'w'});
	public static List<Character> c_addOp    = Arrays.asList(new Character[]{'+'});
	public static List<Character> c_marks    = Arrays.asList(new Character[]{':', '.', '-', '’', ',', '_', ';'});
	
	public enum States {
		
		state_i("initial"),		
		state_sc("small cap"),		
		state_ws("start small cap wo, wa, wu and we"),		
		state_a("primitives"),		
		state_f("node completed"),	    
		state_d("addition operation");		
		
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
		t_i_sc,
		t_i_ws,
		t_i_a,
		t_f_sc,
		t_f_ws,
		t_f_a,
		t_d_sc,
		t_d_ws,
		t_d_a,
		t_a_f,    //alphabet to final
		t_ws_sc,  //w to wa
		t_sc_f,   //sc to final
		t_f_d,
		t_f_f
	}
	
	public static HashMap<String, Transitions> transitionMap = new HashMap<String, Transitions>();
	static {
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
	}
	
	public static Character multiplication = '*';
	public static Character addition = '+';
	public static int MaxMultiplications = 3;
	public static String StateInitial = "Initial";
	public static String StateLetter = "Letter";
	public static String StateMark = "Mark";
	public static String StateAddition = "Addition";
	
	static int counter;
	
	public static String run(String input) {
		
		String result = null;
		
		try {
			counter = 0;
			Parser parser = new Parser();			
			Node n = parser.parse(input);
			//n.PrintNodes("");
			//result = n.Output();
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
	
	Node currentNode;
	States currentState;
	Character previousChar;
	LayerMarkTracker lmTracker; //Used to avoid situations where the next layer mark is of layer inferior to the current layer
	
	public Node parse(String input) throws Exception {
		
		currentNode = null;
		
		currentState = States.state_i;
		previousChar = null;
		lmTracker = new LayerMarkTracker();	
				
		for (counter = 0; counter < input.length(); counter++){
			
			Character charIn = new Character(input.charAt(counter));
						
			stateDispatcher(charIn);
			
			previousChar = charIn;
		}
		
		if (currentState != States.state_f)
			throw new Exception("bad final state");
		
		
		return currentNode.parent != null ? currentNode.parent : currentNode;
	}
	
	private States stateDispatcher(char c) throws Exception {					
		
		if (m_ignore.containsKey(c))
			return currentState;
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
		
	//==================================================create first node ===============
	//just one node as it may be the only thing to parse
	//root may be null
	
	private void a_i_ws(Character c) throws Exception{
		currentNode = new Node(c);
	}	
	//create first node and its parent
	private void a_i_sc(Character c) throws Exception{
		currentNode = new Node(c);
	}
	//create first node and its parent
	private void a_i_a(Character c) throws Exception{
		currentNode = new Node(c);
	}
	
	//==================================================finalize node====================
	private void a_a_f(Character c) throws Exception{			
		if (m_marks.get(c) != 0)
			throw new Exception("layer mark must be '" + c_marks.get(0) + "'");
		finalizeNode(c);
	}
	private void a_sc_f(Character c) throws Exception {
		if (m_marks.get(c) != 1)
			throw new Exception("layer mark must be '" + c_marks.get(1) + "'");
		
		//get name for parent
		String parentName = currentNode.GetName();
		
		//currentNode
		finalizeNode(c);
		
		//fast-forward
		multNode(new Character('E'));
		finalizeNode(new Character(':'));
		multNode(new Character('E'));
		finalizeNode(new Character(':'));
		

		finalizeNode(c);
	}
	//recognized wo, we, wu , wa
	private void a_ws_sc(Character c) throws Exception{
		if (!m_vowels.containsKey(c)) 
			throw new Exception(c + " cannot follow " + previousChar);		
		currentNode.AppendToName(c);
	}
	private void finalizeNode(Character c) throws Exception{		
		lmTracker.Set(c);	
		currentNode.AppendToName(c);
		currentNode.CompleteNode(true);
	}
	
	//================================================new node in multiplication relation
	private void a_f_ws(Character c) throws Exception{
		multNode(c);
	}
	private void a_f_sc(Character c) throws Exception{
		multNode(c);
	}
	private void a_f_a(Character c) throws Exception{
		multNode(c);
	}
	private void multNode(Character c) throws Exception{
		
		if (currentNode.parent == null) {
			Node newRoot = new Node(null);
			newRoot.AddNode(currentNode);
			newRoot.opCode = multiplication;
		}

		if (currentNode.parent.nodes.size() < 3) {
			Node newNode = new Node(c);
			currentNode.parent.AddNode(newNode);			
			currentNode = newNode;
		}
		else {
			throw new Exception("number of parameters in a multiplication relation exceeds maximum");
		}
	}
	
	//======================================================new node in addition relation
	
	private void a_d_ws(Character c) throws Exception{
		additiveNode(c);
	}
	private void a_d_sc(Character c) throws Exception{
		additiveNode(c);
	}
	private void a_d_a(Character c) throws Exception{
		additiveNode(c);
	}
	private void additiveNode(Character c) throws Exception{
		Node newNode = new Node(c);
		currentNode.parent.opCode = addition;
		currentNode.parent.AddNode(newNode);
		currentNode = newNode; 
	}
	
	//==================================================finalize node.parent==============
	
	//finalize next layer node
	private void a_f_f(Character c) throws Exception{				
		lmTracker.CheckAndSet(c);
	}
	
	//get ready for additive relation
	private void a_f_d(char c){		

	}


	//====================================================================================

	

	
	private class LayerMarkTracker {
	
		private Character ch = null;
		
		public void Set(Character c) {
			ch = c;
		}
		
		public void CheckAndSet(Character c) throws Exception {
			if (c_marks.indexOf(ch) + 1 != c_marks.indexOf(c))
				throw new Exception(c + " cannot follow " + ch);			
			Set(c);
		}
	}
	
	private States StateChangeActions(States next, Character c) throws Exception {
		
		String transitionKey = States.GetKey(currentState, next);		
		if (!transitionMap.containsKey(transitionKey))
			throw new Exception("missing transition from "+currentState+" to "+next);
		
		switch (transitionMap.get(transitionKey)){
		
			case t_a_f:				//complete node, start multiplication counter
				a_a_f(c);
				break;
			case t_sc_f:			//complete node, start multiplication counter
				a_sc_f(c);
				break;				
			case t_d_a:				//new node in addition relation
				a_d_a(c);
				break;
			case t_d_sc:			//new node in addition relation
				a_d_sc(c);
				break;
			case t_d_ws:			//new node in addition relation
				a_d_ws(c);
				break;
			case t_f_a:				//new node in multiplication relation
				a_f_a(c);			
				break;
			case t_f_sc:			//new node in multiplication relation
				a_f_sc(c);
				break;
			case t_f_ws:			//new node in multiplication relation				
				a_f_ws(c);
				break;
			case t_i_a:				//initial node
				a_i_a(c);
				break;
			case t_i_sc:			//initial node
				a_i_sc(c);
				break;
			case t_i_ws:			//initial node
				a_i_ws(c);
				break;
			case t_f_d:				//get ready for additive relation
				a_f_d(c);
				break;
			case t_f_f:				//finalize node.parent
				a_f_f(c);
				break;				
			case t_ws_sc:			//update node's name
				a_ws_sc(c);						
				break;
			default:
				throw new Exception("undefined transition from "+currentState+" to "+next);		
		}
		
		States prev = currentState;
		currentState = next;
		
		return prev;
	}
	
	public class Node {
		
		private boolean completed = false;
		private StringBuilder name = null;	
		private int layer = -1;
		public ArrayList<Node> nodes = new ArrayList<Node>();	
		public Node parent = null;
		public Character opCode = null;
		
		public Node(Character c){
			name = c != null ? new StringBuilder(c) : new StringBuilder();
		}
		
		//=============================================
		
		public void AddNode(Node n) throws Exception{
			
			if (completed)
				throw new Exception("already completed, cannot modify me");
			
			n.SetParent(this);		
			nodes.add(n);
		}
		
		public void SetParent(Node n) throws Exception{
			
			if (completed)
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
