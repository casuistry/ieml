package IEMLInterface;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TopDown.Node;
import Utilities.Helper;

public class TermInterface {
	
	String filepath = null;
	public HashMap<String, String> termMap = null;
	
	public TermInterface(String filepath) {
		this.filepath = filepath;
	}
	
	public static HashMap<String, String> LoadDictionary(String filepath){
	
		HashMap<String, String> map = new HashMap<String, String>();
		
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		
		String defaultPath = "C:\\git\\ieml\\Resources\\Dictionary\\ieml_basic_lexicon_2015_02_19_14_41.csv";
		
		String usePath = filepath != null ? filepath : defaultPath;
		
		List<String> result = Helper.ReadFile(usePath);
		
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
		
		return map;
	}
	
    public boolean IsTerm(Node node){
    	
    	if (node == null)
    		return false;
    	
    	if (termMap == null){
    		termMap = LoadDictionary(filepath);
    	}
    	   	
    	if (termMap.containsKey(node.GetPrintableName())) {  //some names might have leading "+"  		
    		return true;
    	}
    	
    	if ( (node.GetDescriptor().equals(Node.NODE) || node.GetDescriptor().equals(Node.ROOT)) && node.GetLayerInt() == 0) {
    		return true;
    	}
    	
    	return false;
    }
}
