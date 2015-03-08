package Rules;

import java.util.ArrayList;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class StructureRule extends BaseIEMLRule {

	//checks if the expression has:
	// 1,2 or 3 child nodes if multiplication (plus opcode)
	// 2 or more child nodes if addition (plus opcode)
	protected String applyRule(Node node) throws Exception {
		String op = node.GetOpcode();
		if (op != null){
			ArrayList<Node> children = node.GetNodes();
			if (op.equals(IEMLLang.Addition)){
				if (children.size() > 2)
					return null;
				else
					return "Wrong number of child nodes in addition relation for node " + node.GetName();
			}
			else if (op.equals(IEMLLang.Multiplication)){
				int s = children.size();
				if (s >= 2 && s <= 4)
					return null;
				else
					return "Wrong number of child nodes in multiplication relation for node " + node.GetName();
			}
			else {
				throw new Exception("Unknown opcode " + op);
			}
		}
		
		return null;
	}
	
	public String GetName(){
		return "StructureRule";
	}
}
