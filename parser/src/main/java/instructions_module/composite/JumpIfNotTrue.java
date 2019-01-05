package instructions_module.composite;

import expressions_module.tree.Node;
import instructions_module.visitors.InstructionVisitor;

public class JumpIfNotTrue extends BaseInstruction{

	private int instructionPointer;
	private Node logicalExpression;

	@Override
	public void accept(InstructionVisitor visitor) {

	}

}
