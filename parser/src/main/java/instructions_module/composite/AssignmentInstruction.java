package instructions_module.composite;

import expressions_module.tree.Node;
import expressions_module.tree.ReadableArgument;
import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignmentInstruction extends BaseInstruction {

	private ReadableArgument target;
	private Node arithmeticExpression;

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visitAssignmentInstruction(this);
	}

}
