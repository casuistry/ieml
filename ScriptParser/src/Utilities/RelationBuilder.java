package Utilities;

import java.util.ArrayList;
import java.util.List;

import NewParser.ParserImpl;
import NewParser.Token;
import NewParser.Tokenizer;
import Utilities.TableGenerator.JsonSlice;
import Utilities.TableGenerator.JsonSliceEntry;
import Utilities.TableGenerator.JsonTable;
import Utilities.TableGenerator.JsonTables;

public class RelationBuilder {

	public static void main(String[] args) {
		
		System.out.println(System.getProperty("java.runtime.version"));
			
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");

		for (String s : db) {
			String[] parts = s.split(",");
			
			if (parts.length != 6) {
				System.out.println("missing: " + s);
				continue;
			}
			
			String ieml =  parts[0].trim().length() > 0 ? parts[0].trim():null;
			String res;
			try {
				res = RelationBuilder.GetRelations(ieml);
				System.out.println(res);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String GetRelations(String input) throws Exception {
		
		ParserImpl parser = new ParserImpl();
		
		try {
			
			Token n = parser.parse(input);
			return BuildRelations(n);
			
		} catch (Exception e) {
			return e.getMessage();
		}		
		finally {
			parser.Reset();
		}
	}
	
	public static String BuildRelations(Token n) throws Exception{
				
		ArrayList<String> result = new ArrayList<String>();
		
		result.addAll(BuildFamily(n));
		result.addAll(BuildTaxonomy(n));
		
		StringBuilder builder = new StringBuilder("{\"relations\":[");
		for (int i = 0; i < result.size(); i++){
			builder.append(result.get(i));
			if (i < result.size() - 1)
				builder.append(",");
		}
		builder.append("]}");
		return builder.toString();
	}
	
	public static ArrayList<String> BuildFamily(Token n) throws Exception {
		
		ArrayList<String> result = new ArrayList<String>();
		
		String parentRel = "ParentOf";
		String childRel = "ChildOf";
				
		String inputName = n.GetName();
		
		if (n.layer == 0) {
			// no relations
		} else if (n.opCode.equals(Tokenizer.addition)) {
			// no relations
		} else if (n.opCode.equals(Tokenizer.multiplication)) {
			
			if (!n.IsEmpty()) {
				
				if (!n.nodes.get(0).IsEmpty()) {
					result.add(build(inputName, n.nodes.get(0).GetName(), childRel)); 
					result.add(build(n.nodes.get(0).GetName(), inputName, parentRel));
				}

				if (!n.nodes.get(1).IsEmpty()) {
					result.add(build(inputName, n.nodes.get(1).GetName(), childRel)); 
					result.add(build(n.nodes.get(1).GetName(), inputName, parentRel));
				}

				if (!n.nodes.get(2).IsEmpty()) {
					result.add(build(inputName, n.nodes.get(2).GetName(), childRel)); 	
					result.add(build(n.nodes.get(2).GetName(), inputName, parentRel));
				}
			}

		} else {
			throw new Exception("Cannot generate family relations");
		}
		
		return result;
	}
	
	public static ArrayList<String> BuildTaxonomy(Token n) {
		
		ArrayList<String> result = new ArrayList<String>();
	
		String TableContains = "TableContains";
		String GeneratedBy = "GeneratedBy";
		String ContainedBy ="ContainedBy";
		
		TableGenerator tGen = new TableGenerator();
		
		JsonTables json;
		try {
			json = tGen.genJSONTables(n);
			
			for (JsonTable table : json.tables){
				
				int z = table.slices.size();
				
				for (JsonSlice slice : table.slices) {
					
					int rows = table.materialRow;
					int cols = table.materialCol;
					
					for (JsonSliceEntry cell : slice.cells){
						if (cell.positionX == 0 && cell.positionY == 0)
							continue;

						//result.add(build(inputName, n.nodes.get(2).GetName(), childRel)); 
					}
					
				}
			}
			
		} catch (Exception e) {

		}

		return result;
	}
	
	public static String build(String start, String stop, String name) {
		String format = "{\"start\":\"%s\", \"stop\":\"%s\", \"name\":\"%s\"}";
		return String.format(format, start, stop, name);
	}
}

/*
{"relations":[
              {"start":"ieml1", "stop":"ieml2", "name":"Parent"}, 
              {...}, 
              {...}
          ]}
*/


