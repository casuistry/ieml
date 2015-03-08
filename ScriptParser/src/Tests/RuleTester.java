package Tests;

import Inspector.BaseInspector;
import Rules.StructureRule;

public class RuleTester extends BaseTester {
	
	protected void run() throws Exception
	{
		//System.out.println("Finished parsing: " + iemlSequence);	
		
		StructureRule rule = new StructureRule();
		String result = rule.ApplyRule(rootNode);
		
		if (result != null)
			System.out.println(result);
		else 
			System.out.println("Passed " + rule.GetName());

	}
	
	protected String getTestName() {
		return "RuleTester";
	}
}
