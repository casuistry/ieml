package Rules;

import java.util.ArrayList;

import TopDown.Node;


public class RuleEngine {
			
	ArrayList<BaseIEMLRule> rules;
	
	public RuleEngine(){
		rules = new ArrayList<BaseIEMLRule>();
		rules.add(new StructureRule());
	}
	
	public void RunRules(Node node) {
		for (BaseIEMLRule r : rules){
			
			String error = r.ApplyRule(node);
			
			if (error != null)
				System.out.println(error);
			else 
				System.out.println("Passed " + node.GetName());
		}
	}
}
