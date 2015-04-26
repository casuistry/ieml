package ScriptGenerator;

import java.util.ArrayList;
import java.util.List;

public class Generator {

	//returns a list of expressions
    public static List<String> GetScript(int layer, int count, BaseIEMLProvider provider, GeneratorConfigurator configurator) {
    	
    	List<String> list = new ArrayList<String>();
    	
    	for (int i=0;i<count;i++){
    		String result;
			try {
				result = GetScript(layer, provider, configurator);
				list.add(result);
			} catch (Exception e) {
				e.printStackTrace();
			}    		    		
    	}
    	
    	return list;
    }
    
    //returns an expression
	public static String GetScript(int layer, BaseIEMLProvider provider, GeneratorConfigurator configurator) throws Exception {
		
		StringBuilder result = new StringBuilder("*");
		result.append(GetExpression(layer, provider, configurator));	
		result.append("**");
		return result.toString();
	}
		
	//returns a sequence
	public static String GetExpression(int layer, BaseIEMLProvider provider, GeneratorConfigurator configurator) throws Exception {
		
		if (layer < 0 || layer >= 7 ) 
			throw new Exception();
		
		StringBuilder result = new StringBuilder();
		
		boolean composite = configurator.random.nextBoolean() && provider.IsCompositeValid();		
		int compositeNumber = composite ? 1 + configurator.random.nextInt(5) : 1;
		
		if (composite && (compositeNumber > 1)) 
			result.append("(");
		
		for (int i = 0; i < compositeNumber; i++) {
			
			if (layer == 0) {

				result.append(provider.GetLetter());
				result.append(provider.GetLayerMark(0));
			}
			else {
				for (int j=0; j<3; j++){
					result.append(GetExpression(layer-1, provider, configurator));
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
