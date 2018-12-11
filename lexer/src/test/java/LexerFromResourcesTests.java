import exceptions.TokenBuildingException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import tokenizer.Lexer;
import tokenizer.Token;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static tokenizer.TokenType.*;

public class LexerFromResourcesTests {

	@Test
	public void nextToken_file1_returnsTokens() throws IOException, TokenBuildingException {
		//before
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("test1.txt");
		Lexer lexer = new Lexer();
		lexer.handleStream(inputStream, Charset.forName("UTF-8"));
		Token token;
		List<String> tokens = IOUtils.readLines(classLoader.getResourceAsStream("test1Tokens.txt"),"UTF-8");
		ArrayList<String> fromLexer = new ArrayList<>();
		//when
		while (!T_CONTROL_ETX.equals((token = lexer.nextToken()).getTokenType()))
			fromLexer.add(token.getTokenType().toString());
		//then
		assertThat(fromLexer).containsExactlyElementsOf(tokens);
	}

	@Test
	public void nextToken_file2_returnsTokens() throws IOException, TokenBuildingException {
		//before
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("test2.txt");
		Lexer lexer = new Lexer();
		lexer.handleStream(inputStream, Charset.forName("UTF-8"));
		Token token;
		List<String> tokens = IOUtils.readLines(classLoader.getResourceAsStream("test2Tokens.txt"),"UTF-8");
		ArrayList<String> fromLexer = new ArrayList<>();
		//when
		while (!T_CONTROL_ETX.equals((token = lexer.nextToken()).getTokenType()))
			fromLexer.add(token.getTokenType().toString());
		//then
		assertThat(fromLexer).containsExactlyElementsOf(tokens);
	}

}
