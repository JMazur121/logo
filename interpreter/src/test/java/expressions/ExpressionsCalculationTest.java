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

public class ExpressionsCalculationTest {

	private LexerAgent agent = new LexerAgent();
	private ExpressionParser parser = new ExpressionParser(agent, new HashMap<>(), new HashMap<>());
	private CalculationVisitor visitor = new CalculationVisitor(new HashMap<>());

	@Test
	public void calculate_simpleAddition_returnsValue() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("1+2+3+4".getBytes());
		agent.handleStream(is);
		Node expression = parser.getArithmeticExpressionTree();
		//when
		EvaluationBag result = visitor.calculate(expression);
		//then
		assertThat(result).isNotNull();
		assertThat(result.isNumeric()).isTrue();
		assertThat(result.getValue()).isEqualTo(10);
	}

}
