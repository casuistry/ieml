package Tests;

import IEMLInterface.ScriptExamples;
import ScriptGenerator.BaseIEMLProvider;
import ScriptGenerator.EmptyIEMLProvider;
import ScriptGenerator.GeneratorConfigurator;


public class Tester {
	
	public static void main(String[] args) {
		
		//BaseTester baseTester = new BaseTester();
		//baseTester.RunTest(null, 6, new BaseIEMLProvider(), null, null);
		
		ParsingTester tester = new ParsingTester();
		tester.RunTest("*S.**", 5, new BaseIEMLProvider(), null, null);			
		//ScriptExamples.StudentLearnsMathematics
		
		//Generate an empty sequence of specified layer
		//NullTester tester = new NullTester();
		//tester.RunTest(null, 3, new EmptyIEMLProvider(), null, null);
		
		//ParsingTester parsingTester = new ParsingTester();
		//parsingTester.RunTest("*S:S:T:.(F:(S:+M:)B:.+M:U:E:.+(T:+S:+A:)(A:+O:+S:)(M:+I:+A:+B:).+(U:+O:+A:+S:)U:(F:+I:+M:).)B:M:F:.-**", 2, new BaseIEMLProvider(), null, null);
		
		//EmptyNodeTester emptyNodeTester = new EmptyNodeTester();
		//emptyNodeTester.RunTest("*S:B:T:.E:E:E:.S:B:T:.-**", 3, new BaseIEMLProvider(), null, null);
	}
}


