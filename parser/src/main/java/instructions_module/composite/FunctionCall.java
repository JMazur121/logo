package instructions_module.composite;

import expressions_module.tree.Node;
import instructions_module.visitors.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tokenizer.Token;
import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class FunctionCall extends BaseInstruction{

	private String identifier;
	private ArrayList<Node> arguments;
	private boolean isEmbeddedMethodCall;

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visitFunctionCall(this);
	}

	public boolean hasArguments() {
		return arguments != null;
	}

	public boolean isStopCall() {
		return "stop".equals(identifier);
	}

}
