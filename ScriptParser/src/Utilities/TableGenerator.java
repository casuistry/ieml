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
		row_substance("at least one variable in substance"),
		row_attribut("at least one variable in attribut"),
		row_mode("at least one variable in mode"),	
		double_mode("constant mode"),		
		double_attribut("constant attibut"),	    
		double_substance("constant substance"),
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
		//db.add("O:M:O:.+M:O:M:., 2, 3, 4, 5, 6");
		db.add("S:S:M:., 2, 3, 4, 5, 6");
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
				
				String ieml =  parts[0].trim().length() > 0 ? parts[0].trim():null;   /* ScriptExamples.UnCoteurraconteUneHistoire; */
				//String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
				//String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
				//String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
				//String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
				//String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
							
				
				
				try {		
					
					Token n = parser.parse(ieml);	
					String json = genJSONTables(n);
					System.out.print(json);
					
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
			
		//System.out.print("analyzing script " + token.GetName() + "\n");
		
		ArrayList<Token> tables = genTables(token);
		if (tables == null || tables.size() == 0) {
			//System.out.println("\tcannot create table for " + token.GetName());
			throw new Exception("not enough semes to generate table");
		}
		
		JsonTables jsonTables = new JsonTables(token.GetName());
		
		for (Token t : tables) {
			//System.out.println("\n\tcreating table for " + t.GetName());
			ArrayList<String> sub, att, mod;
			Token seme;
			seme = t.nodes.get(0);
			//System.out.println("\t\tsubstance is " + seme.GetName());
			sub = genVariables(seme);
			//System.out.print("\t\t\tcomposed of  ");
			//for (String _vars : sub) {
				//System.out.print(String.format("%s ", _vars));
			//}
			//System.out.println();
			
			seme = t.nodes.get(1);
			//System.out.println("\t\tattribut is " + seme.GetName());
			att = genVariables(seme);
			//System.out.print("\t\t\tcomposed of  ");
			//for (String _vars : att) {
				//System.out.print(String.format("%s ", _vars));
			//}
			//System.out.println();
			
			seme = t.nodes.get(2);
			//System.out.println("\t\tmode is " + seme.GetName());
			mod = genVariables(seme);
			//System.out.print("\t\t\tcomposed of  ");
			//for (String _vars : mod) {
				//System.out.print(String.format("%s ", _vars));
			//}
			//System.out.println();
			
			String jTable = writeOutTable(t, sub, att, mod);
			jsonTables.add(jTable);
		}
		
		return jsonTables.get();
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
			
		int v_substance = sub.size();
		int v_attribut = att.size();
		int v_mode = mod.size();
		
		TableType tableType = TableType.undefined;
		
		if (v_substance > 1 && v_attribut > 1 && v_mode > 1) {
			tableType = TableType.triple;
		}
		else if (v_substance > 1) {
			if (v_attribut > 1) {
				tableType = TableType.double_mode;
			}
			else if (v_mode > 1) {
				tableType = TableType.double_attribut;
			}
			else {
				tableType = TableType.row_substance;
			}
		}
		else if (v_attribut > 1) {
			if (v_mode > 1) {
				tableType = TableType.double_substance;
			}
			else {
				tableType = TableType.row_attribut;
			}
		}
		else if (v_mode > 1) {
			tableType = TableType.row_mode;
		}
		else {
			tableType = TableType.no_table;
			throw new Exception("not enough variables to generate table");
		}
		
		if (tableType == TableType.undefined) 
			throw new Exception("ERROR in generateTable, table type is undefined");
		
		
		JsonTable table = new JsonTable();
		table.addTitle(input.GetName());
		table.addFormat(v_substance, v_attribut, v_mode);
		
		if (tableType == TableType.row_substance || tableType == TableType.row_attribut || tableType == TableType.row_mode) {
				
			JsonSlice slice = new JsonSlice(0);
			table.add(slice);
			
			int counter = 0;
			for (int z = 0; z < mod.size(); z++) {
				for (int x = 0; x < sub.size(); x++) {
					for (int y = 0; y < att.size(); y++) {
						String intermediate = Tokenizer.MakeParsable(sub.get(x)+att.get(y)+mod.get(z)+Tokenizer.c_marks.get(input.layer));
						slice.addCell(counter++, 0, intermediate);
					}
				}
			}
		}
		else if (tableType == TableType.double_substance || tableType == TableType.double_attribut || tableType == TableType.double_mode) {
			
			JsonSlice slice = new JsonSlice(0);		
			table.add(slice);
			
			if (tableType == TableType.double_substance) {
				
				for (int x = 0; x < att.size(); x++) {	
					
					String rowh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+att.get(x)+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
					slice.addCell(x+1, 0, rowh);
					
					for (int y = 0; y < mod.size(); y++) {
						
						if (x == 0) {
							String colh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+input.nodes.get(1).GetName()+mod.get(y)+Tokenizer.c_marks.get(input.layer));
							slice.addCell(0, y+1, colh);
						}
						
						String intermediate = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+att.get(x)+mod.get(y)+Tokenizer.c_marks.get(input.layer));
						slice.addCell(x+1, y+1, intermediate);
					}
				}	
			}
			else if (tableType == TableType.double_attribut) {

				for (int x = 0; x < sub.size(); x++) {	
					
					String rowh = Tokenizer.MakeParsable(sub.get(x)+input.nodes.get(1).GetName()+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
					slice.addCell(x+1, 0, rowh);
					
					for (int y = 0; y < mod.size(); y++) {
						
						if (x == 0) {
							String colh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+input.nodes.get(1).GetName()+mod.get(y)+Tokenizer.c_marks.get(input.layer));
							slice.addCell(0, y+1, colh);
						}
						
						String intermediate = Tokenizer.MakeParsable(sub.get(x)+input.nodes.get(1).GetName()+mod.get(y)+Tokenizer.c_marks.get(input.layer));
						slice.addCell(x+1, y+1, intermediate);
					}
				}	
			}
			else {
				for (int x = 0; x < sub.size(); x++) {	
					
					String rowh = Tokenizer.MakeParsable(sub.get(x)+input.nodes.get(1).GetName()+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
					slice.addCell(x+1, 0, rowh);
					
					for (int y = 0; y < att.size(); y++) {
						
						if (x == 0) {
							String colh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+att.get(y)+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
							slice.addCell(0, y+1, colh);
						}
						
						String intermediate = Tokenizer.MakeParsable(sub.get(x)+att.get(y)+input.nodes.get(2).GetName()+Tokenizer.c_marks.get(input.layer));
						slice.addCell(x+1, y+1, intermediate);
					}
				}
			}
		}
		else { // triple
			
			for (int z = 0; z < mod.size(); z++) {
				JsonSlice slice = new JsonSlice(z);
				
				for (int x = 0; x < sub.size(); x++) {
					
					String rowh = Tokenizer.MakeParsable(sub.get(x)+input.nodes.get(1).GetName()+mod.get(z)+Tokenizer.c_marks.get(input.layer));
					//slice.addHeader(x+1, 0, rowh);
					slice.addCell(x+1, 0, rowh);
					
					for (int y = 0; y < att.size(); y++) {
						
						if (x == 0) {
							String colh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+att.get(y)+mod.get(z)+Tokenizer.c_marks.get(input.layer));
							//slice.addHeader(0, y+1, colh);
							slice.addCell(0, y+1, colh);
						}
						
						String intermediate = Tokenizer.MakeParsable(sub.get(x)+att.get(y)+mod.get(z)+Tokenizer.c_marks.get(input.layer));
						slice.addCell(x+1, y+1, intermediate);
					}
				}
				
				table.add(slice);
			}
		}
		
		return table.get();
	}
};

