package tokenizer;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
	T_CONTROL_ETX(""),
	T_KEYWORD_PROC_DEFINITION("def"),
	T_KEYWORD_ITERATION_LOOP("powtarzaj"),
	T_KEYWORD_WHILE_LOOP("tdj"),
	T_KEYWORD_IF("jesli"),
	T_KEYWORD_ELSE("wpw"),
	T_IDENTIFIER(""),
	T_ARITHMETIC_MULT_MULTIPLICATION("*"),
	T_ARITHMETIC_MULT_DIVISION("/"),
	T_ARITHMETIC_MULT_MODULO("%"),
	T_ARITHMETIC_ADDITIVE_PLUS("+"),
	T_ARITHMETIC_ADDITIVE_MINUS("-"),
	T_RELATIONAL_LESS_THAN("<"),
	T_RELATIONAL_GREATER_THAN(">"),
	T_RELATIONAL_LESS_THAN_OR_EQUAL("<="),
	T_RELATIONAL_GREATER_THAN_OR_EQUAL(">="),
	T_RELATIONAL_EQUAL("=="),
	T_RELATIONAL_NOT_EQUAL("!="),
	T_LOGICAL_AND("&"),
	T_LOGICAL_OR("|"),
	T_LOGICAL_NOT("!"),
	T_LEFT_PARENTHESIS("("),
	T_RIGHT_PARENTHESIS(")"),
	T_SEMICOLON(";"),
	T_LEFT_SQUARE_BRACKET("["),
	T_RIGHT_SQUARE_BRACKET("]"),
	T_HASH("#"),
	T_LEFT_CURLY_BRACKET("{"),
	T_RIGHT_CURLY_BRACKET("}"),
	T_ASSIGNMENT(":="),
	T_COMMA(","),
	T_NUMERIC_CONSTANT("");

	private final String lexem;
	private static final Map<String, TokenType> expectedTokens;

	static {
		expectedTokens = new HashMap<>();
		for (TokenType type : TokenType.values())
			expectedTokens.put(type.getLexem(), type);
	}

	TokenType(final String lexem) {
		this.lexem = lexem;
	}

	public String getLexem() {
		return lexem;
	}

	public static TokenType findToken(final String stringRepresentation) {
		return expectedTokens.get(stringRepresentation);
	}

}
