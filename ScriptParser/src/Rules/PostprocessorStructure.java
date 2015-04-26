package Rules;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class PostprocessorStructure extends PostprocessorBase {
		
	protected String process(Node node) throws Exception {		
		return applyRecursively(node);
	}
	
	private String applyRecursively(Node node) throws Exception {
		
		_process(node);
		
		for (Node n : node.GetNodes()){
			if (!status)
				break;
			applyRecursively(n);
		}
		
		return result;			
	}
	
	private void _process(Node node) throws Exception {		
		
		if (!IEMLLang.IsParamNumberValid(node.GetNodes().size(), node.GetOpcode())) {
			status = false;
			result = "Wrong number of child nodes in " + node.GetOpcode() + " relation for node " + node.GetName();
		}		
		
		if (node.IsLeaf() && !node.IsTerm()) {
			//leaf must be a term
			status = false;
			result = "Node " + node.GetName() + " is a leaf but not a term";
		}
	}
	
	public String GetName(){
		return "[PostprocessorStructure]";
	}
	
	public boolean GetStatus(){
		return status;
	}
}
