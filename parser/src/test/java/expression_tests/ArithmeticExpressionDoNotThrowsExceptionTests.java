package expression_tests;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import expressions_module.parser.ExpressionParser;
import org.junit.Test;
import tree.Node;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

public class ArithmeticExpressionDoNotThrowsExceptionTests {

	private LexerAgent agent = new LexerAgent();
	private Map<String, Integer> globalVariables = new HashMap<>();
	private Map<String, Integer> localReferences = new HashMap<>();
	private ExpressionParser parser = new ExpressionParser(agent, globalVariables, localReferences);

	@Test
	public void getArithmeticTree_simpleConstant_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("10".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_simpleGlobalVariable_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("dlugosc".getBytes());
		agent.handleStream(is);
		globalVariables.put("dlugosc", 10);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_simpleLocalReference_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("temp".getBytes());
		agent.handleStream(is);
		localReferences.put("temp",1);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_simpleAddition_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1+2".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_additionInParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(1+2+3+4)".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_multipleAdditions_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1+2+3+4+5+6+7+8".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_simpleSubtraction_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1-2".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_subtractionInParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(1-2-3)".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_multipleSubtractions_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1-2-3-4-5-6-7-8".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_simpleMultiplication_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("5 * 2".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_multiplicationInParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(1*2*3*4)".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_multipleMultiplications_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1*2*3*4*5*6*6*7*8".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_unaryMinusWithConstant_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-5".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_unaryMinusWithParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-(1*2*3*4)".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_unaryMinusWithGlobalVariable_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-a".getBytes());
		agent.handleStream(is);
		globalVariables.put("a", 100);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_unaryMinusWithLocalVariable_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-b".getBytes());
		agent.handleStream(is);
		localReferences.put("b", 15);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_additionWithMultiplication_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("5+2*3".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_nestedParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("((5 + 2))".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_multipleParenthesis_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("((1+2)+(3*5)) * 5".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

	@Test
	public void getArithmeticTree_subtractionWithUnaryMinus_doNotThrowsException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-1--2".getBytes());
		agent.handleStream(is);
		//when
		Node treeRoot = parser.getArithmeticExpressionTree();
		//then
		assertThat(treeRoot).isNotNull();
	}

}
