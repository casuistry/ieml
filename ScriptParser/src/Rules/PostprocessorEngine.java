package Rules;

import java.util.ArrayList;

import TopDown.Node;

public class PostprocessorEngine {

	ArrayList<PostprocessorBase> rules;
	
	public PostprocessorEngine(){
		rules = new ArrayList<PostprocessorBase>();
		rules.add(new PostprocessorEmpty());
		rules.add(new PostprocessorStructure());
		rules.add(new PostprocessorGrammar());
	}
	
	public void RunRules(Node node) {
		
		for (PostprocessorBase p : rules){			
			p.Process(node);
		}
	}
}
