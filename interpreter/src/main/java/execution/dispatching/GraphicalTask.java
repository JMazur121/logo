package execution.dispatching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tree.Node;
import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class GraphicalTask {

	private String methodIdentifier;
	private ArrayList<Node> arguments;

}
