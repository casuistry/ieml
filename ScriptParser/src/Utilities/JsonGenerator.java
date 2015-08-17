package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import NewParser.ParserImpl;
import NewParser.ScriptExamples;
import NewParser.Token;
import NewParser.Tokenizer;

public class JsonGenerator {

	public static void main(String[] args) {
		JsonGenerator json = new JsonGenerator();
		//json.verify();
		//json.generate_paradigms();
		json.generate();
		//json.testGrammaticalClassCalculation();
	}

	public void testGrammaticalClassCalculation() {
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(ScriptExamples.StudentLearnsMathematics);
		list.add(ScriptExamples.CesJeux);
		//list.add(ScriptExamples.JeSaurai);
		//list.add(ScriptExamples.NeSaurontPas);
		list.add(ScriptExamples.UnCoteurraconteUneHistoire);
		//list.add(ScriptExamples.EnfantDeMaVoisine);
		list.add(ScriptExamples.EnfantDeMaVoisin1);
		list.add(ScriptExamples.ieml1);
		list.add(ScriptExamples.ieml2);
		
		ParserImpl parser = new ParserImpl();
		for (String s:list){						
			try {				
				Token n = parser.parse(s);
				n.GetTokenClass();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(s);
			}		
			finally {
				parser.Reset();
			}
		}
		
	}
	
	public void verify() {
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db.csv");
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (String s : db) {
			
			String[] parts = s.split(",");
			
			if (parts.length != 3) {
				System.out.println("verify file check: " + s);
				continue;
			}
			
			String ieml = parts[0].trim();
			
			map.put(ieml, parts[1] + "," + parts[2]);			
		}
		
		List<String> layers = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\layer2.txt");
		
		for (String s : layers) {
			String[] parts = s.split("\\s+");			
			String ieml = parts[0].trim().length() > 0 ? parts[0].trim():null;
			if (ieml == null) {
				System.out.println("null layer: " + s);
				continue;
			}
			
			if (!map.containsKey(ieml)) {
				
				if (!map.containsKey(ieml + "'")){
					System.out.println("layer check: " + s);
				}
				else {
					map.put(ieml, map.remove(ieml + "'"));
				}
			}
		}
		
		try {
			
			BufferedWriter para_clean = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db.csv"));
			
			for (String s : map.keySet()){
				para_clean.write(s + " ," + map.get(s) + "\n" );
			}
			
			para_clean.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// create ieml.db{xx}.csv file from base db file
	public void add_paradigms() {
		
		ParserImpl parser = new ParserImpl();
		
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, Integer> p_map = new HashMap<String, Integer>();
		
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db.csv");
		List<String> paradigms = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\paradigms_clean.txt");
		
		for (String p : paradigms) {
			p_map.put(p, 0);			
		}
		
		try {
			
			//BufferedWriter para_clean = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db2.csv"));
			BufferedWriter para_clean = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv"));
			BufferedWriter first = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\firstToken.csv"));
			
			for (String s : db) {
				
				String[] parts = s.split(",");
								
				try {				
					Token n = parser.parse(parts[0].trim());							
					int ieml_class = n.GetTokenClass();
					
					if (p_map.containsKey(parts[0].trim())){
						p_map.put(parts[0].trim(), p_map.get(parts[0].trim()) + 1);
						para_clean.write(s + " , " + "1" + " , " + String.valueOf(n.layer) +  " , " + String.valueOf(ieml_class) + "\n");
					}
					else {
						para_clean.write(s + " , " + "0" + " , " + String.valueOf(n.layer) +  " , " + String.valueOf(ieml_class) + "\n");
					}
					
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
			
			para_clean.close();
			first.close();
			
			for (String s : p_map.keySet()) {
				if (p_map.get(s) == 0)
					System.out.println("add_paradigms, mising: " + s);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generate_paradigms(){
		
		HashMap<String, String> duplicates = new HashMap<String, String>();
		
		List<String> paradigms = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\paradigms_dump.txt");
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db.csv");
		
		ParserImpl parser = new ParserImpl();
		
		try {
			
			BufferedWriter para_clean = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\paradigms_clean.txt"));
			
			for (String s : paradigms) {
				String[] parts = s.split("\\s+");
				
				String ieml = parts[0].trim().length() > 0 ? parts[0].trim():null;
				
				if (ieml == null) {
					System.out.println("null paradigm: " + s);
					continue;
				}
				
				if (!duplicates.containsKey(ieml)) {
					duplicates.put(ieml, ieml);
				}
				else {
					System.out.println("duplicate paradigm: " + s);
					continue;
				}
				
				try {				
					Token n = parser.parse(ieml);					
					para_clean.write(ieml+"\n" );
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println(ieml);
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
			
			para_clean.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// generate json file using ieml.db{xx}.csv file
	public void generate(){
						
        add_paradigms();

		HashMap<String, String> duplicates = new HashMap<String, String>();
		HashMap<String, String> duplicates_fr = new HashMap<String, String>();
		HashMap<String, String> duplicates_en = new HashMap<String, String>();
		//List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db2.csv");
		List<String> db = Utilities.Helper.ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db3.csv");
		
		try {
			BufferedWriter json = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.json"));			
			BufferedWriter fren = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\fren.json"));		
			BufferedWriter engl = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\engl.json"));
			
			ParserImpl parser = new ParserImpl();
			
			for (String s : db) {
				String[] parts = s.split(",");
				
				if (parts.length != 6) {
					System.out.println("missing: " + s);
					continue;
				}
				
				String ieml = parts[0].trim().length() > 0 ? parts[0].trim():null;
				String fr   = parts[1].trim().length() > 0 ? parts[1].trim():null;
				String en   = parts[2].trim().length() > 0 ? parts[2].trim():null;
				String pa   = parts[3].trim().length() > 0 ? parts[3].trim():null;
				String la   = parts[4].trim().length() > 0 ? parts[4].trim():null;
				String cl   = parts[5].trim().length() > 0 ? parts[5].trim():null;
				
				if (ieml == null || fr == null || en == null) {
					System.out.println("null: " + s);
					continue;
				}
				
				if (!duplicates.containsKey(ieml)) {
					duplicates.put(ieml, ieml);
				}
				else {
					System.out.println("duplicate ieml: " + s);
					continue;
				}
				
				if (!duplicates_fr.containsKey(fr)) {
					duplicates_fr.put(fr, fr);
				}
				else {
					System.out.println("duplicate fr: " + s);
					continue;
				}
				
				if (!duplicates_en.containsKey(en)) {
					duplicates_en.put(en, en);
				}
				else {
					System.out.println("duplicate en: " + s);
					continue;
				}
				
				try {				
					Token n = parser.parse(ieml);	
					
					//create JSON terms: 
					//{ IEML: "S:B:T:.", FR:"qqchose", EN:"something", PARADIGM: "", LAYER: "", CLASS: ""  }
					
					//String j = String.format("{ieml:\"%s\",terms:[{lang:\"FR\",means:\"%s\"},{lang:\"EN\",means:\"%s\"}],paradigm:\"%s\",layer:\"%s\",class:\"%s\"}", 
					//		ieml, fr, en, pa, la, cl);
					
					String j = String.format("{IEML:\"%s\",FR:\"%s\",EN:\"%s\",PARADIGM:\"%s\",LAYER:\"%s\",CLASS:\"%s\"}", ieml, fr, en, pa, la, cl);
					
					json.write(j+"\n" );
					fren.write(fr+"\n");
					engl.write(en+"\n");
					
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
						
			json.close();
			fren.close();
			engl.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}

