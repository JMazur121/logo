package instructions_module.composite;

import expressions_module.tree.Node;
import instructions_module.visitors.InstructionVisitor;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class ConditionalInstruction extends BaseInstruction {

	private ArrayList<Pair<Node, InstructionBlock>> cases;

	@Override
	public void accept(InstructionVisitor visitor) {
		visitor.visit(this);
	}

}
