package Tests;

import ScriptGenerator.Generator;
import TopDown.Node;
import TopDown.TopDownParser;

public class BaseTester {

	protected String iemlSequence;
	protected String cleanString;
	protected Node rootNode;
	
	public void run() throws Exception {
		//override
		System.out.println("No test defined");
	}
	
	public void RunTest(String big, int lb)
	{		
		try {			
			iemlSequence = big == null ? Generator.GetScript(lb) : big;				
			//parsing does not care about '*', '(' and ')', but the names of the nodes will include those characters
			//which will be confusing when printing out a readable name.
			cleanString = iemlSequence.replaceAll("[()*]", ""); 
			rootNode = TopDownParser.Parse(cleanString);
			
			run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
