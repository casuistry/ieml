package Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import NewParser.ParserImpl;
import NewParser.ScriptExamples;
import NewParser.Token;
import NewParser.Tokenizer;

public class TableGenerator {

	public enum TableType {
		
		undefined("undefined"),
		no_table("no variables"),
		basic_table("normal table"),
		row_substance("one variable in substance"),
		row_attribut("one variable in attribut"),
		row_mode("one variable in mode"),	
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
	
	public static void main(String[] args) {
		TableGenerator tGen = new TableGenerator();
		
		tGen.generate();
	}
	
	public void generate(){
		
		//List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		ArrayList<String> db = new ArrayList<String>();
		//db.add("S:M:.e.-S:B:T:.S:B:T:.S:B:T:.-S:B:T:.S:B:T:.S:B:T:.-' , 2, 3, 4, 5, 6");
		//db.add("M:.+O:M:.- , 2, 3, 4, 5, 6");
		//db.add("(S:+B:)(S:+T:).f.- , 2, 3, 4, 5, 6");
		//db.add("S:B:.E:.S:B:.- , 2, 3, 4, 5, 6");
		//db.add("E:F:.O:M:.- , 2, 3, 4, 5, 6");
		db.add("M:O:M:. , 2, 3, 4, 5, 6");
		//
		//try {
			
			//BufferedWriter json = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.json"));			
			
			ParserImpl parser = new ParserImpl();
			
			for (String s : db) {
				String[] parts = s.split(",");
				
				if (parts.length != 6) {
					System.out.println("missing: " + s);
					continue;
				}
				
				String ieml =  parts[0].trim().length() > 0 ? parts[0].trim():null;  /* ScriptExamples.UnCoteurraconteUneHistoire; */
				//String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
				//String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
				//String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
				//String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
				//String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
							
				
				
				try {		
					
					Token n = parser.parse(ieml);	
					genJSONTables(n);
					
				} catch (Exception e) {
					System.out.print(ieml+"\t");
					System.out.println(e.getMessage());
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
					
	
	// STEP 0:
	// Call STEP 1 and STEP 2
	// For all table(s), we get an array of strings that will make the multiplication
	// use it to create a json table representation

	public String genJSONTables(Token token) throws Exception {
		
		StringBuilder builder = new StringBuilder();
		
		//System.out.print("analyzing script " + token.GetName() + "\n");
		
		ArrayList<Token> tables = genTables(token);
		if (tables == null || tables.size() == 0) {
			//System.out.println("\tcannot create table for " + token.GetName());
			throw new Exception("not enough semes to generate table");
		}
		for (Token t : tables) {
			//System.out.println("\n\tcreating table for " + t.GetName());
			ArrayList<String> sub, att, mod;
			Token seme;
			seme = t.nodes.get(0);
			//System.out.println("\t\tsubstance is " + seme.GetName());
			sub = genVariables(seme);
			//System.out.print("\t\t\tcomposed of  ");
			for (String _vars : sub) {
				//System.out.print(String.format("%s ", _vars));
			}
			//System.out.println();
			
			seme = t.nodes.get(1);
			//System.out.println("\t\tattribut is " + seme.GetName());
			att = genVariables(seme);
			//System.out.print("\t\t\tcomposed of  ");
			for (String _vars : att) {
				//System.out.print(String.format("%s ", _vars));
			}
			//System.out.println();
			
			seme = t.nodes.get(2);
			//System.out.println("\t\tmode is " + seme.GetName());
			mod = genVariables(seme);
			//System.out.print("\t\t\tcomposed of  ");
			for (String _vars : mod) {
				//System.out.print(String.format("%s ", _vars));
			}
			//System.out.println();
			
			String jTable = writeOutTable(t, sub, att, mod);
			if (jTable != null) {
				System.out.println(jTable);
				builder.append(jTable);
			}
			else {
				throw new Exception("not enough variables to generate table");
				//System.out.println(t.GetName() + " does not generate a table");
			}
		}
		
		return builder.toString();
	}
	
	// STEP 1:
	// If the input token has an additive relation, it needs to be
	// split before generating a table, otherwise we are good to 
	// generate a table. E.g.: if ABC -> generate table for ABC, 
	// if A+B+C -> generate table for A, B and C 
	public ArrayList<Token> genTables(Token parent) {
		ArrayList<Token> result = new ArrayList<Token>();
		
		if (parent.layer < 1) {
			return result;
		}
		
		if (parent.opCode != null && parent.opCode.equals(Tokenizer.addition)) {
			result.addAll(parent.nodes);
			return result;
		}
		if (parent.opCode != null && parent.opCode.equals(Tokenizer.multiplication)) {
			result.add(parent);
			return result;
		}
		
		return result;
	}
	
	//STEP 2:
	// Creates a set by writing out the "+" operands and replacing "I, F, O, M"
	// by its components
	public ArrayList<String> genVariables(Token parent) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> temp = recursiveGenVariables(parent);
		for (String s : temp){
			
			// kludge: if this is an empty sequence, MakeParsable will abbreviate it
			// and it will not be possible to remove trailing empties afterwards by
			// running MakeParsable again. But we need to run it now in order to find
			// all the abbreviations, etc. So, if after MakeParsable the first letter 
			// is 'E', it means it was an empty sequence and we will not use the output
			//of MakeParsable.
			String intermediate = Tokenizer.MakeParsable(s);
			if ('E' == intermediate.charAt(0))
				result.add(s);
			else 
				result.add(intermediate);
		}
		return result;
	}
	public ArrayList<String> recursiveGenVariables(Token parent) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (parent.opCode == null) { // primitive layer: check if we can expand, if yes, do it, otherwise use parent
			String pName = parent.GetName();
			if (Tokenizer.primitiveLookup.containsKey(pName)) {
				for (Token sub : Tokenizer.primitiveLookup.get(pName).nodes){
					result.add(sub.GetName());
				}
			}
			else {
				result.add(pName);
			}
		}
		else {
			
			if (parent.opCode.equals(Tokenizer.addition)) { // addition
				for (Token child : parent.nodes) {					
					result.addAll(recursiveGenVariables(child));					
				}
			}
			else { // multiplication
				
				if (parent.IsEmpty()) {
					result.add(parent.GenerateSequenceForTable(false));
				}
				else {
					for (Token child : parent.nodes) {	
						if (result.isEmpty()) {
							result.addAll(recursiveGenVariables(child));
						}
						else {
							ArrayList<String> temp = new ArrayList<String>();
							for (String prefix : result) {
								for (String postfix : recursiveGenVariables(child)) {
									temp.add(prefix+postfix);
								}
							}
							result = temp;
						}
					}
					
					ArrayList<String> temp = new ArrayList<String>();
					for (String prefix : result) {
						temp.add(prefix+Tokenizer.c_marks.get(parent.layer));
					}
					
					result = temp;
				}
			}
		}
		
		return result;
	}
	
	// STEP 3
	// Returns JSON string or NULL
	public String writeOutTable(Token input, ArrayList<String> sub, ArrayList<String> att, ArrayList<String> mod) throws Exception {
		
		JsonTable table = new JsonTable();
		
		TableType tableType = TableType.undefined;
		
		int v_substance = sub.size();
		int v_attribut = att.size();
		int v_mode = mod.size();
		
		// restrict the size of table
		if (v_attribut > 12) {
			throw new Exception("input has more than 12 column variables");
		}
		if (v_substance * v_mode> 24) {
			throw new Exception("input has more than 24 row variables");
		}
		// there is no table for the following case
		if (v_substance == 1 && v_attribut == 1 && v_mode == 1) {
			return null;
		}
		
		table.add(JsonTable.TableDef.TableHeader, input.GetName());
		table.addFormat(v_substance, v_attribut, v_mode);
		table.addSemes(input.nodes.get(0).GetName(), input.nodes.get(1).GetName(), input.nodes.get(2).GetName());
		
		if (v_substance == 1 && v_attribut == 1 && v_mode == 1) {
			tableType = TableType.no_table;
		}
		else {
			tableType = TableType.basic_table;
			
			for (int x = 0; x < sub.size(); x++) {
				
				String rowh = Tokenizer.MakeParsable(sub.get(x)+input.nodes.get(1).GetName()+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
				table.addHeader(JsonTable.TableDef.SubstanceHeader, rowh, x);
				
				for (int y = 0; y < att.size(); y++) {
					
					String colh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+att.get(y)+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
					table.addHeader(JsonTable.TableDef.AttributHeader, colh, y);
					
					for (int z = 0; z < mod.size(); z++) {
						
						String zh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+input.nodes.get(1).GetName()+mod.get(z)+Tokenizer.c_marks.get(input.layer));
						table.addHeader(JsonTable.TableDef.ModeHeader, zh, z);
						
						String intermediate = Tokenizer.MakeParsable(sub.get(x)+att.get(y)+mod.get(z)+Tokenizer.c_marks.get(input.layer));
						table.addCell(intermediate, x, y, z); 
					}
				}
			}
		}
		
		if (tableType == TableType.undefined) 
			throw new Exception("ERROR in generateTable, table type is undefined");
		
		return table.toString();
	}
}

//create JSON terms: 
//{ IEML: "S:B:T:.", FR:"qqchose", EN:"something", PARADIGM: "", LAYER: "", CLASS: ""  }
//String j = String.format("{ieml:\"%s\",terms:[{lang:\"FR\",means:\"%s\"},{lang:\"EN\",means:\"%s\"}],paradigm:\"%s\",layer:\"%s\",class:\"%s\"}", 
//		ieml, fr, en, pa, la, cl);
//String j = String.format("{IEML:\"%s\",FR:\"%s\",EN:\"%s\",PARADIGM:\"%s\",LAYER:\"%s\",CLASS:\"%s\"}", ieml, fr, en, pa, la, cl);

class JsonTable {
	
