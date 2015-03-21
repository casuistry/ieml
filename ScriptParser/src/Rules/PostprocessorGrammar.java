package Rules;

import java.util.regex.Pattern;

import IEMLInterface.IEMLLang;
import TopDown.Node;

public class PostprocessorGrammar extends PostprocessorBase {

    boolean morpheme = true;

	protected String process(Node node) {
				
		if (node.GetGConstruct() == null){
			
			for (Node n: node.GetNodes()){
				
				String temp = process(n);				
				morpheme &= temp.equals(IEMLLang.GetTerm()); 
			}
			
			String res = morpheme ? 
			
			node.SetGConstruct(builder.toString());
		}
			
		System.out.println(node.GetGConstruct());
		return node.GetGConstruct();
	}
	
	public String GetName(){
		return "[PostprocessorGrammar]";
	}
	
	public boolean GetStatus(){
		return true;
	}
}
