package NewParser;

import java.util.ArrayList;
import java.util.Arrays;

import IEMLInterface.TermInterface;


public class Tester {
	
	public static void main(String[] args) {		
		
		ArrayList<String> list = new ArrayList<String>();
	
		list.addAll(Arrays.asList(
				"*A: + O:**",						    					//has to fail, ?
				"*S: + M:**",						    					//has to fail, ?
				"*(S:B:E:.+S:T:E:.-)+E:O:M:.-**",						    //ok
				"*(E:+S:+B:)(S:+B:+T:)E:.+M:M:E:.**",						//ok
				"*M:(S:+B:+T:)E:.+(E:+S:+B:)M:E:.**",						//has to fail, ok
				"*O:(S:+B:+T:)E:.+(S:+B:)M:E:.**",				            //has to fail, o
				"*(S:+B:+T:)(S:+B:+T:)E:.+(S:+O:)(S:+B:+T:)E:.**", 			//has to fail, ?
				"*(S:+B:+T:)(S:+B:+T:)E:.+(S:+B:+T:)(S:+B:+T:)E:.**", 		//has to fail, ok
				"*O:+B:**",													//has to fail, ok
				"*T:+i.**", 												//has to fail, ok
				"*S:B:i.**", 												//has to fail - ok
				"*h.i.g.d.S:B:T:.**",										//has to fail - ok
				"*h.S:B:T:.h.-**",											//has to pass - ok
				"*S:+S:**",													//has to fail - ok
				"*we.+we.**",												//has to fail - ok
				"*O:B:(S:+A:)B:S:**"										//has to fail - ok
				));

		list.add(ScriptExamples.StudentLearnsMathematics);
		list.add(ScriptExamples.CesJeux);
		list.add(ScriptExamples.JeSaurai);
		list.add(ScriptExamples.NeSaurontPas);
		list.add(ScriptExamples.UnCoteurraconteUneHistoire);
		list.add(ScriptExamples.EnfantDeMaVoisine);
		list.add(ScriptExamples.EnfantDeMaVoisin1);
		list.add(ScriptExamples.ieml1);
		list.add(ScriptExamples.ieml2);
		
		for (String s:TermInterface.LoadDictionary(null).keySet()){
			list.add("*"+s+"**");
		}


		ParserImpl parser = new ParserImpl();
		for (String s:list){						
			try {				
				Token n = parser.parse(s);				
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
	}
}


