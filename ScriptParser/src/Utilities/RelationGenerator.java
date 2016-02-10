package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import NewParser.ParserImpl;
import NewParser.Token;
import NewParser.Tokenizer;

public class RelationGenerator {

	private static String AscSub = "AscSub";
	private static String AscAtt = "AscAtt";
	private static String AscMod = "AscMod";
	private static String DscSub = "DscSub";
	private static String DscAtt = "DscAtt";
	private static String DscMod = "DscMod";
	
	private static String GermainJumeau ="GermainJumeau";
	private static String GermainOpposes ="GermainOpposes";
	private static String GermainAssocies ="GermainAssocies";
	private static String GermainCroises = "GermainCroises";
	private static String ContenuDans = "ContenuDans";
	private static String Contiens = "Contiens";
	
	// helper to create valid json
	private static String build(String start, String stop, String name) {
		
		//String format = "%s %s %s";
		//return String.format(format, start, name, stop);
		String format = "{\"start\":\"%s\", \"stop\":\"%s\", \"name\":\"%s\"}";
		return String.format(format, start, stop, name);
	}
	
	private static String buildJSON(List<String> result) {
		StringBuilder builder = new StringBuilder("{\"relations\":[");
		for (int i = 0; i < result.size(); i++){
			builder.append(result.get(i));
			if (i < result.size() - 1)
				builder.append(",");
		}
		builder.append("]}");
		return builder.toString();	
	}
	
	private static ParserImpl internalParser = new ParserImpl();
	private static Token GetTokenInternal(String s) {
		Token result = null;
		try {
			internalParser.Reset();
			result = internalParser.parse(Tokenizer.MakeParsable(s));
		} catch(Exception e) {
			System.out.println("Internal parser: " + e.getMessage());
		} 
		return result;
	}
	
