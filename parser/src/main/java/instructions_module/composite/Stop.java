package instructions_module.composite;

import instructions_module.visitors.InstructionVisitor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Stop extends BaseInstruction{

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visitStop(this);
	}

}
