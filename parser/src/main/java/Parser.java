import agent.LexerAgent;
import expressions_module.parser.ExpressionParser;
import instructions_module.composite.BaseInstruction;
import instructions_module.composite.InstructionBlock;
import lombok.Getter;
import java.io.InputStream;
import java.util.Map;

public class Parser {

	private LexerAgent agent;
	@Getter
	private boolean reachedETX;
	private ExpressionParser expressionParser;
	private final Map<String, Integer> globalVariables;

	public Parser(Map<String, Integer> globalVariables) {
		reset();
		this.globalVariables = globalVariables;
		expressionParser = new ExpressionParser(agent, globalVariables);
	}

	public void reset() {
		if (agent == null)
			agent = new LexerAgent();
		reachedETX = false;
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

}
