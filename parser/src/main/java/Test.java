import exceptions.LexerException;
import exceptions.ParserException;
import parser.Parser;
import scope.Scope;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream in = classloader.getResourceAsStream("logo.txt");
		Map<String, Integer> globalVars = new HashMap<>();
		Map<String, Scope> knownMethods = new HashMap<>();
		Parser parser = new Parser(globalVars, knownMethods);
		parser.handleStream(in);
		while (!parser.isReachedETX()) {
			try {
				parser.getNextScope();
			} catch (LexerException | ParserException e) {
				System.out.println(e.getMessage());
				break;
			}
		}
	}

}
