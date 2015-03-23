package NewParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Inspector.BaseInspector;
import TopDown.Node;

public class Parser {
	
	public static List<Character> c_alphabet = Arrays.asList(new Character[]{'S','B','T','U','A','O','M','I','E','F'});
	public static List<Character> c_smallCap = Arrays.asList(new Character[]{'y','o','e','u','a','i','j','g','s','b','t','h','c','k','m','n','p','x','d','f','l'});
	public static List<Character> c_vowels   = Arrays.asList(new Character[]{'o','a','u','e'});
	public static List<Character> c_ignore  = Arrays.asList(new Character[]{'(',')','*'});
	public static List<Character> c_wLetter  = Arrays.asList(new Character[]{'w'});
	public static List<Character> c_addOp    = Arrays.asList(new Character[]{'+'});
	public static List<Character> c_marks    = Arrays.asList(new Character[]{':', '.', '-', '’', ',', '_', ';'});
	
	public static Character multiplication = '*';
	
	public static String StateInitial = "Initial";
	public static String StateLetter = "Letter";
	public static String StateMark = "Mark";
	public static String StateAddition = "Addition";
	
	static int counter;
	
	public static void run(String input) {
	
		Parser parser = new Parser();
		counter = 0;
		
		try {
			Node n = parser.Parse(input);
			n.PrintNodes("");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(input);
			for (int i = 0 ; i < counter; i++)
				System.out.print(" ");
			System.out.print("^");
		}
	}
	
	public Node Parse(String input) throws Exception{
		 
		State state = new State();
		Node current = null;

		for (counter = 0; counter < input.length(); counter++){
			
			Character charIn = new Character(input.charAt(counter));
			
			if (charIn.equals(' ')) 
				continue;
			
			if (charIn.equals(c_wLetter)) {
				
			}
			
			if (c_ignore.contains(charIn))
				continue;
						
			if (c_smallCap.contains(charIn)){
				
			}
			
			if (c_alphabet.contains(charIn)){ //1
								
				if (state.GetState().equals(StateInitial)){
					
					//starting node
					current = new Node();
					current.name = charIn.toString();	
					
					//create parent of current
					Node parent = new Node();
					parent.nodes.add(current);
					current.parent = parent;
				}
								
				else if (state.GetState().equals(StateMark)){ //2
					
					//we should have a parent at this point
					if (current.parent == null)
						throw new Exception("parent of root not defined");
					
					//cannot have more than three nodes in this relation
					if (current.parent.nodes.size() > 2)
						throw new Exception("too many nodes in multiplication relation");
					
					//set parent opcode as multiplication
					current.parent.opCode = multiplication;
					
					//sibling node
					Node sibling = new Node();
					sibling.name = charIn.toString();
					sibling.layer = current.layer;
					sibling.parent = current.parent;
					
					//add sibling
					current.parent.nodes.add(sibling);
					
					//re-point current node to sibling
					current = sibling;
				}
				
				else {
					throw new Exception("do not know how to handle this " + charIn + " in state " + state.GetState());
				}
				
				state.Transition(c_alphabet, charIn);
			}
			
			if (c_marks.contains(charIn)){
								
				if (state.GetState().equals(StateLetter)){ //3
					
					//we should have a parent at this point
					if (current.parent == null)
						throw new Exception("parent of root not defined");
					
					//finalize current node
					//parent can be at this layer or the next, we still do not know
					current.layer = c_marks.indexOf(charIn); 									
				}		
								
				else if (state.GetState().equals(StateMark)){ //4
					
					//we should have a parent at this point
					if (current.parent == null)
						throw new Exception("parent of root not defined");
					
					//can only be the next layer mark from
					//current, anything else is an error					
					if (current.layer + 1 != c_marks.indexOf(charIn))
						throw new Exception("Layer mark: " + charIn + " cannot follow " + c_marks.get(current.layer));
					
					//set parent's layer
					current.parent.layer = current.layer + 1;
					
					//if the parent of the root is a multiplication relation, 
					//verify if three children exist and fill as required					
					if (multiplication.equals(current.parent.opCode)){
						while (current.parent.nodes.size() < 3){							
							Node emptyNode = new Node();
							emptyNode.name = "E";
							emptyNode.layer = current.layer;
							current.parent.nodes.add(emptyNode);
						}
					}
				}
				
				else {
					throw new Exception("do not know how to handle this " + charIn + " in state " + state.GetState());
				}
				
				state.Transition(c_marks, charIn);
			}
		}
		
		current.parent.name = input;
		return current.parent;
	}
	
	class State {
		
		String current;
		int currentLayer = -1;
		HashMap<String, HashMap<List<Character>, String>> transitions;
				
		public State(){
			
			current = StateInitial;
			transitions = new HashMap<String, HashMap<List<Character>, String>>();
					
			//from Initial
			HashMap<List<Character>, String> fromInitial = new HashMap<List<Character>, String>();
			fromInitial.put(c_alphabet, StateLetter);			
			transitions.put(StateInitial, fromInitial);	
			
			//from Letter
			HashMap<List<Character>, String> fromLetter = new HashMap<List<Character>, String>();
			fromLetter.put(c_marks, StateMark);			
			transitions.put(StateLetter, fromLetter);	
			
			//from Mark
			HashMap<List<Character>, String> fromMark = new HashMap<List<Character>, String>();
			fromMark.put(c_alphabet, StateLetter);		
			fromMark.put(c_addOp, StateAddition);
			fromMark.put(c_marks, StateMark);
			transitions.put(StateMark, fromMark);
			
			//from Addition
			HashMap<List<Character>, String> fromAddition = new HashMap<List<Character>, String>();
			fromAddition.put(c_alphabet, StateLetter);		
			transitions.put(StateAddition, fromAddition);			
		}
		
		public String Transition(List<Character> cl, Character c) throws Exception{
			
			if (!transitions.containsKey(current))
				throw new Exception("cannot transition from state " + current);
			
			HashMap<List<Character>, String> h = transitions.get(current);
			
			if (!h.containsKey(cl))
				throw new Exception("cannot transition from state " + current + " for input " + c);
			
			//keep track of layers
			if (c_marks.contains(c)){
				int newLayer = c_marks.indexOf(c);
				if (newLayer != currentLayer + 1 && newLayer != currentLayer)
					throw new Exception("bad layer numbering progression: current layer is " + currentLayer);
				currentLayer = newLayer;
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
	}
}
