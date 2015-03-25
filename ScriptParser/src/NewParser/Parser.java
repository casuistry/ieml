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
	
	enum States {
		state_i,		//initial
		state_sc,		//small cap 
		state_ws,		//start small cap wo, wa, wu and we
		state_a,		//primitives
		state_finish,	//node completed
		state_add,		//addition operation
	}
	
	Node currentNode;
	States currentState;
	Character previousChar;
	int lmCounter;
	Character previousLM;
	
	public Node parse(String input) throws Exception {
		
		currentNode = null;
		currentState = States.state_i;
		previousChar = null;
		lmCounter = 0;
		previousLM = null;
		
		//state variables
		Node root = null;
		
		for (counter = 0; counter < input.length(); counter++){
			
			Character charIn = new Character(input.charAt(counter));
			
			if (c_ignore.contains(charIn))
				continue;
			
			stateDispatcher(charIn);
			
			previousChar = charIn;
		}
		
		if (currentState != States.state_finish)
			throw new Exception("bad final state");
		
		//currentNode.parent.name = input;
		return root != null ? root.parent : null;
	}
	
	private void stateDispatcher(char c) throws Exception {
				
		if (c_addOp.contains(c)){
			switch (currentState) {
				case state_i:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_a:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_add:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_finish:
					action_op(c);
					break;
				case state_sc:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_ws:
					throw new Exception(c + " cannot follow " + previousChar);
				default:
					throw new Exception("unrecognized state " + currentState);			
			}
			
			currentState = States.state_add;
		}
		else if (c_wLetter.contains(c)){
			switch (currentState) {
				case state_i:
					action_ci(c);
					break;
				case state_a:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_add:
					action_ca(c);
					break;
				case state_finish:
					action_cf(c);
					break;
				case state_sc:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_ws:
					throw new Exception(c + " cannot follow " + previousChar);
				default:
					throw new Exception("unrecognized state " + currentState);			
			}
			
			currentState = States.state_ws;
		}		
		else if (c_marks.contains(c)){
			switch (currentState) {
				case state_i:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_a:
					if (c != c_marks.get(0))
						throw new Exception("layer mark must be '" + c_marks.get(0) + "'");
					action_fn(c);
					break;
				case state_add:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_finish:
					action_fnn(c);
					break;
				case state_sc:
					if (c != c_marks.get(1))
						throw new Exception("layer mark must be '" + c_marks.get(1) + "'");
					action_fn(c);
					break;
				case state_ws:
					throw new Exception(c + " cannot follow " + previousChar);
				default:
					throw new Exception("unrecognized state " + currentState);			
			}
			
			currentState = States.state_finish;
		}		
		else if (c_smallCap.contains(c)){
			switch (currentState) {
				case state_i:
					action_ci(c);
					break;
				case state_a:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_add:
					action_ca(c);
					break;
				case state_finish:
					action_cf(c);
					break;
				case state_sc:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_ws:
					if (c_vowels.contains(c))
						action_cw(c);
					else 
						throw new Exception(c + " cannot follow " + previousChar);
				default:
					throw new Exception("unrecognized state " + currentState);			
			}
			
			currentState = States.state_sc;
		}
		else if (c_alphabet.contains(c)){
			switch (currentState) {
				case state_i:
					action_ci(c);
					break;
				case state_a:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_add:
					action_ca(c);
					break;
				case state_finish:
					action_cf(c);
					break;
				case state_sc:
					throw new Exception(c + " cannot follow " + previousChar);
				case state_ws:
					throw new Exception(c + " cannot follow " + previousChar);
				default:
					throw new Exception("unrecognized state " + currentState);			
			}
		
			currentState = States.state_a;
		}
		else {
			throw new Exception("unrecognized input " + c);
		}
	}	
	
	//create first node and its parent
	private void action_ci(char c){
		
	}
	
	//new node in additive relation
	private void action_ca(char c){
		
	}
	
	//get ready for additive relation
	private void action_op(char c){
		
		lmCounter = 0;
	}
	
	//new node in multiplication relation
	private void action_cf(char c){

	}
	
	//recognized wo, we, wu , wa
	private void action_cw(char c){
		
	}
	
	//finalize node
	private void action_fn(char c) throws Exception{
		
		if (lmCounter >= MaxMultiplications)
			throw new Exception("too many nodes in multiplication relation");
		else
			lmCounter++;		
		
		previousLM = c;
	}
	
	//finalize next node
	private void action_fnn(char c) throws Exception{
		
		lmCounter = 0;
		
		if (c_marks.indexOf(previousLM) + 1 != c_marks.indexOf(c))
			throw new Exception(c + " cannot follow " + previousLM);
		else
			previousLM = c;
	}
		
	public class State {
		
		String current;
		HashMap<String, HashMap<List<Character>, String>> Tx;
		
		Character currentLayer;
		String stateMark;
		HashMap<String, HashMap<String, String>> markTx;
		String _same = "_same";
		String _greaterByOne = "_greaterByOne";
		String _reset = "_reset";
		String _first = "_first";
		String _second = "_second";
		String _third = "_third";
		String _higher = "_higher";		
				
		public State(){			
			_init_state();
			_init_layer();			
		}
		
		private void _init_state(){
			
			current = StateInitial;
			Tx = new HashMap<String, HashMap<List<Character>, String>>();
			
			//from Initial
			HashMap<List<Character>, String> fromInitial = new HashMap<List<Character>, String>();
			fromInitial.put(c_alphabet, StateLetter);			
			Tx.put(StateInitial, fromInitial);	
			
			//from Letter
			HashMap<List<Character>, String> fromLetter = new HashMap<List<Character>, String>();
			fromLetter.put(c_marks, StateMark);			
			Tx.put(StateLetter, fromLetter);	
			
			//from Mark
			HashMap<List<Character>, String> fromMark = new HashMap<List<Character>, String>();
			fromMark.put(c_alphabet, StateLetter);		
			fromMark.put(c_addOp, StateAddition);
			fromMark.put(c_marks, StateMark);
			Tx.put(StateMark, fromMark);
			
			//from Addition
			HashMap<List<Character>, String> fromAddition = new HashMap<List<Character>, String>();
			fromAddition.put(c_alphabet, StateLetter);		
			Tx.put(StateAddition, fromAddition);
		}
		
		private void _init_layer(){
			
			currentLayer = null;
			stateMark = StateInitial;
			markTx = new HashMap<String, HashMap<String, String>>();
			
			HashMap<String, String> fromInitial = new HashMap<String, String>();
			fromInitial.put(_same, "A");	
			HashMap<String, String> fromA = new HashMap<String, String>();
			fromA.put(_same, "B");
			fromA.put(_greaterByOne, "D");
			HashMap<String, String> fromB =  new HashMap<String, String>();
			fromB.put(_same, "C");
			fromB.put(_greaterByOne, "D");
			HashMap<String, String> fromC = new HashMap<String, String>();
			fromC.put(_same, "D");
			HashMap<String, String> fromD = new HashMap<String, String>();
			fromD.put(_reset, "A");
			fromD.put(_greaterByOne, "D");
			
			markTx.put(StateInitial, fromInitial);
			markTx.put(StateInitial, fromA);
			markTx.put(StateInitial, fromB);
			markTx.put(StateInitial, fromC);
			markTx.put(StateInitial, fromD);
		}
		
		public String Transition(List<Character> cl, Character c) throws Exception{
			
			if (!Tx.containsKey(current))
				throw new Exception("cannot transition from state " + current);
			
			HashMap<List<Character>, String> h = Tx.get(current);
			
			if (!h.containsKey(cl))
				throw new Exception("cannot transition from state " + current + " for input " + c);
			
			//keep count of layer marks
			if (c_marks.contains(c)){
				
				int newLayer = c_marks.indexOf(c);
				
				if (currentLayer == null) {					
					if (newLayer != 0) 
						//TODO: there will be an exception to this with small caps
						throw new Exception("cannot start from layer mark " + c);									
					currentLayer = c;
					stateMark = _first;
				}
				else {
					
				}
				
				if (newLayer != currentLayer + 1 && newLayer != currentLayer)
					throw new Exception("bad layer numbering progression: current layer is " + currentLayer);
				
			}
			
			current = h.get(cl);
			return current;
		}
		
		public String GetState(){
			return current;
		}		
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
