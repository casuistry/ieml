package Utilities;

import java.util.Comparator;

import NewParser.Token;

public class IemlOrderComparator implements Comparator<Token> {
    @Override
	public int compare(Token smaller, Token bigger)
	{	
		//Returns:the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y Since:1.7	

		int res = 0;
		
		try {
			smaller.ComputeTaille();
			bigger.ComputeTaille();
			
    		if (smaller.layer < bigger.layer)
    			return -1;
    		if (smaller.layer > bigger.layer)
    			return 1;
    		if (smaller.taille < bigger.taille)
    			return -1;
    		if (smaller.taille > bigger.taille)
    			return 1;
    		
    		if (smaller.canonicalOrder.size() == bigger.canonicalOrder.size()){		    			
    			for (int i = 0; i < smaller.canonicalOrder.size(); i++){				    				
    				int comp = smaller.canonicalOrder.get(i).compareTo(bigger.canonicalOrder.get(i));
    				if (comp == 0)
    					continue;
    				return comp;
    			}	    				
    		}
    		else if (smaller.canonicalOrder.size() < bigger.canonicalOrder.size()){
    			for (int i = 0; i < smaller.canonicalOrder.size(); i++){
    				int comp = smaller.canonicalOrder.get(i).compareTo(bigger.canonicalOrder.get(i));
    				if (comp == 0)
    					continue;
    				return comp;
    			}
    			return -1;
    		}
    		else {
    			for (int i = 0; i < bigger.canonicalOrder.size(); i++){
    				int comp = smaller.canonicalOrder.get(i).compareTo(bigger.canonicalOrder.get(i));
    				if (comp == 0)
    					continue;
    				return comp;
    			}
    			return 1;
    		}
    			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    		

		return res;
	}	
}
