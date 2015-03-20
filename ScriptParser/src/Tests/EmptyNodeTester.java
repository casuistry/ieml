package Tests;

import Inspector.EmptyInspector;

public class EmptyNodeTester extends BaseTester {

	protected void run() throws Exception
	{
		System.out.println(iemlSequence);
		rootNode.PrintNodes("", new EmptyInspector());
	}
	
	protected String getTestName() {
		return "EmptyNodeTester";
	}
	
}
