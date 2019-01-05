package instructions_module.composite;

import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Stop extends BaseInstruction{

	@Override
	public void accept(InstructionVisitor visitor) {

	}

}
