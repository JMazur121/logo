package instructions_module.composite;

import instructions_module.visitors.InstructionVisitor;
import lombok.*;
import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class InstructionBlock extends BaseInstruction{
	
	private ArrayList<BaseInstruction> instructions;
	private int numberOfLocalVariables = 0;
	private int numberOfArguments = 0;

	@Override
	public void accept(InstructionVisitor visitor) {
		for (BaseInstruction instruction: instructions) {
			instruction.accept(visitor);
		}
	}

}
