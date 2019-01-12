package parser_tests;

import exceptions.LexerException;
import exceptions.ParserException;
import expressions_module.tree.Node;
import instructions_module.composite.BaseInstruction;
import instructions_module.composite.FunctionCall;
import instructions_module.scope.Scope;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import parser.Parser;
import static org.assertj.core.api.Assertions.*;

public class InstructionsBuildingTest {

	private Map<String, Integer> globalVars = new HashMap<>();
	private Map<String, Scope> knownMethods = new HashMap<>();
	private Parser parser = new Parser(globalVars, knownMethods);


	@Test
	public void getNextScope_givenEmptyStream_reachedETX() throws LexerException, ParserException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
		parser.reset();
		parser.handleStream(is);
		//when
		Scope scope = parser.getNetScope();
		//then
		assertThat(scope).isNull();
		assertThat(parser.isReachedETX()).isTrue();
	}

	@Test
	public void getNextScope_embeddedInstructionCallWithZeroArguments_returnsCall() throws LexerException, ParserException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("czysc()".getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNetScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(1);
		BaseInstruction call = instructions.get(0);
		assertThat(call).isInstanceOf(FunctionCall.class);
		FunctionCall functionCall = (FunctionCall)call;
		assertThat(functionCall.getIdentifier()).isEqualTo("czysc");
		assertThat(functionCall.hasArguments()).isFalse();
		assertThat(functionCall.isEmbeddedMethodCall()).isTrue();
	}

	@Test
	public void getNextScope_embeddedInstructionWithArguments_returnsCall() throws LexerException, ParserException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("skok(10,20)".getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNetScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(1);
		BaseInstruction call = instructions.get(0);
		assertThat(call).isInstanceOf(FunctionCall.class);
		FunctionCall functionCall = (FunctionCall)call;
		assertThat(functionCall.getIdentifier()).isEqualTo("skok");
		assertThat(functionCall.hasArguments()).isTrue();
		assertThat(functionCall.getArguments()).hasSize(2);
		assertThat(functionCall.isEmbeddedMethodCall()).isTrue();
	}

}