	//https://jsonformatter.curiousconcept.com/
	
	StringBuilder json;
	
	public enum TableDef {
		
		TableHeader("TableHeader"), // input script
		CellContent("CellContent"), // wrapper for cells
		X("X"), // x coordinate
		Y("Y"), // y coordinate
	    Z("Z"), // z coordinate
	    V("V"),  // cell value
		Substance("Substance"),  // substance seme
		Attribut("Attribut"),  // attribut seme
	    Mode("Mode"),	 // mode seme   
		SubstanceHeader("RowHeader"),  
		AttributHeader("ColHeader"),
		ModeHeader("ModeHeader");

	    private final String fieldDescription;

	    TableDef(String descr) {
	        this.fieldDescription = descr;
	    }
	    
	    public String getFieldDescription() {
	        return fieldDescription;
	    }
	}
	
	public JsonTable() {
		json = new StringBuilder();
	}
	
	public String toString() {
		if (json.toString().equals("")) {
			return null;
		}
		return "{" + json.toString() + "}";
	}
	
	public void add(TableDef def, String value) {
		
		// sanity!
		Helper.roundTrip(value);
		
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":\"%s\"", def.getFieldDescription(), value));
	}
	
	public void addFormat(int x, int y, int z) {
		
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":\"%s\",", TableDef.X, x));
		json.append(String.format("\"%s\":\"%s\",", TableDef.Y, y));
		json.append(String.format("\"%s\":\"%s\"", TableDef.Z, z));
	}
	
	public void addSemes(String x, String y, String z) {
		
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":\"%s\",", TableDef.Substance, x));
		json.append(String.format("\"%s\":\"%s\",", TableDef.Attribut, y));
		json.append(String.format("\"%s\":\"%s\"", TableDef.Mode, z));
	}
	
	public void addCell(String v, int x, int y, int z) {

		//sanity!
		String entry = Tokenizer.MakeParsable(v);
		Helper.roundTrip(entry);
		
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":{", TableDef.CellContent));
		json.append(String.format("\"%s\":\"%s\",", TableDef.X, x));
		json.append(String.format("\"%s\":\"%s\",", TableDef.Y, y));
		json.append(String.format("\"%s\":\"%s\",", TableDef.Z, z));
		json.append(String.format("\"%s\":\"%s\"", TableDef.V, entry));
		json.append("}");
	}
	
	public void addHeader(TableDef def, String v, int pos) {
		
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":{", def.getFieldDescription()));
		json.append(String.format("\"%s\":\"%s\",", "position", pos));
		json.append(String.format("\"%s\":\"%s\"", "value", v));
		json.append("}");
	}	
}



