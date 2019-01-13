package parser_tests;

import exceptions.LexerException;
import exceptions.ParserException;
import instructions.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import parser.Parser;
import scope.Scope;
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
		Scope scope = parser.getNextScope();
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
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(1);
		BaseInstruction call = instructions.get(0);
		assertThat(call).isInstanceOf(FunctionCall.class);
		FunctionCall functionCall = (FunctionCall) call;
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
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(1);
		BaseInstruction call = instructions.get(0);
		assertThat(call).isInstanceOf(FunctionCall.class);
		FunctionCall functionCall = (FunctionCall) call;
		assertThat(functionCall.getIdentifier()).isEqualTo("skok");
		assertThat(functionCall.hasArguments()).isTrue();
		assertThat(functionCall.getArguments()).hasSize(2);
		assertThat(functionCall.isEmbeddedMethodCall()).isTrue();
	}

	@Test
	public void getNextScope_globalAssignment_returnsAssignmentScope() throws LexerException, ParserException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("var1 := 100".getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(1);
		BaseInstruction assignment = instructions.get(0);
		assertThat(assignment).isInstanceOf(AssignmentInstruction.class);
		AssignmentInstruction instruction = (AssignmentInstruction) assignment;
		assertThat(instruction.getTarget().readKey()).isEqualTo("var1");
		assertThat(instruction.getArithmeticExpression().isArgumentNode()).isTrue();
	}

	@Test
	public void getNextScope_simpleIf_returnsConditionalScope() throws LexerException, ParserException {
		//before
		globalVars.put("var2", 10);
		ByteArrayInputStream is = new ByteArrayInputStream("jesli(var2 > 0) stop()".getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(3);
		JumpIfNotTrue jint = (JumpIfNotTrue) instructions.get(0);
		assertThat(jint.getInstructionPointer()).isEqualTo(3);
		FunctionCall call = (FunctionCall) instructions.get(1);
		assertThat(call.getIdentifier()).isEqualTo("stop");
		Jump jump = (Jump) instructions.get(2);
		assertThat(jump.getInstructionPointer()).isEqualTo(3);
	}

	@Test
	public void getNextScope_compositeIf_returnsConditionalScope() throws LexerException, ParserException {
		//before
		globalVars.put("var3", 100);
		ByteArrayInputStream is = new ByteArrayInputStream(("jesli(var3 > 0) stop() " +
				"wpw jesli(var3 < 0) [czysc()] wpw stop()").getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(7);
		JumpIfNotTrue ifTest = (JumpIfNotTrue)instructions.get(0);
		assertThat(ifTest.getInstructionPointer()).isEqualTo(3);
		FunctionCall firstCall = (FunctionCall)instructions.get(1);
		assertThat(firstCall.getIdentifier()).isEqualTo("stop");
		Jump ifJump = (Jump)instructions.get(2);
		assertThat(ifJump.getInstructionPointer()).isEqualTo(7);
		JumpIfNotTrue elseIfTest = (JumpIfNotTrue)instructions.get(3);
		assertThat(elseIfTest.getInstructionPointer()).isEqualTo(6);
		FunctionCall secondCall = (FunctionCall)instructions.get(4);
		assertThat(secondCall.getIdentifier()).isEqualTo("czysc");
		Jump elseIfJump = (Jump)instructions.get(5);
		assertThat(elseIfJump.getInstructionPointer()).isEqualTo(7);
		FunctionCall lastCall = (FunctionCall)instructions.get(6);
		assertThat(lastCall.getIdentifier()).isEqualTo("stop");
	}

	@Test
	public void getNextScope_basicForLoop_returnsForLoopScope() throws LexerException, ParserException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream("powtarzaj(idx1,10) [czysc()]".getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(6);
		BaseInstruction rightBoundAssignment = instructions.get(0);
		assertThat(rightBoundAssignment).isInstanceOf(AssignmentInstruction.class);
		BaseInstruction indexAssignment = instructions.get(1);
		assertThat(indexAssignment).isInstanceOf(AssignmentInstruction.class);
		ForConditionalJump forJump = (ForConditionalJump)instructions.get(2);
		assertThat(forJump.getInstructionPointer()).isEqualTo(6);
		FunctionCall call = (FunctionCall)instructions.get(3);
		assertThat(call.getIdentifier()).isEqualTo("czysc");
		BaseInstruction indexIncrement = instructions.get(4);
		assertThat(indexIncrement).isInstanceOf(AssignmentInstruction.class);
		Jump jumpToCondition = (Jump)instructions.get(5);
		assertThat(jumpToCondition.getInstructionPointer()).isEqualTo(2);
	}

	@Test
	public void getNextScope_whileLoop_returnsWhileLoopScope() throws LexerException, ParserException {
		//before
		globalVars.put("varW", 100);
		ByteArrayInputStream is = new ByteArrayInputStream("tdj(varW > 10) [czysc()]".getBytes());
		parser.handleStream(is);
		//when
		Scope scope = parser.getNextScope();
		//then
		assertThat(scope).isNotNull();
		assertThat(scope.isFunctionDefinition()).isFalse();
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		assertThat(instructions).hasSize(3);
		JumpIfNotTrue jint = (JumpIfNotTrue)instructions.get(0);
		assertThat(jint.getInstructionPointer()).isEqualTo(3);
		FunctionCall call = (FunctionCall)instructions.get(1);
		assertThat(call.getIdentifier()).isEqualTo("czysc");
		Jump jump = (Jump)instructions.get(2);
		assertThat(jump.getInstructionPointer()).isEqualTo(0);
	}

}