/*
 
 	private static void niveauTest(String big, int lb){
		try {			
			String iemlSequence = big == null ? Generator.GetScript(lb) : big;				
			//parsing does not care about '*', '(' and ')', but the names of the nodes will include those characters
			//which will be confusing when printing out a readable name.
			String bigString = iemlSequence.replaceAll("[()*]", ""); 
			Node bigRoot = TopDownParser.Parse(bigString);	
			
			HashMap<String, ArrayList<Node>> result = Node.GetNiveauSequence(null, bigRoot, null);
					
			for (String seq : result.keySet()){		
				System.out.println("Niveau = " + seq);
				for (Node n : result.get(seq)){
					System.out.println("  Term = " + inspector.Inspect(n));
				}
			}
			
			System.out.println("Number of terms: " + result.size());
			
			for (String seq : result.keySet()){		
				System.out.println("Niveau = " + seq);
			}
			
			System.out.println();				
			System.out.println(iemlSequence);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private static void termsTest(String big, int lb){
		
		try {
							
			String iemlSequence = big == null ? Generator.GetScript(lb) : big;				
			//parsing does not care about '*', '(' and ')', but the names of the nodes will include those characters
			//which will be confusing when printing out a readable name.
			String bigString = iemlSequence.replaceAll("[()*]", ""); 
			Node bigRoot = TopDownParser.Parse(bigString);	
	
			int max = 0;
			HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
			HashMap<Node, String> parentOfTerms = new HashMap<Node, String>();
			
			List<Node> result = Node.GetTerms(bigRoot);				
			System.out.println("Number of terms: " + result.size());
					
			if (result.size() == 0){
				System.out.print("Term = " + inspector.Inspect(bigRoot));
				System.out.println(" Niveau = 0");
			}
						
			//for each term
			for (Node n : result){
				
				Node parent = n.GetParent();
				if (parent == null)
					continue;
				
				System.out.print("Term = " + inspector.Inspect(n));
							
				if (parentOfTerms.containsKey(parent)){
					//children of the same parent will have the same niveau sequence
					System.out.println(" Niveau = " + parentOfTerms.get(parent));
					continue;
				}
				
				String niveauSequence = Node.GetNiveau(n);
				int l = niveauSequence.length();				
				if (max < l)
					max = l;
				
				//store niveau sequence per sequence length
				if (!map.containsKey(l)){
					ArrayList<String> v = new ArrayList<String>();
					v.add(niveauSequence);
					map.put(l, v);
				}
				else 
					map.get(l).add(niveauSequence);
				
				parentOfTerms.put(parent, niveauSequence);
				
				System.out.println(" Niveau = " + niveauSequence);
			}
				
			ArrayList<String> diffs = new ArrayList<String>();
			
			System.out.println();
			//for all niveau sequence lengths
			for (Integer i : map.keySet()){
				String comp = null;				
				System.out.print("\tNiveau found = " + i);
				boolean first = true;
				
				for (String s : map.get(i)){
					
					if (first) {
						first = false;
						System.out.print(" [" + s + "] ");
					}						
					
					if (i==max){						
						if (comp==null){
							comp = s;
						    diffs.add(s);
						}
						else if (!comp.equals(s)) {
							diffs.add(s);
						}
					}					
				}

				System.out.println();
			}
			
			if (diffs.size() > 1)
			{
				System.out.println();
				System.out.println("\tHighest niveaux:");
				for (String s : diffs){
					System.out.print("\t" + "[" + s + "] ");
				}

			}

			
			System.out.println();				
			System.out.println(iemlSequence);
			System.out.println();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Returns nodes at lower layer
	private static void getNodesAtLayerTest(String big, int lb){
		String bigString;
    	
		try {
						
			bigString = big == null ? Generator.GetScript(lb).replaceAll("[()*]", "") : big; 
			Node bigRoot = TopDownParser.Parse(bigString);
			int layer = big == null ? bigRoot.GetLayerInt()-1 : lb;
					
			ArrayList<Node> result = new ArrayList<Node>();
			Node.GetNodesAtLayer(bigRoot, layer, result);
				
			System.out.println();
			System.out.println(result.size());
			System.out.println();
			
			for(Node n : result){
				System.out.println(inspector.Inspect(n));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void minDifferenceTest(String big, int lb, String small, int ls){
		
		String bigString, smallString;
    		
		try {
			
			bigString = big == null ? Generator.GetScript(lb).replaceAll("[()*]", "") : big;
			Node bigRoot = TopDownParser.Parse(bigString);
			//bigRoot.PrintNode("", null);
			System.out.println("------------------");
			
			smallString = small == null ? Generator.GetScript(ls).replaceAll("[()*]", "") : small;
			Node smallRoot = TopDownParser.Parse(smallString);
			//smallRoot.PrintNode("", null);
			System.out.println("------------------");				         

			Counter diff = Node.GetNumberOfDifferences(bigRoot, smallRoot);	
			System.out.println("Found " + diff.min + " difference(s) and " + diff.occurances + " repetition(s)");	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Returns an integer indicating how many changes are required
	//to make 'big' script contain 'small' script. If 0, the 'big'
	//already contains 'small'.
	private static void minDiffTest(String big, int lb, String small, int ls){
		
		String bigString, smallString;
    		
		try {
			
			bigString = big == null ? Generator.GetScript(lb).replaceAll("[()*]", "") : big;
			Node bigRoot = TopDownParser.Parse(bigString);
			//bigRoot.PrintNode("", null);
			System.out.println("------------------");
			
			smallString = small == null ? Generator.GetScript(ls).replaceAll("[()*]", "") : small;
			Node smallRoot = TopDownParser.Parse(smallString);
			//smallRoot.PrintNode("", null);
			System.out.println("------------------");				         

			ArrayList<Node> result = new ArrayList<Node>();
			Node.GetNodesAtLayer(bigRoot, smallRoot.GetLayerInt(), result);
			
			String[] f = Utilities.getChunks(smallString); 
			
			PriorityQueue<CompMatch> queue = null;
			
			for(Node n : result){		

				//parser leaves initial "+"
				String temp = n.GetName().startsWith("+") ? n.GetName().substring(1) : n.GetName();
				String temp_s = smallRoot.GetName().startsWith("+") ? smallRoot.GetName().substring(1) : smallRoot.GetName();
				
				String[] b = Utilities.getChunks(temp);    		        
				
				List<int[]> index = Utilities.convIndex(b.length, f.length, false);                        
				queue = CompMatch.compArray(index, b, f, null);	  
				
				
	            //while (queue.size() != 0)
	            //{
	                CompMatch h = queue.remove();
	                System.out.print("Requires " + (f.length - h.k) + " changes");
	                System.out.println("\t" + Arrays.toString(h.v) + " " + h.k + "\t\t" + temp_s + "\t" +  temp);	                
	            //}
				

			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	   
    private static void IsMinDifferenceTest(String big, String small){
    	    					
		String bigString, smallString;
    	
		try {
			
			bigString = big == null ? Generator.GetScript(2).replaceAll("[()*]", "") : big;
			Node bigRoot = TopDownParser.Parse(bigString);
			//bigRoot.PrintNode("", null);
			System.out.println("------------------");
			
			smallString = small == null ? Generator.GetScript(1).replaceAll("[()*]", "") : small;
			Node smallRoot = TopDownParser.Parse(smallString);
			//smallRoot.PrintNode("", null);
			System.out.println("------------------");				         

	        String[] f = Utilities.getChunks(smallString);        
	        
			List<String> components = bigRoot.Reconstruct(smallRoot.GetLayerInt());
			
			for (String s : components){
				
				System.out.println("computing for " + s);
				
				String[] b = Utilities.getChunks(s); 
	            
				List<int[]> index = Utilities.convIndex(b.length, f.length, false);                        
	            PriorityQueue<CompMatch> q = CompMatch.compArray(index, b, f, null);
	            
	            while (q.size() != 0)
	            {
	                CompMatch h = q.remove();
	                System.out.println(Arrays.toString(h.v) + " " + h.k);
	            }
	            
				//System.out.println(s.equals(smallRoot.GetName()) + "\t" + smallRoot.GetName() + "\t" + s);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	private static void IsInsideTest(){
		
		String bigString, smallString;
		try {
			
			bigString = Generator.GetScript(2).replaceAll("[()*]", "");
			Node bigRoot = TopDownParser.Parse(bigString);
			//bigRoot.PrintNode("", null);
			System.out.println("------------------");
			
			smallString = Generator.GetScript(1).replaceAll("[()*]", "");
			Node smallRoot = TopDownParser.Parse(smallString);
			//smallRoot.PrintNode("", null);
			System.out.println("------------------");
			
			ArrayList<Node> result = new ArrayList<Node>();
			Node.GetNodesAtLayer(bigRoot, smallRoot.GetLayerInt(), result);
			
			for(Node n : result){		
				String reconstructed = n.Reconstruct();
				System.out.println(reconstructed.equals(smallRoot.GetName()) + "\t" + smallRoot.GetName() + "\t" + reconstructed + " " +"["+n.GetName()+"]");
			}
			
			System.out.println("------------------");
			
			List<String> components = bigRoot.Reconstruct(smallRoot.GetLayerInt());
			for (String s : components){
				System.out.println(s.equals(smallRoot.GetName()) + "\t" + smallRoot.GetName() + "\t" + s);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
 
 */
