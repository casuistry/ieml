package Tests;

import ScriptGenerator.BaseProvider;
import ScriptGenerator.Generator;
import TopDown.Node;
import TopDown.TopDownParser;

public class BaseTester {

	protected String iemlSequence;
	protected String cleanString;
	protected Node rootNode;
	
	protected void run() throws Exception {
		System.out.println("No test defined");
	}
	
	protected String getTestName() {
		return "not specified";
	}
	
	//entry point for tests
	public void RunTest(String big, int lb, BaseProvider provider)
	{		
		try {			
			
			System.out.println(getTestName());
			
			iemlSequence = big == null ? Generator.GetScript(lb, provider) : big;				
			//parsing does not care about '*', '(' and ')', but the names of the nodes will include those characters
			//which will be confusing when printing out a readable name.
			cleanString = iemlSequence.replaceAll("[()*\\s]", ""); 
			
			
			rootNode = TopDownParser.Parse(cleanString);
			
			run();
			
			System.out.println(getTestName());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
		}
	}
}
