package tokenizer;

import agent.CharacterStreamAgent;
import com.google.common.collect.ImmutableMap;
import exceptions.IncompleteExpressionException;
import lombok.Getter;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import static agent.CharacterStreamAgent.CHAR_ETX;
import static agent.CharacterStreamAgent.CHAR_NULL;
import static tokenizer.TokenType.*;

public class Lexer {

	private static final Map<String, TokenType> expectedTokens;
	private static final Token etxToken;
	public static final int IDENTIFIER_MAX_LENGTH = 30;
	@Getter
	private int lineNumber;
	@Getter
	private int positionInLine;
	private CharacterStreamAgent agent;
	@Getter
	private boolean isCorrupted;
	@Getter
	private boolean reachedEnd;

	static {
		expectedTokens = ImmutableMap.<String, TokenType>builder()
				.put("def", T_KEYWORD_PROC_DEFINITION)
				.put("powtarzaj", T_KEYWORD_ITERATION_LOOP)
				.put("tdj", T_KEYWORD_WHILE_LOOP)
				.put("jesli", T_KEYWORD_IF)
				.put("wpw", T_KEYWORD_ELSE)
				.put("+", T_ARITHMETIC_ADDITIVE_PLUS)
				.put("-", T_ARITHMETIC_ADDITIVE_MINUS)
				.put("*", T_ARITHMETIC_MULT_MULTIPLICATION)
				.put("/", T_ARITHMETIC_MULT_DIVISION)
				.put("%", T_ARITHMETIC_MULT_MODULO)
				.put("<", T_RELATIONAL_LESS_THAN)
				.put(">", T_RELATIONAL_GREATER_THAN)
				.put("<=", T_RELATIONAL_LESS_THAN_OR_EQUAL)
				.put(">=", T_RELATIONAL_GREATER_THAN_OR_EQUAL)
				.put("==", T_RELATIONAL_EQUAL)
				.put("!=", T_RELATIONAL_NOT_EQUAL)
				.put("&", T_LOGICAL_AND)
				.put("|", T_LOGICAL_OR)
				.put("!", T_LOGICAL_NOT)
				.put("(", T_LEFT_PARENTHESIS)
				.put(")", T_RIGHT_PARENTHESIS)
				.put(";", T_SEMICOLON)
				.put("[", T_LEFT_SQUARE_BRACKET)
				.put("]", T_RIGHT_SQUARE_BRACKET)
				.put("#", T_HASH)
				.put("{", T_LEFT_CURLY_BRACKET)
				.put("}", T_RIGHT_CURLY_BRACKET)
				.put(":=", T_ASSIGNMENT)
				.put(",", T_COMMA)
				.build();
		etxToken = Token.builder().tokenType(T_CONTROL_ETX).build();
	}

	public Lexer() {
		restart();
	}

	public void handleStream(InputStream inputStream) {
		agent.handleStream(inputStream);
	}

	public void handleStream(InputStream inputStream, Charset charset) {
		agent.handleStream(inputStream, charset);
	}

	public void restart() {
		lineNumber = 1;
		positionInLine = 1;
		if (agent != null)
			agent.resetAgent();
		else
			agent = new CharacterStreamAgent();
		isCorrupted = false;
		reachedEnd = false;
	}

	public Optional<TokenType> findToken(String stringRepresentation) {
		TokenType found = expectedTokens.get(stringRepresentation);
		return (found == null) ? Optional.empty() : Optional.of(found);
	}

	public Token nextToken() throws StreamCorruptedException, IncompleteExpressionException {
		if (isCorrupted)
			throw new StreamCorruptedException();
		if (reachedEnd)
			return etxToken;
		skipWhitespaces();
		skipComments();
		char nextChar = agent.bufferAndGetChar();
		if (nextChar == CHAR_ETX) {
			handleETX();
			return etxToken;
		}
		if (nextChar == CHAR_NULL) {
			isCorrupted = true;
			throw new StreamCorruptedException();
		}
		if (Character.isDigit(nextChar))
			return buildNumericConstant(nextChar);
		else if (Character.isLetter(nextChar))
			return buildIdentifierOrKeyword(nextChar);
		else
			return buildOperator(nextChar);
	}

	private void skipWhitespaces() {
		char nextChar;
		while (Character.isWhitespace(nextChar = agent.bufferAndGetChar())) {
			commitAndMovePosition();
			if (nextChar == '\n')
				handleNewLine();
		}
	}

	private void commitAndMovePosition() {
		++positionInLine;
		agent.commitBufferedChar();
	}

	private void handleNewLine() {
		++lineNumber;
		positionInLine = 1;
	}

	private void skipComments() throws IncompleteExpressionException {
		skipSingleLineComment();
		skipMultiLineComment();
	}

	private void skipSingleLineComment() {
		char nextChar = agent.bufferAndGetChar();
		if (nextChar == '#') {
			while ((nextChar != '\n') && (nextChar != CHAR_ETX)) {
				agent.commitBufferedChar();
				nextChar = agent.bufferAndGetChar();
			}
			if (nextChar == '\n') {
				handleNewLine();
				agent.commitBufferedChar();
			} else
				handleETX();
		}
	}

	private void handleETX() {
		reachedEnd = true;
		agent.closeReader();
	}

	private void skipMultiLineComment() throws IncompleteExpressionException {
		char nextChar = agent.bufferAndGetChar();
		if (nextChar == '{') {
			commitAndMovePosition();
			while ((nextChar != '}') && (nextChar != CHAR_ETX)) {
				nextChar = agent.bufferAndGetChar();
				commitAndMovePosition();
				if (nextChar == '\n') {
					handleNewLine();
				}
			}
			if (nextChar == CHAR_ETX) {
				throw new IncompleteExpressionException("Incomplete multi-line comment - ETX reached");
			}
		}
	}

	private TokenPosition buildTokenPosition() {
		return new TokenPosition(lineNumber, positionInLine, agent.getBufferedPosition());
	}

	private Token buildNumericConstant(char currentDigit) {
		TokenPosition position = buildTokenPosition();
		StringBuilder builder = new StringBuilder();
		while (Character.isDigit(currentDigit)) {
			builder.append(currentDigit);
			commitAndMovePosition();
			currentDigit = agent.bufferAndGetChar();
		}
		int value = Integer.parseInt(builder.toString());
		return new NumericToken(position, value);
	}

	private Token buildIdentifierOrKeyword(char currentChar) {
		TokenPosition position = buildTokenPosition();
		StringBuilder builder = new StringBuilder();
		boolean wasDigit = false;
		while (Character.isLetter(currentChar)) {
			builder.append(currentChar);
			commitAndMovePosition();
			currentChar = agent.bufferAndGetChar();
		}
		for (int i=0; i<IDENTIFIER_MAX_LENGTH; i++) {
			if ()
		}
		// TODO: 2018-12-08 Naprawic budowanie identyfikatorow i pilnowanie dlugosci 
	}

	private String buildIdentifier(StringBuilder builder, char currentChar) {
		while (Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
			builder.append(currentChar);
			commitAndMovePosition();
			currentChar = agent.bufferAndGetChar();
		}
		return builder.toString();
	}

	private Token buildOperator(char firstChar) {

	}

}
