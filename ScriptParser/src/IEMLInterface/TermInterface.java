package IEMLInterface;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TopDown.Node;
import Utilities.Helper;

public class TermInterface {
	
    private static HashMap<String, TermInterface> map = new HashMap<String, TermInterface>();
    
    public static synchronized TermInterface getInstance(String filepath) {
    	
    	if (!map.containsKey(filepath)){
    		TermInterface t = new TermInterface(filepath);
    		map.put(filepath, t);
    	}
    	
    	return map.get(filepath);
    }
    
    private HashMap<String, String> termMap = null;
    
    private TermInterface(String filepath) {
    	termMap = LoadDictionary(filepath);
    }
    	
    public boolean IsTerm(Node node){
    	
    	if (node == null)
    		return false;
    	   	
    	if (termMap.containsKey(node.GetName()))
    		return true;
    	
    	if (node.IsPrimitive())
    		return true;
    	
    	return false;
    }
    
    public String GetMapping(String key){
    	if (termMap.containsKey(key))
    		return termMap.get(key);
    	else
    		return "No mapping";
    }
    
	public static HashMap<String, String> LoadDictionary(String filepath){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		
		String defaultPath = "C:\\git\\ieml\\Resources\\Dictionary\\std_dict.csv";
		
		String usePath = filepath != null ? filepath : defaultPath;
		
		File f = new File(usePath);
		if (!f.exists()) {
			Helper.ProcessDictionary(null);
		}
		
		List<String> result = Helper.ReadFile(usePath);
		
		for (String s : result){
			
			String[] temps = new String[2];
			int index = 0;
			
			Matcher matcher = pattern.matcher(s);			
			while (matcher.find())	{				
				temps[index++] = matcher.group();
			}
			
			String entry = temps[0].replaceAll("\"", "");
			String means = temps[1].replaceAll("\"", "");
			map.put(entry, means);
		}
		
		return map;
	}
}
