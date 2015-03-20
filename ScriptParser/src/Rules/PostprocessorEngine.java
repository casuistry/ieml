package Rules;

import java.util.ArrayList;

import TopDown.Node;

public class PostprocessorEngine {

	ArrayList<PostprocessorBase<String>> rules;
	
	public PostprocessorEngine(){
		rules = new ArrayList<PostprocessorBase<String>>();
		rules.add(new PostprocessorEmpty());
	}
	
	public void RunRules(Node node) {
		
		for (PostprocessorBase<String> p : rules){			
			p.Process(node);
		}
	}
}
