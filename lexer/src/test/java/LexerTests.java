import exceptions.IncompleteExpressionException;
import exceptions.TokenBuildingException;
import org.junit.Test;
import tokenizer.Lexer;
import tokenizer.Token;
import tokenizer.TokenPosition;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static tokenizer.TokenType.*;

public class LexerTests {

	private Lexer lexer = new Lexer();

	@Test
	public void reset_lexerCreated_lineStartsWithOne() {
		//when
		lexer.restart();
		//then
		assertThat(lexer.getLineNumber()).isEqualTo(1);
	}

	@Test
	public void reset_lexerCreated_positionInLineStartsWithOne() {
		//when
		lexer.restart();
		//then
		assertThat(lexer.getPositionInLine()).isEqualTo(1);
	}

	@Test
	public void nextToken_lexerOnEmptyStream_returnsETXToken() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		lexer.handleStream(emptyStream());
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test(expected = TokenBuildingException.class)
	public void nextToken_lexerOnUnknownCharacters_throwsException() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		lexer.handleStream(corruptedStream());
		//when
		Token token = lexer.nextToken();
	}

	@Test
	public void nextToken_streamWithSingleIdentifier_returnsIdentifierToken() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("identifier".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_IDENTIFIER);
		TokenPosition position = token.getPosition();
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getPositionInLine()).isEqualTo(1);
		assertThat(position.getAbsolutePosition()).isEqualTo(1);
	}

	@Test
	public void nextToken_singleIdentifierWithDigits_returnsIdentifierToken() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("a123".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_IDENTIFIER);
		TokenPosition position = token.getPosition();
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getPositionInLine()).isEqualTo(1);
		assertThat(position.getAbsolutePosition()).isEqualTo(1);
	}

	@Test
	public void nextToken_identifierWith29Characters_returnsIdentifierToken() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("a1bbb2bbb3bbb4bbb5bbb6bbb7bbb".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_IDENTIFIER);
		TokenPosition position = token.getPosition();
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getPositionInLine()).isEqualTo(1);
		assertThat(position.getAbsolutePosition()).isEqualTo(1);
	}

	@Test
	public void nextToken_identifierWith30Characters_returnsIdentifierToken() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("a1bbb2bbb3bbb4bbb5bbb6bbb7bbbx".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_IDENTIFIER);
		TokenPosition position = token.getPosition();
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getPositionInLine()).isEqualTo(1);
		assertThat(position.getAbsolutePosition()).isEqualTo(1);
	}

	@Test(expected = TokenBuildingException.class)
	public void nextToken_tooLongIdentifier_throwsException() throws IncompleteExpressionException, TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("a1bbb2bbb3bbb4bbb5bbb6bbb7bbbxx".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
	}

//	@Test
//	public void nextToken_streamWithSingleNumericConstant_returnsNumericConstantToken() {
//		//before
//		lexer.restart();
//		ByteArrayInputStream is = new ByteArrayInputStream("1410".getBytes());
//		lexer.handleStream(is);
//		//when
//		Optional<Token> token = lexer.nextToken();
//		//then
//		assertThat(token.isPresent()).isTrue();
//		Token numericToken = token.get();
//		assertThat(numericToken.getTokenType()).isEqualTo(T_NUMERIC_CONSTANT);
//		assertThat(numericToken.getLine()).isEqualTo(1);
//		assertThat(numericToken.getPositionInLine()).isEqualTo(1);
//		assertThat(numericToken.getAbsolutePosition()).isEqualTo(1);
//	}
//
//	@Test
//	public void nextToken_streamAfterSingleIdentifier_returnsETXToken() {
//		//before
//		lexer.restart();
//		ByteArrayInputStream is = new ByteArrayInputStream("identifier".getBytes());
//		lexer.handleStream(is);
//		lexer.nextToken();
//		//when
//		Optional<Token> token = lexer.nextToken();
//		//then
//		assertThat(token.isPresent()).isTrue();
//		assertThat(token.get().getTokenType()).isEqualTo(T_CONTROL_ETX);
//	}
//
//	@Test
//	public void nextToken_streamWithSingleOperator_returnsOperatorToken() {
//		//before
//		lexer.restart();
//		ByteArrayInputStream is = new ByteArrayInputStream("+".getBytes());
//		lexer.handleStream(is);
//		//when
//		Optional<Token> token = lexer.nextToken();
//		//then
//		assertThat(token.isPresent()).isTrue();
//		Token plusToken = token.get();
//		assertThat(plusToken.getTokenType()).isEqualTo(T_ARITHMETIC_ADDITIVE_PLUS);
//		assertThat(plusToken.getLine()).isEqualTo(1);
//		assertThat(plusToken.getPositionInLine()).isEqualTo(1);
//		assertThat(plusToken.getAbsolutePosition()).isEqualTo(1);
//	}
//
//	@Test
//	public void nextToken_streamWithWhitespaces_returnsTwoTokens() {
//		//before
//		lexer.restart();
//		ByteArrayInputStream is = new ByteArrayInputStream("def kwadrat".getBytes());
//		lexer.handleStream(is);
//		//when
//		Optional<Token> defToken = lexer.nextToken();
//		Optional<Token> identifierToken = lexer.nextToken();
//		//then
//		assertThat(defToken.isPresent()).isTrue();
//		assertThat(identifierToken.isPresent()).isTrue();
//		Token def = defToken.get();
//		Token identifier = identifierToken.get();
//		assertThat(def.getTokenType()).isEqualTo(T_KEYWORD_PROC_DEFINITION);
//		assertThat(def.getAbsolutePosition()).isEqualTo(1);
//		assertThat(def.getLine()).isEqualTo(1);
//		assertThat(def.getPositionInLine()).isEqualTo(1);
//		assertThat(identifier.getTokenType()).isEqualTo(T_IDENTIFIER);
//		assertThat(identifier.getAbsolutePosition()).isEqualTo(5);
//		assertThat(identifier.getLine()).isEqualTo(1);
//		assertThat(identifier.getPositionInLine()).isEqualTo(5);
//	}
//
//	@Test
//	public void nextToken_streamWithLineComment_returnsETXToken() {
//		//before
//		lexer.restart();
//		ByteArrayInputStream is = new ByteArrayInputStream("#comment".getBytes());
//		lexer.handleStream(is);
//		//when
//		Optional<Token> token = lexer.nextToken();
//		//then
//		assertThat(token.isPresent()).isTrue();
//		assertThat(token.get().getTokenType()).isEqualTo(T_CONTROL_ETX);
//	}
//
//	@Test
//	public void nextToken_streamWithBlockComment_returnsETXToken() {
//		//before
//		lexer.restart();
//		ByteArrayInputStream is = new ByteArrayInputStream("{sdad\ndasd\nsadd\n}".getBytes());
//		lexer.handleStream(is);
//		//when
//		Optional<Token> token = lexer.nextToken();
//		//then
//		assertThat(token.isPresent()).isTrue();
//		assertThat(token.get().getTokenType()).isEqualTo(T_CONTROL_ETX);
//	}
//
//	@Test
//	public void isCorrupted_lexerOnUnknownCharacters_becomesCorrupted() {
//		//before
//		lexer.restart();
//		lexer.handleStream(corruptedStream());
//		//when
//		lexer.nextToken();
//		//then
//		assertThat(lexer.isCorrupted()).isTrue();
//	}

	private ByteArrayInputStream emptyStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	private ByteArrayInputStream corruptedStream() {
		return new ByteArrayInputStream("$$?^".getBytes());
	}

}
