package instructions_module.visitors;

import instructions_module.composite.*;

public interface InstructionVisitor {

	void visitAssignmentInstruction(AssignmentInstruction assignmentInstruction);
	void visitFunctionCall(FunctionCall functionCall);
	void visitForConditionalJump(ForConditionalJump forConditionalJump);
	void visitJump(Jump jump);
	void visitJumpIfNotTrue(JumpIfNotTrue jumpIfNotTrue);
	void visitStop(Stop stop);

}
