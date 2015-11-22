package Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import NewParser.ParserImpl;
import NewParser.Token;
import NewParser.Tokenizer;

public class OrderValidator {

	public static String alphabet = "abcdefghijklmnopqrstuvwxy";
	
	public static void main(String[] args) {
		
		System.out.println(System.getProperty("java.runtime.version"));

		ArrayList<Token> resultToOrder = new ArrayList<Token>();
		
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		//ArrayList<String> db = new ArrayList<String>();
		//db.add("E:F:U:. , 2, 3, 4, 5, 6");
		//db.add("O:O:. , 2, 3, 4, 5, 6");
		//db.add("U: , 2, 3, 4, 5, 6");
		//db.add("B: , 2, 3, 4, 5, 6");
		//db.add("T: , 2, 3, 4, 5, 6");
		//db.add("S: , 2, 3, 4, 5, 6");
		//db.add("I: , 2, 3, 4, 5, 6");
		//db.add("F: , 2, 3, 4, 5, 6");
		
		
		ParserImpl parser = new ParserImpl();
		
		for (String s : db) {
			String[] parts = s.split(",");
			
			if (parts.length != 6) {
				System.out.println("missing: " + s);
				continue;
			}
			
			String ieml =  parts[0].trim().length() > 0 ? parts[0].trim():null;   
			//String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
			//String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
			//String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
			//String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
			//String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
						
			try {		
				
				Token n = parser.parse(ieml);	
				
				//n.orderString = calcStringOrder(n);
				//System.out.println(ieml+"\t"+n.orderString);				
				resultToOrder.add(n);
				
			} catch (Exception e) {
				System.out.print(ieml+"\t");
				System.out.println(e.getMessage());
			}		
			finally {
				parser.Reset();
			}				
		}
		
		System.out.println("have this many tokens: " + resultToOrder.size());	
		
	    Comparator<Token> x = new Comparator<Token>() {	    	
	    	@Override
	    	public int compare(Token smaller, Token bigger)
	    	{	
	    		//Returns:the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y Since:1.7	
	    		try {
	    			Boolean res = IsOrdered(smaller, bigger); 
					if (res == null)
						return 0;
					if (res)
						return -1;					
				} catch (Exception e) {
					System.out.println("In comparator");
					System.out.println(e.getMessage());
				}
	    		return 1;
	    	}	    	
	    };

	    Comparator<Token> y = new Comparator<Token>() {	    	
	    	@Override
	    	public int compare(Token smaller, Token bigger)
	    	{	
	    		//Returns:the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y Since:1.7	

	    		int res = 0;
	    		
	    		if (smaller.orderString.length() < bigger.orderString.length())
	    			return -1;
	    		if (smaller.orderString.length() > bigger.orderString.length())
	    			return 1;
	    		
	    		for (int i = 0; i < smaller.orderString.length(); i++){
	    			if (smaller.orderString.charAt(i) == bigger.orderString.charAt(i))
	    				continue;
	    			
	    			int s = Character.getNumericValue(smaller.orderString.charAt(i));
	    			int b = Character.getNumericValue(bigger.orderString.charAt(i));
	    			
	    			res = Integer.compare(s, b);
	    			break;
	    		}
	    		
	    		return res;
	    	}	    	
	    };
	    
	    Comparator<Token> z = new Comparator<Token>() {	    	
	    	@Override
	    	public int compare(Token smaller, Token bigger)
	    	{	
	    		//Returns:the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y Since:1.7	

	    		int res = 0;
	    		
	    		for (int i = 0; i < smaller.orderString.length(); i++){
	    			if (smaller.orderString.charAt(i) == bigger.orderString.charAt(i))
	    				continue;
	    			
	    			int s = Character.getNumericValue(smaller.orderString.charAt(i));
	    			int b = Character.getNumericValue(bigger.orderString.charAt(i));
	    			
	    			res = Integer.compare(s, b);
	    			break;
	    		}
	    		
	    		return res;
	    	}	    	
	    };
	    
	    Collections.sort(resultToOrder, x);
	    
	    System.out.println("Ordered list:");
	    
	    for (Token token : resultToOrder){
	    	System.out.println(token.GetName());
	    }
	    
	}
	
