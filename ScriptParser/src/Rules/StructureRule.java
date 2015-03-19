package Rules;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class StructureRule extends BaseIEMLRule {

	protected String applyRule(Node node) throws Exception {
		
		String result = null;
		String op = node.GetOpcode();
		int num = node.GetNodes().size();
		
		if (!IEMLLang.IsParamNumberValid(num, op))
			result = "Wrong number of child nodes in " + op + " relation for node " + node.GetName();
		
		return result;
	}
	
	public String GetName(){
		return "StructureRule";
	}
}
