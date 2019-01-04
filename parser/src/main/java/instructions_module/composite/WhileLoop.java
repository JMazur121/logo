package instructions_module.composite;

import expressions_module.tree.Node;
import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WhileLoop extends BaseInstruction{

	private Node condition;
	private InstructionBlock instructions;

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visit(this);
	}

}
