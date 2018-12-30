package expressions_module.tree;

import expressions_module.visitors.Visitor;
import tokenizer.Token;

public class ArgumentNode extends Node {

	public ArgumentNode(Node leftChild, Node rightChild, Token argument) {
		super(leftChild, rightChild, argument);
	}

	@Override
	protected void accept(Visitor visitor) {
		visitor.visitArgumentNode(this);
	}

}
