package instructions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tree.Node;
import tree.ReadableArgument;
import visitors.InstructionVisitor;

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
