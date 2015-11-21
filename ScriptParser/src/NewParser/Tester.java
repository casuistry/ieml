package NewParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Tester {
	
	public static void main(String[] args) {		
		
		List<String> list = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
			
		//ArrayList<String> list = new ArrayList<String>();
		
		list.addAll(Arrays.asList(
				"E:+F:",												//has to fail - OK
				"U:S:.E:.U:S:.-", 										//has to fail - OK
				"U:S:.U:S:.E:.-", 										//has to fail - OK
				"(k.o.-k.o.-E:.-') + (E:.y.M:.-')",						//has to fail - OK
				"(k.o.-k.o.-E:E:E:.-') + (E:.y.M:.-')",					//has to fail - OK
				"y.wo.E:.-",						    				//has to fail - OK
				"U:S:E:.",						    					//has to fail - OK
				"M: + M:",						    					//has to fail - OK
				"M: + O:",						    					//has to fail - OK
				"O: + M:",						    					//has to fail - OK
				"M:.+O:.",												//has to fail - OK
				"F: + O:",						    					//has to fail - OK
				"A: + O:",						    					//has to fail - OK
				"S: + M:",						    					//has to fail - OK
				"E:+A:+T:",												//has to pass - OK
				"(S:B:E:.+S:T:E:.-)+E:O:M:.-",						    //has to fail - OK
				"(E:+S:+B:)(S:+B:+T:)E:.+M:M:E:.",						//has to fail - OK
				"M:(S:+B:+T:)E:.+(E:+S:+B:)M:E:.",						//has to fail - OK
				"O:(S:+B:+T:)E:.+(S:+B:)M:E:.",				            //has to fail - OK
				"(S:+B:+T:)(S:+B:+T:)E:.+(S:+O:)(S:+B:+T:)E:.", 		//has to fail - OK
				"(S:+B:+T:)(S:+B:+T:)E:.+(S:+B:+T:)(S:+B:+T:)E:.", 		//has to fail - OK
				"O:+B:",												//has to fail - OK
				"T:+i.", 												//has to fail - OK
				"S:B:i.", 												//has to fail - OK
				"h.i.g.d.S:B:T:.",										//has to fail - OK
				"h.S:B:T:.h.-",											//has to pass - OK
				"S:+S:",												//has to fail - OK
				"we.+we.",												//has to fail - OK
				"O:B:(S:+A:)B:S:",										//has to fail - OK
				"E:O:+M:.wo.-",
				"S:A:+T:."
				));

		//list.add(ScriptExamples.StudentLearnsMathematics);
		//list.add(ScriptExamples.CesJeux);
		//list.add(ScriptExamples.JeSaurai);
		//list.add(ScriptExamples.NeSaurontPas);
		//list.add(ScriptExamples.UnCoteurraconteUneHistoire);
		//list.add(ScriptExamples.EnfantDeMaVoisine);
		//list.add(ScriptExamples.EnfantDeMaVoisin1);
		//list.add(ScriptExamples.ieml1);
		//list.add(ScriptExamples.ieml2);

		
		//Utilities.Helper.ProcessDictionaryV2(null);
		//Utilities.Helper.nextCheck();
		
		
		ParserImpl parser = new ParserImpl();
		for (String s:list){
			
			String[] parts = s.split(",");
			
			//if (parts.length != 6) {
			//	System.out.println("missing: " + s);
			//	continue;
			//}
			
			String ieml =  parts[0].trim().length() > 0 ? parts[0].trim():null; 
			//String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
			//String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
			//String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
			//String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
			//String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
			
			try {				
				Token n = parser.parse(ieml);				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(s);
				StringBuilder builder = new StringBuilder();
				for (int i = 0 ; i < parser.GetCounter(); i++)
					builder.append(" ");
				builder.append("^");
				System.out.println(builder.toString());
			}		
			finally {
				parser.Reset();
			}
		}
		System.out.println("Done.");
	}
}


