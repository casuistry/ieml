package TopDown;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

import IEMLInterface.IEMLLang;
import IEMLInterface.TermInterface;
import Inspector.BaseInspector;
import Structures.CompMatch;
import Structures.Counter;
import Structures.Utilities;

public class Node {
		
	public static AtomicLong TotalNodes = new AtomicLong(0);
	
	public static int DefaultLayer = -2;
	public static int OpcodeLayer = -1;
	
	//data for Node class with default values
	private String name = "NO_NAME";	
	//-2 is not set, -1 is opcode, 0 through n are layers
	private int layer = DefaultLayer;	
	
	//children
	private ArrayList<Node> nodes = null;	
	//parent
	private Node parent = null;
	
	//-----------------------------------------calculated values-----------------------------------
	
	public Boolean isEmpty = null;
	public boolean isTerm = false;
	
	//-----------------------------------------basic methods-----------------------------------
	
	public static Node GetNewOpcodeNode(String s){
		if (IEMLLang.IsOpcodeValid(s))
			return new Node(s, OpcodeLayer);
		return null;
	}
	
	public static Node GetNewNode(String s, int l){
		if (s != null && IEMLLang.IsLayerValid(l))
			return new Node(s, l);
		return null;
	}
	
	private Node(String n, int l) {
		
		TotalNodes.getAndIncrement();
		
		name = n;
		layer = l;
	}
	
	public boolean IsPrimitive(){
		return (layer == 0 && (nodes == null || nodes.size() == 0));
	}
	
	public boolean IsOpcode(){
		return (layer == OpcodeLayer);
	}
	
	public ArrayList<Node> GetNodes(){
		return nodes;
	}
	
	public void SetParent(Node n){
		parent = n;
	}
	
	public Node GetParent(){
		return parent;
	}
	
	public void AddNode(Node n){
		if (nodes == null)
			nodes = new ArrayList<Node>();
		n.SetParent(this);		
		nodes.add(n);
	}
	
	//get the opcode affecting children of this node (can be null)
	public String GetOpcode(){
		
		if (IsOpcode())
			return GetName();
		
		if (nodes!=null){
			for (Node n : nodes){
				if (n.IsOpcode()){
					return n.GetName();
				}
			}
		}
		return null;
	}
	
	//name of this node
	public String GetName(){
		if (IsOpcode())
			return name; //opcode can be "+", we want to keep it
		return name.startsWith("+") ? name.substring(1) : name;
	}

	//layer of this node
    public int GetLayer(){
		return layer;
	}
	
	//Readable representation of a node and its children in a list-form
	public void PrintNodes(String prepend, BaseInspector inspector){
		
		String toPrint = inspector.Inspect(this);
		
		if (toPrint != null){
			System.out.println(prepend + toPrint);
		}

		if (nodes != null) {
			for(Node node : nodes){
				node.PrintNodes(prepend+"\t", inspector);
			}
		}		
	}
	
	//-----------------------------------------basic semantic-----------------------------------
	
	//Checks if a node contains 'empty' ieml. 
	public Boolean IsEmpty() throws Exception {
				
		if (isEmpty != null)
			return isEmpty;
		
		if (layer == 0){
			if (GetName().equals("E"))
				isEmpty = true;
			else 
				isEmpty = false;
		}
		else {
			if (!IsOpcode()) {
				for (Node n : nodes){		
					Boolean b = n.IsEmpty();
					if (b != null){
						if (isEmpty != null)
							isEmpty &= b;	
						else
							isEmpty = b;
					}						
				}	
		    }
		}
		
		return isEmpty;
	}
	
	public static void MarkTerms(TermInterface termMap, Node node){
		
    	if (termMap.IsTerm(node)){    		
    		node.isTerm = true;
    	}
    	else {
    		if (node.nodes != null) {
    			for(Node n : node.nodes){	
    				MarkTerms(termMap, n);
    			}
    		}
    		else 
    		{
    			//Node may be at level 0 but not be a term (depends on the dictionary?)
    			//throw new Exception("Node is not a term, but has no children");			
    		}
    	}
	}
	
	//-----------------------------------------other methods-----------------------------------
	