	public static String calcStringOrder(Token n) throws Exception{
		
		n.ComputeTaille();
		
		StringBuilder builder = new StringBuilder();
		builder.append(n.layer);
		builder.append(n.taille);
		
		if (n.opCode == null) {
			int index = Tokenizer.c_alphabet.indexOf(n.GetName().charAt(0));
			builder.append(index);
		}
		else if (n.opCode.equals(Tokenizer.addition)){
			String s;
			if (n.layer > 0) {
				s = calcStringOrder(n.nodes.get(0).nodes.get(0));
			}
			else {
				s = calcStringOrder(n.nodes.get(0));
			}
			builder.append(s);
			builder.append(s);
			builder.append(s);
		}
		else {
			for (int i = 0; i < 3; i++){
				builder.append(calcStringOrder(n.nodes.get(i)));
			}
		}
		
		return builder.toString();
	}
	
	public static Boolean IsOrdered(Token smaller, Token bigger) throws Exception {
		
		//System.out.println("Comparing " + smaller.GetName() + " and " + bigger.GetName());
		
		smaller.ComputeTaille();
		bigger.ComputeTaille();
				
		if (smaller.layer < bigger.layer)
			return true;
		if (smaller.layer > bigger.layer)
			return false;
		if (smaller.taille < bigger.taille)
			return true;
		if (smaller.taille > bigger.taille)
			return false;
		
		if (smaller.opCode == null) {
			if (bigger.opCode == null) {
				return n_n(smaller, bigger);
			}
			else if (bigger.opCode.equals(Tokenizer.multiplication)) {
				return n_m(smaller, bigger);
			}
			else if (bigger.opCode.equals(Tokenizer.addition)) {
				return n_a(smaller, bigger);
			}			
			//sanity
			throw new Exception("unrecognized opCode");
		}
		else if (smaller.opCode.equals(Tokenizer.multiplication)) {
			if (bigger.opCode == null) {
				return m_n(smaller, bigger);
			}
			else if (bigger.opCode.equals(Tokenizer.multiplication)) {
				return m_m(smaller, bigger);
			}
			else if (bigger.opCode.equals(Tokenizer.addition)) {
				return m_a(smaller, bigger);
			}			
			//sanity
			throw new Exception("unrecognized opCode");
		}
		else if (smaller.opCode.equals(Tokenizer.addition)) {
			if (bigger.opCode == null) {
				return a_n(smaller, bigger);
			}
			else if (bigger.opCode.equals(Tokenizer.multiplication)) {
				return a_m(smaller, bigger);
			}
			else if (bigger.opCode.equals(Tokenizer.addition)) {
				return a_a(smaller, bigger);
			}			
			//sanity
			throw new Exception("unrecognized opCode");
		}
		
		//sanity
		throw new Exception("unhandled case");
	}
	
	private static Boolean n_n(Token smaller, Token bigger) throws Exception{
		int a = Tokenizer.m_alphabet.get(smaller.GetName().charAt(0));
		int b = Tokenizer.m_alphabet.get(bigger.GetName().charAt(0));
		if (a == b)
			return null;
		if (a < b)
			return true;			
		return false;
	}
	
	private static Boolean n_m(Token smaller, Token bigger) throws Exception{
		throw new Exception("ordering error n_m");
	}
	private static Boolean n_a(Token smaller, Token bigger) throws Exception{
		throw new Exception("ordering error n_a");
	}
	private static Boolean m_n(Token smaller, Token bigger) throws Exception{
		throw new Exception("ordering error m_n");
	}
	private static Boolean a_n(Token smaller, Token bigger) throws Exception{		
		throw new Exception("ordering error a_n");
	}


	// same layer, same 'taille' but the number of terms may be different
	// there are many ways to define an order, this is just one of them:
	// assume that higher number of terms wins
	// if same number of terms then compare term by term
	
	private static Boolean m_a(Token smaller, Token bigger) throws Exception{
		return true;
		//return IsOrdered(smaller, bigger.nodes.get(0));
	}
	private static Boolean a_m(Token smaller, Token bigger) throws Exception{
		return false;
		//return IsOrdered(smaller.nodes.get(0), bigger);
	}
	private static Boolean a_a(Token smaller, Token bigger) throws Exception{	
		if (smaller.nodes.size() == bigger.nodes.size()){
			for (int i = 0; i < smaller.nodes.size(); i++ ){
				Boolean res = IsOrdered(smaller.nodes.get(i), bigger.nodes.get(i));
				if (res != null)
					return res;
			}
			return null;
		}
		return smaller.nodes.size() < bigger.nodes.size();
		//return IsOrdered(smaller.nodes.get(0), bigger.nodes.get(0));
	}
	
	private static Boolean m_m(Token smaller, Token bigger) throws Exception{
		for (int i = 0; i < 3; i++ ){
			Boolean res = IsOrdered(smaller.nodes.get(i), bigger.nodes.get(i));
			if (res != null)
				return res;
		}
		return null;
	}
}
