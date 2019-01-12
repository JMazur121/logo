package instructions;

import lombok.Getter;
import lombok.Setter;
import tree.Node;
import visitors.InstructionVisitor;

@Getter
@Setter
public class JumpIfNotTrue extends BaseInstruction{

	private int instructionPointer;
	private Node logicalExpression;

	public JumpIfNotTrue(Node logicalExpression) {
		this.logicalExpression = logicalExpression;
	}

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visitJumpIfNotTrue(this);
	}

}
