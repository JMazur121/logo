package expressions_module.parser;

import agent.LexerAgent;
import exceptions.*;
import expressions_module.tree.ArgumentNode;
import expressions_module.tree.Node;
import expressions_module.tree.OperatorNode;
import lombok.Setter;
import tokenizer.LiteralToken;
import tokenizer.NumericToken;
import tokenizer.Token;
import tokenizer.TokenType;
import java.io.IOException;
import java.util.Map;
import static tokenizer.TokenType.*;

public class ExpressionParser {

	private LexerAgent agent;
	private final Map<String, Integer> globalVariables;
	@Setter
	private Map<String, Integer> localReferences;
	private boolean isArithmetic;

	public ExpressionParser(LexerAgent agent, Map<String, Integer> globalVariables) {
		this.agent = agent;
		this.globalVariables = globalVariables;
	}

	public ExpressionParser(LexerAgent agent, Map<String, Integer> globalVariables, Map<String, Integer> localReferences) {
		this(agent, globalVariables);
		this.localReferences = localReferences;
	}

	public Node getArithmeticExpressionTree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		isArithmetic = true;
		Node currentRoot = getAdditiveSubtree();
		Token nextToken = agent.bufferAndGetToken();
		TokenType nextTokenType = nextToken.getTokenType();
		while (isAdditiveOperator(nextTokenType)) {
			agent.commitBufferedToken();
			Node rightSubtree = getAdditiveSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isArithmeticOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = agent.bufferAndGetToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	public Node getLogicalExpressionTree() {
		isArithmetic = false;
	}

	private Node getAdditiveSubtree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		Node currentRoot = getMultiplicativeSubtree();
		Token nextToken = agent.bufferAndGetToken();
		TokenType nextTokenType = nextToken.getTokenType();
		while (isMultiplicativeOperator(nextTokenType)) {
			agent.commitBufferedToken();
			Node rightSubtree = getMultiplicativeSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isArithmeticOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = agent.bufferAndGetToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	private Node getMultiplicativeSubtree() throws UndefinedReferenceException, TokenMissingException, LexerException, ExpressionCorruptedException {
		Token firstToken = agent.bufferAndGetToken();
		TokenType firstTokenType = firstToken.getTokenType();
		Node subtreeRoot;
		if (T_ARITHMETIC_ADDITIVE_MINUS.equals(firstTokenType) || T_LOGICAL_NOT.equals(firstTokenType)) {
			agent.commitBufferedToken();
			subtreeRoot = getTermSubtree();
			Node unaryRoot = new OperatorNode(subtreeRoot, null, firstToken);
			if (T_ARITHMETIC_ADDITIVE_MINUS.equals(firstTokenType)) {
				if (isArithmeticOperationPossible(unaryRoot))
					return unaryRoot;
				else
					throw new ExpressionCorruptedException(firstToken);
			}
			else {
				if (isLogicalOperationPossible(unaryRoot))
					return unaryRoot;
				else
					throw new ExpressionCorruptedException(firstToken);
			}
		}
		else
			subtreeRoot = getTermSubtree();
		return subtreeRoot;
	}

	private boolean isMultiplicativeOperator(TokenType type) {
		return T_ARITHMETIC_MULT_DIVISION.equals(type) || T_ARITHMETIC_MULT_MULTIPLICATION.equals(type)
				|| T_ARITHMETIC_MULT_MODULO.equals(type);
	}

	private boolean isAdditiveOperator(TokenType type) {
		return T_ARITHMETIC_ADDITIVE_PLUS.equals(type) || T_ARITHMETIC_ADDITIVE_MINUS.equals(type);
	}

	private boolean isArithmeticOperationPossible(Node subtreeRoot) {
		//both children should be of arithmetic type
		Node leftChild = subtreeRoot.getLeftChild();
		Node rightChild = subtreeRoot.getRightChild();
		if (rightChild == null)
			return leftChild.returnsNumericValue();
		else
			return leftChild.returnsNumericValue() && rightChild.returnsNumericValue();
	}

	private boolean isLogicalOperationPossible(Node subtreeRoot) {
		Node leftChild = subtreeRoot.getLeftChild();
		Node rightChild = subtreeRoot.getRightChild();
		OperatorNode operatorRoot = (OperatorNode) subtreeRoot;
		TokenType operatorType = operatorRoot.getOperatorToken().getTokenType();
		if (subtreeRoot.isLogicalOperatorNode()) {
			if (rightChild == null)
				return leftChild.returnsBooleanValue();
			else
				return leftChild.returnsBooleanValue() && rightChild.returnsBooleanValue();
		}
		else {
			//it's a relational operator
			if (T_RELATIONAL_EQUAL.equals(operatorType) || T_RELATIONAL_NOT_EQUAL.equals(operatorType)) {
				//if both return boolean we'll have (true==true), same when both returns numeric (false==false)
				return leftChild.returnsBooleanValue() == rightChild.returnsBooleanValue();
			}
			else
				return leftChild.returnsNumericValue() && rightChild.returnsNumericValue();
		}
	}

	private Node getTermSubtree() throws UndefinedReferenceException, TokenMissingException, LexerException {
		Token nextToken = agent.bufferAndGetToken();
		Node subtreeRoot;
		if (T_NUMERIC_CONSTANT.equals(nextToken.getTokenType())) {
			subtreeRoot = ArgumentNode.buildConstantArgumentNode(((NumericToken) nextToken).getValue());
			agent.commitBufferedToken();
		}
		else if (T_IDENTIFIER.equals(nextToken.getTokenType())) {
			String id = ((LiteralToken) nextToken).getWord();
			if (globalVariables.containsKey(id))
				subtreeRoot = ArgumentNode.buildDictionaryArgumentNode(id);
			else {
				Integer localIndex = localReferences.get(id);
				if (localReferences == null || localIndex == null)
					throw new UndefinedReferenceException(nextToken);
				subtreeRoot = ArgumentNode.buildIndexedArgumentNode(localIndex);
			}
			agent.commitBufferedToken();
		}
		else
			subtreeRoot = getParenthesisSubtree();
		return subtreeRoot;
	}

	private Node getParenthesisSubtree() throws TokenMissingException, LexerException {
		Token nextToken = agent.bufferAndGetToken();
		Node subtreeRoot;
		if (!T_LEFT_PARENTHESIS.equals(nextToken.getTokenType()))
			throw new TokenMissingException("Parenthesis expression", T_LEFT_PARENTHESIS.getLexem(), nextToken);
		agent.commitBufferedToken();
		if (isArithmetic)
			subtreeRoot = getArithmeticExpressionTree();
		else
			subtreeRoot = getLogicalExpressionTree();
		nextToken = agent.bufferAndGetToken();
		if (!T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType()))
			throw new TokenMissingException("Parenthesis expression", T_RIGHT_PARENTHESIS.getLexem(), nextToken);
		agent.commitBufferedToken();
		return subtreeRoot;
	}

}
