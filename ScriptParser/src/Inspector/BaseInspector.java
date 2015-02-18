package Inspector;

import TopDown.Node;

public class BaseInspector {

	public String Inspect(Node node) {
		StringBuilder builder = new StringBuilder("[" + node.GetDescriptor() + "|" + node.GetPrintableName());
		if (node.GetLayer() != "-1")
			builder.append("|" + node.GetLayer());

		builder.append("]");
		return builder.toString();
	}
}
