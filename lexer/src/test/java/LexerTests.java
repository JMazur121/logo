import org.junit.Test;
import tokenizer.Lexer;
import tokenizer.Token;
import java.io.ByteArrayInputStream;
import java.util.Optional;
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
	public void reset_lexerCreated_positionInLineStartsWithZero() {
		//when
		lexer.restart();
		//then
		assertThat(lexer.getPositionInLine()).isEqualTo(0);
	}

	@Test
	public void reset_lexerCreated_lexerIsNotCorrupted() {
		//when
		lexer.restart();
		//then
		assertThat(lexer.isCorrupted()).isFalse();
	}

	@Test
	public void nextToken_lexerOnEmptyStream_returnsETXToken() {
		//before
		lexer.restart();
		lexer.handleStream(emptyStream());
		//when
		Optional<Token> token = lexer.nextToken();
		//then
		assertThat(token.isPresent()).isTrue();
		assertThat(token.get().getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test
	public void nextToken_lexerOnUnknownCharacters_returnsEmptyOptional() {
		//before
		lexer.restart();
		lexer.handleStream(corruptedStream());
		//when
		Optional<Token> token = lexer.nextToken();
		//then
		assertThat(token.isPresent()).isFalse();
	}

	@Test
	public void nextToken_streamWithSingleIdentifier_returnsIdentifierToken() {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("identifier".getBytes());
		lexer.handleStream(is);
		//when
		Optional<Token> token = lexer.nextToken();
		//then
		assertThat(token.isPresent()).isTrue();
		Token identifierToken = token.get();
		assertThat(identifierToken.getTokenType()).isEqualTo(T_IDENTIFIER);
		assertThat(identifierToken.getLine()).isEqualTo(1);
		assertThat(identifierToken.getPositionInLine()).isEqualTo(1);
		assertThat(identifierToken.getAbsolutePosition()).isEqualTo(1);
	}

	@Test
	public void nextToken_streamWithSingleNumericConstant_returnsNumericConstantToken() {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("1410".getBytes());
		lexer.handleStream(is);
		//when
		Optional<Token> token = lexer.nextToken();
		//then
		assertThat(token.isPresent()).isTrue();
		Token numericToken = token.get();
		assertThat(numericToken.getTokenType()).isEqualTo(T_NUMERIC_CONSTANT);
		assertThat(numericToken.getLine()).isEqualTo(1);
		assertThat(numericToken.getPositionInLine()).isEqualTo(1);
		assertThat(numericToken.getAbsolutePosition()).isEqualTo(1);

	}

	@Test
	public void nextToken_streamAfterSingleIdentifier_returnsETXToken() {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("identifier".getBytes());
		lexer.handleStream(is);
		lexer.nextToken();
		//when
		Optional<Token> token = lexer.nextToken();
		//then
		assertThat(token.isPresent()).isTrue();
		assertThat(token.get().getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test
	public void nextToken_streamWithLineComment_returnsETXToken() {
		//before
		lexer.restart();
		ByteArrayInputStream is = new ByteArrayInputStream("#comment".getBytes());
		lexer.handleStream(is);
		//when
		Optional<Token> token = lexer.nextToken();
		//then
		assertThat(token.isPresent()).isTrue();
		assertThat(token.get().getTokenType()).isEqualTo(T_CONTROL_ETX);
	}

	@Test
	public void isCorrupted_lexerOnUnknownCharacters_becomesCorrupted() {
		//before
		lexer.restart();
		lexer.handleStream(corruptedStream());
		//when
		lexer.nextToken();
		//then
		assertThat(lexer.isCorrupted()).isTrue();
	}

	private ByteArrayInputStream emptyStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	private ByteArrayInputStream corruptedStream() {
		return new ByteArrayInputStream("$$?^".getBytes());
	}

}
