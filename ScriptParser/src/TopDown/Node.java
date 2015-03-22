package TopDown;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

import IEMLInterface.IEMLLang;
import Inspector.BaseInspector;
import Structures.CompMatch;
import Structures.Counter;
import Structures.Utilities;

public class Node {
		
	public static AtomicLong TotalNodes = new AtomicLong(0);	
	public static int DefaultLayer = -1;
	
	private String name = "NO_NAME";	
	private int layer = DefaultLayer;	
	private ArrayList<Node> nodes;	
	private Node parent;
	private String opCode;
	
	//-----------------------------------------calculated values-----------------------------------
	
	private Boolean isEmpty = null; //null is undefined
	private boolean isTerm = false;
	private String grammaticalConstruct = null;
	
	//-----------------------------------------basic methods-----------------------------------
	
	public static Node GetNewNode(String s, int l){
		
		if (s != null && IEMLLang.IsLayerValid(l)) {			
			TotalNodes.getAndIncrement();
			return new Node(s, l);
		}
			
		return null;
	}
	
	public static Node GetEmptyNode(int l){
		Node emptyNode = GetNewNode(IEMLLang.GetEmpty() + IEMLLang.LM[l], l);
		if (emptyNode != null){
			emptyNode.SetEmpty(true);
			emptyNode.SetTerm(true);
		}
		return emptyNode;
	}
	
	private Node(String n, int l) {						
		name = n.startsWith("+") ? n.substring(1) : n;
		layer = l;
		nodes = new ArrayList<Node>();
		parent = null;
		opCode = null;
	}
	
	public boolean IsPrimitive(){
		return (layer == 0 && name.length() == 2 && IEMLLang.AlphabetList.contains(name.substring(0, 1)));
	}
	
	public boolean IsLeaf(){
		return (nodes.size() == 0 && opCode == null);
	}
	
	public void SetEmpty(boolean b){
		isEmpty = b;
	}
	
	public Boolean IsEmpty(){
		return isEmpty;
	}
	
	public void SetTerm(boolean b){
		isTerm = b;
		grammaticalConstruct = isTerm ? "Z" : null;
	}
	
	public Boolean IsTerm(){
		return isTerm;
	}
	
	public String GetGConstruct(){
		return grammaticalConstruct;
	}
	
	public void SetGConstruct(String s){
		grammaticalConstruct = s;
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
		n.SetParent(this);		
		nodes.add(n);
	}
	
	public String GetOpcode(){
		return opCode;
	}
	
	public void SetOpCode(String op){
		if (IEMLLang.IsOpcodeValid(op))
			opCode = op;
	}

	public String GetName(){
		return name;
	}

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
		
	//-----------------------------------------other methods----------------------------------- 
    
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
