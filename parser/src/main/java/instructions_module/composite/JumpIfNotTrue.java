package instructions_module.composite;

import expressions_module.tree.Node;
import instructions_module.visitors.InstructionVisitor;
import lombok.Getter;
import lombok.Setter;

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
