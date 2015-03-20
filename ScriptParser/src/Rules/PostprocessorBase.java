package Rules;

import TopDown.Node;

public class PostprocessorBase<T> {
	
	public T Process(Node node){
			
		T result = null;
		
		try {
			result = process(node);
			System.out.println(GetName() + " " + (GetStatus() ? "passed" : "failed"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}
	
	protected T process(Node node) throws Exception {
		return null;
	}
	
	public String GetName(){
		return "PostprocessorBase";
	}
	
	public boolean GetStatus(){
		return true;
	}
}
