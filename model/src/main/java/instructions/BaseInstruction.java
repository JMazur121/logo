package instructions;

import exceptions.InterpreterException;
import visitors.InstructionVisitor;

public abstract class BaseInstruction {

	public abstract void accept(InstructionVisitor visitor) throws InterpreterException;

}