	public static void main(String[] args) {
				
		//String string = "T:M:.e.-M:M:.i.-E:.-+wa.e.-'";
	    
		//String string = "O:S:T:.";
		//String string = "O:M:.";
		//String string = "(S:+O:)O:.";
		//String string = "O:O:.+M:O:.";
		//String string = "M:M:.-O:M:.-E:.-+s.y.-'";
		
		String string = "M:M:.-O:M:.-E:.-+s.y.-'+M:M:.-M:O:.-E:.-+s.y.-'"; //from Pierre
		//String string = "O:M:.";                     //no germains
		//String string = "M:O:.M:M:.-+M:M:.M:O:.-";   //germains oppose
		//String string = "O:M:.O:M:.-+M:O:.M:O:.-";   //germain croises
		//String string = "O:M:.O:M:.-";                 //contenu check
		
		List<String> result = new ArrayList<String>();
		try {
			result = generateRelationsImpl(string, 1);
			
			System.out.println(generateRelations(string, 1));
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HashMap<String, String> check = new HashMap<String, String>();
		
		for (String rel : result) {					
			if (!check.containsKey(rel))
				check.put(rel, rel);
			else
				System.out.println("Duplicate: " + rel);
		}
		
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\relations.log"));
			
			for (String rel : result){		
				bw.write(rel);
				bw.newLine();
			}
			
			bw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Done ");
	}
	
	// API
	public static String generateRelations(String input, int paradigm) {
		
		try {
			List<String> result = generateRelationsImpl(input, paradigm);
			return buildJSON(result);			
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private static List<String> generateRelationsImpl(String input, int paradigm) throws Exception {
		
		ArrayList<String> result = new ArrayList<String>();
		HashMap<Integer, HashMap<Token, Set<String>>> map = new HashMap<Integer, HashMap<Token, Set<String>>>();
		
		ArrayList<Set<String>> cRels = new ArrayList<Set<String>>();  
		ArrayList<Token> cRelsTokens = new ArrayList<Token>();
				
		try {
			//get the root
			Token rootToken = GetTokenInternal(input);
			
			//get all possibilities through permutation and create data structure
			HashMap<String, String> permutations = GetExpanded(rootToken);
			for (String perms : permutations.keySet()) {		

				Token token = GetTokenInternal(perms);
				//if additive at top layer, ignore as it cannot be a table or a cell
				if (token.layer > 0 && token.opCode.equals(Tokenizer.addition))
					continue;								
				 
				ArrayList<String> components = Token.genVariables(token, true);				
				int size = components.size();				
				if (!map.containsKey(size)) {					
					HashMap<Token, Set<String>> h = new HashMap<Token, Set<String>>();
					map.put(size, h);
				}
				// add terms that generate sets of same cardinality
				Set<String> set = new HashSet<String>(components);
				map.get(size).put(token, set);
				
				//for contain/containing relations
				cRels.add(new HashSet<String>(components));
				cRelsTokens.add(token);
			}
			
//			//extract parental relations by reading the data structure
//			HashMap<String, String> temp_rels = new HashMap<String, String>();
//			for (Token t : cRelsTokens) {
//				HashMap<String, String> temp = BuildFamilyRecursive(t);
//				for (String k : temp.keySet()) {
//					if (!temp_rels.containsKey(k))
//						temp_rels.put(k, k);
//				}
//			}			
//			result.addAll(temp_rels.keySet());
			
			//extract parental relations by reading the data structure
			HashMap<String, String> temp_rels = new HashMap<String, String>();
			HashMap<String, String> temp = BuildFamilyRecursive(rootToken);
			for (String k : temp.keySet()) {
				if (!temp_rels.containsKey(k))
					temp_rels.put(k, k);
			}		
			result.addAll(temp_rels.keySet());
			
			if (paradigm == 1) {
				
				//extract germain relations by reading the data structure
				for (Integer integer : map.keySet()) {
					
					ArrayList<Token> tokenRef = new ArrayList<Token>();
					ArrayList<Set<String>> setRef = new ArrayList<Set<String>>();
					
					HashMap<Token, Set<String>> m = map.get(integer);
									
					for (Token t : m.keySet()) {
						tokenRef.add(t);
						setRef.add(m.get(t));
					}
					
					for (int i = 0; i < setRef.size(); i++ ) {
						for (int j = i + 1; j < setRef.size(); j++) {
							Set<String> intersection = new HashSet<String>(setRef.get(i));
							intersection.retainAll(setRef.get(j));
							
							if (intersection.size() == 0) {						
								List<String> l = GermainsRemarquables(tokenRef.get(i), tokenRef.get(j));
								result.addAll(l);
							}
							else {
								//nothing
							}
						}
					}
				}

				//extract contain relations by reading the data structure
				for (int i = 0; i < cRels.size(); i++){
					
					Set<String> setA = cRels.get(i);
					//we only want tables, so skip sequences singulieres
					if (setA.size() < 2 )
						continue;
					
					result.add(build(rootToken.GetName(), cRelsTokens.get(i).GetName(), Contiens));
					result.add(build(cRelsTokens.get(i).GetName(), rootToken.GetName(), ContenuDans));
					
					for (int j = i+1; j < cRels.size(); j++){
						
						Set<String> setB = cRels.get(j);					
						//we only want tables, so skip sequences singulieres
						if (setB.size() < 2)
							continue;
											
						if (setA.containsAll(setB)) {
							result.add(build(cRelsTokens.get(i).GetName(), cRelsTokens.get(j).GetName(), Contiens));
							result.add(build(cRelsTokens.get(j).GetName(), cRelsTokens.get(i).GetName(), ContenuDans));
						}
						if (setB.containsAll(setA)) {
							result.add(build(cRelsTokens.get(j).GetName(), cRelsTokens.get(i).GetName(), Contiens));
							result.add(build(cRelsTokens.get(i).GetName(), cRelsTokens.get(j).GetName(), ContenuDans));
						} 
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// handle additive relations
	private static HashMap<String,String> GetExpanded(Token n) {
		
		HashMap<String,String> result = GetExpandedRecursive(n);
		
		if (n.opCode != null && n.opCode.equals(Tokenizer.addition)) {
			
			for (Token child : n.nodes){
				
				if (!result.containsKey(child.GetName())) {
					result.put(child.GetName(), child.GetName());
				}
			}
		}
		
		return result;
	}
	
	//gets all permutations of multiplicative input
	private static HashMap<String, String> GetExpandedRecursive(Token n) {
		
		HashMap<String,String> result = new HashMap<String,String>();
				
		if (n.opCode != null) {
			
			if (n.opCode.equals(Tokenizer.addition)) {
				for (int i = 0; i < n.nodes.size(); i++){				
					HashMap<String,String> temp = GetExpanded(n.nodes.get(i));
					for (String key : temp.keySet()) {
						if (!result.containsKey(key)) {
							result.put(key, key);
						}
					}
				}
			}
			else {
				
				HashMap<String,String> sub = GetExpanded(n.nodes.get(0));
				HashMap<String,String> att = GetExpanded(n.nodes.get(1));
				HashMap<String,String> mod = GetExpanded(n.nodes.get(2));

				for (String _sub : sub.keySet()) {
					
					String __sub = GetTokenInternal(_sub).GenerateSequenceForTable(false);
					
					for (String _att : att.keySet()) {
						
						String __att = GetTokenInternal(_att).GenerateSequenceForTable(false);
						
						for (String _mod : mod.keySet()) {	
							
							String __mod = GetTokenInternal(_mod).GenerateSequenceForTable(false);
							
							String s = __sub+__att+__mod+Tokenizer.c_marks.get(n.layer);
							if (!result.containsKey(s)) {
								result.put(s, s);
							}							
						}
					}
				}
				
				if (sub.size() > 1) {
					for (String _sub : sub.keySet()) {
						
						String __sub = GetTokenInternal(_sub).GenerateSequenceForTable(false);
						
						String s = __sub+n.nodes.get(1).GetName()+n.nodes.get(2).GetName()+Tokenizer.c_marks.get(n.layer);
						if (!result.containsKey(s)) {
							result.put(s, s);	
						}
					}
				}
				
				if (att.size() > 1) {
					for (String _att : att.keySet()) {
						
						String __att = GetTokenInternal(_att).GenerateSequenceForTable(false);
						
						String s = n.nodes.get(0).GetName()+__att+n.nodes.get(2).GetName()+Tokenizer.c_marks.get(n.layer);
						if (!result.containsKey(s)) {
							result.put(s, s); 
						}
					}
				}
				
				if (mod.size() > 1) {
					for (String _mod : mod.keySet()) {
						
						String __mod = GetTokenInternal(_mod).GenerateSequenceForTable(false);
						
						String s = n.nodes.get(0).GetName()+n.nodes.get(1).GetName()+__mod+Tokenizer.c_marks.get(n.layer);
						if (!result.containsKey(s)) {
							result.put(s, s);
						}
					}
				}
			}
		}	
		else {
			result.put(n.GetName(), n.GetName());
		}
		
		return result;
	}
	
	private static List<String> GermainsRemarquables(Token tokenA, Token tokenB) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (tokenA.layer != tokenB.layer) 
			return result;
		
		if (tokenA.layer < 1 || tokenB.layer < 1) 
			return result;
		
		if (tokenA.opCode.equals(Tokenizer.addition) || tokenB.opCode.equals(Tokenizer.addition)) 
			return result;
				
		if (IsGermainOppose(tokenA, tokenB)) {
			result.add(build(tokenA.GetName(), tokenB.GetName(), GermainOpposes));
		}
		
		if (IsGermainAssocies(tokenA, tokenB)) 
		{
			result.add(build(tokenA.GetName(), tokenB.GetName(), GermainAssocies));
		}
		
		if (IsGermainJumeau(tokenA, tokenB)) 
		{
			result.add(build(tokenA.GetName(), tokenB.GetName(), GermainJumeau));
		}

		if (IsGermainCroise(tokenA, tokenB)) 
		{
			result.add(build(tokenA.GetName(), tokenB.GetName(), GermainCroises));
		}
		
		return result;
	}
	
	private static boolean IsGermainOppose(Token tokenA, Token tokenB) {

		return !tokenA.nodes.get(0).GetName().equals(tokenA.nodes.get(1).GetName()) &&
		    tokenA.nodes.get(0).GetName().equals(tokenB.nodes.get(1).GetName())	&&
		    tokenA.nodes.get(1).GetName().equals(tokenB.nodes.get(0).GetName());
	}
	
	private static boolean IsGermainAssocies(Token tokenA, Token tokenB) {
		return (tokenA.nodes.get(0).GetName().equals(tokenB.nodes.get(0).GetName()) &&
			    tokenA.nodes.get(1).GetName().equals(tokenB.nodes.get(1).GetName())	&&
			    !tokenA.nodes.get(2).GetName().equals(tokenB.nodes.get(2).GetName()));
	}
	
	private static boolean IsGermainJumeau(Token tokenA, Token tokenB) {
		return tokenA.nodes.get(0).GetName().equals(tokenA.nodes.get(1).GetName()) &&
			    tokenB.nodes.get(0).GetName().equals(tokenB.nodes.get(1).GetName());
	}
	
	private static boolean IsGermainCroise(Token tokenA, Token tokenB) {
		if (tokenA.layer > 1 && tokenB.layer > 1) 
		{

			Token subA = tokenA.nodes.get(0);
			Token subB = tokenB.nodes.get(0);
			Token attA = tokenA.nodes.get(1);
			Token attB = tokenB.nodes.get(1);
			
			if (subA.opCode.equals(Tokenizer.addition) || 
				subB.opCode.equals(Tokenizer.addition) ||
				attA.opCode.equals(Tokenizer.addition) ||
				attB.opCode.equals(Tokenizer.addition)) 
			{
				return false;
			}
			else {
				if (IsGermainOppose(subA, subB) && IsGermainOppose(attA, attB)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private static HashMap<String, String> BuildFamilyRecursive(Token n) throws Exception {
		
		HashMap<String, String> result = new HashMap<String, String>();
				
		String inputName = n.GetName();
		
		if (n.layer == 0) {
			// no relations
		} else if (n.opCode.equals(Tokenizer.addition)) {
			// no relations
		} else if (n.opCode.equals(Tokenizer.multiplication)) {
			
			if (!n.IsEmpty()) {
				
				if (!n.nodes.get(0).IsEmpty()) {
					String rel1 = build(inputName, n.nodes.get(0).GetName(), DscSub);					
					if (!result.containsKey(rel1))
						result.put(rel1, rel1);
					String rel2 = build(n.nodes.get(0).GetName(), inputName, AscSub);
					if (!result.containsKey(rel2))
						result.put(rel2, rel2);					
//					HashMap<String, String> t = BuildFamilyRecursive(n.nodes.get(0));
//					for (String k : t.keySet()) {
//						if (!result.containsKey(k))
//							result.put(k, k);
//					}
				}

				if (!n.nodes.get(1).IsEmpty()) {
					String rel1 = build(inputName, n.nodes.get(1).GetName(), DscAtt);					
					if (!result.containsKey(rel1))
						result.put(rel1, rel1);
					String rel2 = build(n.nodes.get(1).GetName(), inputName, AscAtt);
					if (!result.containsKey(rel2))
						result.put(rel2, rel2);					
//					HashMap<String, String> t = BuildFamilyRecursive(n.nodes.get(1));
//					for (String k : t.keySet()) {
//						if (!result.containsKey(k))
//							result.put(k, k);
//					}
				}

				if (!n.nodes.get(2).IsEmpty()) {
					String rel1 = build(inputName, n.nodes.get(2).GetName(), DscMod);					
					if (!result.containsKey(rel1))
						result.put(rel1, rel1);
					String rel2 = build(n.nodes.get(2).GetName(), inputName, AscMod);
					if (!result.containsKey(rel2))
						result.put(rel2, rel2);					
					HashMap<String, String> t = BuildFamilyRecursive(n.nodes.get(2));
//					for (String k : t.keySet()) {
//						if (!result.containsKey(k))
//							result.put(k, k);
//					}
				}
			}

		} else {
			throw new Exception("Cannot generate family relations");
		}
		
		return result;
	}
}