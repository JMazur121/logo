package expressions_module.visitors;

import expressions_module.tree.ArgumentNode;
import expressions_module.tree.OperatorNode;

public interface ExpressionVisitor {

	void visitArgumentNode(ArgumentNode node);
	void visitOperatorNode(OperatorNode node);
	
}