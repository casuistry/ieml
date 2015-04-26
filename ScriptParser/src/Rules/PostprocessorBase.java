package Rules;

import TopDown.Node;

public class PostprocessorBase {
	
	protected boolean status = true;
	protected String result = null;
	
	public String Process(Node node){
			
		String result = null;
		
		try {
			result = process(node);
			System.out.println(GetName() + " " + (GetStatus() ? "passed" : result));			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}
	
	protected String process(Node node) throws Exception {
		return null;
	}
	
	public String GetName(){
		return "[PostprocessorBase]";
	}
	
	public boolean GetStatus(){
		return true;
	}
}
