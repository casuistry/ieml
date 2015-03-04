package Tests;

import Inspector.BaseInspector;

public class ParsingTester extends BaseTester {

	protected void run() throws Exception
	{
		System.out.println("Finished parsing: " + iemlSequence);	
		rootNode.PrintNodes("", new BaseInspector());
	}
	
	protected String getTestName() {
		return "ParsingTester";
	}
}
