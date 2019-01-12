package tree;

import lombok.Getter;
import visitors.ExpressionVisitor;

@Getter
public abstract class Node {

	protected Node leftChild;
	protected Node rightChild;

	public Node(Node leftChild, Node rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	public abstract void accept(ExpressionVisitor expressionVisitor);

	public abstract boolean isArithmeticOperatorNode();

	public abstract boolean isLogicalOperatorNode();

	public abstract boolean isRelationalOperatorNode();

	public abstract boolean isArgumentNode();

	public abstract boolean returnsNumericValue();

	public abstract boolean returnsBooleanValue();

}
