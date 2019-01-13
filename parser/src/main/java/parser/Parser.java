package parser;

import agent.LexerAgent;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import exceptions.*;
import expressions_module.parser.ExpressionParser;
import instructions.*;
import lombok.Getter;
import scope.Scope;
import tokenizer.LiteralToken;
import tokenizer.Token;
import tokenizer.TokenType;
import tree.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private boolean isGlobalScope;

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
				.put("wypisz", 1)
				.build();
	}

	public Parser(Map<String, Integer> globalVariables, Map<String, Scope> knownMethods) {
		agent = new LexerAgent();
		expressionParser = new ExpressionParser(agent, globalVariables);
		this.globalVariables = globalVariables;
		this.knownMethods = knownMethods;
		reset();
	}

	public void reset() {
		reachedETX = false;
		resetLocalReferences();
		agent.restart();
	}

	public void handleStream(InputStream inputStream) {
		reset();
		agent.handleStream(inputStream);
	}

	/**
	 * Main function
	 *
	 * @return Executable scope or procedure definition
	 */
	public Scope getNetScope() throws LexerException, ParserException {
		if (reachedETX)
			return null;
		Token nextToken = agent.bufferAndGetToken();
		if (T_CONTROL_ETX.equals(nextToken.getTokenType())) {
			reachedETX = true;
			return null;
		}
		resetLocalReferences();
		if (T_KEYWORD_PROC_DEFINITION.equals(nextToken.getTokenType())) {
			isGlobalScope = false;
			agent.commitBufferedToken();
			Scope methodScope = new Scope();
			parseProcedureDefinition(methodScope);
			return methodScope;
		}
		else {
			isGlobalScope = true;
			parseSingleInstruction();
			return Scope.builder()
					.numberOfArguments(0)
					.numberOfLocalVariables(currentLocalReferences.size())
					.isFunctionDefinition(false)
					.instructions(currentInstructionList)
					.build();
		}
	}

	private void parseProcedureDefinition(Scope methodScope) throws LexerException, ParserException {
		Token nextToken = agent.bufferAndGetToken();
		if (T_IDENTIFIER.equals(nextToken.getTokenType())) {
			LiteralToken identifier = (LiteralToken) nextToken;
			if (knownMethods.containsKey(identifier.getWord()))
				throw new ParserException("Procedure name already in use : " + identifier);
			agent.commitBufferedToken();
			checkForToken(T_LEFT_PARENTHESIS, "Procedure definition");
			nextToken = agent.bufferAndGetToken();
			if (T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType())) {
				agent.commitBufferedToken();
				methodScope.setNumberOfArguments(0);
			}
			else {
				buildUnlimitedArgumentsList();
				checkForToken(T_RIGHT_PARENTHESIS, "Procedure definition");
				methodScope.setNumberOfArguments(currentLocalReferences.size());
			}
			knownMethods.put(identifier.getWord(), methodScope);
			try {
				parseInstructionBlock();
			} catch (Exception e) {
				knownMethods.remove(identifier.getWord());
				throw new ParserException(e.getMessage());
			}
			methodScope.setFunctionDefinition(true);
			methodScope.setInstructions(currentInstructionList);
			methodScope.setNumberOfLocalVariables(currentLocalReferences.size());
		}
		else
			throw new ParserException("Expected method identifier but found : " + nextToken);
	}

	private void parseInstructionBlock() throws LexerException, ParserException {
		isGlobalScope = false;
		checkForToken(T_LEFT_SQUARE_BRACKET, "Instruction block");
		Token nextToken = agent.bufferAndGetToken();
		if (T_RIGHT_SQUARE_BRACKET.equals(nextToken.getTokenType()))
			throw new ParserException("Empty instruction block at line : " + nextToken.getPosition().getLine());
		do {
			parseSingleInstruction();
			nextToken = agent.bufferAndGetToken();
			if (T_CONTROL_ETX.equals(nextToken.getTokenType()))
				throw new ParserException("Reached ETX before end of instruction block at line : " + nextToken.getPosition().getLine());
		} while (!T_RIGHT_SQUARE_BRACKET.equals(nextToken.getTokenType()));
		agent.commitBufferedToken();
	}

	private void parseSingleInstruction() throws LexerException, ParserException {
		Token nextToken = agent.bufferAndGetToken();
		TokenType type = nextToken.getTokenType();
		agent.commitBufferedToken();
		switch (type) {
			case T_IDENTIFIER:
				LiteralToken identifier = (LiteralToken) nextToken;
				nextToken = agent.bufferAndGetToken();
				agent.commitBufferedToken();
				if (T_ASSIGNMENT.equals(nextToken.getTokenType()))
					parseAssignmentInstruction(identifier);
				else if (T_LEFT_PARENTHESIS.equals(nextToken.getTokenType()))
					parseFunctionCall(identifier);
				else
					throw new ParserException("Expected \":=\" or \"(\", but found" + nextToken);
				break;
			case T_KEYWORD_ITERATION_LOOP:
				parseForLoop();
				break;
			case T_KEYWORD_WHILE_LOOP:
				parseWhileLoop();
				break;
			case T_KEYWORD_IF:
				parseConditionalInstruction();
				break;
			default:
				throw new ParserException("Expected new instruction but found : " + nextToken);
		}
	}

	private void checkForToken(TokenType expected, String parsedExpression) throws LexerException, ParserException {
		Token nextToken = agent.bufferAndGetToken();
		if (expected.equals(nextToken.getTokenType()))
			agent.commitBufferedToken();
		else {
			throw new ParserException("Error while parsing " + parsedExpression + ". expected: " + expected.getLexem()
					+ " but got " + nextToken);
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

	private void parseAssignmentInstruction(LiteralToken identifier) throws LexerException, ParserException {
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
				instruction = new AssignmentInstruction(new IndexedArgument(localReference, false), expression);
			}
			else {
				expression = buildArithmeticExpression();
				instruction = new AssignmentInstruction(new DictionaryArgument(id), expression);
			}
		}
		addInstructionToList(instruction);
	}

	private void buildUnlimitedArgumentsList() throws LexerException, ParserException {
		Token nextToken = agent.bufferAndGetToken();
		if (T_IDENTIFIER.equals(nextToken.getTokenType())) {
			nextToken = addToLocalReferencesAndGetNext((LiteralToken) nextToken);
			boolean isWorkToDo = true;
			TokenType type = nextToken.getTokenType();
			while (isWorkToDo) {
				if (T_CONTROL_ETX.equals(type))
					throw new ParserException("Found ETX before end of arguments' list at line : " + nextToken.getPosition().getLine());
				if (T_RIGHT_PARENTHESIS.equals(type))
					isWorkToDo = false;
				else {
					checkForToken(T_COMMA, "Method-definition's arguments list");
					nextToken = agent.bufferAndGetToken();
					type = nextToken.getTokenType();
					if (T_IDENTIFIER.equals(type)) {
						nextToken = addToLocalReferencesAndGetNext((LiteralToken) nextToken);
						type = nextToken.getTokenType();
					}
					else
						throw new ParserException("Expected argument's identifier, but found : " + nextToken);
				}
			}
		}
		else
			throw new ParserException("Expected argument's identifier, but found : " + nextToken);
	}

	private Token addToLocalReferencesAndGetNext(LiteralToken token) throws ParserException, LexerException {
		if (currentLocalReferences.containsKey(token.getWord()))
			throw new ParserException("Argument's identifier already in use : " + token);
		currentLocalReferences.put(token.getWord(), lastIndex++);
		return commitAndGetNext();
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
				instruction = new FunctionCall(id, Lists.newArrayList(), true);
			}
			else {
				ArrayList<Node> arguments = buildLimitedArgumentsList(embeddedMethodArguments);
				checkForToken(T_RIGHT_PARENTHESIS, "Function-call");
				instruction = new FunctionCall(id, arguments, true);
			}
		}
		else {
			Scope method = knownMethods.get(id);
			if (method == null)
				throw new ParserException("Undefined reference to : " + identifier);
			Token nextToken = agent.bufferAndGetToken();
			if (T_RIGHT_PARENTHESIS.equals(nextToken.getTokenType())) {
				if (method.getNumberOfArguments() == 0)
					instruction = new FunctionCall(id, Lists.newArrayList(), false);
				else {
					TokenMissingException e = new TokenMissingException("Non-zero arguments function call", "function argument", nextToken);
					throw new ParserException(e.getMessage());
				}
			}
			else {
				ArrayList<Node> arguments = buildArgumentsList(method.getNumberOfArguments());
				checkForToken(T_RIGHT_PARENTHESIS, "Function call");
				instruction = new FunctionCall(id, arguments, false);
			}
		}
		addInstructionToList(instruction);
	}

	private void parseConditionalInstruction() throws LexerException, ParserException {
		checkForToken(T_LEFT_PARENTHESIS, "Conditional instruction");
		Node logicalExpression = buildLogicalExpression();
		checkForToken(T_RIGHT_PARENTHESIS, "Conditional instruction");
		JumpIfNotTrue jumpIfNotTrue = new JumpIfNotTrue(logicalExpression);
		addInstructionToList(jumpIfNotTrue);
		Token nextToken = agent.bufferAndGetToken();
		if (T_LEFT_SQUARE_BRACKET.equals(nextToken.getTokenType()))
			parseInstructionBlock();
		else
			parseSingleInstruction();
		Jump jumpFromConditionalTree = new Jump();
		addInstructionToList(jumpFromConditionalTree);
		jumpIfNotTrue.setInstructionPointer(instructionPointer);
		nextToken = agent.bufferAndGetToken();
		if (T_KEYWORD_ELSE.equals(nextToken.getTokenType())) {
			agent.commitBufferedToken();
			nextToken = agent.bufferAndGetToken();
			if (T_KEYWORD_IF.equals(nextToken.getTokenType())) {
				agent.commitBufferedToken();
				parseConditionalInstruction();
			}
			else if (T_LEFT_SQUARE_BRACKET.equals(nextToken.getTokenType()))
				parseInstructionBlock();
			else
				parseSingleInstruction();
		}
		jumpFromConditionalTree.setInstructionPointer(instructionPointer);
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

	private IndexedArgument addIndexedArgumentAssignment(int index, Node expression) {
		IndexedArgument leftValue = new IndexedArgument(index, false);
		AssignmentInstruction instruction = new AssignmentInstruction(leftValue, expression);
		addInstructionToList(instruction);
		return leftValue;
	}

	private void addSimpleAddition(int leftIndex, Node expression) {
		ArgumentNode indexNode = ArgumentNode.buildIndexedArgumentNode(leftIndex);
		OperatorNode addition = new OperatorNode(indexNode, expression, T_ARITHMETIC_ADDITIVE_PLUS);
		IndexedArgument leftValue = new IndexedArgument(leftIndex, false);
		AssignmentInstruction assignmentInstruction = new AssignmentInstruction(leftValue, addition);
		addInstructionToList(assignmentInstruction);
	}

	private void parseForLoopWithDefaultStep(LiteralToken identifier, ArrayList<Node> expressions) throws LexerException, ParserException {
		//references
		int loopIndexReference = lastIndex++;
		currentLocalReferences.put(identifier.getWord(), loopIndexReference);
		int rightBoundIndex = lastIndex++;
		//init right bound with expression
		IndexedArgument rightBound = addIndexedArgumentAssignment(rightBoundIndex, expressions.get(0));
		//init index with zero
		ArgumentNode constZero = ArgumentNode.buildConstantArgumentNode(0);
		IndexedArgument loopIndex = addIndexedArgumentAssignment(loopIndexReference, constZero);
		//conditional jumps
		ForConditionalJump conditionalJump = new ForConditionalJump(loopIndex, rightBound);
		int conditionalJumpIndex = instructionPointer;
		addInstructionToList(conditionalJump);
		parseInstructionBlock();
		//index incrementation
		addSimpleAddition(loopIndexReference, ArgumentNode.buildConstantArgumentNode(1));
		//jump to condition check
		Jump jumpToCheck = new Jump(conditionalJumpIndex);
		addInstructionToList(jumpToCheck);
		conditionalJump.setInstructionPointer(instructionPointer);
	}

	private void parseBoundedForLoopWithDefaultStep(LiteralToken identifier, ArrayList<Node> expressions) throws LexerException, ParserException {
		//references
		int loopIndexReference = lastIndex++;
		currentLocalReferences.put(identifier.getWord(), loopIndexReference);
		int rightBoundIndex = lastIndex++;
		//init right bound with expression
		IndexedArgument rightBound = addIndexedArgumentAssignment(rightBoundIndex, expressions.get(1));
		//init index with expression
		IndexedArgument loopIndex = addIndexedArgumentAssignment(loopIndexReference, expressions.get(0));
		//conditional jumps
		ForConditionalJump conditionalJump = new ForConditionalJump(loopIndex, rightBound);
		int conditionalJumpIndex = instructionPointer;
		addInstructionToList(conditionalJump);
		parseInstructionBlock();
		//index incrementation
		addSimpleAddition(loopIndexReference, ArgumentNode.buildConstantArgumentNode(1));
		//jump to condition check
		Jump jumpToCheck = new Jump(conditionalJumpIndex);
		addInstructionToList(jumpToCheck);
		conditionalJump.setInstructionPointer(instructionPointer);
	}

	private void parseBoundedForLoopWithDefinedStep(LiteralToken identifier, ArrayList<Node> expressions) throws LexerException, ParserException {
		//references
		int loopIndexReference = lastIndex++;
		currentLocalReferences.put(identifier.getWord(), loopIndexReference);
		int rightBoundIndex = lastIndex++;
		//init right bound with expression
		IndexedArgument rightBound = addIndexedArgumentAssignment(rightBoundIndex, expressions.get(1));
		//init index with expression
		IndexedArgument loopIndex = addIndexedArgumentAssignment(loopIndexReference, expressions.get(0));
		//conditional jumps
		ForConditionalJump conditionalJump = new ForConditionalJump(loopIndex, rightBound);
		int conditionalJumpIndex = instructionPointer;
		addInstructionToList(conditionalJump);
		parseInstructionBlock();
		//index incrementation
		addSimpleAddition(loopIndexReference, expressions.get(2));
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
		addInstructionToList(jumpIfNotTrue);
		parseInstructionBlock();
		Jump jumpToConditionCheck = new Jump(jumpIfNotTrueIndex);
		addInstructionToList(jumpToConditionCheck);
		jumpIfNotTrue.setInstructionPointer(instructionPointer);
	}

}
