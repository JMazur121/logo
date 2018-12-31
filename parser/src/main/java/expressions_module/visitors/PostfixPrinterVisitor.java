package expressions_module.visitors;

import expressions_module.tree.ArgumentNode;
import expressions_module.tree.OperatorNode;
import tokenizer.LiteralToken;
import tokenizer.NumericToken;
import tokenizer.Token;
import tokenizer.TokenType;

public class PostfixPrinterVisitor implements ExpressionVisitor {

	private StringBuilder builder;

	public PostfixPrinterVisitor() {
		restartPrinter();
	}

	@Override
	public void visitArgumentNode(ArgumentNode node) {
		Token token = node.getToken();
		if (token.getTokenType().equals(TokenType.T_NUMERIC_CONSTANT)) {
			builder.append(((NumericToken) token).getValue());
		}
		else
			builder.append(((LiteralToken)token).getWord());
	}

	@Override
	public void visitOperatorNode(OperatorNode node) {
		Token token = node.getToken();
		builder.append(token.getTokenType().getLexem());
	}

	public void restartPrinter() {
		builder = new StringBuilder();
	}

	public String print() {
		return builder.toString();
	}

}
