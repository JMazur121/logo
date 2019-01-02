package expressions_module.parser;

import agent.LexerAgent;
import exceptions.TokenBuildingException;
import exceptions.TokenMissingException;
import exceptions.UndefinedReferenceException;
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

	public Node getArithmeticExpressionTree() {
		isArithmetic = true;
	}

	public Node getLogicalExpressionTree() {
		isArithmetic = false;
	}

	private Node getAdditiveSubtree() {

	}

	private Node getMultiplicativeSubtree() throws IOException, TokenBuildingException, UndefinedReferenceException, TokenMissingException {
		Token nextToken = agent.bufferAndGetToken();
		TokenType firstTokenType = nextToken.getTokenType();
		Node subtreeRoot;
		if (T_ARITHMETIC_ADDITIVE_MINUS.equals(firstTokenType) || T_LOGICAL_NOT.equals(firstTokenType)) {
			agent.commitBufferedToken();
			subtreeRoot = getTermSubtree();
			if ()
		}
		else
			subtreeRoot = getTermSubtree();
		return subtreeRoot;
	}

	private boolean isArithmeticOperationPossible(Node subtreeRoot) {
		//both children should be of arithmetic type
		boolean isLeftChildArithmetic, isRightChildArithmetic;
		Node leftChild = subtreeRoot.getLeftChild();
		Node rightChild = subtreeRoot.getRightChild();
		isLeftChildArithmetic = (leftChild.isArgumentNode() || ((OperatorNode) leftChild).isArithmeticOperator());
		if (rightChild != null)
			isRightChildArithmetic = (rightChild.isArgumentNode() || ((OperatorNode) rightChild).isArithmeticOperator());
		else
			isRightChildArithmetic = true;
		return (isLeftChildArithmetic && isRightChildArithmetic);
	}

	private boolean isLogicalOperationPossible(Node subtreeRoot) {
		Node leftChild = subtreeRoot.getLeftChild();
		Node rightChild = subtreeRoot.getRightChild();
		OperatorNode operatorRoot = (OperatorNode) subtreeRoot;
		TokenType operatorType = operatorRoot.getOperatorToken().getTokenType();
		if (T_LOGICAL_NOT.equals(operatorType)) {
			return (!leftChild.isArgumentNode() && !((OperatorNode) leftChild).isArithmeticOperator());
		}
		else if (T_LOGICAL_AND.equals(operatorType) || T_LOGICAL_OR.equals(operatorType)) {
			boolean isLeftLogical = (leftChild.isOperatorNode() && ((OperatorNode)leftChild).isLogicalOperator());
			boolean isRightLogical = (rightChild.isOperatorNode() && ((OperatorNode)rightChild).isLogicalOperator());
			return (isLeftLogical && isRightLogical);
		}
		else if (T_RELATIONAL_EQUAL.equals(operatorType) || T_RELATIONAL_NOT_EQUAL.equals(operatorType)) {
			//both children must be arithmetic or logical
			if (leftChild.isArgumentNode() && rightChild.isArgumentNode())
				return true;
			else if (leftChild.isArgumentNode() && ((OperatorNode)rightChild).isArithmeticOperator())
				return true;
			else if (rightChild.isArgumentNode() && ((OperatorNode)leftChild).isArithmeticOperator())
				return true;
			boolean differentTypes = ((OperatorNode)leftChild).isArithmeticOperator() ^ ((OperatorNode)rightChild).isArithmeticOperator();
			//if types are different (differentTypes = true) we return false. Otherwise, we return false, so it's always !differentTypes value.
			return !differentTypes;
		}
		else {
			//both children must be of arithmetic types
			if (leftChild.isArgumentNode() && rightChild.isArgumentNode())
				return true;
			else if (leftChild.isArgumentNode() && ((OperatorNode)rightChild).isArithmeticOperator())
				return true;
			else
				return rightChild.isArgumentNode() && ((OperatorNode) leftChild).isArithmeticOperator();
		}
	}

	private Node getTermSubtree() throws IOException, TokenBuildingException, UndefinedReferenceException, TokenMissingException {
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
				if (localIndex == null)
					throw new UndefinedReferenceException(nextToken);
				subtreeRoot = ArgumentNode.buildIndexedArgumentNode(localIndex);
			}
			agent.commitBufferedToken();
		}
		else
			subtreeRoot = getParenthesisSubtree();
		return subtreeRoot;
	}

	private Node getParenthesisSubtree() throws IOException, TokenBuildingException, TokenMissingException {
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
