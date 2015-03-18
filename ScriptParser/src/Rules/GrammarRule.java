package Rules;

import java.util.ArrayList;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class GrammarRule extends BaseIEMLRule {

	//checks if the expression has:
	// 1,2 or 3 child nodes if multiplication (plus opcode)
	// 2 or more child nodes if addition (plus opcode)
	protected String applyRule(Node node) throws Exception {
		
		if (node.IsOpcode())
			return null;
		
		String op = node.GetOpcode();
		
		if (op != null){
			
			ArrayList<Node> children = node.GetNodes();
			
			if (op.equals(IEMLLang.Addition)){
				

			}
			else if (op.equals(IEMLLang.Multiplication)){

			}
			else {
				throw new Exception("Unknown opcode " + op);
			}
		}
		else {
			if (!node.IsPrimitive())
				return "Node is not primitive but has no children for node " + node.GetName();			
		}
		
		return null;
	}
	
	public String GetName(){
		return "GrammarRule";
	}
}
