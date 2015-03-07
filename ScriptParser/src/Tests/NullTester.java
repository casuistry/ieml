package Tests;

import Inspector.BaseInspector;
import ScriptGenerator.GeneratorConfigurator;

public class NullTester extends BaseTester {
	
	protected void run() throws Exception
	{
		System.out.println("Finished parsing: " + iemlSequence);	
		rootNode.PrintNodes("", new BaseInspector());
	}
	
	protected String getTestName() {
		return "NullTester";
	}
	
	protected GeneratorConfigurator GetGeneratorConfigurator(){
		GeneratorConfigurator configurator = new GeneratorConfigurator();
		configurator.IsCompositeValid = false;
		return configurator;
	}
}
