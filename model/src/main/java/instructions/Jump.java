package instructions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import visitors.InstructionVisitor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jump extends BaseInstruction{

	private int instructionPointer;

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visitJump(this);
	}

}
