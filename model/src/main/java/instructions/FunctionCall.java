package instructions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tree.Node;
import visitors.InstructionVisitor;
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
		return !arguments.isEmpty();
	}

	public boolean isStopCall() {
		return "stop".equals(identifier);
	}

}
