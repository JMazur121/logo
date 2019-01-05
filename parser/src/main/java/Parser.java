import agent.LexerAgent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import exceptions.*;
import expressions_module.parser.ExpressionParser;
import expressions_module.tree.*;
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

	private void parseInstructionBlock() {
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

	private void addInstructionToList(BaseInstruction instruction) {
		currentInstructionList.add(instruction);
		++instructionPointer;
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
		Token nextToken = agent.bufferAndGetToken();
		if (T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType()))
			return argumentsList;
		argumentsList.add(buildArithmeticExpression());
		int createdArguments = 1;
		while (createdArguments < maxArguments) {
			nextToken = agent.bufferAndGetToken();
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

	}

	private void parseForLoop() throws LexerException, ParserException {
		checkForToken(T_LEFT_PARENTHESIS, "For-loop");
		Token nextToken = agent.bufferAndGetToken();
		if (!T_IDENTIFIER.equals(nextToken.getTokenType())) {
			TokenMissingException e = new TokenMissingException("For-loop", "identifier", nextToken);
			throw new ParserException(e.getMessage());
		}
		LiteralToken index = (LiteralToken) nextToken;
		if (globalVariables.containsKey(index.getWord()) || currentLocalReferences.containsKey(index.getWord()))
			throw new ParserException("For-loop's index-identifier must be unique. Received token : " + nextToken);
		agent.commitBufferedToken();
		checkForToken(T_COMMA, "For-loop");
		ArrayList<Node> expressions = buildLimitedArgumentsList(3);
		if (expressions.isEmpty())
			throw new ParserException("Too few arguments in for-loop statement in line : " + nextToken.getPosition().getLine());
		checkForToken(T_RIGHT_PARENTHESIS, "For-loop");
		switch (expressions.size()) {
			case 1:
				parseForLoopWithDefaultStep(index, expressions);
				break;
			case 2:
				parseBoundedForLoopWithDefaultStep(index, expressions);
				break;
			default:
				parseBoundedForLoopWithDefinedStep(index, expressions);
		}
	}

	private void parseForLoopWithDefaultStep(LiteralToken identifier, ArrayList<Node> expressions) {
		//references
		int loopIndexReference = lastIndex++;
		currentLocalReferences.put(identifier.getWord(), loopIndexReference);
		int rightBoundIndex = lastIndex++;
		//init right bound with expression
		IndexedArgument rightBound = new IndexedArgument(rightBoundIndex, false);
		AssignmentInstruction rightBoundAssignment = new AssignmentInstruction(rightBound, expressions.get(0));
		addInstructionToList(rightBoundAssignment);
		//init index with zero
		ArgumentNode constZero = ArgumentNode.buildConstantArgumentNode(0);
		IndexedArgument loopIndex = new IndexedArgument(loopIndexReference, false);
		AssignmentInstruction indexAssignment = new AssignmentInstruction(loopIndex, constZero);
		addInstructionToList(indexAssignment);
		//conditional jumps
		ForConditionalJump conditionalJump = new ForConditionalJump(loopIndex, rightBound);
		int conditionalJumpIndex = instructionPointer;
		addInstructionToList(conditionalJump);
		parseInstructionBlock();
		//index incrementation
		ArgumentNode indexNode = ArgumentNode.buildIndexedArgumentNode(loopIndexReference);
		ArgumentNode constOne = ArgumentNode.buildConstantArgumentNode(1);
		Token operator = new Token(T_ARITHMETIC_ADDITIVE_PLUS, null);
		OperatorNode incrementation = new OperatorNode(indexNode, constOne, operator);
		AssignmentInstruction indexIncrement = new AssignmentInstruction(loopIndex, incrementation);
		addInstructionToList(indexIncrement);
		//jump to condition check
		Jump jumpToCheck = new Jump(conditionalJumpIndex);
		addInstructionToList(jumpToCheck);
		conditionalJump.setInstructionPointer(instructionPointer);
	}

	private void parseBoundedForLoopWithDefaultStep(LiteralToken identifier, ArrayList<Node> expressions) {
		//references
		int loopIndexReference = lastIndex++;
		currentLocalReferences.put(identifier.getWord(), loopIndexReference);
		int rightBoundIndex = lastIndex++;
		//init right bound with expression
		IndexedArgument rightBound = new IndexedArgument(rightBoundIndex, false);
		AssignmentInstruction rightBoundAssignment = new AssignmentInstruction(rightBound, expressions.get(1));
		addInstructionToList(rightBoundAssignment);
		//init index with expression
		IndexedArgument loopIndex = new IndexedArgument(loopIndexReference, false);
		AssignmentInstruction loopIndexAssignment = new AssignmentInstruction(loopIndex, expressions.get(0));
		addInstructionToList(loopIndexAssignment);
		//conditional jumps
		ForConditionalJump conditionalJump = new ForConditionalJump(loopIndex, rightBound);
		int conditionalJumpIndex = instructionPointer;
		addInstructionToList(conditionalJump);
		parseInstructionBlock();
		//index incrementation
		ArgumentNode indexNode = ArgumentNode.buildIndexedArgumentNode(loopIndexReference);
		ArgumentNode constOne = ArgumentNode.buildConstantArgumentNode(1);
		Token operator = new Token(T_ARITHMETIC_ADDITIVE_PLUS, null);
		OperatorNode incrementation = new OperatorNode(indexNode, constOne, operator);
		AssignmentInstruction indexIncrement = new AssignmentInstruction(loopIndex, incrementation);
		addInstructionToList(indexIncrement);
		//jump to condition check
		Jump jumpToCheck = new Jump(conditionalJumpIndex);
		addInstructionToList(jumpToCheck);
		conditionalJump.setInstructionPointer(instructionPointer);
	}

	private void parseBoundedForLoopWithDefinedStep(LiteralToken identifier, ArrayList<Node> expressions) {
		//references
		int loopIndexReference = lastIndex++;
		currentLocalReferences.put(identifier.getWord(), loopIndexReference);
		int rightBoundIndex = lastIndex++;
		//init right bound with expression
		IndexedArgument rightBound = new IndexedArgument(rightBoundIndex, false);
		AssignmentInstruction rightBoundAssignment = new AssignmentInstruction(rightBound, expressions.get(1));
		addInstructionToList(rightBoundAssignment);
		//init index with expression
		IndexedArgument loopIndex = new IndexedArgument(loopIndexReference, false);
		AssignmentInstruction loopIndexAssignment = new AssignmentInstruction(loopIndex, expressions.get(0));
		addInstructionToList(loopIndexAssignment);
		//conditional jumps
		ForConditionalJump conditionalJump = new ForConditionalJump(loopIndex, rightBound);
		int conditionalJumpIndex = instructionPointer;
		addInstructionToList(conditionalJump);
		parseInstructionBlock();
		//index incrementation
		ArgumentNode indexNode = ArgumentNode.buildIndexedArgumentNode(loopIndexReference);
		Token operator = new Token(T_ARITHMETIC_ADDITIVE_PLUS, null);
		OperatorNode stepAddition = new OperatorNode(indexNode, expressions.get(2), operator);
		AssignmentInstruction indexIncrement = new AssignmentInstruction(loopIndex, stepAddition);
		addInstructionToList(indexIncrement);
		//jump to condition check
		Jump jumpToCheck = new Jump(conditionalJumpIndex);
		addInstructionToList(jumpToCheck);
		conditionalJump.setInstructionPointer(instructionPointer);
	}

	private void parseWhileLoop() throws LexerException, ParserException {
		checkForToken(T_LEFT_PARENTHESIS, "While-loop");
		Node expression = buildLogicalExpression();
		checkForToken(T_RIGHT_PARENTHESIS, "While-loop");
		JumpIfNotTrue jumpIfNotTrue = new JumpIfNotTrue(expression);
		int jumpIfNotTrueIndex = instructionPointer;
		currentInstructionList.add(jumpIfNotTrue);
		++instructionPointer;
		parseInstructionBlock();
		BaseInstruction jumpToConditionCheck = new Jump(jumpIfNotTrueIndex);
		currentInstructionList.add(jumpToConditionCheck);
		++instructionPointer;
		jumpIfNotTrue.setInstructionPointer(instructionPointer);
	}

}
