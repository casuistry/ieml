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
		
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		//ArrayList<String> db = new ArrayList<String>();
		//db.add("M:.+O:M:.- , 2, 3, 4, 5, 6");
		//db.add("(S:+B:)(S:+T:).f.- , 2, 3, 4, 5, 6");
		//db.add("S:B:.E:.S:B:.- , 2, 3, 4, 5, 6");
		//db.add("E:F:.O:M:.- , 2, 3, 4, 5, 6");
		
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
				
				String ieml = parts[0].trim().length() > 0 ? parts[0].trim():null; /*ScriptExamples.UnCoteurraconteUneHistoire;*/
				//String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
				//String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
				//String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
				//String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
				//String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
							
				try {				
					Token n = parser.parse(ieml);	
					ArrayList<Token> tables = createTableSet(n);
										
					StringBuilder builder = new StringBuilder();
					
					for (Token table : tables) {
						String tableDesc = generateTable(table);
						if (tableDesc != null)
							builder.append(tableDesc);
					}
					
					String toPrint = builder.toString();
					
					if (toPrint.length() > 0)
					{
						System.out.println(n.GetName());
						System.out.println(toPrint);
					}
					
					//GenerateTables(n);
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
					
	// generates one table
	public String generateTable(Token start) throws Exception{
		
		if (start.opCode == null || start.layer == 0) {
			return null;
		}
		
		if (start.opCode.equals(Tokenizer.addition) ){
			throw new Exception ("ERROR in generateTable, CreateTableSet must be run before this method");
		}		
		
		Token substance = start.nodes.get(0);
		Token attribut = start.nodes.get(1);
		Token mode = start.nodes.get(2);
		
		ArrayList<Token> substance_list = recursiveGetScriptVariables(substance);
		ArrayList<Token> attribut_list = recursiveGetScriptVariables(attribut);
		ArrayList<Token> mode_list = recursiveGetScriptVariables(mode);
				
		int v_substance = substance_list.size();
		int v_attribut = attribut_list.size();
		int v_mode = mode_list.size();
		
		JsonTable json = new JsonTable();
		
		TableType tableType = TableType.undefined;
		
		if (v_substance < 1 && v_attribut < 1 && v_mode < 1) {
			tableType = TableType.no_table;
		}
		else if (v_substance > 0 && v_attribut > 0 && v_mode > 0) {
			tableType = TableType.triple;
		}
		else if (v_substance == 0 && v_attribut == 0 && v_mode > 0) {			
			
			if (v_mode == 1) {
				tableType = TableType.row_mode;
				ArrayList<String> a = expandScript(mode);
				json.add(JsonTable.TableDef.TableHeader, start.GetName());
				for (String s : a) {
					String clean = Tokenizer.MakeParsable(substance.GetName()+attribut.GetName()+s+Tokenizer.c_marks.get(start.layer));
					json.add(JsonTable.TableDef.ColHeader, clean);
				}
			}
			else {
				tableType = TableType.matrix_mode;
			}
		}
		else if (v_substance == 0 && v_attribut > 0 && v_mode == 0) {
			
			if (v_attribut == 1) {
				tableType = TableType.row_attribut;
				ArrayList<String> a = expandScript(attribut);
				json.add(JsonTable.TableDef.TableHeader, start.GetName());
				for (String s : a) {
					String clean = Tokenizer.MakeParsable(substance.GetName()+s+mode.GetName()+Tokenizer.c_marks.get(start.layer));
					json.add(JsonTable.TableDef.ColHeader, clean);
				}
			}
			else {
				tableType = TableType.matrix_attribut;
			}
		}
		else if (v_substance > 0 && v_attribut == 0 && v_mode == 0) {
			
			if (v_substance == 1) {
				tableType = TableType.row_substance;				
				ArrayList<String> a = expandScript(substance);
				json.add(JsonTable.TableDef.TableHeader, start.GetName());
				for (String s : a) {
					String clean = Tokenizer.MakeParsable(s+attribut.GetName()+mode.GetName()+Tokenizer.c_marks.get(start.layer));
					json.add(JsonTable.TableDef.ColHeader, clean);
				}
			}
			else {
				tableType = TableType.matrix_substance;
			}
		}
		else if (v_substance > 0 && v_attribut > 0 && v_mode == 0) {
			tableType = TableType.double_mode;
			
			json.add(JsonTable.TableDef.TableHeader, start.GetName());
			
			ArrayList<String> col = expandScript(substance);
			ArrayList<String> row = expandScript(attribut);
			
			for (String s_col : col) {
				String clean = Tokenizer.MakeParsable(s_col+attribut.GetName()+mode.GetName()+Tokenizer.c_marks.get(start.layer));
				json.add(JsonTable.TableDef.ColHeader, clean);
			}
			for (String s_row : row) {
				String clean = Tokenizer.MakeParsable(substance.GetName()+s_row+mode.GetName()+Tokenizer.c_marks.get(start.layer));
				json.add(JsonTable.TableDef.RowHeader, clean);
			}
			for (String s_row : row) {
				for (String s_col : col) {
					String clean = Tokenizer.MakeParsable(s_col+s_row+mode.GetName()+Tokenizer.c_marks.get(start.layer));
					json.add(JsonTable.TableDef.CellContent, clean);
				}
			}
		}
		else if (v_substance > 0 && v_attribut == 0 && v_mode > 0) {
			tableType = TableType.double_attribut;
		}
		else if (v_substance == 0 && v_attribut > 0 && v_mode > 0) {
			tableType = TableType.double_substance;
		}
		
		StringBuilder log = new StringBuilder("  creating table for " + start.GetName() + " ");
		log.append("[substance=" + v_substance);
		log.append(" attribut=" + v_attribut);
		log.append(" mode=" + v_mode + "] ");
		log.append("["+tableType.getFieldDescription()+"]\n");
		
		//if (tableType != TableType.no_table) 
			//System.out.println(log.toString());
		
		if (tableType == TableType.undefined) 
			throw new Exception("ERROR in generateTable, table type is undefined");
			
		return json.toString();
	}
		
	public ArrayList<Token> recursiveGetScriptVariables(Token start) {
		
		ArrayList<Token> result = new ArrayList<Token>();
		
		if (start.opCode != null) {
			if (start.opCode.equals(Tokenizer.addition)) {
                result.add(start);
			}
			else {
				for (Token child : start.nodes) {
					result.addAll(recursiveGetScriptVariables(child));
				}
			}
		}

		return result;
	}
	
	public ArrayList<Token> createTableSet(Token parent) {

		ArrayList<Token> roots = new ArrayList<Token>();
		
		for (String s : recursiveCreateTableSet(parent)) {

			ParserImpl parser = new ParserImpl();
			
			try {			
				String parsable = Tokenizer.MakeParsable(s);
				roots.add(parser.parse(parsable));	
			} catch (Exception e) {
				System.out.println("ERROR CreateTableSet [" + e.getMessage()+"]");
			}		
			finally {
				parser.Reset();			
			}
		}
		
		return roots;
	}
	
	public ArrayList<String> recursiveCreateTableSet(Token parent) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (parent.opCode != null && parent.layer > 0) {
			if (parent.opCode.equals(Tokenizer.addition)) {
				for (Token child : parent.nodes) {					
					result.addAll(recursiveCreateTableSet(child));					
				}
				return result;
			}
			else {
				for (Token child : parent.nodes) {					
					if (result.isEmpty()) {
						result.addAll(recursiveCreateTableSet(child));
					}
					else {
						ArrayList<String> temp = new ArrayList<String>();
						ArrayList<String> r = recursiveCreateTableSet(child);
						for (String prefix : result) {
							for (String postfix : r) {
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
				
				return temp;
			}
		}		
		else {
			result.add(parent.GetName());
			return result;
		}
	}
	
	public ArrayList<String> expandScript(Token token) {
		
		String expandable = token.GetName();
		
		ArrayList<String> expanded = new ArrayList<String>();
		
		for (int i = 0; i < expandable.length() - 1; i++) {
			String key = expandable.substring(i, i+2);
			if (Tokenizer.primitiveLookup.containsKey(key)) {
				Token replacements = Tokenizer.primitiveLookup.get(key);
				for (Token t : replacements.nodes) {
					StringBuilder builder = new StringBuilder(expandable.substring(0, i));
					builder.append(t.GetName());
					builder.append(expandable.substring(i+2, expandable.length()));
					expanded.add(builder.toString());
				}
			}			
		}
		
		return expanded;
	}
}

//create JSON terms: 
//{ IEML: "S:B:T:.", FR:"qqchose", EN:"something", PARADIGM: "", LAYER: "", CLASS: ""  }
//String j = String.format("{ieml:\"%s\",terms:[{lang:\"FR\",means:\"%s\"},{lang:\"EN\",means:\"%s\"}],paradigm:\"%s\",layer:\"%s\",class:\"%s\"}", 
//		ieml, fr, en, pa, la, cl);
//String j = String.format("{IEML:\"%s\",FR:\"%s\",EN:\"%s\",PARADIGM:\"%s\",LAYER:\"%s\",CLASS:\"%s\"}", ieml, fr, en, pa, la, cl);

class JsonTable {
	
	StringBuilder json;
	
	public enum TableDef {
		
		TableHeader("TableHeader"),
		RowHeader("RowHeader"),
		CellContent("CellContent"),
		ColHeader("ColHeader");

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
		json.append(String.format("%s:\"%s\"", def.getFieldDescription(), value));
	}
}



