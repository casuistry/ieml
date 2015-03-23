package Rules;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class PostprocessorEmpty extends PostprocessorBase {
	
	protected String process(Node node) {
		
		//fill in missing multiplication spots, if required
		if (IEMLLang.Multiplication.equals(node.GetOpcode())){
			while (node.GetNodes().size() < 3){
				node.AddNode(Node.GetEmptyNode(node.GetLayer()-1));
			}
		}
		
		if (node.GetLayer() == 0){
			node.SetEmpty(IEMLLang.IsEmpty(node.GetName()));						
		}
		else {
			StringBuilder builder = new StringBuilder();
			for (Node n: node.GetNodes()){
				builder.append(process(n)); 
			}
			node.SetEmpty(IEMLLang.IsEmpty(builder.toString()));			
		}
		
		return node.IsEmpty() ? IEMLLang.GetEmpty() : IEMLLang.GetInvalid();
	}
	
	public String GetName(){
		return "[PostprocessorEmpty]";
	}
}
