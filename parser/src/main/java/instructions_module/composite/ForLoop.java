package instructions_module.composite;

import expressions_module.tree.ReadableArgument;
import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForLoop extends BaseInstruction{

	private ReadableArgument index;
	private int begin = 0;
	private int end;
	private int step = 1;
	private InstructionBlock instructions;

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visit(this);
	}

}
