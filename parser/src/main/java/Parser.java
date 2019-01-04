import agent.LexerAgent;
import exceptions.LexerException;
import exceptions.TokenMissingException;
import expressions_module.parser.ExpressionParser;
import instructions_module.composite.*;
import lombok.Getter;
import tokenizer.LiteralToken;
import tokenizer.Token;
import java.io.InputStream;
import java.util.Map;
import static tokenizer.TokenType.*;

public class Parser {

	private LexerAgent agent;
	@Getter
	private boolean reachedETX;
	private ExpressionParser expressionParser;
	private final Map<String, Integer> globalVariables;
	private final Map<String, InstructionBlock> knownMethods;
	private Map<String, Integer> currentLocalReferences;
	private int lastIndex;

	public Parser(Map<String, Integer> globalVariables, Map<String, InstructionBlock> knownMethods) {
		reset();
		this.globalVariables = globalVariables;
		this.knownMethods = knownMethods;
		expressionParser = new ExpressionParser(agent, globalVariables);
	}

	public void reset() {
		if (agent == null)
			agent = new LexerAgent();
		reachedETX = false;
		resetLocalReferences();
		agent.restart();
	}

	public void handleStream(InputStream inputStream) {
		agent.handleStream(inputStream);
	}

	public InstructionBlock getProcedureDefinition() throws LexerException, TokenMissingException {
		if (reachedETX)
			return null;
		Token nextToken = agent.bufferAndGetToken();
		if (T_CONTROL_ETX.equals(nextToken.getTokenType())) {
			reachedETX = true;
			return null;
		}
		if (T_KEYWORD_PROC_DEFINITION.equals(nextToken.getTokenType())) {
			nextToken = commitAndGetNext();
			if (!T_IDENTIFIER.equals(nextToken.getTokenType()))
				throw new TokenMissingException("Procedure definition", "identifier", nextToken);
			String procedureIdentifier = ((LiteralToken) nextToken).getWord();
			agent.commitBufferedToken();
			checkLeftParenthesis("Procedure definition");
			nextToken = agent.bufferAndGetToken();
			if (T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType())) {
				agent.commitBufferedToken();
				return buildProcedureWithZeroArguments(procedureIdentifier);
			}
			return buildProcedureWithNonZeroArguments(procedureIdentifier);
		}
		else
			return null;
	}

	public BaseInstruction getNextInstruction() throws LexerException {
		if (reachedETX)
			return null;
		Token nextToken = agent.bufferAndGetToken();
		if (T_CONTROL_ETX.equals(nextToken.getTokenType())) {
			reachedETX = true;
			return null;
		}
	}

	private void checkLeftParenthesis(String parsedExpression) throws LexerException, TokenMissingException {
		Token nextToken = agent.bufferAndGetToken();
		if (T_LEFT_PARENTHESIS.equals(nextToken.getTokenType()))
			agent.commitBufferedToken();
		else
			throw new TokenMissingException(parsedExpression, T_LEFT_PARENTHESIS.getLexem(), nextToken);

	}

	private Token commitAndGetNext() throws LexerException {
		agent.commitBufferedToken();
		return agent.bufferAndGetToken();
	}

	private void resetLocalReferences() {
		lastIndex = 0;
		currentLocalReferences = null;
	}

	private AssignmentInstruction parseAssignmentInstruction(boolean isGlobalScope) {
		return null;
	}

	private FunctionCall parseFunctionCall() {
		return null;
	}

	private ConditionalInstruction parseConditionalInstruction() {
		return null;
	}

	private ForLoop parseForLoop() {
		return null;
	}

	private WhileLoop parseWhileLoop() {
		return null;
	}

}
