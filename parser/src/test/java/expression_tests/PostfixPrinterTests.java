package expression_tests;

import expressions_module.visitors.PostfixPrinterVisitor;
import org.junit.Test;
import tokenizer.Token;
import tree.ArgumentNode;
import tree.OperatorNode;
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
		OperatorNode expression = new OperatorNode(left, right, T_ARITHMETIC_ADDITIVE_PLUS);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additionWithDictionaryArgument_returnsPostfixAddition() {
		//before
		String postfixNotation = "5zmienna+";
		ArgumentNode left = ArgumentNode.buildConstantArgumentNode(5);
		ArgumentNode right = ArgumentNode.buildDictionaryArgumentNode("zmienna");
		OperatorNode expression = new OperatorNode(left, right, T_ARITHMETIC_ADDITIVE_PLUS);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additionWithLocalReference_returnsPostfixAddition() {
		//before
		String postfixNotation = "5idx10+";
		ArgumentNode left = ArgumentNode.buildConstantArgumentNode(5);
		ArgumentNode right = ArgumentNode.buildIndexedArgumentNode(10);
		OperatorNode expression = new OperatorNode(left, right, T_ARITHMETIC_ADDITIVE_PLUS);
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
		OperatorNode expression = new OperatorNode(left, right, T_ARITHMETIC_ADDITIVE_MINUS);
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
		OperatorNode expression = new OperatorNode(left, right, T_RELATIONAL_LESS_THAN);
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
		OperatorNode expression = new OperatorNode(left, null, T_ARITHMETIC_ADDITIVE_MINUS);
		//when
		expression.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

}
