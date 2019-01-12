package visitors;

import instructions.*;

public interface InstructionVisitor {

	void visitAssignmentInstruction(AssignmentInstruction assignmentInstruction);
	void visitFunctionCall(FunctionCall functionCall);
	void visitForConditionalJump(ForConditionalJump forConditionalJump);
	void visitJump(Jump jump);
	void visitJumpIfNotTrue(JumpIfNotTrue jumpIfNotTrue);

}
