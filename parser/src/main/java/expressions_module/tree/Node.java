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

}
