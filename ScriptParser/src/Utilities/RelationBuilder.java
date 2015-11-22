package Utilities;

import java.util.List;

import NewParser.ParserImpl;
import NewParser.Token;
import NewParser.Tokenizer;

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
				
		StringBuilder builder = new StringBuilder("{\"relations\":[");
		
		builder.append(BuildFamily(n));
		
		builder.append("]}");
		
		return builder.toString();
	}
	
	public static String BuildFamily(Token n) throws Exception {
		
		StringBuilder builder = new StringBuilder();

		String parentRel = "ParentOf";
		String childRel = "ChildOf";
				
		String inputName = n.GetName();
		
		if (n.layer == 0) {
			
		} else if (n.opCode.equals(Tokenizer.addition)) {
			
		} else if (n.opCode.equals(Tokenizer.multiplication)) {
			builder.append(build(inputName, n.nodes.get(0).GetName(), childRel)); builder.append(",");
			builder.append(build(inputName, n.nodes.get(1).GetName(), childRel)); builder.append(",");
			builder.append(build(inputName, n.nodes.get(2).GetName(), childRel)); builder.append(",");			
			builder.append(build(n.nodes.get(0).GetName(), inputName, parentRel));builder.append(",");
			builder.append(build(n.nodes.get(1).GetName(), inputName, parentRel));builder.append(",");
			builder.append(build(n.nodes.get(2).GetName(), inputName, parentRel));
		} else {
			throw new Exception("Cannot generate family relations");
		}
		
		return builder.toString();
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


