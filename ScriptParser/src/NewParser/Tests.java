package NewParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tests {

	private static String GrandChildOf = "GrandChildOf";
	private static String parentRel = "ParentOf";
	private static String childRel = "ChildOf";
	private static String GermainJumeau ="GermainJumeau";
	private static String GermainOpposes ="GermainOpposes";
	private static String GermainAssocies ="GermainAssocies";
	private static String GermainCroises = "GermainCroises";
	private static String ContenuDans = "ContenuDans";
	private static String Contiens = "Contiens";
	
	// helper to create valid json
	private static String build(String start, String stop, String name) {
		String format = "{\"start\":\"%s\", \"stop\":\"%s\", \"name\":\"%s\"}";
		return String.format(format, start, stop, name);
	}
	
	//gets all permutations of input
	private static HashMap<String,String> GetExpandedRecursive(Token n) {
		
		HashMap<String,String> result = new HashMap<String,String>();
		
		if (n.opCode != null) {
			
			if (n.opCode.equals(Tokenizer.addition)) {
				for (int i = 0; i < n.nodes.size(); i++){
					HashMap<String,String> temp = GetExpandedRecursive(n.nodes.get(i));
					for (String key : temp.keySet()) {
						if (!result.containsKey(key)) {
							result.put(key, key);
						}
					}
				}
			}
			else {
				
				HashMap<String,String> sub = GetExpandedRecursive(n.nodes.get(0));
				HashMap<String,String> att = GetExpandedRecursive(n.nodes.get(1));
				HashMap<String,String> mod = GetExpandedRecursive(n.nodes.get(2));

				for (String _sub : sub.keySet()) {
					for (String _att : att.keySet()) {
						for (String _mod : mod.keySet()) {
							String s = _sub+_att+_mod+Tokenizer.c_marks.get(n.layer);
							if (!result.containsKey(s)) {
								result.put(s, s);
							}
						}
					}
				}
				
				if (sub.size() > 1) {
					for (String _sub : sub.keySet()) {
						String s = _sub+n.nodes.get(1).GetName()+n.nodes.get(2).GetName()+Tokenizer.c_marks.get(n.layer);
						if (!result.containsKey(s)) {
							result.put(s, s);
						}
					}
				}
				
				if (att.size() > 1) {
					for (String _att : att.keySet()) {
						String s = n.nodes.get(0).GetName()+_att+n.nodes.get(2).GetName()+Tokenizer.c_marks.get(n.layer);
						if (!result.containsKey(s)) {
							result.put(s, s);
						}
					}
				}
				
				if (mod.size() > 1) {
					for (String _mod : mod.keySet()) {
						String s = n.nodes.get(0).GetName()+n.nodes.get(1).GetName()+_mod+Tokenizer.c_marks.get(n.layer);
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
	
	// use calculated values to store contain/containing relations
	private static HashMap<String,String> containRelations;
		
	public static void main(String[] args) {
		
		ParserImpl parser = new ParserImpl();
		try {
			
			//String string = "T:M:.e.-M:M:.i.-E:.-+wa.e.-'";
		    
			//String string = "O:S:T:.";
			String string = "O:O:.O:.-";
			
			//String string = "M:M:.-O:M:.-E:.-+s.y.-'+M:M:.-M:O:.-E:.-+s.y.-'"; //from Pierre
			//String string = "O:M:.";                     //no germains
			//String string = "M:O:.M:M:.-+M:M:.M:O:.-";   //germains oppose
			//String string = "O:M:.O:M:.-+M:O:.M:O:.-";   //germain croises
			//String string = "O:M:.O:M:.-";                 //contenu check
			
			containRelations = new HashMap<String,String>();
			
			Token n = parser.parse(string);
			HashMap<String, String> headerResult = ScriptHeaderCalculator(n);
			HashMap<String, String> cellResult = ScriptCellCalculator(n);
			
			for (String s : GetExpandedRecursive(n).keySet()) {
				System.out.println(Tokenizer.MakeParsable(s));
			}
			
			List<String> headers = GermainsOrdinairesFromSameParadigm(new ArrayList<String>(headerResult.values()));
						
			System.out.println("HEADERS");
			for (String rel : headers) {
				System.out.println(rel);
			}
			
			List<String> cells = GermainsOrdinairesFromSameParadigm(new ArrayList<String>(cellResult.values()));
			
			System.out.println("CELLS");
			for (String rel : cells) {
				System.out.println(rel);
			}
			
			ArrayList<String> family = BuildFamily(n);
			System.out.println("FAMILY");
			for (String rel : family) {
				System.out.println(rel);
			}
			
			System.out.println("CONTAIN");
			for (String rel : containRelations.keySet()) {
				System.out.println(rel);
			}
									
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	private static List<String> GermainsOrdinairesFromSameParadigm(List<String> candidates) {
		//condition 1: belong to same paradigm is met by the assumption that the input is from the same paradigm
		//condition 2: same cardinality
		//condition 3: empty intersection
		
		ArrayList<String> result = new ArrayList<String>();
		
		HashMap<Integer, HashMap<Token, Set<String>>> map = new HashMap<Integer, HashMap<Token, Set<String>>>();
		
		ParserImpl parser = new ParserImpl();
		
		for (String s : candidates) {
			try {				
				Token token = parser.parse(s);
				
				ArrayList<String> components = Token.genVariables(token, true);
				
				int size = components.size();
				
				if (!map.containsKey(size)) {
					
					HashMap<Token, Set<String>> h = new HashMap<Token, Set<String>>();
					map.put(size, h);
				}

				// add terms that generate sets of same cardinality
				Set<String> foo = new HashSet<String>(components);
				map.get(size).put(token, foo);

		    } catch (Exception e) {
				e.printStackTrace();		
		    } finally {
		    	parser.Reset();
		    }
        } 
			
		for (Integer integer : map.keySet()) {
			
			ArrayList<Token> tokenRef = new ArrayList<Token>();
			ArrayList<Set<String>> setRef = new ArrayList<Set<String>>();
			
			HashMap<Token, Set<String>> m = map.get(integer);
			
			//Set<String> intersection = new HashSet<String>(s1); // use the copy constructor
			//intersection.retainAll(s2);
//			Retains only the elements in this set that are contained in the specified collection 
//			(optional operation). In other words, removes from this set all of its elements that 
//			are not contained in the specified collection. If the specified collection is also a 
//			set, this operation effectively modifies this set so that its value is the intersection 
//			of the two sets.
			
			for (Token t : m.keySet()) {
				tokenRef.add(t);
				setRef.add(m.get(t));
			}
			
			for (int i = 0; i < setRef.size(); i++ ) {
				for (int j = i + 1; j < setRef.size(); j++) {
					Set<String> intersection = new HashSet<String>(setRef.get(i));
					intersection.retainAll(setRef.get(j));
					
					if (intersection.size() == 0) {
						//System.out.println(tokenRef.get(i).GetName() + " germain of " + tokenRef.get(j).GetName());
						
						List<String> l = GermainsRemarquables(tokenRef.get(i), tokenRef.get(j));
						result.addAll(l);
					}
					else {
						//System.out.println(tokenRef.get(i).GetName() + " NOT germain of " + tokenRef.get(j).GetName());
					}
				}
			}
		}
		
		return result;
	}
	
	//Input token may be multi-table
	private static HashMap<String, String> ScriptHeaderCalculator(Token n) throws Exception {
		
		HashMap<String, String> result = new HashMap<String, String>();
		
		if (n.opCode.equals(Tokenizer.addition)) {
			
			for (Token child : n.nodes){
				
				if (!result.containsKey(child.GetName())) {
					result.put(child.GetName(), child.GetName());
				}
				
				Set<String> childSet = TableHeaderCalculator(child).keySet();
				
				if (childSet.size() > 0) {
					//additive
					//table --> included in
					//table --> including
					String c1 = build(n.GetName(), child.GetName(), Contiens);
					if (!containRelations.containsKey(c1)) {
						containRelations.put(c1,  c1);
					}
					String c2 = build(child.GetName(), n.GetName(), ContenuDans);
					if (!containRelations.containsKey(c2)) {
						containRelations.put(c2,  c2);
					}
				}
				
				for (String s : childSet) {
					if (!result.containsKey(s)) {
						result.put(s, s);
					}
				}
			}
		}
		else {
			return TableHeaderCalculator(n);
		}
		
		return result;
	}
	
	private static HashMap<String, String> ScriptCellCalculator(Token n) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		
		ArrayList<String> components = Token.genVariables(n, true);
		
		for (String component : components){
			
			if (!result.containsKey(component)) {
				result.put(component, component);
			}
		}
		
		return result;
	}
	
	//Input token must be one table
	//
	//not table --> included in
	//additive | multiplicative
	//table --> included in
	//table --> including
	//
	private static HashMap<String, String> TableHeaderCalculator(Token n) throws Exception {
		
		if (n == null) {
			throw new Exception("cannot compute headers for null input");
		}
		
		if (n.opCode.equals(Tokenizer.addition)) {
			throw new Exception("cannot compute headers for multi-table");
		}
		
		HashMap<String, String> result = new HashMap<String, String>();
		HashMap<String, String> duplicates = new HashMap<String, String>();

		if (n.layer < 1) {
			return result;
		}
				
		duplicates = GetExpandedRecursive(n);
		
		// now check if those results are really headers
		ParserImpl internal_parser = new ParserImpl();
		
		for (String dup : duplicates.keySet()) {
			try {		
				String s = Tokenizer.MakeParsable(dup);
				Token s_n = internal_parser.parse(s);
				
				ArrayList<String> internal_components = Token.genVariables(s_n, true);
				if (internal_components.size() > 1) {
										
					// it is a header
					if (!result.containsKey(s)) {
						result.put(s, s);
						
						//multiplicative
						//table --> included in
						//table --> including
						String c1 = build(n.GetName(), s_n.GetName(), Contiens);
						if (!containRelations.containsKey(c1)) {
							containRelations.put(c1,  c1);
						}
						String c2 = build(s_n.GetName(), n.GetName(), ContenuDans);
						if (!containRelations.containsKey(c2)) {
							containRelations.put(c2,  c2);
						}

					}

					for (String ss : ScriptHeaderCalculator(s_n).keySet()) {
						if (!result.containsKey(ss)) {
							result.put(ss, ss);
						}
					}
				}

		    } catch (Exception e) {
				e.printStackTrace();		
		    } finally {
		    	internal_parser.Reset();
		    }
        } 

		return result;
	}

	// relations etymologiques
	private static ArrayList<String> BuildFamily(Token n) throws Exception {
		
		ArrayList<String> result = new ArrayList<String>();
				
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
					result.addAll(BuildGrandpa(n.nodes.get(0)));
				}

				if (!n.nodes.get(1).IsEmpty()) {
					result.add(build(inputName, n.nodes.get(1).GetName(), childRel)); 
					result.add(build(n.nodes.get(1).GetName(), inputName, parentRel));
					result.addAll(BuildGrandpa(n.nodes.get(1)));
				}

				if (!n.nodes.get(2).IsEmpty()) {
					result.add(build(inputName, n.nodes.get(2).GetName(), childRel)); 	
					result.add(build(n.nodes.get(2).GetName(), inputName, parentRel));
					result.addAll(BuildGrandpa(n.nodes.get(2)));
				}
			}

		} else {
			throw new Exception("Cannot generate family relations");
		}
		
		return result;
	}
		
	// relations etymologiques: special case for family relations
	private static ArrayList<String> BuildGrandpa(Token n) throws Exception {
		
		ArrayList<String> result = new ArrayList<String>();
				
		String inputName = n.parent.GetName();
		
		if (n.layer == 0) {
			// no relations
		} else if (n.opCode.equals(Tokenizer.addition)) {
			// no relations
		} else if (n.opCode.equals(Tokenizer.multiplication)) {
			
			if (!n.IsEmpty()) {  //GrandChildOf
				
				if (!n.nodes.get(0).IsEmpty()) {
					
					if (n.nodes.get(1).IsEmpty() && n.nodes.get(2).IsEmpty()) {
						
					}
					
					result.add(build(inputName, n.nodes.get(0).GetName(), GrandChildOf)); 
				}
			}

		} else {
			throw new Exception("Cannot generate GrandChildOf relations");
		}
		
		return result;
	}
}
