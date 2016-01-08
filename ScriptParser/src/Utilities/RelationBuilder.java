package Utilities;

import java.util.ArrayList;
import java.util.Arrays;
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
			
		/*
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
		*/
		
		//String input = "O:M:.-O:M:.-we.h.-'";
		//String input = "O:M:.M:M:.- + S:.O:M:.M:M:.- + M:M:.O:M:.- ";
		
		//String input = "S:O:."; // no jumeau
		//String input = "O:O:M:."; // jumeau
		//String input = "E:O:."; // jumeau
		
		//String input = "O:O:M:.";
		//String input = "O:M:O:.";
		//String input = "M:O:O:.";
		//String input = "M:M:M:.";
		//String input = "T:O:M:. + M:O:T:.";
		//String input = "O:M:.M:M:.- + M:M:.O:M:.-";
		//String input = "M:M:.a.-M:M:.a.-E:.-+f.o.-'";
		String input = "M:O:.M:M:.-+M:M:.M:O:.-";
		
		
		
		
		
		try {
			String output = RelationBuilder.GetRelations(input);
			System.out.println(output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String GrandChildOf = "GrandChildOf";
	public static String parentRel = "ParentOf";
	public static String childRel = "ChildOf";
	public static String GermainJumeau ="GermainJumeau";
	public static String GermainOpposes ="GermainOpposes";
	public static String GermainAssocies ="GermainAssocies";
	public static String GermainCroises = "GermainCroises";
	
	// main entry point
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
	
	// calls all the methods responsible for building relationa,
	// and creates a valid json document with all relations
	public static String BuildRelations(Token n) throws Exception{
				
		ArrayList<String> result = new ArrayList<String>();
		
		result.addAll(BuildFamily(n));    // ethymologiques
		result.addAll(BuildGermains(n));  // etymologiques - contd
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
	
	// relations etymologiques - contd
	private static ArrayList<String> BuildGermains(Token n) {
		
		ArrayList<String> result = new ArrayList<String>();
		String inputName = n.GetName();

		try {
			
			TableGenerator tGen = new TableGenerator();
			JsonTables json = tGen.genJSONTables(n);
						
			result.addAll(BuildGermainsAssocies(json));
			
			if (json.tables.size() == 1) {				
				// In case JUMEAU:
				// there must be a table
				// table must be 2D or 3D
				// otherwise there will be no diagonal	
				int rows = json.tables.get(0).materialRow;
				int cols = json.tables.get(0).materialCol;
				if (rows > 1 && cols > 1) {
					// get diagonal only if at least two semes are same
					// return value has a list of which semes (their position) are same
					List<List<Integer>> res = jumeauSemes(n);
					int resSize = res.size();
					for (List<Integer> i : res) {
						
						int posA = i.get(0);
						int posB = i.get(1);
						
						// check number of slices, this will determine where to get the "diagonal"
						int slices = json.tables.get(0).slices.size();
						
						if (slices == 1) {
							for (JsonSliceEntry entry : json.tables.get(0).slices.get(0).getCells()) {
								if (entry.positionX == entry.positionY && entry.positionX > 0) {
									result.add(build(inputName, entry.value, GermainJumeau));
								}								 
							}
						}
						else {
							// with many slices "diagonal" might be a row, depending which semes are same
							if (resSize == 1) {
								if (posA == 0 && posB == 1){ //O:O:M:.
									for (JsonSlice allSlices : json.tables.get(0).slices) {
										for (JsonSliceEntry entry : allSlices.getCells()) {
											if (entry.positionX == entry.positionY && entry.positionX > 0) {
												result.add(build(inputName, entry.value, GermainJumeau));
											}								 
										}
									}
								} 
								else if (posA == 0 && posB == 2) { //O:M:O:.
									for (int sliceCount = 0; sliceCount < json.tables.get(0).slices.size(); sliceCount++) {
										for (JsonSliceEntry entry : json.tables.get(0).slices.get(sliceCount).getCells()) {
											if (entry.positionX == sliceCount + 1 ) {
												result.add(build(inputName, entry.value, GermainJumeau));
											}								 
										}
									}
								} else if (posA == 1 && posB == 2) { //M:O:O:.
									for (int sliceCount = 0; sliceCount < json.tables.get(0).slices.size(); sliceCount++) {
										for (JsonSliceEntry entry : json.tables.get(0).slices.get(sliceCount).getCells()) {
											if (entry.positionY == sliceCount + 1 ) {
												result.add(build(inputName, entry.value, GermainJumeau));
											}								 
										}
									}
								}
								else {
									throw new Exception("error");
								}
							}
							else { //"M:M:M:.";
								for (int sliceCount = 0; sliceCount < json.tables.get(0).slices.size(); sliceCount++) {
									for (JsonSliceEntry entry : json.tables.get(0).slices.get(sliceCount).getCells()) {
										if (entry.positionX == sliceCount + 1 && entry.positionY == sliceCount + 1) {
											result.add(build(inputName, entry.value, GermainJumeau));
										}								 
									}
								}
							}
						}
						
						break; // just one will do, since all is already specified in the json table
					}						
				}
			}				
			
			result.addAll(BuildGermainsOpposes(json, n));
							
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return result;
	}
	
	private static ArrayList<String> BuildGermainsOpposes(JsonTables json, Token n) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (json.tables.size() > 1) { //"T:O:M:. + M:O:T:.";
			// In case OPPOSES:
			
			for (int i = 0; i < n.nodes.size(); i++) {
				for (int j = i+1; j < n.nodes.size(); j++) {
					
					ArrayList<List<Integer>> swaps = swapSemes(n.nodes.get(i), n.nodes.get(j));
					
					for (List<Integer> positions : swaps) {
						
						result.add(build(n.nodes.get(i).GetName(), n.nodes.get(j).GetName(), GermainOpposes));
						result.add(build(n.nodes.get(j).GetName(), n.nodes.get(i).GetName(), GermainOpposes));						
						
						break;  // skip case of two semes being same/swapped
					}
					
					result.addAll(BuildGermainsCroises(n.nodes.get(i), n.nodes.get(j)));
				}
			}
		}
		
		return result;
	}
	
	private static ArrayList<String> BuildGermainsCroises(Token a, Token b) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (a == null || b == null)
			return result;
		if (a.nodes.size() == 0 || b.nodes.size() == 0)
			return result;
		if (a.opCode.equals(Tokenizer.addition)) 
			return result;
		if (b.opCode.equals(Tokenizer.addition)) 
			return result;
		if (a.layer < 1) 
			return result;
		if (b.layer < 1) 
			return result;
		
		ArrayList<List<Integer>> swaps = swapSemes(a.nodes.get(0), b.nodes.get(0));
		
		if (swaps.size() == 0)
			return result;
		
		swaps = swapSemes(a.nodes.get(1), b.nodes.get(1));
		
		if (swaps.size() == 0)
			return result;
		
		result.add(build(a.GetName(), b.GetName(), GermainCroises));
		result.add(build(b.GetName(), a.GetName(), GermainCroises));
		
		return result;
	}
	
	private static ArrayList<String> BuildGermainsAssocies(JsonTables json) {
		
		ArrayList<String> result = new ArrayList<String>();
				
		for (JsonTable table : json.tables) {
			
			int depth = table.slices.size();
			
			if (depth > 1) { //three variable semes
				
				for (int k = 0; k < table.slices.get(0).getCells().size(); k++){ // all cells
					
					// between slices
					for (int i = 0; i < depth; i++) {
						for (int j = i + 1; j < depth; j++){
							
							String A = table.slices.get(i).getCells().get(k).value;
							String B = table.slices.get(j).getCells().get(k).value;

							if (A != null && !A.isEmpty() && B != null && !B.isEmpty()) {
								result.add(build(A, B, GermainAssocies));
								result.add(build(B, A, GermainAssocies));
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	// relations taxonomiques - not done yet
	private static ArrayList<String> BuildTaxonomy(Token n) {
		
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
	
	// Return list of sequence of int. Calculates where semes are the same
	private static ArrayList<List<Integer>> jumeauSemes(Token a) {
		
		ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < 3; i++) {
			for (int j = i+1; j < 3; j++) {
				if (a.nodes.get(i).GetName().equals(a.nodes.get(j).GetName())) {
					result.add(Arrays.asList(i, j));
				}
			}
		}
		
		return result;
	}
	
	// Return list of sequence of int. Sequence of ints represents position where semes are same, position of seme in first term
	// that is same to the position of seme in second term
	private static ArrayList<List<Integer>> swapSemes(Token a, Token b) {
		
		ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
		
		if (a == null || b == null)
			return result;
		if (a.nodes.size() == 0 || b.nodes.size() == 0)
			return result;		
		if (a.opCode.equals(Tokenizer.addition)) 
			return result;
		if (b.opCode.equals(Tokenizer.addition)) 
			return result;
		if (a.layer < 1) 
			return result;
		if (b.layer < 1) 
			return result;
		
		for (int i = 0; i < 3; i++) {
			if (a.nodes.get(i).GetName().equals(b.nodes.get(i).GetName())) {
				
				int swapA = (i+1) % 3; //1,2,0,1,...
				int swapB = (i+2) % 3; //2,0,1,2,... 
				
				if (a.nodes.get(swapA).GetName().equals(b.nodes.get(swapB).GetName())) {
					result.add(Arrays.asList(i, swapA, swapB));
				}
				if (a.nodes.get(swapB).GetName().equals(b.nodes.get(swapA).GetName())) {
					result.add(Arrays.asList(i, swapB, swapA));
				}
			}
		}
		
		return result;
	}
	
	// helper to create valid json
	private static String build(String start, String stop, String name) {
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


