package Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import NewParser.ParserImpl;
import NewParser.Token;

public class Helper {

	public static List<String> ReadFile(String filepath){
		ArrayList<String> result = new ArrayList<String>();
		
		BufferedReader br = null;
		String line;
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			while ((line = br.readLine()) != null) {
			   result.add(line);
			}	
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void WriteFile(String filepath, HashMap<String, String> map){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			
			for (String key : map.keySet()){		
				bw.write("\""+key+"\",\""+map.get(key)+"\"\n" );
			}
			
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, String> ProcessDictionary(String filepath){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		
		String defaultPath = "C:\\git\\ieml\\Resources\\Dictionary\\ieml_basic_lexicon_2015_02_19_14_41.csv";
		String outputPath = "C:\\git\\ieml\\Resources\\Dictionary\\std_dict.csv";
		
		String usePath = filepath != null ? filepath : defaultPath;
		
		List<String> result = ReadFile(usePath);
		
		for (String s : result){
			
			String[] temps = new String[3];
			int index = 0;
			
			Matcher matcher = pattern.matcher(s);			
			while (matcher.find())	{				
				temps[index++] = matcher.group();
			}
			
			if (temps[1] == null || temps[2] == null){
				//System.out.println(temps[0].replaceAll("\"", ""));
			}
			else {
				String entry = temps[0].replaceAll("\"", "");
				String means = temps[1].replaceAll("\"", "");
				map.put(entry, means);
			}
		}
		
		WriteFile(outputPath, map);
		
		return map;
	}
	
	public static void ProcessDictionaryV2(String filepath){
						
		List<String> resultFR = ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.fr.cleanup.csv");
		List<String> resultEN = ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.en.cleanup.csv");
		
		ParserImpl parser = new ParserImpl();
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (String s : resultFR){
			
			String[] parts = s.split(",");
			
			try {			
				
				parser.parse(parts[0].trim());	
				
			    if (parts[1].trim().equalsIgnoreCase("null") || parts[1].trim().length() == 0) {
					System.out.println(parts[0].trim() + " [" + parts[1].trim() + "]");
					System.out.println();		
					continue;
			    }

			    if (map.containsKey(parts[0].trim())) {
			    	System.out.println("DUPLICATE");
					System.out.println(parts[0].trim() + " [" + parts[1].trim() + "]");
					System.out.println();		
					continue;
			    }
			    
			    map.put(parts[0].trim(), parts[1].trim());
			    
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(parts[0].trim() + " [" + parts[1].trim() + "]");
				System.out.println();
			}		
			finally {
				parser.Reset();
			}
		}
		
		HashMap<String, String> engDup = new HashMap<String, String>(); 
		
		for (String s : resultEN){
			
			String[] parts = s.split(",");
			
			try {			
				
				parser.parse(parts[0].trim());	
				
			    if (parts[1].trim().equalsIgnoreCase("null") || parts[1].trim().length() == 0) {
					System.out.println(parts[0].trim() + " [" + parts[1].trim() + "]");
					System.out.println();		
					continue;
			    }

			    if (engDup.containsKey(parts[0].trim()) ){
			    	continue;
			    }

			    engDup.put(parts[0].trim(), parts[0].trim());
			    
			    if (map.containsKey(parts[0].trim())) {
			    	map.put(parts[0].trim(), map.get(parts[0].trim()) + " , "+ parts[1].trim());		
					continue;
			    }
			    
			    map.put(parts[0].trim(), " , " + parts[1].trim());
			    
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(parts[1].trim() + " [" + parts[0].trim() + " " + parts[2].trim() + "]");
				System.out.println();
			}		
			finally {
				parser.Reset();
			}
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.clean.csv"));			

			for (String s : map.keySet()) {
				bw.write(s+" , "+map.get(s)+"\n" );
			}
						
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		nextCheck();
	}
	
	public static void nextCheck(){
		HashMap<String, String> engDup = new HashMap<String, String>(); 
		ArrayList<String> next = new ArrayList<String>(); 
		
		List<String> result = ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.clean.csv");
		for (String s : result){			
			String[] parts = s.split(",");
			
			if (parts.length != 3) {
				System.out.println(parts[0]);
				continue;
			}
			
			if (engDup.containsKey(parts[1]) ) {
				System.out.println("DUP NL FR");
				System.out.println(parts[0]);
				continue;
			}
			else 
				engDup.put(parts[1], parts[1]);
			
			if (engDup.containsKey(parts[2]) ) {
				System.out.println("DUP NL EN");
				System.out.println(parts[0]);
				continue;
			}
			else 
				engDup.put(parts[2], parts[2]);
			
			next.add(s);
		}	
		

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.cleanv2.csv"));			

			for (String s : next) {
				bw.write(s+"\n" );
			}
						
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		
		//create JSON
		//{ ieml: "S:B:T:.", terms: [{lang:"FR",means:"something"},{lang:"EN",means:"somethingElse"}]  }
		
		List<String> db = ReadFile("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.db.csv");
		
		try {
			BufferedWriter json = new BufferedWriter(new FileWriter("C:\\Users\\casuistry\\Desktop\\IEML\\Architecture\\ieml.json"));			

			for (String s : db) {
				String[] parts = s.split(",");
				
				String j = String.format("{ieml:\"%s\",terms:[{lang:\"FR\",means:\"%s\"},{lang:\"EN\",means:\"%s\"}]}", 
						parts[0].trim(), parts[1].trim().length() > 0 ? parts[1].trim():"", parts[2].trim().length() > 0 ? parts[2].trim():"");
				json.write(j+"\n" );
			}
						
			json.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String niceString(String s) {
		
		StringBuilder builder = new StringBuilder(s);
		for (int i=0;i<50-s.length();i++)
			builder.append(" ");
		return builder.toString();
	}
	
	public static Token roundTrip(String toParse) {
		
		Token n = null;
		
		ParserImpl parser = new ParserImpl();
		
		try {				
			n = parser.parse(toParse);	
			//System.out.println("Round trip OK: " + n.GetName());
			return n;
		} catch (Exception e) {
			System.out.println("ERROR round-tripping [" + e.getMessage()+"]");
			return n;
		}		
		finally {
			parser.Reset();			
		}
	}
}
