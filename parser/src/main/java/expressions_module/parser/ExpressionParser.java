package expressions_module.parser;

import agent.LexerAgent;
import exceptions.*;
import lombok.Setter;
import tokenizer.LiteralToken;
import tokenizer.NumericToken;
import tokenizer.Token;
import tokenizer.TokenType;
import tree.ArgumentNode;
import tree.Node;
import tree.OperatorNode;
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
		return getArithmeticSubtree();
	}

	public Node getLogicalExpressionTree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		isArithmetic = false;
		Node currentRoot = getAlternativeSubtree();
		Token nextToken = agent.bufferAndGetToken();
		TokenType nextTokenType = nextToken.getTokenType();
		while (T_LOGICAL_OR.equals(nextTokenType)) {
			agent.commitBufferedToken();
			Node rightSubtree = getAlternativeSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isLogicalOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = agent.bufferAndGetToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	private Token getNextToken() throws LexerException, ExpressionCorruptedException {
		if (isArithmetic) {
			Token nextToken = agent.bufferAndGetToken();
			TokenType type = nextToken.getTokenType();
			if (type.isRelationalOperator() || type.isLogicalOperator())
				throw new ExpressionCorruptedException(nextToken);
			return nextToken;
		}
		else
			return agent.bufferAndGetToken();
	}

	private Node getAlternativeSubtree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		Node currentRoot = getConjunctionSubtree();
		Token nextToken = getNextToken();
		if (!currentRoot.returnsBooleanValue())
			throw new ExpressionCorruptedException(nextToken);
		TokenType nextTokenType = nextToken.getTokenType();
		while (T_LOGICAL_AND.equals(nextTokenType)) {
			agent.commitBufferedToken();
			Node rightSubtree = getConjunctionSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isLogicalOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = getNextToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	private Node getConjunctionSubtree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		Node currentRoot = getArithmeticSubtree();
		Token nextToken = getNextToken();
		TokenType nextTokenType = nextToken.getTokenType();
		while (nextTokenType.isRelationalOperator()) {
			agent.commitBufferedToken();
			Node rightSubtree = getArithmeticSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isLogicalOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = getNextToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	private Node getArithmeticSubtree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		Node currentRoot = getAdditiveSubtree();
		Token nextToken = getNextToken();
		TokenType nextTokenType = nextToken.getTokenType();
		while (nextTokenType.isAdditiveOperator()) {
			agent.commitBufferedToken();
			Node rightSubtree = getAdditiveSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isArithmeticOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = getNextToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	private Node getAdditiveSubtree() throws LexerException, ExpressionCorruptedException, UndefinedReferenceException, TokenMissingException {
		Node currentRoot = getMultiplicativeSubtree();
		Token nextToken = getNextToken();
		TokenType nextTokenType = nextToken.getTokenType();
		while (nextTokenType.isMultiplicativeOperator()) {
			agent.commitBufferedToken();
			Node rightSubtree = getMultiplicativeSubtree();
			currentRoot = new OperatorNode(currentRoot, rightSubtree, nextToken);
			if (!isArithmeticOperationPossible(currentRoot))
				throw new ExpressionCorruptedException(nextToken);
			nextToken = getNextToken();
			nextTokenType = nextToken.getTokenType();
		}
		return currentRoot;
	}

	private Node getMultiplicativeSubtree() throws UndefinedReferenceException, TokenMissingException, LexerException, ExpressionCorruptedException {
		Token firstToken = getNextToken();
		TokenType firstTokenType = firstToken.getTokenType();
		Node subtreeRoot;
		if (T_ARITHMETIC_ADDITIVE_MINUS.equals(firstTokenType)) {
			agent.commitBufferedToken();
			subtreeRoot = getTermSubtree();
			Node unaryRoot = new OperatorNode(subtreeRoot, null, firstToken);
			if (!isArithmeticOperationPossible(unaryRoot))
				throw new ExpressionCorruptedException(firstToken);
			return unaryRoot;
		}
		else if (T_LOGICAL_NOT.equals(firstTokenType)) {
			agent.commitBufferedToken();
			Token nextToken = getNextToken();
			if (!T_LEFT_PARENTHESIS.equals(nextToken.getTokenType()))
				throw new TokenMissingException("Logical expression", T_LEFT_PARENTHESIS.getLexem(), nextToken);
			subtreeRoot = getTermSubtree();
			Node unaryRoot = new OperatorNode(subtreeRoot, null, firstToken);
			if (!isLogicalOperationPossible(unaryRoot))
				throw new ExpressionCorruptedException(firstToken);
			return unaryRoot;
		}
		else
			return getTermSubtree();
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

	private Node getTermSubtree() throws UndefinedReferenceException, TokenMissingException, LexerException, ExpressionCorruptedException {
		Token nextToken = getNextToken();
		TokenType nextTokenType = nextToken.getTokenType();
		Node subtreeRoot;
		if (T_NUMERIC_CONSTANT.equals(nextTokenType)) {
			subtreeRoot = ArgumentNode.buildConstantArgumentNode(((NumericToken) nextToken).getValue());
			agent.commitBufferedToken();
		}
		else if (T_IDENTIFIER.equals(nextTokenType)) {
			String id = ((LiteralToken) nextToken).getWord();
			if (globalVariables.containsKey(id))
				subtreeRoot = ArgumentNode.buildDictionaryArgumentNode(id);
			else {
				Integer localIndex;
				if (localReferences == null || (localIndex = localReferences.get(id)) == null)
					throw new UndefinedReferenceException(nextToken);
				subtreeRoot = ArgumentNode.buildIndexedArgumentNode(localIndex);
			}
			agent.commitBufferedToken();
		}
		else {
			if (T_LEFT_PARENTHESIS.equals(nextTokenType)) {
				agent.commitBufferedToken();
				subtreeRoot = getParenthesisSubtree();
			}
			else
				throw new TokenMissingException("Arithmetic expression", "Value, identifier or \"(\"", nextToken);
		}
		return subtreeRoot;
	}

	private Node getParenthesisSubtree() throws TokenMissingException, LexerException, UndefinedReferenceException, ExpressionCorruptedException {
		Node subtreeRoot;
		if (isArithmetic)
			subtreeRoot = getArithmeticExpressionTree();
		else
			subtreeRoot = getLogicalExpressionTree();
		Token nextToken = getNextToken();
		if (!T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType()))
			throw new TokenMissingException("Parenthesis expression", T_RIGHT_PARENTHESIS.getLexem(), nextToken);
		agent.commitBufferedToken();
		return subtreeRoot;
	}

}
