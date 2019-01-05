import agent.LexerAgent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import exceptions.*;
import expressions_module.parser.ExpressionParser;
import expressions_module.tree.DictionaryArgument;
import expressions_module.tree.IndexedArgument;
import expressions_module.tree.Node;
import instructions_module.composite.*;
import instructions_module.scope.Scope;
import lombok.Getter;
import tokenizer.LiteralToken;
import tokenizer.Token;
import tokenizer.TokenType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static tokenizer.TokenType.*;

public class Parser {

	private LexerAgent agent;
	@Getter
	private boolean reachedETX;
	private ExpressionParser expressionParser;
	private final Map<String, Integer> globalVariables;
	private final Map<String, Scope> knownMethods;
	private Map<String, Integer> currentLocalReferences;
	public static final Map<String, Integer> embeddedMethods;
	private int lastIndex;
	private int instructionPointer;
	private ArrayList<BaseInstruction> currentInstructionList;

	static {
		embeddedMethods = ImmutableMap.<String, Integer>builder()
				.put("naprzod", 1)
				.put("wstecz", 1)
				.put("lewo", 1)
				.put("prawo", 1)
				.put("czysc", 0)
				.put("podnies", 0)
				.put("opusc", 0)
				.put("zamaluj", 0)
				.put("kolorPisaka", 3)
				.put("kolorMalowania", 3)
				.put("paleta", 4)
				.put("foremny", 1)
				.put("okrag", 1)
				.put("kolo", 1)
				.put("skok", 2)
				.put("stop", 0)
				.build();
	}

