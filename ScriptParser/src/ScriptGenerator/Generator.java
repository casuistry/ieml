package ScriptGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import IEMLInterface.IEMLLang;

public class Generator {

	private static Random random = new Random();
	
    public static List<String> GetScript(int layer, int count) {
    	
    	List<String> list = new ArrayList<String>();
    	
    	for (int i=0;i<count;i++){
    		String result;
			try {
				result = GetScript(layer);
				list.add(result);
			} catch (Exception e) {
				e.printStackTrace();
			}    		    		
    	}
    	
    	return list;
    }
    
	public static String GetScript(int layer) throws Exception {
		
		StringBuilder result = new StringBuilder("*");
		result.append(GetExpression(layer, true));	
		result.append("**");
		return result.toString();
	}
		
	public static String GetExpression(int layer, boolean compositeValid) throws Exception {
		
		if (layer < 0 || layer >= 7 ) 
			throw new Exception();
		
		StringBuilder result = new StringBuilder();
		
		boolean composite = random.nextBoolean();		
		int compositeNumber = composite ? 1 + random.nextInt(5) : 1;
		
		if (composite && compositeValid && (compositeNumber > 1)) 
			result.append("(");
		
		for (int i = 0; i < compositeNumber; i++) {
			
			if (layer == 0) {

				result.append(IEMLLang.alphabet[random.nextInt(10)]);
				result.append(IEMLLang.LM[0]);	
			}
			else {
				for (int j=0; j<3; j++){
					result.append(GetExpression(layer-1, true));
				}
				
				result.append(IEMLLang.LM[layer]);	
			}
			
			if (composite && i < compositeNumber - 1) 
				result.append("+");
		}

		if (composite && compositeValid && (compositeNumber > 1)) 
			result.append(")");
		
		return result.toString();
	}	
}
