package instructions_module.composite;

import expressions_module.tree.IndexedArgument;
import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForConditionalJump extends BaseInstruction {

	private IndexedArgument index;
	private IndexedArgument begin;
	private IndexedArgument end;
	private IndexedArgument step;

	@Override
	public void accept(InstructionVisitor visitor) {

	}

}
