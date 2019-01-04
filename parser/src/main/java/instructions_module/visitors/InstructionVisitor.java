package instructions_module.visitors;

import instructions_module.composite.*;

public interface InstructionVisitor {

	void visit(AssignmentInstruction assignmentInstruction);
	void visit(FunctionCall functionCall);
	void visit(ConditionalInstruction conditionalInstruction);
	void visit(ForLoop forLoop);
	void visit(WhileLoop whileLoop);

}