class JsonSlice {
	
	int positionZ;
	
	JsonSlice(int z) {
		positionZ = z;
	}
	
	//ArrayList<JsonSliceEntry> headers = new ArrayList<JsonSliceEntry>();
	ArrayList<JsonSliceEntry> cells = new ArrayList<JsonSliceEntry>();
	
	/*
	public void addHeader(int x, int y, String v){
		headers.add(new JsonSliceEntry(x, y, v));
	}
	*/
	
	public void addCell(int x, int y, String v){
		cells.add(new JsonSliceEntry(x, y, v));
	}
	
	public String get() {
		
		StringBuilder json = new StringBuilder("{");

		json.append(String.format("\"%s\":%s,", JsonTable.TableDef.Z, positionZ));
		String comma = null;
		
		/*
		json.append(String.format("\"%s\": [", JsonTable.TableDef.TableHeader));
		comma = null;
		for (JsonSliceEntry entry : headers) {
			if (comma != null)
				json.append(comma);
			json.append(entry.get());
			if (comma == null)
				comma = ",";
		}
		json.append("],");
		*/
		
		json.append(String.format("\"%s\": [", JsonTable.TableDef.CellContent));
		comma = null;
		for (JsonSliceEntry entry : cells) {
			if (comma != null)
				json.append(comma);
			json.append(entry.get());
			if (comma == null)
				comma = ",";
		}
		json.append("]");
		
		json.append("}");
		return json.toString();
	}
};

