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
		//db.add("t.e.-m.u.-' , 2, 3, 4, 5, 6");
		//db.add("s.o.-y.y.-' , 2, 3, 4, 5, 6");
		
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
				//n.ComputeTaille();
				//Token.SetCanonical(n);			
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
		    	    
	    Collections.sort(resultToOrder, new IemlOrderComparator());
	    
	    System.out.println("Ordered list:");
	    
	    for (Token token : resultToOrder){
	    	System.out.print(token.GetName() + "\t");
	    	//for (String s : token.canonicalOrder){
	    	//	System.out.print(s + " ");
	    	//}
	    	System.out.println();
	    }
	}
	
}
