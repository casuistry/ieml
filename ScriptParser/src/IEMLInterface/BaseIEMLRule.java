package IEMLInterface;

import TopDown.Node;

public class BaseIEMLRule {

	public String ApplyRule(Node node){
		try {
			return applyRuleRecursively(node);
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private String applyRuleRecursively(Node node) throws Exception {
		
		String result = applyRule(node);
		
		if (result == null) {
			for (Node n : node.GetNodes()){
				applyRuleRecursively(n);
			}
		}
		
		return result;			
	}
	
	protected String applyRule(Node node) throws Exception{
		return null;
	}
	
	public String GetName(){
		return "BaseIEMLRule";
	}
}
