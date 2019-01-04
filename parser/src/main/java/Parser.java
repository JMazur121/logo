import agent.LexerAgent;
import expressions_module.parser.ExpressionParser;
import instructions_module.composite.*;
import lombok.Getter;
import java.io.InputStream;
import java.util.Map;

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

	public InstructionBlock getProcedureDefinition() {
		return null;
	}

	public BaseInstruction getNextInstruction() {
		return null;
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
