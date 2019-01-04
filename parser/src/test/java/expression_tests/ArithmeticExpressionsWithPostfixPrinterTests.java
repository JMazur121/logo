package expression_tests;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import expressions_module.parser.ExpressionParser;
import expressions_module.tree.Node;
import expressions_module.visitors.PostfixPrinterVisitor;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class ArithmeticExpressionsWithPostfixPrinterTests {

	private PostfixPrinterVisitor visitor = new PostfixPrinterVisitor();
	private LexerAgent agent = new LexerAgent();
	private Map<String, Integer> globalVariables = new HashMap<>();
	private Map<String, Integer> localReferences = new HashMap<>();
	private ExpressionParser parser = new ExpressionParser(agent, globalVariables, localReferences);

	@Test
	public void print_simpleConstant_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "100";
		ByteArrayInputStream is = new ByteArrayInputStream("100".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_singleGlobalVariable_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "globalna1";
		globalVariables.put("globalna1", 200);
		ByteArrayInputStream is = new ByteArrayInputStream("globalna1".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_singleLocalReference_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "idx1";
		localReferences.put("local1", 1);
		ByteArrayInputStream is = new ByteArrayInputStream("local1".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_constantsAddition_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "510+";
		ByteArrayInputStream is = new ByteArrayInputStream("5 + 10".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

}
