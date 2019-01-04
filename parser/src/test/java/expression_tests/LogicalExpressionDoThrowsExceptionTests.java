package expression_tests;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import expressions_module.parser.ExpressionParser;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class LogicalExpressionDoThrowsExceptionTests {

	private LexerAgent agent = new LexerAgent();
	private Map<String, Integer> globalVariables = new HashMap<>();
	private Map<String, Integer> localReferences = new HashMap<>();
	private ExpressionParser parser = new ExpressionParser(agent, globalVariables, localReferences);

	@Test(expected = TokenMissingException.class)
	public void getLogicalTree_emptyStream_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_singleConstant_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("10".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_simpleAddition_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1+2".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_conjunctionOnNumeric_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 & 2 & 3".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_alternativeOnNumeric_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 | 2 | 3".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_alternativeAndConjunctionOnNumeric_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 & 2 | 3".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = TokenMissingException.class)
	public void getLogicalTree_negationOnSimpleNumeric_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("!3".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_negationOnArithmeticParenthesis_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("!(1+2*5)".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_additionOnRelational_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 > 2 + 3 < 10".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_additionOnRelationalWithParenthesis_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(1 > 2) + (3 < 10)".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_logicalOnNumericWithParenthesis_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(1 & 2) < (3 | 10)".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_conjunctionOnArithmetic_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 + 2 & 3 * 10".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = TokenMissingException.class)
	public void getLogicalTree_sequenceOfRelationalOperators_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("> < <= ==".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = TokenMissingException.class)
	public void getLogicalTree_sequenceOfNot_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("! ! ! !".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = TokenMissingException.class)
	public void getLogicalTree_notOnEmptyParenthesis_throwsTokenMissingException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("!()".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

	@Test(expected = ExpressionCorruptedException.class)
	public void getLogicalTree_additionOnNegation_throwsExpressionCorruptedException() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("124 + !(2 > 3)".getBytes());
		agent.handleStream(is);
		//when
		parser.getLogicalExpressionTree();
	}

}
