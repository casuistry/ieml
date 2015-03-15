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
    	
		if (!node.isTerm)
			return null;
		
		StringBuilder builder = new StringBuilder(node.GetName() + "\t[" + termInterface.GetMapping(node.GetName()) + "]");
		return builder.toString();
	}
}
