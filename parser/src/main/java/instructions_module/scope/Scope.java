package instructions_module.scope;

import instructions_module.composite.BaseInstruction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.ArrayList;

@Getter
@AllArgsConstructor
@Builder
public class Scope {

	private boolean isFunctionDefinition;
	private int numberOfLocalVariables;
	private int numberOfArguments;
	private ArrayList<BaseInstruction> instructions;

}
