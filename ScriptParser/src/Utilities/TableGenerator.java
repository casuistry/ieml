package Utilities;
import java.util.ArrayList;
import java.util.List;
import NewParser.ParserImpl;
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
		
		System.out.println(System.getProperty("java.runtime.version"));
		
		TableGenerator tGen = new TableGenerator();
		
		tGen.generate();
	}
		
	public void generate(){
		
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		//ArrayList<String> db = new ArrayList<String>();
		//db.add("S:M:.e.-S:B:T:.S:B:T:.S:B:T:.-S:B:T:.S:B:T:.S:B:T:.-' , 2, 3, 4, 5, 6");
		//db.add("M:O:.j.- , 2, 3, 4, 5, 6");
		//db.add("(S:+B:)(S:+T:).f.- , 2, 3, 4, 5, 6");
		//db.add("S:B:.E:.S:B:.- , 2, 3, 4, 5, 6");
		//db.add("E:F:.O:M:.- , 2, 3, 4, 5, 6");
		//db.add("O:M:.+M:O:., 2, 3, 4, 5, 6");
		//db.add("t.i.-s.i.-', 2, 3, 4, 5, 6");
		//db.add("O:. + O:O:O:. + M:O:M:., 2, 3, 4, 5, 6");
		//db.add("O:O:.-, 2, 3, 4, 5, 6");
		//db.add("O:., 2, 3, 4, 5, 6");
		//db.add("M:M:.e.-, 2, 3, 4, 5, 6");
		//db.add("M:M:.e.-+O:M:O:.-, 2, 3, 4, 5, 6");
		//db.add("M:M:M:.o.-O:.-M:O:.M:O:.-', 2, 3, 4, 5, 6");
		//db.add("S:M:., 2, 3, 4, 5, 6");
		//db.add("E:O:+M:.wo.-, 2, 3, 4, 5, 6");
		//db.add("E:F:.d.-, 2, 3, 4, 5, 6");
		//db.add("B:B:., 2, 3, 4, 5, 6");
		//db.add("A:+T:, 2, 3, 4, 5, 6");
		
		
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
	public String genJSONTables(Token token) throws Exception {
			
		JsonTables jsonTables = new JsonTables(token.GetName());
			
		for (Token root : genTables(token)) {

			// handle special case of O, M, F, I
			if (root.layer == 0 && Tokenizer.primitiveLookup.containsKey(root.GetName())) {
				//JsonTable table = writeTable(root);
				//jsonTables.add(table);
				//continue;
				throw new Exception("table for " + root.GetName() + " not implemented yet");
			}
			
			ArrayList<String> sub, att, mod;
			
			sub = Token.genVariables(root.nodes.get(0));
			att = Token.genVariables(root.nodes.get(1));
			mod = Token.genVariables(root.nodes.get(2));
			
			String prefix = "";
			String postfix = "";
			Token rootToken = root;
			
			TableType t_type = getTableType(sub, att, mod);
			AlgoDef a_type = getAlgoType( t_type, root.nodes.get(0), root.nodes.get(1), root.nodes.get(2), sub, att, mod);
			
			TableType useType = t_type;
			
			if (a_type == AlgoDef.Layer) {
				
				if (t_type == TableType.row_substance) {
					rootToken = root.nodes.get(0);
					prefix = "";
					postfix = root.nodes.get(1).GetName() + root.nodes.get(2).GetName() + Tokenizer.c_marks.get(root.layer);					
					sub = Token.genVariables(rootToken.nodes.get(0));
					att = Token.genVariables(rootToken.nodes.get(1));
					mod = Token.genVariables(rootToken.nodes.get(2));
					useType = getTableType(sub, att, mod);
				}
				
				if (t_type == TableType.row_attribut) {
					rootToken = root.nodes.get(1);
					prefix = root.nodes.get(0).GetName();
					postfix = root.nodes.get(2).GetName() + Tokenizer.c_marks.get(root.layer);
					sub = Token.genVariables(rootToken.nodes.get(0));
					att = Token.genVariables(rootToken.nodes.get(1));
					mod = Token.genVariables(rootToken.nodes.get(2));
					useType = getTableType(sub, att, mod);
				}
				
				if (t_type == TableType.row_mode) {
					rootToken = root.nodes.get(2);
					prefix = root.nodes.get(0).GetName() + root.nodes.get(1).GetName();
					postfix = Tokenizer.c_marks.get(root.layer).toString();
					sub = Token.genVariables(rootToken.nodes.get(0));
					att = Token.genVariables(rootToken.nodes.get(1));
					mod = Token.genVariables(rootToken.nodes.get(2));
					useType = getTableType(sub, att, mod);
				}
			}
			
			JsonTable table = writeTable(rootToken, useType, sub, att, mod);
			
			table.ensureMaterialSize();
			
			if (a_type == AlgoDef.Layer) {
				
				if (t_type == TableType.row_substance) {
                    table.fixEntry(prefix, postfix);
				}
				
				if (t_type == TableType.row_attribut) {
					table.fixEntry(prefix, postfix);
				}
				
				if (t_type == TableType.row_mode) {
					table.fixEntry(prefix, postfix);
				}
			}
			
			jsonTables.add(table);
		}
		
		return jsonTables.getMaterial();
	}
	
	
	// STEP 1:
	// If the input token has an additive relation, it needs to be
	// split before generating a table, otherwise we are good to 
	// generate a table. E.g.: if ABC -> generate table for ABC, 
	// if A+B+C -> generate table for A, B and C 
	private ArrayList<Token> genTables(Token parent) throws Exception {
		
		ArrayList<Token> result = new ArrayList<Token>();
		
		// allow O, M, F, I
		if (parent.layer == 0 && Tokenizer.primitiveLookup.containsKey(parent.GetName())) {
			result.add(parent);
			return result;
		}
		
		if (parent.opCode != null && parent.opCode.equals(Tokenizer.addition)) {
			result.addAll(parent.nodes);
		}
		
		if (parent.opCode != null && parent.opCode.equals(Tokenizer.multiplication)) {
			result.add(parent);
		}
		
		if (result.size() == 0) {
			throw new Exception("not enough semes to generate table");
		}
		
		for (Token t : result) {
			if (t.layer == 0) {
				if (!Tokenizer.primitiveLookup.containsKey(t.GetName())) {
					throw new Exception("not enough semes to generate table");
				}
			}
		}
		
		return result;
	}

	// STEP 3
	// Returns JSON string or NULL
	private JsonTable writeTable(Token input, TableType tableType, ArrayList<String> sub, ArrayList<String> att, ArrayList<String> mod) throws Exception {
		
		JsonTable table = new JsonTable(input.GetName());
		table.addFormat(sub.size(), att.size(), mod.size());
				
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
				JsonSlice slice = new JsonSlice(z, Tokenizer.MakeParsable(mod.get(z)));
				
				for (int x = 0; x < sub.size(); x++) {
					
					String rowh = Tokenizer.MakeParsable(sub.get(x)+input.nodes.get(1).GetName()+mod.get(z)+Tokenizer.c_marks.get(input.layer));
					slice.addCell(x+1, 0, rowh);
					
					for (int y = 0; y < att.size(); y++) {
						
						if (x == 0) {
							String colh = Tokenizer.MakeParsable(input.nodes.get(0).GetName()+att.get(y)+mod.get(z)+Tokenizer.c_marks.get(input.layer));
							slice.addCell(0, y+1, colh);
						}
						
						String intermediate = Tokenizer.MakeParsable(sub.get(x)+att.get(y)+mod.get(z)+Tokenizer.c_marks.get(input.layer));
						slice.addCell(x+1, y+1, intermediate);
					}
				}
				
				table.add(slice);
			}
		}
		
		return table;
	}
	
	private TableType getTableType(ArrayList<String> sub, ArrayList<String> att, ArrayList<String> mod) throws Exception {
		
		int v_substance = sub.size();
		int v_attribut = att.size();
		int v_mode = mod.size();
		
		if (v_substance > 1 && v_attribut > 1 && v_mode > 1) {
			return TableType.triple;
		}
		else if (v_substance > 1) {
			if (v_attribut > 1) {
				return TableType.double_mode;
			}
			else if (v_mode > 1) {
				return TableType.double_attribut;
			}
			else {
				return TableType.row_substance;
			}
		}
		else if (v_attribut > 1) {
			if (v_mode > 1) {
				return TableType.double_substance;
			}
			else {
				return TableType.row_attribut;
			}
		}
		else if (v_mode > 1) {
			return TableType.row_mode;
		}
		else {
			throw new Exception("not enough variables to generate table");
		}
	}
	
	private AlgoDef getAlgoType(TableType tableType, Token a, Token b, Token c, ArrayList<String> sub, ArrayList<String> att, ArrayList<String> mod) {
		
		if (a.layer < 1)
			return AlgoDef.Normal;
		
		int vars = sub.size()*att.size()*mod.size();
		
		if (tableType == TableType.row_substance && vars >= 4 && !a.GetName().contains("+"))  {
			return AlgoDef.Layer;
		}
		else if (tableType == TableType.row_attribut && vars >= 4 && !b.GetName().contains("+"))  {
			return AlgoDef.Layer;
		}
		else if (tableType == TableType.row_mode && vars >= 4 && !c.GetName().contains("+"))  {
			return AlgoDef.Layer;
		}	
		else {
			return AlgoDef.Normal;
		}
	}
	
	public enum AlgoDef {
		
		Normal("Normal"),
		Layer("Layer");

	    private final String fieldDescription;

	    AlgoDef(String descr) {
	        this.fieldDescription = descr;
	    }
	    
	    public String getFieldDescription() {
	        return fieldDescription;
	    }
	}
	
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
	
	
	class JsonTables {
		
		String value;
		ArrayList<JsonTable> tables = new ArrayList<JsonTable>();
		
		JsonTables(String v) {
			value = v;
		}
		
		public void add(JsonTable table) {
			if (table != null)
				tables.add(table);
		}
		
		public String getMaterial() throws Exception {
					
			if (tables.isEmpty()) {
				throw new Exception("Could not generate tables");
			}
			
			StringBuilder json = new StringBuilder();
			
			json.append(String.format("\"input\":\"%s\",\"%s\": [", value, TableDef.Tables));
			String comma = null;
			for (JsonTable entry : tables) {
				
				if (comma != null)
					json.append(comma);
				
				json.append(entry.getMaterial());
				
				if (comma == null)
					comma = ",";
			}
			json.append("]");
			
			return "{" + json.toString() + "}";
		}
		
		public String get() {
			
			StringBuilder json = new StringBuilder();
			
			json.append(String.format("\"%s\":\"%s\",", TableDef.TableScript, value));
			json.append(String.format("\"%s\": [", TableDef.Tables));
			String comma = null;
			for (JsonTable entry : tables) {
				if (comma != null)
					json.append(comma);
				json.append(entry.get());
				if (comma == null)
					comma = ",";
			}
			json.append("]");
			
			return "{" + json.toString() + "}";
		}
	}

	
	class JsonTable {
		
		//https://jsonformatter.curiousconcept.com/
		
		// used to generate material format
		String tableTitle = "";
		int materialRow = 0;
		int materialCol = 0;
		
		ArrayList<JsonSlice> slices = new ArrayList<JsonSlice>();

		public JsonTable(String value) {
			tableTitle = value;
		}
		
		public void add(JsonSlice slice) {
			slices.add(slice);
		}
		
		public void fixEntry(String prefix, String postfix){
			
			for (JsonSlice slice : slices) {
				for (JsonSliceEntry sliceEntry : slice.getCells()) {
					sliceEntry.fixEntry(prefix, postfix);
				}
			}
			
			tableTitle = Helper.fixEntry(tableTitle, prefix, postfix);
		}
		
		public void ensureMaterialSize() throws Exception {
			if (materialRow > 12)
				throw new Exception("too many rows");
			if (materialCol > 12)
				throw new Exception("too many columns");
			if (slices.size() > 12)
				throw new Exception("too many slices");
			if ( materialRow*materialCol > 144)
				throw new Exception("too many 2D cells");
			if ( materialRow*materialCol*slices.size() > 360)
				throw new Exception("too many 3D cells");
		}
		
		public String getMaterial() {
			
			String format = "{\"span\":{\"row\":%s, \"col\":%s}, \"means\":{\"fr\":\"\", \"en\":\"\"}, \"background\":\"%s\", \"value\":\"%s\", \"editable\":%s, \"creatable\":false}";
			StringBuilder builder = new StringBuilder();
						
			int rowHead = materialRow > 1 ? materialRow : 0;
			int colHead = materialCol > 1 ? materialCol : 0;
					
			JsonSliceEntry[] sliceEntryArray;
			if (rowHead > 1 && colHead > 1) {
				sliceEntryArray = new JsonSliceEntry[(rowHead+1)*(colHead+1)];
				// in 2D case, add the 'empty' cell
				sliceEntryArray[0] = new JsonSliceEntry(0,0,"");
			}
			else {
				sliceEntryArray = new JsonSliceEntry[rowHead];
			}
			
			String c1 = null;
			
			builder.append(String.format("{\"Col\":\"%s\",\"table\":[", colHead+1));
			
			for (JsonSlice slice : slices) {
			
				if (c1 != null)
					builder.append(c1);
				
				builder.append(String.format("{\"tabTitle\":\"%s\",\"slice\":[", slice.getSliceTitle()));
				
				for (JsonSliceEntry sliceEntry : slice.getCells()) {
					
					if (rowHead > 1 && colHead > 1) {
						sliceEntryArray[sliceEntry.positionX*(colHead+1)+sliceEntry.positionY] = sliceEntry;
					}
					else {
						sliceEntryArray[sliceEntry.positionX] = sliceEntry;
					}
				}
				
				// title
				builder.append(String.format(format, 1, (rowHead > 1 && colHead > 1)?colHead+1:1, "green", tableTitle, "true"));
				builder.append(",");
				
				String comma = null;
				for (int i = 0; i < sliceEntryArray.length; i++) {
					
					if (comma != null)
						builder.append(comma);
						
					String color = "gray";
					
					if (rowHead > 1 && colHead > 1) {
						if (i == 0) {
							color = "gray";
						}
						else if ((i > 0 && i < colHead+1) || i%(colHead+1) == 0) {
							color = "blue";
						}
					}
									
					String editable = sliceEntryArray[i].value == "" ? "false" : "true";					
					builder.append(String.format(format, 1, 1, color, sliceEntryArray[i].value, editable));
					
					if (comma == null)
						comma = ",";
				}
				
				builder.append("]}");
				
				if (c1 == null)
					c1 = ",";
			}

			builder.append("]}");

			return builder.toString();
		}
		
		public String get() {

			StringBuilder json = new StringBuilder();
			
			json.append(String.format("\"%s\": [", TableDef.TableSlice));
			String comma = null;
			for (JsonSlice entry : slices) {
				if (comma != null)
					json.append(comma);
				json.append(entry.get());
				if (comma == null)
					comma = ",";
			}
			json.append("]");
			
			//if (!json.toString().equals("")) {
			//	json.append(",");
			//}
			//json.append(String.format("\"%s\":%s,", TableDef.X, x));
			//json.append(String.format("\"%s\":%s,", TableDef.Y, y));
			//json.append(String.format("\"%s\":%s", TableDef.Z, z));
			
			//if (!json.toString().equals("")) {
			//	json.append(",");
			//}
			//json.append(String.format("\"%s\":\"%s\"", TableDef.TableTitle, value));
			
			return "{" + json.toString() + "}";
		}
		
		public void addFormat(int x, int y, int z) {
			
			if (x > 1 && y > 1 && z > 1) {
				materialRow = x;
				materialCol = y;
			}
			else if (x > 1) {
				materialRow = x;
				if (y > 1) {
					materialCol = y;
				}
				else if (z > 1) {
					materialCol = z;
				}
				else {
					materialCol = 1;
				}
			}
			else if (y > 1) {
				materialRow = y;
				if (z > 1) {
					materialCol = z;
				}
				else {
					materialCol = 1;
				}
			}
			else if (z > 1) {
				materialRow = z;
				materialCol = 1;
			}
		}
	}

	
	class JsonSlice {
		
		int positionZ;
		String sTitle = "";
		
		JsonSlice(int z) {
			positionZ = z;
			sTitle = "";
		}
		
		JsonSlice(int z, String sliceTitle) {
			positionZ = z;
			sTitle = sliceTitle;
		}
		
		//ArrayList<JsonSliceEntry> headers = new ArrayList<JsonSliceEntry>();
		ArrayList<JsonSliceEntry> cells = new ArrayList<JsonSliceEntry>();
		
		/*
		public void addHeader(int x, int y, String v){
			headers.add(new JsonSliceEntry(x, y, v));
		}
		*/
		
		public String getSliceTitle() {
			return sTitle;
		}
		
		public ArrayList<JsonSliceEntry> getCells() {
			return cells;
		}
		
		public void addCell(int x, int y, String v){
			cells.add(new JsonSliceEntry(x, y, v));
		}
		
		public String get() {
			
			StringBuilder json = new StringBuilder("{");

			json.append(String.format("\"%s\":%s,", TableDef.Z, positionZ));
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
			
			json.append(String.format("\"%s\": [", TableDef.CellContent));
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
	}
	
	
	class JsonSliceEntry {
		
		int positionX, positionY;
		String value;
		
		JsonSliceEntry(int x, int y, String v) {
			positionX = x;
			positionY = y;
			value = v;
		}
		
		public void fixEntry(String prefix, String postfix){
			value = Helper.fixEntry(value, prefix, postfix);
		}
		
		public String get() {
			
			StringBuilder json = new StringBuilder();
			
			json.append("{");
			json.append(String.format("\"%s\":%s,", TableDef.X, positionX));
			json.append(String.format("\"%s\":%s,", TableDef.Y, positionY));
			json.append(String.format("\"%s\":\"%s\"", TableDef.V, value));
			json.append("}");
			
			return json.toString();
		}
	}
	
};









