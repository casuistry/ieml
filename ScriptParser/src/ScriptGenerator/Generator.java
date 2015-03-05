package ScriptGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import IEMLInterface.IEMLLang;

public class Generator {

	private static Random random = new Random();
	
    public static List<String> GetScript(int layer, int count, BaseProvider provider) {
    	
    	List<String> list = new ArrayList<String>();
    	
    	for (int i=0;i<count;i++){
    		String result;
			try {
				result = GetScript(layer, provider);
				list.add(result);
			} catch (Exception e) {
				e.printStackTrace();
			}    		    		
    	}
    	
    	return list;
    }
    
	public static String GetScript(int layer, BaseProvider provider) throws Exception {
		
		StringBuilder result = new StringBuilder("*");
		result.append(GetExpression(layer, provider));	
		result.append("**");
		return result.toString();
	}
		
	public static String GetExpression(int layer, BaseProvider provider) throws Exception {
		
		if (layer < 0 || layer >= 7 ) 
			throw new Exception();
		
		StringBuilder result = new StringBuilder();
		
		boolean composite = random.nextBoolean() && provider.IsCompositeValid();		
		int compositeNumber = composite ? 1 + random.nextInt(5) : 1;
		
		if (composite && (compositeNumber > 1)) 
			result.append("(");
		
		for (int i = 0; i < compositeNumber; i++) {
			
			if (layer == 0) {

				result.append(provider.GetLetter());
				result.append(provider.GetLayerMark(0));	
			}
			else {
				for (int j=0; j<3; j++){
					result.append(GetExpression(layer-1, provider));
				}
				
				result.append(provider.GetLayerMark(layer));	
			}
			
			if (composite && i < compositeNumber - 1) 
				result.append("+");
		}

		if (composite && (compositeNumber > 1)) 
			result.append(")");
		
		return result.toString();
	}	
}
