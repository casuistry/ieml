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
		
		TermInterface termInterface = new TermInterface(null);
		Node.MarkTerms(termInterface, rootNode);	
		//rootNode.PrintNodes("", new TermInspector(termInterface));
		rootNode.PrintNode("", new BaseInspector());
	}
	
	protected String getTestName() {
		return "TermTester";
	}

}
