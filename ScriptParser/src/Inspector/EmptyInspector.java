package Inspector;

import TopDown.Node;

public class EmptyInspector extends BaseInspector {

	@Override
	public String Inspect(Node node) {
		
		StringBuilder builder = new StringBuilder(node.GetName() + " is empty = " + node.isEmpty);
		return builder.toString();
	}
}
