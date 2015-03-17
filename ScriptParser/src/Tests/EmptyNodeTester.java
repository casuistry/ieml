package Tests;

import Inspector.BaseInspector;
import Inspector.EmptyInspector;

public class EmptyNodeTester extends BaseTester {

	protected void run() throws Exception
	{
		System.out.println(iemlSequence);
		
		//rootNode.PrintNode("", new BaseInspector());
		rootNode.IsEmpty();
		rootNode.PrintNodes("", new EmptyInspector());
	}
	
	protected String getTestName() {
		return "EmptyNodeTester";
	}
	
}
