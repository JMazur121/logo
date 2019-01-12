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

public class ArithmeticExpressionsCalculationTest {

	private LexerAgent agent = new LexerAgent();
	private ExpressionParser parser = new ExpressionParser(agent, new HashMap<>(), new HashMap<>());
	private CalculationVisitor visitor = new CalculationVisitor(new HashMap<>());

	@Test
	public void calculate_simpleConstant_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1000".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(1000);
	}

	@Test
	public void calculate_simpleUnaryMinus_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-50".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(-50);
	}

	@Test
	public void calculate_simpleAddition_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1+2+3+4".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(10);
	}

	@Test
	public void calculate_simpleSubtraction_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("10-5-3-2".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(0);
	}

	@Test
	public void calculate_additionWithSubtraction_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("-1+2+0-5-10".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(-14);
	}

	@Test
	public void calculate_additionWithParenthesis_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("((1+2)+(3+4))+(10+10)".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(30);
	}

	@Test
	public void calculate_subtractionWithParenthesis_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("(-10 - 5)- (3 - 1)".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(-17);
	}

	@Test
	public void calculate_additionWithSubtractionAndParenthesis_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1 - (10 + 5 + (-3 -2) )".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(-9);
	}

	@Test
	public void calculate_simpleMultiplication_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1*2*3*4".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(24);
	}

	@Test
	public void calculate_simpleDivision_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("20 /2 /5".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(2);
	}

	@Test
	public void calculate_multiplicationWithDivision() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("3 * 10 / 2".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(15);
	}

}
