package Rules;

import TopDown.Node;

public class PostprocessorBase<T> {

	public T Process(Node node){
		
		T result = null;
		
		try {
			result = process(node);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return result;
	}
	
	protected T process(Node node) {
		return null;
	}
}
