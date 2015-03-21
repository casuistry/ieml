package Inspector;

import IEMLInterface.TermInterface;
import TopDown.Node;

public class TermInspector extends BaseInspector {

	TermInterface termInterface = null;
	
	public TermInspector(TermInterface termInterface){
		this.termInterface = termInterface;
	}
	
	@Override
	protected String inspect(Node node) {
    	
		StringBuilder builder = new StringBuilder();
		
		if (!node.IsTerm())
			builder.append(node.GetName() + "\t[#]");
		else
			builder.append(node.GetName() + "\t[" + termInterface.GetMapping(node.GetName()) + "]");
		
		return builder.toString();
	}
}
