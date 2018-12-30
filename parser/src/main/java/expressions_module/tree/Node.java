package expressions_module.tree;

import expressions_module.visitors.Visitor;
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

	protected abstract void accept(Visitor visitor);

}
