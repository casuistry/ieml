package Tests;

import java.util.List;

import IEMLInterface.TermInterface;
import Inspector.BaseInspector;
import Inspector.TermInspector;
import TopDown.Node;

public class TermTester extends BaseTester {

	protected void run() throws Exception
	{
		System.out.println(iemlSequence);	
		rootNode.PrintNodes("", new BaseInspector());
	}
	
	protected String getTestName() {
		return "TermTester";
	}

}
