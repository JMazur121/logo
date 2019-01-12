package tree;

import lombok.Getter;
import tokenizer.Token;
import tokenizer.TokenType;
import visitors.ExpressionVisitor;

public class OperatorNode extends Node {

	@Getter
	private Token operatorToken;

	public OperatorNode(Node leftChild, Node rightChild, Token operatorToken) {
		super(leftChild, rightChild);
		this.operatorToken = operatorToken;
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		if (leftChild != null)
			leftChild.accept(expressionVisitor);
		if (rightChild != null)
			rightChild.accept(expressionVisitor);
		expressionVisitor.visitOperatorNode(this);
	}

	@Override
	public boolean isArgumentNode() {
		return false;
	}

	@Override
	public boolean isArithmeticOperatorNode() {
		TokenType type = operatorToken.getTokenType();
		return type.isAdditiveOperator() || type.isMultiplicativeOperator();
	}

	@Override
	public boolean isLogicalOperatorNode() {
		return operatorToken.getTokenType().isLogicalOperator();
	}

	@Override
	public boolean isRelationalOperatorNode() {
		return !(isArithmeticOperatorNode() || isLogicalOperatorNode());
	}

	@Override
	public boolean returnsNumericValue() {
		return isArithmeticOperatorNode();
	}

	@Override
	public boolean returnsBooleanValue() {
		return !isArithmeticOperatorNode();
	}

}
