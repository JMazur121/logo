package expression_tests;

import expressions_module.tree.ArgumentNode;
import expressions_module.tree.OperatorNode;
import expressions_module.visitors.PostfixPrinterVisitor;
import org.junit.Test;
import tokenizer.Token;
import static org.assertj.core.api.Assertions.*;
import static tokenizer.TokenType.*;

public class PostfixPrinterTests {

	private PostfixPrinterVisitor visitor = new PostfixPrinterVisitor();

	@Test
	public void print_simpleAddition_returnsPostfixAddition() {
		//before
		String postfixNotation = "35+";
		ArgumentNode left = ArgumentNode.buildConstantArgumentNode(3);
		ArgumentNode right = ArgumentNode.buildConstantArgumentNode(5);
		Token operator = new Token(T_ARITHMETIC_ADDITIVE_PLUS, null);
		OperatorNode expression = new OperatorNode(left, right, operator);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleSubtraction_returnsPostfixSubtraction() {
		//before
		String postfixNotation = "35-";
		ArgumentNode left = ArgumentNode.buildConstantArgumentNode(3);
		ArgumentNode right = ArgumentNode.buildConstantArgumentNode(5);
		Token operator = new Token(T_ARITHMETIC_ADDITIVE_MINUS, null);
		OperatorNode expression = new OperatorNode(left, right, operator);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleLessThanComparison_returnsPostfixComparison() {
		//before
		String postfixNotation = "35<";
		ArgumentNode left = ArgumentNode.buildConstantArgumentNode(3);
		ArgumentNode right = ArgumentNode.buildConstantArgumentNode(5);
		Token operator = new Token(T_RELATIONAL_LESS_THAN, null);
		OperatorNode expression = new OperatorNode(left, right, operator);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleUnaryMinus_returnsPostfixUnaryMinus() {
		//before
		String postfixNotation = "5-";
		ArgumentNode left = ArgumentNode.buildConstantArgumentNode(5);
		Token operator = new Token(T_ARITHMETIC_ADDITIVE_MINUS, null);
		OperatorNode expression = new OperatorNode(left, null, operator);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

}
