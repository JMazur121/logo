package instructions_module.composite;

import instructions_module.visitors.InstructionVisitor;

public abstract class BaseInstruction {

	public abstract void accept(InstructionVisitor visitor);

}
