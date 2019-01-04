package expression_tests;

import agent.LexerAgent;
import exceptions.ExpressionCorruptedException;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
import expressions_module.parser.ExpressionParser;
import expressions_module.tree.Node;
import expressions_module.visitors.PostfixPrinterVisitor;
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
	private static int number = 0;

	private String getUniqueVarID() {
		return String.format("var%d", number++);
	}

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
		String varName = getUniqueVarID();
		globalVariables.put(varName, 200);
		ByteArrayInputStream is = new ByteArrayInputStream(varName.getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(varName);
	}

	@Test
	public void print_singleLocalReference_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String varName = getUniqueVarID();
		localReferences.put(varName, 1);
		ByteArrayInputStream is = new ByteArrayInputStream(varName.getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo("idx1");
	}

	@Test
	public void print_unaryMinusWithConstant_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "100-";
		ByteArrayInputStream is = new ByteArrayInputStream("-100".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_unaryMinusWithGlobalVariable_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String varName = getUniqueVarID();
		globalVariables.put(varName, 200);
		ByteArrayInputStream is = new ByteArrayInputStream(String.format("-%s", varName).getBytes());
		agent.handleStream(is);
		String postfixNotation = String.format("%s-", varName);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_unaryMinusWithLocalVariable_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String varName = getUniqueVarID();
		localReferences.put(varName, 1);
		ByteArrayInputStream is = new ByteArrayInputStream(String.format("-%s", varName).getBytes());
		agent.handleStream(is);
		String postfixNotation = "idx1-";
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

	@Test
	public void print_multipleConstantsAddition_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "12+3+4+";
		ByteArrayInputStream is = new ByteArrayInputStream("1 + 2 + 3 + 4".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additionOnGlobals_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String var1 = getUniqueVarID();
		String var2 = getUniqueVarID();
		globalVariables.put(var1, 10);
		globalVariables.put(var2, 5);
		ByteArrayInputStream is = new ByteArrayInputStream(String.format("%s+%s", var1, var2).getBytes());
		String postfixNotation = String.format("%s%s+", var1, var2);
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additionOnLocals_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String var1 = getUniqueVarID();
		String var2 = getUniqueVarID();
		localReferences.put(var1, 1);
		localReferences.put(var2, 2);
		ByteArrayInputStream is = new ByteArrayInputStream(String.format("%s+%s", var1, var2).getBytes());
		String postfixNotation = "idx1idx2+";
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_multipleAdditionsOnGlobals_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String var1 = getUniqueVarID();
		String var2 = getUniqueVarID();
		String var3 = getUniqueVarID();
		String var4 = getUniqueVarID();
		globalVariables.put(var1, 10);
		globalVariables.put(var2, 5);
		globalVariables.put(var3, 15);
		globalVariables.put(var4, 120);
		ByteArrayInputStream is = new ByteArrayInputStream(String.format("%s+%s+%s+%s", var1, var2, var3, var4).getBytes());
		String postfixNotation = String.format("%s%s+%s+%s+", var1, var2, var3, var4);
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_multipleAdditionsOnLocals_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String var1 = getUniqueVarID();
		String var2 = getUniqueVarID();
		String var3 = getUniqueVarID();
		String var4 = getUniqueVarID();
		localReferences.put(var1, 1);
		localReferences.put(var2, 2);
		localReferences.put(var3, 3);
		localReferences.put(var4, 4);
		ByteArrayInputStream is = new ByteArrayInputStream(String.format("%s+%s+%s+%s", var1, var2, var3, var4).getBytes());
		String postfixNotation = "idx1idx2+idx3+idx4+";
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_constantsSubtraction_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "510-";
		ByteArrayInputStream is = new ByteArrayInputStream("5 - 10".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_multipleConstantsSubtraction_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "12-3-4-5-";
		ByteArrayInputStream is = new ByteArrayInputStream("1 - 2 - 3 -4 -5".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additionInParenthesis_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "1-2+3-4+56+++";
		ByteArrayInputStream is = new ByteArrayInputStream("(-1 + 2 + (-3 + 4 + (5 + 6)))".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_simpleMultiplication_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "12*";
		ByteArrayInputStream is = new ByteArrayInputStream("1 * 2".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_sequenceOfMultiplicative_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "12*10/12%";
		ByteArrayInputStream is = new ByteArrayInputStream("1 * 2 / 10 % 12".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additiveWithMultiplicative_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "5-15*+8-";
		ByteArrayInputStream is = new ByteArrayInputStream("-5 + 1*5 - 8".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

	@Test
	public void print_additiveWithMultiplicativeAndParenthesis_printsPostfixNotation() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		//before
		visitor.restartPrinter();
		String postfixNotation = "238*+-5*";
		ByteArrayInputStream is = new ByteArrayInputStream("-(2 + 3 * 8) * 5".getBytes());
		agent.handleStream(is);
		//when
		Node root = parser.getArithmeticExpressionTree();
		root.accept(visitor);
		//then
		assertThat(visitor.print()).isEqualTo(postfixNotation);
	}

}
