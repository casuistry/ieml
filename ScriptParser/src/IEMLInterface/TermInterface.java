package IEMLInterface;

import java.io.File;
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
	
    public boolean IsTerm(Node node){
    	
    	if (node == null)
    		return false;
    	
    	if (termMap == null){
    		termMap = LoadDictionary(filepath);
    	}
    	   	
    	if (termMap.containsKey(node.GetName())) {   		
    		return true;
    	}
    	
    	if ( node.GetDescriptor().equals(Node.ATOM) || (node.GetDescriptor().equals(Node.ROOT) && node.GetLayerInt() == 0)) {
    		return true;
    	}
    	
    	return false;
    }
}
