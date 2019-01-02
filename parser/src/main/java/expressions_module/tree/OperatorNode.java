package expressions_module.tree;

import expressions_module.visitors.ExpressionVisitor;
import lombok.Getter;
import tokenizer.Token;

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
		switch (operatorToken.getTokenType()) {
			case T_ARITHMETIC_ADDITIVE_PLUS:
				return true;
			case T_ARITHMETIC_ADDITIVE_MINUS:
				return true;
			case T_ARITHMETIC_MULT_MULTIPLICATION:
				return true;
			case T_ARITHMETIC_MULT_DIVISION:
				return true;
			case T_ARITHMETIC_MULT_MODULO:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean isLogicalOperatorNode() {
		switch (operatorToken.getTokenType()) {
			case T_LOGICAL_AND:
				return true;
			case T_LOGICAL_OR:
				return true;
			case T_LOGICAL_NOT:
				return true;
			default:
				return false;
		}
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
