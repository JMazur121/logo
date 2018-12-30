package expressions_module.tree;

import expressions_module.visitors.Visitor;
import tokenizer.Token;

public class OperatorNode extends Node {

	public OperatorNode(Node leftChild, Node rightChild, Token token) {
		super(leftChild, rightChild, token);
	}

	@Override
	public void accept(Visitor visitor) {
		if (leftChild != null)
			leftChild.accept(visitor);
		if (rightChild != null)
			rightChild.accept(visitor);
		visitor.visitOperatorNode(this);
	}

}
