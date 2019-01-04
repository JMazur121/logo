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

public class ArithmeticExpressionDoThrowsExceptionTests {

	private LexerAgent agent = new LexerAgent();
	private Map<String, Integer> globalVariables = new HashMap<>();
	private Map<String, Integer> localReferences = new HashMap<>();
	private ExpressionParser parser = new ExpressionParser(agent, globalVariables, localReferences);

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_emptyStream_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_unaryMinusWithoutValue_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_twoMinuses_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("--".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_multipleMinuses_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("--------".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_singlePlus_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("+".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_plusWithMissingArgument_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1+".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_twoPluses_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("++".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_multiplePluses_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("++++++".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_singleMultiplication_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("*".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_multiplicationWithMissingArgument_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1*".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_multipleDivision_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("////////".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_missingRightParenthesis_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(1+2".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_emptyParenthesis_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("( )".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = TokenMissingException.class)
	public void getArithmeticTree_additionWithCorruptedMultiplication_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 + 2 *".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test(expected = UndefinedReferenceException.class)
	public void getArithmeticTree_undefinedVariableReference_throwsUndefinedReferenceException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("zmienna".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}


}
