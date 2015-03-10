package TopDown;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	//predefined descriptors for nodes
	public static String ATOM = "ATOM"; 	//layer
	public static String NODE = "NODE"; 	//type of node
	public static String ROOT = "ROOT"; 	//type of node
	public static String OPCODE = "OPCODE"; //type of node
	
	//data for Node class with default values
	private String name = "NO_NAME";
	private String descriptor = "NO_DESC";
	private String layer = "-1";	
	private ArrayList<Node> nodes = null;	
	private Node parent = null;
	
	//-----------------------------------------calculated values-----------------------------------
	
	public Boolean isEmpty = null;
	public boolean isTerm = false;
	
	//-----------------------------------------basic methods-----------------------------------
	
	//(substrings.get(0), Node.NODE, Node.ATOM);
	//i.e string fragment, descriptor (node, opcode, root), layer
	public Node(String n, String desc, String l){
		name = n;
		descriptor = desc;
		layer = l;
		
		TotalNodes.getAndIncrement();
	}
	
	public Node(String n, String desc){
		this(n, desc, "-1");
	}
	
	public ArrayList<Node> GetNodes(){
		return nodes;
	}
	public void SetLayer(String l){
		layer = l;
	}
	public void SetLayer(int l){		
		layer = Integer.toString(l);
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
		if (nodes!=null){
			for (Node n : nodes){
				if (n.GetDescriptor().equals(OPCODE)){
					return n.GetName();
				}
			}
		}
		return null;
	}
	//name of this node
	public String GetName(){
		if (descriptor.equals(OPCODE))
			return name; //opcode can be "+", we want to keep it
		return name.startsWith("+") ? name.substring(1) : name;
	}
	//descriptor of this node
	public String GetDescriptor(){
		return descriptor;
	}
	//number of children of this node
	public int GetSize(){
		if (nodes == null)
			return 0;
		return nodes.size();
	}
	//layer of this node
    public String GetLayer(){
		return layer;
	}
    //layer of this node as an integer
	public int GetLayerInt(){
		int cLayer = -1;
		try {			
			cLayer = Integer.parseInt(layer);
		}
		catch (Exception e) {}
		
		return cLayer;
	}	
	
	//Readable representation of a node and its children in a tree-form
	public void PrintNode(String prepend, BaseInspector inspector){
		
		if (nodes != null) {
			StringBuilder builder = new StringBuilder(prepend);
			builder.append(inspector.Inspect(this));
			builder.append(" = ");
			for(Node node : nodes){
				// descent
				node.PrintNode(prepend+"\t", inspector);
				builder.append(inspector.Inspect(node));
			}
			System.out.println(builder.toString());
		}		
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
		
		String d = GetDescriptor();
		
		if (GetLayer().equals(ATOM)){
			if (GetName().equals("E"))
				isEmpty = true;
			else 
				isEmpty = false;
		}
		else {
			if (d.equals(NODE) || d.equals(ROOT)) {
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
    
    public static HashMap<String, ArrayList<Node>> GetNiveauSequence(HashMap<String, ArrayList<Node>> result, Node node, String seq){
    	
    	if (result == null)
    		result = new HashMap<String, ArrayList<Node>>();
    	
    	if (seq == null)
    		seq = new String();
    	
    	String opcodeForChildren = node.GetOpcode();
    	/*
    	if (TermInterface.IsTerm(node)){      		
    		if (!result.containsKey(seq)){
    			ArrayList<Node> list = new ArrayList<Node>();
    			result.put(seq, list);
    		}
    		
    		result.get(seq).add(node);
    	}
    	else if (node.nodes != null && opcodeForChildren != null) {
    		seq += opcodeForChildren;
    		for (Node subnode : node.nodes){
    			GetNiveauSequence(result, subnode, seq);
    		} 
    	}
    	*/
    	return result;
    } 
    
    //find "niveau" for a term, i.e sequence of additions and/or multiplications 
    //from the term to the root node
    public static String GetNiveau(Node n){
    	
    	StringBuilder result = new StringBuilder();
    	
    	Node p = n.GetParent();
    	
    	if (p == null)
    		return result.toString();
    	 		    	    	
		for (Node c : p.nodes){
			if (c.GetDescriptor().equals(OPCODE)){
				result.append(c.GetName());
				result.append(GetNiveau(p));
				break;
			}
		}   
		
		return result.toString();
    }
    
	public static Counter GetNumberOfDifferences(Node a, Node b) throws Exception{
		
		Counter result = new Counter();
		
		int layer_a = a.GetLayerInt();
		int layer_b = b.GetLayerInt();
		
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
					
					//TODO: how do we count erasures???
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
			
	public List<String> Reconstruct(int layer){
		
		ArrayList<String> result = new ArrayList<String>();
		
		int currentLayer = GetLayerInt();
		
		if (layer > currentLayer){
			return result;
		}
		else if (layer < currentLayer){
			if (nodes != null) {
				for(Node node : nodes){			
					if (node.descriptor.equals(NODE)){
						result.addAll(node.Reconstruct(layer));
					}
				}
			}
		}
		else {
			if (result.size() == 0){
				result.add(Reconstruct());
			}
		}
		
		return result;		
	}
	
	public String Reconstruct(){
	
		StringBuilder builder = new StringBuilder();
		
		if (nodes != null) {

			String opcode = "";
			
			for(Node node : nodes){
				
				if (node.descriptor.equals(OPCODE)){
					opcode = node.name.equals("+") ? "+" : "";
					continue;
				}
				
				if (builder.length() > 0)
					builder.append(opcode);
				
				builder.append(node.Reconstruct());
			}
			
			if (opcode.isEmpty())
				builder.append(IEMLLang.LM[GetLayerInt()]);
		}		
		else if (layer.equals(ATOM)){
			builder.append(name);		
		}
		
		return builder.toString();
	}
	
	//Recursively descend the tree to find node(s) of specified layer
	//Return empty result (something wrong), result of length 1 (same layer), 
	//result of length 3 (script of form xyz) or result of length multiple of 
	//3 (script of form xyz + abc)
	public static void GetNodesAtLayer(Node n, int l, ArrayList<Node> result){
				 			
		if (n != null && n.nodes != null && l >= 0 && l <= 6){
			
			int currentlayer = n.GetLayerInt();
			
			if  (currentlayer > l){
				for(Node node : n.nodes){
					GetNodesAtLayer(node, l, result);
				}
			}
			else if (currentlayer == l){
				result.add(n);
			}
		}
	}	
}
