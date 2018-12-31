package expressions_module.tree;

import expressions_module.visitors.ExpressionVisitor;
import lombok.Getter;
import tokenizer.Token;

@Getter
public abstract class Node {

	protected Node leftChild;
	protected Node rightChild;
	protected Token token;

	public Node(Node leftChild, Node rightChild, Token token) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.token = token;
	}

	public abstract void accept(ExpressionVisitor expressionVisitor);

}
