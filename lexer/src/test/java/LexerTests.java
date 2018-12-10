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
	public void nextToken_lexerOnEmptyStream_returnsETXToken() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		lexer.handleStream(emptyStream());
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test(expected = TokenBuildingException.class)
	public void nextToken_lexerOnUnknownCharacters_throwsException() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		lexer.handleStream(corruptedStream());
		//when
		Token token = lexer.nextToken();
	}

	@Test
	public void nextToken_streamWithSingleIdentifier_returnsIdentifierToken() throws TokenBuildingException, IOException {
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
	public void nextToken_singleIdentifierWithDigits_returnsIdentifierToken() throws TokenBuildingException, IOException {
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
	public void nextToken_identifierWith29Characters_returnsIdentifierToken() throws TokenBuildingException, IOException {
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
	public void nextToken_identifierWith30Characters_returnsIdentifierToken() throws TokenBuildingException, IOException {
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
	public void nextToken_tooLongIdentifier_throwsException() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("a1bbb2bbb3bbb4bbb5bbb6bbb7bbbxx".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
	}

	@Test
	public void nextToken_streamWithSingleNumericConstant_returnsNumericConstantToken() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("1410".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_NUMERIC_CONSTANT);
		TokenPosition position = token.getPosition();
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getPositionInLine()).isEqualTo(1);
		assertThat(position.getAbsolutePosition()).isEqualTo(1);
	}

	@Test(expected = TokenBuildingException.class)
	public void nextToken_tooWideNumericConstant_throwsException() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("200000000000".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
	}

	@Test
	public void nextToken_streamAfterSingleIdentifier_returnsETXToken() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("identifier".getBytes());
		lexer.handleStream(is);
		lexer.nextToken();
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test
	public void nextToken_streamWithSingleOperator_returnsOperatorToken() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("+".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_ARITHMETIC_ADDITIVE_PLUS);
		TokenPosition position = token.getPosition();
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getPositionInLine()).isEqualTo(1);
		assertThat(position.getAbsolutePosition()).isEqualTo(1);
	}

	@Test
	public void nextToken_streamWithWhitespaces_returnsTwoTokens() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("def kwadrat".getBytes());
		lexer.handleStream(is);
		//when
		Token defToken = lexer.nextToken();
		Token identifierToken = lexer.nextToken();
		TokenPosition defPosition = defToken.getPosition();
		TokenPosition identifierPosition = identifierToken.getPosition();
		//then
		assertThat(defToken.getTokenType()).isEqualTo(T_KEYWORD_PROC_DEFINITION);
		assertThat(defPosition.getAbsolutePosition()).isEqualTo(1);
		assertThat(defPosition.getLine()).isEqualTo(1);
		assertThat(defPosition.getPositionInLine()).isEqualTo(1);
		assertThat(identifierToken.getTokenType()).isEqualTo(T_IDENTIFIER);
		assertThat(identifierPosition.getAbsolutePosition()).isEqualTo(5);
		assertThat(identifierPosition.getLine()).isEqualTo(1);
		assertThat(identifierPosition.getPositionInLine()).isEqualTo(5);
	}

	@Test
	public void nextToken_streamWithNewLine_changesPositions() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("id2 \nid3".getBytes());
		lexer.handleStream(is);
		//when
		lexer.nextToken();
		lexer.nextToken();
		//then
		assertThat(lexer.getLineNumber()).isEqualTo(2);
		assertThat(lexer.getPositionInLine()).isEqualTo(4);
	}

	@Test
	public void nextToken_streamWithLineComment_returnsETXToken() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("#comment".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test
	public void nextToken_streamWithBlockComment_returnsETXToken() throws TokenBuildingException, IOException {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("{sdad\ndasd\nsadd\n}".getBytes());
		lexer.handleStream(is);
		//when
		Token token = lexer.nextToken();
		//then
		assertThat(token.getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	private ByteArrayInputStream emptyStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	private ByteArrayInputStream corruptedStream() {
		return new ByteArrayInputStream("$$?^".getBytes());
	}

}
