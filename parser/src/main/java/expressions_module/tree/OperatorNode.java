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
	public boolean isOperatorNode() {
		return true;
	}

	public boolean isArithmeticOperator() {
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

}
