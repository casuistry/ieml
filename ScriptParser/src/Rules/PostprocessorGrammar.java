package Rules;

import IEMLInterface.Detector;
import TopDown.Node;

public class PostprocessorGrammar extends PostprocessorBase {

    boolean morpheme = true;

	protected String process(Node node) throws Exception {
				
		if (node.GetGConstruct() == null){
			
			Detector detector = new Detector();
			
			detector.Next(node.GetOpcode());
			
			for (Node n: node.GetNodes()){				
				detector.Next(process(n)); 
			}
			
			node.SetGConstruct(detector.GetResult());
		}
			
		System.out.println(node.GetGConstruct());
		return node.GetGConstruct();
	}
	
	public String GetName(){
		return "[PostprocessorGrammar]";
	}
	
	public boolean GetStatus(){
		return true;
	}
}
