package expressions_module.visitors;

import expressions_module.tree.ArgumentNode;
import expressions_module.tree.OperatorNode;
import expressions_module.tree.ReadableArgument;
import tokenizer.Token;

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
			if (argument.isConstantValue())
				builder.append("idx");
			builder.append(argument.readValue());
		}
	}

	@Override
	public void visitOperatorNode(OperatorNode node) {
		Token token = node.getOperatorToken();
		builder.append(token.getTokenType().getLexem());
	}

	public void restartPrinter() {
		builder = new StringBuilder();
	}

	public String print() {
		return builder.toString();
	}

}
