package expressions_module.visitors;

import tokenizer.Token;
import tree.ArgumentNode;
import tree.OperatorNode;
import tree.ReadableArgument;
import visitors.ExpressionVisitor;

public class PostfixPrinterVisitor implements ExpressionVisitor {

	private StringBuilder builder;

	public PostfixPrinterVisitor() {
		restartPrinter();
	}

	@Override
	public void visitArgumentNode(ArgumentNode node) {
		ReadableArgument argument = node.getArgument();
		if (argument.isDictionaryArgument())
			builder.append(argument.readKey());
		else {
			if (!argument.isConstantValue())
				builder.append("idx");
			builder.append(argument.readValue());
		}
	}

	@Override
	public void visitOperatorNode(OperatorNode node) {
		builder.append(node.getOperatorType().getLexem());
	}

	public void restartPrinter() {
		builder = new StringBuilder();
	}

	public String print() {
		return builder.toString();
	}

}