	public Parser(Map<String, Integer> globalVariables, Map<String, Scope> knownMethods) {
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

	/**
	 * Main function
	 * @return Executable scope or procedure definition
	 */
	public Scope getNetScope() throws LexerException {
		if (reachedETX)
			return null;
		Token nextToken = agent.bufferAndGetToken();
		if (T_CONTROL_ETX.equals(nextToken.getTokenType())) {
			reachedETX = true;
			return null;
		}
	}

	public InstructionBlock getProcedureDefinition() throws LexerException, ParserException {
		if (T_KEYWORD_PROC_DEFINITION.equals(nextToken.getTokenType())) {
			nextToken = commitAndGetNext();
			if (!T_IDENTIFIER.equals(nextToken.getTokenType())) {
				TokenMissingException e = new TokenMissingException("Procedure definition", "identifier", nextToken);
				throw new ParserException(e.getMessage());
			}
			String procedureIdentifier = ((LiteralToken) nextToken).getWord();
			agent.commitBufferedToken();
			checkForToken(T_LEFT_PARENTHESIS, "Procedure definition");
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

	private InstructionBlock buildInstructionBlock() {
		// TODO: 2019-01-05 Najbardziej potrzebna funkcja
		//first check for [
		//then try to parse as many instructions as possible
		//check for ]
		//return block
	}

	private void checkForToken(TokenType expected, String parsedExpression) throws LexerException, ParserException {
		Token nextToken = agent.bufferAndGetToken();
		if (expected.equals(nextToken.getTokenType()))
			agent.commitBufferedToken();
		else {
			TokenMissingException e = new TokenMissingException(parsedExpression, expected.getLexem(), nextToken);
			throw new ParserException(e.getMessage());
		}

	}

	private Node buildArithmeticExpression() throws ParserException {
		try {
			return expressionParser.getArithmeticExpressionTree();
		} catch (LexerException | ExpressionCorruptedException | TokenMissingException | UndefinedReferenceException e) {
			throw new ParserException(e.getMessage());
		}
	}

	private Node buildLogicalExpression() throws ParserException {
		try {
			return expressionParser.getLogicalExpressionTree();
		} catch (LexerException | ExpressionCorruptedException | TokenMissingException | UndefinedReferenceException e) {
			throw new ParserException(e.getMessage());
		}
	}

	private Token commitAndGetNext() throws LexerException {
		agent.commitBufferedToken();
		return agent.bufferAndGetToken();
	}

	private void resetLocalReferences() {
		lastIndex = 0;
		currentLocalReferences = new HashMap<>();
		instructionPointer = 0;
		currentInstructionList = new ArrayList<>();
		expressionParser.setLocalReferences(currentLocalReferences);
	}

	private void parseAssignmentInstruction(boolean isGlobalScope, LiteralToken identifier) throws LexerException, ParserException {
		checkForToken(T_ASSIGNMENT, "Assignment instruction");
		String id = identifier.getWord();
		Node expression;
		AssignmentInstruction instruction;
		if (isGlobalScope) {
			globalVariables.putIfAbsent(id, 0);
			expression = buildArithmeticExpression();
			instruction = new AssignmentInstruction(new DictionaryArgument(id), expression);
		}
		else {
			Integer globalVariable = globalVariables.get(id);
			if (globalVariable == null) {
				Integer localReference = currentLocalReferences.computeIfAbsent(id, k -> lastIndex++);
				expression = buildArithmeticExpression();
				instruction = new AssignmentInstruction(new IndexedArgument(localReference,false), expression);
			}
			else {
				expression = buildArithmeticExpression();
				instruction = new AssignmentInstruction(new DictionaryArgument(identifier.getWord()), expression);
			}
		}
		currentInstructionList.add(instruction);
		++instructionPointer;
	}

	private ArrayList<Node> buildArgumentsList(int expectedArgumentsListSize) throws ParserException, LexerException {
		ArrayList<Node> argumentsList = new ArrayList<>(expectedArgumentsListSize);
		argumentsList.add(buildArithmeticExpression());
		--expectedArgumentsListSize;
		while (expectedArgumentsListSize > 0) {
			checkForToken(T_COMMA, "Function-call's arguments list");
			argumentsList.add(buildArithmeticExpression());
			--expectedArgumentsListSize;
		}
		return argumentsList;
	}

	private ArrayList<Node> buildLimitedArgumentsList(int maxArguments) throws ParserException, LexerException {
		ArrayList<Node> argumentsList = new ArrayList<>(maxArguments);
		argumentsList.add(buildArithmeticExpression());
		int createdArguments = 1;
		while (createdArguments < maxArguments) {
			Token nextToken = agent.bufferAndGetToken();
			if (T_COMMA.equals(nextToken.getTokenType())) {
				agent.commitBufferedToken();
				argumentsList.add(buildArithmeticExpression());
				++createdArguments;
			}
			else if (T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType()))
				break;
			else
				throw new ParserException("Unexpected token while parsing an arguments list : " + nextToken);
		}
		return argumentsList;
	}

	private void parseFunctionCall(LiteralToken identifier) throws LexerException, ParserException {
		String id = identifier.getWord();
		BaseInstruction instruction;
		Integer embeddedMethodArguments = embeddedMethods.get(id);
		if (embeddedMethodArguments != null) {
			if (embeddedMethodArguments == 0) {
				checkForToken(T_RIGHT_PARENTHESIS, "Function-call");
				instruction = new FunctionCall(identifier, null, true);
			}
			else {
				ArrayList<Node> arguments = buildLimitedArgumentsList(embeddedMethodArguments);
				checkForToken(T_RIGHT_PARENTHESIS, "Function-call");
				instruction = new FunctionCall(identifier, arguments, true);
			}
		}
		else {
			Scope method = knownMethods.get(id);
			if (method == null)
				throw new ParserException("Undefined reference to : " + identifier);
			Token nextToken = agent.bufferAndGetToken();
			if (T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType())) {
				if (method.getNumberOfArguments() == 0)
					instruction = new FunctionCall(identifier, null, false);
				else {
					TokenMissingException e = new TokenMissingException("Non-zero arguments function call", "function argument", nextToken);
					throw new ParserException(e.getMessage());
				}
			}
			else {
				ArrayList<Node> arguments = buildArgumentsList(method.getNumberOfArguments());
				checkForToken(T_RIGHT_PARENTHESIS, "Function call");
				instruction = new FunctionCall(identifier, arguments, false);
			}
		}
		currentInstructionList.add(instruction);
		++instructionPointer;
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
