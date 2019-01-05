package instructions_module.composite;

import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jump extends BaseInstruction{

	private int instructionPointer;

	@Override
	public void accept(InstructionVisitor visitor) {

	}

}
