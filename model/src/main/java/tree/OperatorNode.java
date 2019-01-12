package tree;

import lombok.Getter;
import tokenizer.Token;
import tokenizer.TokenType;
import visitors.ExpressionVisitor;

public class OperatorNode extends Node {

	@Getter
	private TokenType operatorType;

	public OperatorNode(Node leftChild, Node rightChild, TokenType operatorType) {
		super(leftChild, rightChild);
		this.operatorType = operatorType;
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
		return operatorType.isAdditiveOperator() || operatorType.isMultiplicativeOperator();
	}

	@Override
	public boolean isLogicalOperatorNode() {
		return operatorType.isLogicalOperator();
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

	public boolean isBinaryOperator() {
		return rightChild != null;
	}

}
