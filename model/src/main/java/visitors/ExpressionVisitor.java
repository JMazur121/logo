package visitors;

import tree.ArgumentNode;
import tree.OperatorNode;

public interface ExpressionVisitor {

	void visitArgumentNode(ArgumentNode node);
	void visitOperatorNode(OperatorNode node);

}
