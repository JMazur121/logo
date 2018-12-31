package expressions_module.tree;

import expressions_module.visitors.ExpressionVisitor;
import tokenizer.Token;

public class ArgumentNode extends Node {

	public ArgumentNode(Node leftChild, Node rightChild, Token argument) {
		super(leftChild, rightChild, argument);
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visitArgumentNode(this);
	}

}
