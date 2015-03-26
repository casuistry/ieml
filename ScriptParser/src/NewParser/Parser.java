package NewParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	LayerMarkCounter lmCounter; //Used to avoid a situation like: S:S:S:S:
	LayerMarkTracker lmTracker; //Used to avoid situations where the next layer mark is of layer inferior to the current layer
	
	public Node parse(String input) throws Exception {
		
		currentNode = null;
		currentState = States.state_i;
		previousChar = null;
		lmCounter = new LayerMarkCounter();
		lmTracker = new LayerMarkTracker();	
		
		//state variables
		Node root = null;
		
		for (counter = 0; counter < input.length(); counter++){
			
			Character charIn = new Character(input.charAt(counter));
						
			stateDispatcher(charIn);
			
			previousChar = charIn;
		}
		
		if (currentState != States.state_f)
			throw new Exception("bad final state");
		
		//currentNode.parent.name = input;
		return root != null ? root.parent : null;
	}
	
	private States stateDispatcher(char c) throws Exception {					
		
		if (m_ignore.containsKey(c))
			return currentState;
		else if (m_addOp.containsKey(c)){			
			if (currentState == States.state_f)
				return StateChangeActions(States.state_d, c);
		}
		else if (m_wLetter.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f) 
				return StateChangeActions(States.state_ws, c);	
		}		
		else if (m_marks.containsKey(c)){
			if (currentState == States.state_a || currentState == States.state_f || currentState == States.state_sc) 
				return StateChangeActions(States.state_f, c);
		}		
		else if (m_smallCap.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f || currentState == States.state_ws) 
				return StateChangeActions(States.state_sc, c);
		}
		else if (m_alphabet.containsKey(c)){
			if (currentState == States.state_i || currentState == States.state_d || currentState == States.state_f) 
				return StateChangeActions(States.state_a, c);
		}

		throw new Exception("cannot process " + c + " following " + previousChar + " in state " + currentState.getFieldDescription());
	}	
	
	//create first node and its parent
	private void a_i_ws(char c){
		
	}
	
	//new node in additive relation
	private void a_d_ws(char c){
		
	}
	
	//get ready for additive relation
	private void a_f_d(char c){		
		lmCounter.Reset();
	}
	
	//new node in multiplication relation
	private void a_f_ws(char c){

	}
	
	//recognized wo, we, wu , wa
	private void a_ws_sc(char c){
		
	}
	
	//finalize node
	private void a_a_f(char c) throws Exception{		
		lmCounter.Increment();			
		lmTracker.Set(c);
	}
	
	//finalize next layer node
	private void a_f_f(char c) throws Exception{		
		lmCounter.Reset();
		lmTracker.CheckAndSet(c);
	}
		
	private void a_a_f_exit(char c) throws Exception {
		if (m_marks.get(c) != 0)
			throw new Exception("layer mark must be '" + c_marks.get(0) + "'");
	}
	
	private void a_sc_f_exit(char c) throws Exception {
		if (m_marks.get(c) != 1)
			throw new Exception("layer mark must be '" + c_marks.get(1) + "'");
	}
	
	private void a_ws_sc_exit(char c) throws Exception {
		if (!m_vowels.containsKey(c)) 
			throw new Exception(c + " cannot follow " + previousChar);
	}
	
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
	
	private class LayerMarkCounter{
		
		private byte counter = 0;
		
		public void Increment() throws Exception{
			if (counter >= MaxMultiplications)
				throw new Exception("too many nodes in multiplication relation");
			else
				counter++;	
		}
		
		public void Reset(){
			counter = 0;
		}		
	}
	
	private States StateChangeActions(States next, char c) throws Exception {
		
		String transitionKey = States.GetKey(currentState, next);		
		if (!transitionMap.containsKey(transitionKey))
			throw new Exception("missing transition from "+currentState+" to "+next);
		
		switch (transitionMap.get(transitionKey)){
			case t_a_f:
				a_a_f_exit(c);
				a_a_f(c);
				break;
			case t_d_a:
				break;
			case t_d_sc:
				break;
			case t_d_ws:
				a_d_ws(c);
				break;
			case t_f_a:
				break;
			case t_f_d:
				a_f_d(c);
				break;
			case t_f_f:
				a_f_f(c);
				break;
			case t_f_sc:
				break;
			case t_f_ws:
				a_f_ws(c);
				break;
			case t_i_a:
				break;
			case t_i_sc:
				break;
			case t_i_ws:
				a_i_ws(c);
				break;
			case t_sc_f:
				a_sc_f_exit(c);
				a_a_f(c);
				break;
			case t_ws_sc:
				a_ws_sc_exit(c);
				a_ws_sc(c);
				break;
			default:
				throw new Exception("undefined transition from "+currentState+" to "+next);		
		}
		
		States prev = currentState;
		currentState = next;
		
		return prev;
	}
	
	public class Node{
		private String name = null;	
		private int layer = -1;
		public ArrayList<Node> nodes = new ArrayList<Node>();	
		public Node parent = null;
		public Character opCode = multiplication;
		
		//Readable representation of a node and its children in a list-form
		public void PrintNodes(String prepend){
			
			StringBuilder builder = new StringBuilder("[" + name);
			
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
			
			StringBuilder builder = new StringBuilder("[" + name);			
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
