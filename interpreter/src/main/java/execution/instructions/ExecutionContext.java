package execution.instructions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExecutionContext {

	private int instructionPointer;
	private int[] localVariables;

}
