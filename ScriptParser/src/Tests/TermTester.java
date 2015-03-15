package Tests;

import Inspector.TermInspector;

public class TermTester extends BaseTester {

	protected void run() throws Exception
	{
		System.out.println(iemlSequence);	
		rootNode.PrintNodes("", new TermInspector(null));
	}
	
	protected String getTestName() {
		return "TermTester";
	}

}
