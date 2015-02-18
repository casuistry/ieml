package Inspector;

import TopDown.Node;

public class BaseInspector {

	protected String inspect(Node node){
		StringBuilder builder = new StringBuilder("[" + node.GetDescriptor() + "|" + node.GetPrintableName());
		if (node.GetLayer() != "-1")
			builder.append("|" + node.GetLayer());

		builder.append("]");
		return builder.toString();
	}
	
	public String Inspect(Node node) {
		String result = null;
		try {			
			result = inspect(node);			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			
		}
		return result;		
	}
}
