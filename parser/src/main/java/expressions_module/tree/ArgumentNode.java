package expressions_module.tree;

import expressions_module.visitors.ExpressionVisitor;
import lombok.Getter;

public class ArgumentNode extends Node {

	@Getter
	private ReadableArgument argument;

	private ArgumentNode(String dictionaryKey) {
		super(null, null);
		argument = new DictionaryArgument(dictionaryKey);
	}

	private ArgumentNode(int value, boolean isConstantValue) {
		super(null, null);
		argument = new IndexedArgument(value, isConstantValue);
	}

	public static ArgumentNode buildDictionaryArgumentNode(String dictionaryKey) {
		return new ArgumentNode(dictionaryKey);
	}

	public static ArgumentNode buildIndexedArgumentNode(int index) {
		return new ArgumentNode(index, false);
	}

	public static ArgumentNode buildConstantArgumentNode(int value) {
		return new ArgumentNode(value, true);
	}

	@Override
	public void accept(ExpressionVisitor expressionVisitor) {
		expressionVisitor.visitArgumentNode(this);
	}

	@Override
	public boolean isArgumentNode() {
		return true;
	}

	@Override
	public boolean isArithmeticOperatorNode() {
		return false;
	}

	@Override
	public boolean isLogicalOperatorNode() {
		return false;
	}

	@Override
	public boolean isRelationalOperatorNode() {
		return false;
	}

	@Override
	public boolean returnsNumericValue() {
		return true;
	}

	@Override
	public boolean returnsBooleanValue() {
		return false;
	}

}
