package Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
