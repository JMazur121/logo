package execution.expressions;

import lombok.Setter;
import tokenizer.TokenType;
import tree.*;
import visitors.ExpressionVisitor;
import java.util.Map;
import java.util.Stack;
import static execution.expressions.EvaluationBag.newBooleanBag;
import static execution.expressions.EvaluationBag.newNumericBag;
import static tokenizer.TokenType.*;

public class CalculationVisitor implements ExpressionVisitor {

	private Stack<EvaluationBag> evaluationStack;
	@Setter
	private int[] localVariables;
	private Map<String, Integer> globalVariables;

	public CalculationVisitor(Map<String, Integer> globalVariables) {
		evaluationStack = new Stack<>();
		this.globalVariables = globalVariables;
	}

	public void reset() {
		evaluationStack.clear();
	}

	public boolean hasElementsOnStack() {
		return !evaluationStack.empty();
	}

	public EvaluationBag calculate(Node node) {
		node.accept(this);
		EvaluationBag evaluationBag = evaluationStack.pop();
		evaluationStack.clear();
		return evaluationBag;
	}

	@Override
	public void visitArgumentNode(ArgumentNode node) {
		ReadableArgument argument = node.getArgument();
		if (argument.isDictionaryArgument())
			evaluationStack.push(newNumericBag(globalVariables.get(argument.readKey())));
		else {
			IndexedArgument indexedArgument = (IndexedArgument) argument;
			int value = indexedArgument.readValue();
			if (indexedArgument.isConstantValue())
				evaluationStack.push(newNumericBag(value));
			else
				evaluationStack.push(newNumericBag(localVariables[value]));
		}
	}

	@Override
	public void visitOperatorNode(OperatorNode node) {
		if (node.isBinaryOperator())
			visitBinaryNode(node);
		else
			visitUnaryNode(node);
	}

	private void visitBinaryNode(OperatorNode node) {
		EvaluationBag rightOperand = evaluationStack.pop();
		EvaluationBag leftOperand = evaluationStack.pop();
		if (node.isArithmeticOperatorNode()) {
			visitArithmeticBinaryNode(leftOperand.getValue(), rightOperand.getValue(), node.getOperatorType());
		}
		else
			visitLogicalBinaryNode(leftOperand.getBooleanValue(), rightOperand.getBooleanValue(), node.getOperatorType());
	}

	private void visitArithmeticBinaryNode(int left, int right, TokenType operatorType) {
		switch (operatorType) {
			case T_ARITHMETIC_ADDITIVE_PLUS:
				evaluationStack.push(newNumericBag(left + right));
				break;
			case T_ARITHMETIC_ADDITIVE_MINUS:
				evaluationStack.push(newNumericBag(left - right));
				break;
			case T_ARITHMETIC_MULT_MULTIPLICATION:
				evaluationStack.push(newNumericBag(left * right));
				break;
			case T_ARITHMETIC_MULT_DIVISION:
				evaluationStack.push(newNumericBag(left / right));
				break;
			case T_ARITHMETIC_MULT_MODULO:
				evaluationStack.push(newNumericBag(left % right));
				break;
		}
	}

	private void visitLogicalBinaryNode(boolean left, boolean right, TokenType operatorType) {
		switch (operatorType) {
			case T_LOGICAL_AND:
				evaluationStack.push(newBooleanBag(left && right));
				break;
			case T_LOGICAL_OR:
				evaluationStack.push(newBooleanBag(left || right));
				break;
		}
	}

	private void visitUnaryNode(OperatorNode node) {
		EvaluationBag operand = evaluationStack.pop();
		if (T_ARITHMETIC_ADDITIVE_MINUS.equals(node.getOperatorType()))
			evaluationStack.push(newNumericBag(-operand.getValue()));
		else
			evaluationStack.push(newBooleanBag(!operand.getBooleanValue()));
	}

}
