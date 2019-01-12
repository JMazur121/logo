package instructions_module.composite;

import expressions_module.tree.IndexedArgument;
import instructions_module.visitors.InstructionVisitor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForConditionalJump extends BaseInstruction {

	private IndexedArgument index;
	private IndexedArgument end;
	private int instructionPointer;

	public ForConditionalJump(IndexedArgument index, IndexedArgument end) {
		this.index = index;
		this.end = end;
	}

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visitForConditionalJump(this);
	}

}
