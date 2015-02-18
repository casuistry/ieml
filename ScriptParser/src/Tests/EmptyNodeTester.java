package Tests;

import Inspector.EmptyInspector;

public class EmptyNodeTester extends BaseTester {

	public void run() throws Exception
	{
		System.out.println(iemlSequence);
		
		//rootNode.PrintNode("", new BaseInspector());
		rootNode.IsEmpty();
		rootNode.PrintNodes("", new EmptyInspector());
	}
}
