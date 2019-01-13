package visitors;

import exceptions.InterpreterException;
import instructions.*;

public interface InstructionVisitor {

	void visitAssignmentInstruction(AssignmentInstruction assignmentInstruction);
	void visitFunctionCall(FunctionCall functionCall) throws InterpreterException;
	void visitForConditionalJump(ForConditionalJump forConditionalJump);
	void visitJump(Jump jump);
	void visitJumpIfNotTrue(JumpIfNotTrue jumpIfNotTrue);

}
