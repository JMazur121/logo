package instructions_module.scope;

import instructions_module.composite.BaseInstruction;
import lombok.*;
import java.util.ArrayList;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Scope {

	private boolean isFunctionDefinition;
	private int numberOfLocalVariables;
	private int numberOfArguments;
	private ArrayList<BaseInstruction> instructions;

}
