package instructions;

import lombok.Getter;
import lombok.Setter;
import tree.IndexedArgument;
import visitors.InstructionVisitor;

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
