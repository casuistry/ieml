package Tests;

import ScriptGenerator.BaseIEMLProvider;
import ScriptGenerator.Generator;
import ScriptGenerator.GeneratorConfigurator;
import TopDown.Node;
import TopDown.ParserConfigurator;
import TopDown.TopDownParser;

public class BaseTester {

	protected String iemlSequence;
	protected String cleanString;
	protected Node rootNode;
	
	protected void run() throws Exception {

	}
	
	protected String getTestName() {
		return "BaseTester";
	}
	
	protected GeneratorConfigurator GetGeneratorConfigurator(){
		return new GeneratorConfigurator();
	}
	
	protected ParserConfigurator GetParserConfigurator(){
		return new ParserConfigurator();
	}
	
	//entry point for tests
	public void RunTest(String big, int lb, BaseIEMLProvider provider, GeneratorConfigurator configurator, ParserConfigurator pConf)
	{		
		try {			
			
			if (configurator == null)
				configurator = GetGeneratorConfigurator();
			
			if (pConf == null)
				pConf = GetParserConfigurator();
			
			System.out.println(getTestName());
			
			iemlSequence = big == null ? Generator.GetScript(lb, provider, configurator) : big;				
			//parsing does not care about '*', '(' and ')', but the names of the nodes will include those characters
			//which will be confusing when printing out a readable name.
			cleanString = iemlSequence.replaceAll("[()*\\s]", ""); 			
			
			rootNode = TopDownParser.Parse(cleanString, pConf._Mode);
			
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
