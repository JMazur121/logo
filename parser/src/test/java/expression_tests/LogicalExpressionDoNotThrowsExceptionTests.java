package expression_tests;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import expressions_module.parser.ExpressionParser;
import expressions_module.tree.Node;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

public class LogicalExpressionDoNotThrowsExceptionTests {

	private LexerAgent agent = new LexerAgent();
	private Map<String, Integer> globalVariables = new HashMap<>();
	private Map<String, Integer> localReferences = new HashMap<>();
	private ExpressionParser parser = new ExpressionParser(agent, globalVariables, localReferences);

	@Test
	public void getLogicalTree_simpleRelationalOperation_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("2 > 1".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getLogicalExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getLogicalTree_simpleEqualsOperation_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("3 == 5".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getLogicalExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getLogicalTree_relationalOperationsWithParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(5 > 2)".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getLogicalExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getLogicalTree_relationalOperationsWithNestedParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(((5 > 2)))".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getLogicalExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getLogicalTree_relationalWithAnd_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("5 > 2 & 1 < 3".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getLogicalExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getLogicalTree_relationalWithConjunctionAndParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(5 > 2) & (1 < 2)".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getLogicalExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

}
