package instructions;

import visitors.InstructionVisitor;

public abstract class BaseInstruction {

	public abstract void accept(InstructionVisitor visitor);

}
