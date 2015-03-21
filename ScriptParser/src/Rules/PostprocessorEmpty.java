package Rules;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class PostprocessorEmpty extends PostprocessorBase {
	
	protected String process(Node node) {
		
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
		
		return node.IsEmpty() ? "E" : "X";
	}
	
	public String GetName(){
		return "[PostprocessorEmpty]";
	}
}
