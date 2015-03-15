package Tests;

import IEMLInterface.TermInterface;
import Inspector.TermInspector;
import TopDown.Parser.Mode;
import TopDown.ParserConfigurator;

public class TermTester extends BaseTester {

	ParserConfigurator p;
	
	public TermTester(){
		p = new ParserConfigurator();
		p._Mode = Mode.TermOnly;
		p._TermFilePath = null;
	}
	
	protected void run() throws Exception
	{
		System.out.println(iemlSequence);	
		rootNode.PrintNodes("", new TermInspector(TermInterface.getInstance(p._TermFilePath)));
	}
	
	protected String getTestName() {
		return "TermTester";
	}
	
	protected ParserConfigurator GetParserConfigurator(){
		return p; 
	}
}
