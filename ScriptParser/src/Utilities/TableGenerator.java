package Utilities;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import NewParser.ParserImpl;
import NewParser.Token;
import NewParser.Tokenizer;
import Utilities.Helper;



public class TableGenerator {

	public static void main(String[] args) {
		TableGenerator tGen = new TableGenerator();
		
		tGen.generate();
	}
	
	public void generate(){
		
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		//ArrayList<String> db = new ArrayList<String>();
		//db.add("S:E:S:.- , 2, 3, 4, 5, 6");
		 //E:M:.M:M:.-
		//try {
			
			//BufferedWriter json = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.json"));			
			
			ParserImpl parser = new ParserImpl();
			
			for (String s : db) {
				String[] parts = s.split(",");
				
				if (parts.length != 6) {
					System.out.println("missing: " + s);
					continue;
				}
				
				String ieml = parts[0].trim().length() > 0 ? parts[0].trim():null;
				//String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
				//String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
				//String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
				//String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
				//String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
							
				try {				
					Token n = parser.parse(ieml);	
					String recreated = n.GenerateCleanSequenceForTable(false);
					
					//n.PrintNodes(" ");
					System.out.println(n.GetName() + "\t" + recreated + "\t");
					
					//n.removeFromTableGenetration();
					//String stuff = n.GenerateCleanSequenceForTable(true);
					//System.out.println(stuff != null ? stuff : "EXPECTED --> nothing for the table 1");
					
					//n.addToTableGenetration();
					//stuff = n.GenerateCleanSequenceForTable(true);
					//System.out.println(stuff != null ? "Recreating script: " + stuff : "ERROR --> nothing for the table 2");
					
					Token recoveredToken = roundTrip(recreated);
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println(s);
				}		
				finally {
					parser.Reset();
				}				
			}
			
			
			//json.close();
			
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}	
	}	
	
	public Token roundTrip(String toParse) {
		
		Token n = null;
		
		ParserImpl parser = new ParserImpl();
		//parser.recover = true;
		
		try {				
			n = parser.parse(toParse);	
			//System.out.println("Round trip OK: " + n.GetName());
			return n;
		} catch (Exception e) {
			System.out.println("ERROR round-tripping [" + e.getMessage()+"]");
			return n;
		}		
		finally {
			parser.Reset();			
		}
	}
	
}





