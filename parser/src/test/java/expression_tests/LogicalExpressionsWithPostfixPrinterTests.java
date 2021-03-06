package expression_tests;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import expressions_module.parser.ExpressionParser;
import expressions_module.visitors.PostfixPrinterVisitor;
import org.junit.Test;
import tree.Node;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class LogicalExpressionsWithPostfixPrinterTests {

	private PostfixPrinterVisitor visitor = new PostfixPrinterVisitor();
	private LexerAgent agent = new LexerAgent();
	private Map<String, Integer> globalVariables = new HashMap<>();
	private Map<String, Integer> localReferences = new HashMap<>();
	private ExpressionParser parser = new ExpressionParser(agent, globalVariables, localReferences);
	private static int number = 0;

	private String getUniqueVarID() {
		return String.format("var%d", number++);
	}

	@Test
	public void print_simpleGreaterThan_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "21>";
		ByteArrayInputStream is = new ByteArrayInputStream("2 > 1".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleEquals_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "11==";
		ByteArrayInputStream is = new ByteArrayInputStream("1 == 1".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleLessThanInParenthesis_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "21>";
		ByteArrayInputStream is = new ByteArrayInputStream("(2 > 1)".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleNegation_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "52<!";
		ByteArrayInputStream is = new ByteArrayInputStream("!(5 < 2)".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_relationalWithConjunction_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "52>12<&";
		ByteArrayInputStream is = new ByteArrayInputStream("(5 > 2) & (1 < 2)".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_relationalWithAlternative_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "52>12<|";
		ByteArrayInputStream is = new ByteArrayInputStream("(5 > 2) | (1 < 2)".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_conjunctionWithAlternative_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "12>23<11==&|";
		ByteArrayInputStream is = new ByteArrayInputStream("1 > 2 | 2 < 3 & 1 == 1".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getLogicalExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

}
