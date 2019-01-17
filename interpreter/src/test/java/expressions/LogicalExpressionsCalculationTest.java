package expressions;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import execution.expressions.CalculationVisitor;
import execution.expressions.EvaluationBag;
import expressions_module.parser.ExpressionParser;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import org.junit.Test;
import tree.Node;
import static org.assertj.core.api.Assertions.*;

public class LogicalExpressionsCalculationTest {

	private LexerAgent agent = new LexerAgent();
	private ExpressionParser parser = new ExpressionParser(agent, new HashMap<>(), new HashMap<>());
	private CalculationVisitor visitor = new CalculationVisitor(new HashMap<>());

	@Test
	public void calculate_simpleComparison_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("10 < 100".getBytes());
		agent.handleStream(is);
		Node expression = parser.getLogicalExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isBoolean()).isTrue();
		assertThat(result.getBooleanValue()).isTrue();
	}

	@Test
	public void calculate_simpleNegation_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("!(10 == 10)".getBytes());
		agent.handleStream(is);
		Node expression = parser.getLogicalExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isBoolean()).isTrue();
		assertThat(result.getBooleanValue()).isFalse();
	}

	@Test
	public void calculate_simpleLogicalAnd_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("10 < 100 & 1 == 1".getBytes());
		agent.handleStream(is);
		Node expression = parser.getLogicalExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isBoolean()).isTrue();
		assertThat(result.getBooleanValue()).isTrue();
	}

	@Test
	public void calculate_simpleLogicalOr_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("10 > 100 | 1 == 1".getBytes());
		agent.handleStream(is);
		Node expression = parser.getLogicalExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isBoolean()).isTrue();
		assertThat(result.getBooleanValue()).isTrue();
	}

	@Test
	public void calculate_logicalAndMixedWithOr_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1>10 & 1==1 | 0<12".getBytes());
		agent.handleStream(is);
		Node expression = parser.getLogicalExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isBoolean()).isTrue();
		assertThat(result.getBooleanValue()).isTrue();
	}

	@Test
	public void calculate_logicalAndWithOrAndParenthesis_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1>10 & (1==1 | 0<12)".getBytes());
		agent.handleStream(is);
		Node expression = parser.getLogicalExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isBoolean()).isTrue();
		assertThat(result.getBooleanValue()).isFalse();
	}

}
