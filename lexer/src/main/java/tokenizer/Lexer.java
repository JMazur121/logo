package tokenizer;

import agent.CharacterStreamAgent;
import com.google.common.collect.ImmutableMap;
import exceptions.TokenBuildingException;
import lombok.Getter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import static agent.CharacterStreamAgent.CHAR_ETX;
import static tokenizer.TokenType.*;

public class Lexer {

	private static final Map<String, TokenType> expectedTokens;
	private static final Token etxToken;
	public static final int IDENTIFIER_MAX_LENGTH = 30;
	public static final String IDENTIFIER_TOO_LONG = "Lexer error - identifier length exceeds maximum (30).";
	public static final String UNKNOWN_TOKEN = "Lexer error - token was not recognized.";
	public static final String NUMBER_PARSING_ERROR = "Lexer error - number cannot be parsed.";
	public static final String MULTILINE_COMMENT_ERROR = "Lexer error - missing comment's closing bracket.";
	@Getter
	private int lineNumber;
	@Getter
	private int positionInLine;
	@Getter
	private CharacterStreamAgent agent;
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
		reachedEnd = false;
	}

	private Optional<TokenType> findToken(String stringRepresentation) {
		TokenType found = expectedTokens.get(stringRepresentation);
		return (found == null) ? Optional.empty() : Optional.of(found);
	}

	public Token nextToken() throws IOException, TokenBuildingException {
		if (reachedEnd)
			return etxToken;
		skipWhitespaces();
		skipComments();
		char nextChar = agent.bufferAndGetChar();
		if (nextChar == CHAR_ETX) {
			reachedEnd = true;
			return etxToken;
		}
		if (Character.isDigit(nextChar))
			return buildNumericConstant(nextChar);
		else if (Character.isLetter(nextChar))
			return buildIdentifierOrKeyword(nextChar);
		else
			return buildOperator(nextChar);
	}

	private void skipWhitespaces() throws IOException {
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

	private void skipComments() throws IOException, TokenBuildingException {
		skipSingleLineComment();
		skipMultiLineComment();
	}

	private void skipSingleLineComment() throws IOException {
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
				reachedEnd = true;
		}
	}

	private void skipMultiLineComment() throws IOException, TokenBuildingException {
		TokenPosition position = buildTokenPosition();
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
				throw new TokenBuildingException(position, "", MULTILINE_COMMENT_ERROR);
			}
		}
	}

	private TokenPosition buildTokenPosition() {
		return new TokenPosition(lineNumber, positionInLine, agent.getBufferedPosition());
	}

	private Token buildNumericConstant(char currentDigit) throws IOException, TokenBuildingException {
		TokenPosition position = buildTokenPosition();
		StringBuilder builder = new StringBuilder();
		while (Character.isDigit(currentDigit)) {
			builder.append(currentDigit);
			commitAndMovePosition();
			currentDigit = agent.bufferAndGetChar();
		}
		try {
			int value = Integer.parseInt(builder.toString());
			return new NumericToken(position, value);
		} catch (Exception ex) {
			throw new TokenBuildingException(position, builder.toString(), NUMBER_PARSING_ERROR);
		}
	}

	private Token buildIdentifierOrKeyword(char currentChar) throws IOException, TokenBuildingException {
		TokenPosition position = buildTokenPosition();
		StringBuilder builder = new StringBuilder();
		int length = 0;
		//we allow to build string longer than maximum, so that we know that identifier is incorrect
		while (Character.isLetterOrDigit(currentChar) && length <= (IDENTIFIER_MAX_LENGTH + 1)) {
			builder.append(currentChar);
			++length;
			commitAndMovePosition();
			currentChar = agent.bufferAndGetChar();
		}
		if (length > 30)
			throw new TokenBuildingException(position, builder.toString(), IDENTIFIER_TOO_LONG);
		String createdWord = builder.toString();
		Optional<TokenType> foundType = findToken(createdWord);
		return foundType.map(tokenType -> new Token(tokenType, position)).orElseGet(() -> new LiteralToken(T_IDENTIFIER, position, createdWord));
	}

	private Token buildOperator(char firstChar) throws IOException, TokenBuildingException {
		TokenPosition position = buildTokenPosition();
		StringBuilder builder = new StringBuilder(2);
		builder.append(firstChar);
		commitAndMovePosition();
		String oneCharToken = builder.toString();
		builder.append(agent.bufferAndGetChar());
		String twoCharsToken = builder.toString();
		//first try to find 2-characters operator
		Optional<TokenType> twoCharsType = findToken(twoCharsToken);
		if (twoCharsType.isPresent()) {
			commitAndMovePosition();
			return new Token(twoCharsType.get(), position);
		}
		//then try to find one-character operator
		Optional<TokenType> oneCharType = findToken(oneCharToken);
		if (oneCharType.isPresent())
			return new Token(oneCharType.get(), position);
		//there are not any options left - token is unknown
		throw new TokenBuildingException(position, twoCharsToken, UNKNOWN_TOKEN);
	}

}
