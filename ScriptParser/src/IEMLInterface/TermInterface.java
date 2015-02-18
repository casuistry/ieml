package IEMLInterface;

import TopDown.Node;

public class TermInterface {
	
    public static boolean IsTerm(Node node){
    	
    	if (node == null)
    		return false;
    	
    	//Need a dictionary of terms. Dictionary of terms should be loaded around the same time as parsing IEML string.
    	//

    	//System.out.println("Checking if " + node.GetPrintableName() + " is a term.");
    	
    	if ( (node.GetDescriptor().equals(Node.NODE) || node.GetDescriptor().equals(Node.ROOT)) && node.GetLayerInt() == 0)
    		return true;
    	
    	return false;
    }
}
