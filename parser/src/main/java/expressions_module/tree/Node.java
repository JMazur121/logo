package expressions_module.tree;

import expressions_module.visitors.ExpressionVisitor;
import lombok.Getter;

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