	//Gets all the "terms" of the supplied node. A term is defined in a dictionary.
	//This dictionary will be supplied externally. 
    public static List<Node> GetTerms(Node r) throws Exception {
    	
    	TermInterface terms = new TermInterface(null);
    	
    	ArrayList<Node> result = new ArrayList<Node>();
    	
    	if (terms.IsTerm(r)){    		
    		result.add(r);
    	}
    	else {
    		if (r.nodes != null) {
    			for(Node node : r.nodes){		
    				result.addAll(GetTerms(node));
    			}
    		}
    		else 
    		{
    			//Node may be at level 0 but not be a term (depends on the dictionary?)
    			//throw new Exception("Node is not a term, but has no children");			
    		}
    	}

        return result;
    }  
    
	public static Counter GetNumberOfDifferences(Node a, Node b) throws Exception{
		
		Counter result = new Counter();
		
		int layer_a = a.GetLayer();
		int layer_b = b.GetLayer();
		
		if (layer_a == layer_b){
			
			if (layer_a == 0){
				
				String longer;
				String shorter;
				boolean swap = false;
				
				if (a.GetName().length() >= b.GetName().length()){
					longer = a.GetName();
					shorter = b.GetName();					
				}
				else 
				{
					longer = b.GetName();
					shorter = a.GetName();	
					swap = true;
				}
				
				String[] base = Utilities.getChunks(longer.replaceAll("[+" + IEMLLang.LM[0] +"]", ""));
				String[] frag = Utilities.getChunks(shorter.replaceAll("[+" + IEMLLang.LM[0] +"]", ""));
				
				List<int[]> index =	Utilities.convIndex(base.length, frag.length, true);
							
				PriorityQueue<CompMatch> queue = CompMatch.compArray(index, base, frag, null);	  	
				
				result.min = Integer.MAX_VALUE;
	            while (queue.size() != 0)
	            {
					CompMatch h = queue.remove();	
					
					int t = swap ? base.length - h.k : frag.length - h.k;
					
					if (t <= result.min) {
						result.min = t;
						if (t == 0) {
							result.occurances++;
						}
					}
					else {
						continue;
					}
	            }
	            
	            if (result.min != 0 && result.occurances != 0)
	            	throw new Exception("occurances");
	            
				return result;     
			}
			else {
                  				
				//if base is of form xxx and frag of form yyy then:
				//if result.min == 0, there is 1 repetition
				if (a.nodes.get(0).GetName().equals("*") &&
					b.nodes.get(0).GetName().equals("*")){
					result.min = GetNumberOfDifferences(a.nodes.get(1), b.nodes.get(1)).min + 
						   GetNumberOfDifferences(a.nodes.get(2), b.nodes.get(2)).min + 
						   GetNumberOfDifferences(a.nodes.get(3), b.nodes.get(3)).min;	
					return result;
				}
				//if base is of form xxx and frag of form y+y+...+y then:
				//there are no repetitions
				else if (a.nodes.get(0).GetName().equals("*") &&
						b.nodes.get(0).GetName().equals("+")){
					
					result.min = Integer.MAX_VALUE;					
					for (int i = 1; i < b.nodes.size(); i++){
						int t = GetNumberOfDifferences(a, b.nodes.get(i)).min;
						if (t < result.min)
							result.min = t;
					}
					
					return result;
				}
				//if base is of form x+x+...+x and frag of form yyy then:
				//there can be up to length(base) repetitions
				else if (a.nodes.get(0).GetName().equals("+") &&
						b.nodes.get(0).GetName().equals("*")){
					
					result.min = Integer.MAX_VALUE;					
					for (int i = 1; i < a.nodes.size(); i++){
						Counter t = GetNumberOfDifferences(a.nodes.get(i), b);
						
						if (t.min < result.min)
							result.min = t.min;
						
						if (t.min == 0)
							result.occurances++;
					}
					
					return result;
				}
				//if base is of form x+x+...+x and frag of form y+y+...+y then:
				//there can be only 1 repetition
				else if (a.nodes.get(0).GetName().equals("+") &&
						b.nodes.get(0).GetName().equals("+")){
					
				}
				else {
					throw new Exception("unhandled");
				}
			}
		}
			
		return result;
	}
}
