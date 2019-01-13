package execution.instructions;

import scope.Scope;
import java.util.Stack;

public class ScopeExecutor {

	private Stack<ExecutionContext> contextStack;
	private int currentInstructionPointer;
	private int[] currentLocalVariables;

	public ScopeExecutor() {
		contextStack = new Stack<>();
	}

	public void executeScope(Scope scope) {

	}

	private void saveCurrentContext() {
		contextStack.push(new ExecutionContext(currentInstructionPointer + 1, currentLocalVariables));
	}

	private void restoreContext() {
		ExecutionContext restored = contextStack.pop();
		currentInstructionPointer = restored.getInstructionPointer();
		currentLocalVariables = restored.getLocalVariables();
	}

}
