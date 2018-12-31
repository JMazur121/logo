package expressions_module.tree;

import expressions_module.visitors.ExpressionVisitor;
import tokenizer.Token;

public class OperatorNode extends Node {

	public OperatorNode(Node leftChild, Node rightChild, Token token) {
		super(leftChild, rightChild, token);
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		if (leftChild != null)
			leftChild.accept(expressionVisitor);
		if (rightChild != null)
			rightChild.accept(expressionVisitor);
		expressionVisitor.visitOperatorNode(this);
	}

}
