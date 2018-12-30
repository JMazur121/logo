import exceptions.TokenBuildingException;
import tokenizer.Lexer;
import tokenizer.Token;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import static tokenizer.TokenType.T_CONTROL_ETX;

public class Main {

	public static void main(String[] args) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream in = classloader.getResourceAsStream("logo.txt");
		Lexer lexer = new Lexer();
		lexer.handleStream(in, Charset.forName("UTF-8"));
		Token token;
		try {
			while (!T_CONTROL_ETX.equals((token = lexer.nextToken()).getTokenType()))
				System.out.println(token);
		} catch (IOException | TokenBuildingException e) {
			lexer.restart();
			System.out.println(e.getMessage());
		}
	}

}
