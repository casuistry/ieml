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
import NewParser.Tokenizer.States;
import Utilities.Helper;



public class TableGenerator {

	public static void main(String[] args) {
		TableGenerator tGen = new TableGenerator();
		
		tGen.generate();
	}
	
	public void generate(){
		
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		//ArrayList<String> db = new ArrayList<String>();
		//db.add("U:+M: , 2, 3, 4, 5, 6");
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
					GenerateTables(n);
					//String recreated = n.GenerateCleanSequenceForTable(false);					
					
					//n.PrintNodes(" ");
					//System.out.println(n.GetName() + "\t" + recreated + "\t");
					
					//n.removeFromTableGenetration();
					//String stuff = n.GenerateCleanSequenceForTable(true);
					//System.out.println(stuff != null ? stuff : "EXPECTED --> nothing for the table 1");
					
					//n.addToTableGenetration();
					//stuff = n.GenerateCleanSequenceForTable(true);
					//System.out.println(stuff != null ? "Recreating script: " + stuff : "ERROR --> nothing for the table 2");
					
					//Token recoveredToken = roundTrip(recreated);
					
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
		
	public void GenerateTables(Token start) {	
		
		HashMap<Token, ArrayList<ArrayList<Token>>> result = getTableInflectionPoints(start);
		
		for (Token t : result.keySet()) {
			generateTable(t, result.get(t));
		}
	}
	
	public enum TableType {
		
		undefined("undefined"),
		no_table("no variables"),
		row_mode("one variable in mode"),	
		row_attribut("one variable in attribut"),	
		row_substance("one variable in substance"),
		matrix_mode("two or more variables in mode"),	
		matrix_attribut("two or more variables in attribut"),
		matrix_substance("two or more variables in substance"),		
		double_mode("constant mode"),		
		double_attribut("constant attibut"),	    
		double_substance("constant_substance"),
		triple("at least one variable in each seme");

	    private final String fieldDescription;

	    TableType(String descr) {
	        this.fieldDescription = descr;
	    }
	    
	    public String getFieldDescription() {
	        return fieldDescription;
	    }
	}
	
	// generates one table
	public String generateTable(Token start, ArrayList<ArrayList<Token>> table){
		
		StringBuilder builder = new StringBuilder("creating table for " + start.GetName() + " ");
		
		TableType tableType = TableType.undefined;
		
		ArrayList<Token> substance = table.get(0);
		ArrayList<Token> attribut = table.get(1);
		ArrayList<Token> mode = table.get(2);
		
		builder.append("[substance=" + substance.size());
		builder.append(" attribut=" + attribut.size());
		builder.append(" mode=" + mode.size() + "] ");
		
		int v_substance = substance.size();
		int v_attribut = attribut.size();
		int v_mode = mode.size();
		
		if (v_substance < 1 && v_attribut < 1 && v_mode < 1) {
			tableType = TableType.no_table;
		}
		else if (v_substance > 0 && v_attribut > 0 && v_mode > 0) {
			tableType = TableType.triple;
		}
		else if (v_substance == 0 && v_attribut == 0 && v_mode > 0) {
			if (v_mode == 1) {
				tableType = TableType.row_mode;
			}
			else {
				tableType = TableType.matrix_mode;
			}
		}
		else if (v_substance == 0 && v_attribut > 0 && v_mode == 0) {
			if (v_attribut == 1) {
				tableType = TableType.row_attribut;
			}
			else {
				tableType = TableType.matrix_attribut;
			}
		}
		else if (v_substance > 0 && v_attribut == 0 && v_mode == 0) {
			if (v_substance == 1) {
				tableType = TableType.row_substance;
			}
			else {
				tableType = TableType.matrix_substance;
			}
		}
		else if (v_substance > 0 && v_attribut > 0 && v_mode == 0) {
			tableType = TableType.double_mode;
		}
		else if (v_substance > 0 && v_attribut == 0 && v_mode > 0) {
			tableType = TableType.double_attribut;
		}
		else if (v_substance == 0 && v_attribut > 0 && v_mode > 0) {
			tableType = TableType.double_substance;
		}
		
		builder.append("["+tableType.getFieldDescription()+"]");
		
		//if (tableType == TableType.undefined) 
		//if (tableType == TableType.no_table) 	
		if (tableType != TableType.no_table) {
			System.out.println(builder.toString());
			
			if (tableType == TableType.double_substance) {
				
				// substance is constant				
				String s = start.nodes.get(0).GenerateCleanSequenceForTable(false);
				
				ArrayList<String> row_headers = new ArrayList<String>();

				
				ArrayList<String> col_headers = new ArrayList<String>();
				
			}
			
			return "";
		}
		else {
			return null;
		}
	}
	
	public HashMap<Token, ArrayList<ArrayList<Token>>> getTableInflectionPoints(Token start) {
		
		HashMap<Token, ArrayList<ArrayList<Token>>> result = new HashMap<Token, ArrayList<ArrayList<Token>>>();
		
		if (start.opCode == null || start.layer == 0){ //theree are no semes
			ArrayList<Token> zeroVariables = new ArrayList<Token>();
			ArrayList<ArrayList<Token>> constantSemes = new ArrayList<ArrayList<Token>>();
			constantSemes.add(zeroVariables);
			constantSemes.add(zeroVariables);
			constantSemes.add(zeroVariables);
			result.put(start, constantSemes);
		}
		else {
			if (start.opCode.equals(Tokenizer.multiplication) ){
				result.put(start, getScriptInflectionPoints(start));
			}
			if (start.opCode.equals(Tokenizer.addition) ){
				for (Token token : start.nodes){
					result.put(token, getScriptInflectionPoints(token));
				}				
			}
		}
		
		return result;
	}
	
	// inflection points for 3 semes
	public ArrayList<ArrayList<Token>> getScriptInflectionPoints(Token start) {
		
		ArrayList<ArrayList<Token>> result = new ArrayList<ArrayList<Token>>();
		
		if (start.opCode == null || !start.opCode.equals(Tokenizer.multiplication) ){
			System.out.println("ERROR in getScriptInflectionPoints");
		}
		else {
			result.add(getSemeInflectionPoints(start.nodes.get(0)));
			result.add(getSemeInflectionPoints(start.nodes.get(1)));
			result.add(getSemeInflectionPoints(start.nodes.get(2)));
		}
		
		return result;
	}
	
	// inflection points for a seme
	public ArrayList<Token> getSemeInflectionPoints(Token start) {
		
		ArrayList<Token> result = new ArrayList<Token>();
		
		if (start.opCode != null) {
			if (start.opCode.equals(Tokenizer.addition)) {
                result.add(start);
			}
			for (Token child : start.nodes) {
				result.addAll(getSemeInflectionPoints(child));
			}
		}

		return result;
	}
	
}





