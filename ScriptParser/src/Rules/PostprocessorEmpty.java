package Rules;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class PostprocessorEmpty extends PostprocessorBase<String> {
	
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
		
		return node.GetEmpty() ? "E" : "X";
	}
	
	public String GetName(){
		return "PostprocessorEmpty";
	}
}