class JsonSliceEntry {
	
	int positionX, positionY;
	String value;
	
	JsonSliceEntry(int x, int y, String v) {
		positionX = x;
		positionY = y;
		value = v;
	}
	
	public String get() {
		
		StringBuilder json = new StringBuilder();
		
		json.append("{");
		json.append(String.format("\"%s\":%s,", JsonTable.TableDef.X, positionX));
		json.append(String.format("\"%s\":%s,", JsonTable.TableDef.Y, positionY));
		json.append(String.format("\"%s\":\"%s\"", JsonTable.TableDef.V, value));
		json.append("}");
		
		return json.toString();
	}
}

class JsonTables {
	
	String value;
	ArrayList<String> tables = new ArrayList<String>();
	
	JsonTables(String v) {
		value = v;
	}
	
	public void add(String table) {
		if (table != null)
			tables.add(table);
	}
	
	public String get() {
		
		StringBuilder json = new StringBuilder();
		
		json.append(String.format("\"%s\":\"%s\",", JsonTable.TableDef.TableScript, value));
		json.append(String.format("\"%s\": [", JsonTable.TableDef.Tables));
		String comma = null;
		for (String entry : tables) {
			if (comma != null)
				json.append(comma);
			json.append(entry);
			if (comma == null)
				comma = ",";
		}
		json.append("]");
		
		return "{" + json.toString() + "}";
	}
}

class JsonTable {
	
	//https://jsonformatter.curiousconcept.com/
	
	StringBuilder json;
	ArrayList<JsonSlice> slices = new ArrayList<JsonSlice>();
	
	public enum TableDef {
		
		TableScript("Script"),
		Tables("Tables"),
		TableTitle("Title"), 
		//TableHeader("Header"), 
		CellContent("Cell"), 
		X("X"), // x coordinate
		Y("Y"), // y coordinate
	    Z("Z"), // z coordinate
	    V("V"),  // cell value
		TableSlice("Slice");

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
	
	public void add(JsonSlice slice) {
		slices.add(slice);
	}
	
	public String get() {
		if (json.toString().equals("")) {
			return null;
		}
		
		if (!json.toString().equals("")) {
			json.append(",");
		}
		
		json.append(String.format("\"%s\": [", JsonTable.TableDef.TableSlice));
		String comma = null;
		for (JsonSlice entry : slices) {
			if (comma != null)
				json.append(comma);
			json.append(entry.get());
			if (comma == null)
				comma = ",";
		}
		json.append("]");
		
		return "{" + json.toString() + "}";
	}
	
	public void addTitle(String value) {	
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":\"%s\"", TableDef.TableTitle, value));
	}
	
	public void addFormat(int x, int y, int z) {
		if (!json.toString().equals("")) {
			json.append(",");
		}
		json.append(String.format("\"%s\":%s,", TableDef.X, x));
		json.append(String.format("\"%s\":%s,", TableDef.Y, y));
		json.append(String.format("\"%s\":%s", TableDef.Z, z));
	}
}



