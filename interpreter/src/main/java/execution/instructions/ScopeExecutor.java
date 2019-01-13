package execution.instructions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import execution.dispatching.GraphicExecutor;
import execution.expressions.CalculationVisitor;
import execution.expressions.EvaluationBag;
import instructions.*;
import scope.Scope;
import tree.Node;
import tree.ReadableArgument;
import visitors.InstructionVisitor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;

public class ScopeExecutor implements InstructionVisitor {

	private Stack<ExecutionContext> contextStack;
	private int currentInstructionPointer;
	private int[] currentLocalVariables;
	private GraphicExecutor graphicExecutor;
	private BlockingQueue<Runnable> graphicalTasksQueue;
	private Map<String, Integer> globalVariables;
	private Map<String, Scope> knownMethods;
	private CalculationVisitor calculationVisitor;

	public ScopeExecutor(GraphicExecutor graphicExecutor, BlockingQueue<Runnable> graphicalTasksQueue,
						 Map<String, Integer> globalVariables, Map<String, Scope> knownMethods) {
		contextStack = new Stack<>();
		this.graphicExecutor = graphicExecutor;
		this.graphicalTasksQueue = graphicalTasksQueue;
		this.globalVariables = globalVariables;
		this.knownMethods = knownMethods;
		calculationVisitor = new CalculationVisitor(globalVariables);
		currentLocalVariables = null;
	}

	public void executeScope(Scope scope, boolean firstCall) {
		currentInstructionPointer = 0;
		int sizeOfVariablesTable = scope.getNumberOfLocalVariables();
		if (firstCall && sizeOfVariablesTable > 0) {
			currentLocalVariables = new int[sizeOfVariablesTable];
			calculationVisitor.setLocalVariables(currentLocalVariables);
		}
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		while (currentInstructionPointer < instructions.size()) {
			BaseInstruction nextInstruction = instructions.get(currentInstructionPointer);
			nextInstruction.accept(this);
		}
	}

	public void reset() {
		contextStack.clear();
		calculationVisitor.reset();
	}

	private void saveCurrentContext() {
		contextStack.push(new ExecutionContext(currentInstructionPointer, currentLocalVariables));
	}

	private void restoreContext() {
		ExecutionContext restored = contextStack.pop();
		currentInstructionPointer = restored.getInstructionPointer();
		currentLocalVariables = restored.getLocalVariables();
		calculationVisitor.setLocalVariables(currentLocalVariables);
	}

	private void incrementPointer() {
		++currentInstructionPointer;
	}

	private void setPointer(int value) {
		currentInstructionPointer = value;
	}

	@Override
	public void visitAssignmentInstruction(AssignmentInstruction assignmentInstruction) {
		EvaluationBag result = calculationVisitor.calculate(assignmentInstruction.getArithmeticExpression());
		ReadableArgument target = assignmentInstruction.getTarget();
		if (target.isDictionaryArgument())
			globalVariables.put(target.readKey(), result.getValue());
		else
			currentLocalVariables[target.readValue()] = result.getValue();
		incrementPointer();
	}

	@Override
	public void visitFunctionCall(FunctionCall functionCall) {
		if (functionCall.isEmbeddedMethodCall()) {
			if ("stop".equals(functionCall.getIdentifier()))
				setPointer(Integer.MAX_VALUE);
			else if (functionCall.hasArguments()) {
				
			}
			else {
				callEmbeddedMethodWithNoArguments(functionCall);
				incrementPointer();
			}
		}
		else
			visitDefinedMethodCall(functionCall);
	}

	private void callEmbeddedMethodWithNoArguments(FunctionCall call) {
		Runnable newTask;
		switch (call.getIdentifier()) {
			case "czysc" :
				newTask = () -> graphicExecutor.clear();
				break;
			case "podnies" :
				newTask = () -> graphicExecutor.drawerUp();
				break;
			case "opusc" :
				newTask = () -> graphicExecutor.drawerDown();
				break;
			default:
				newTask = () -> graphicExecutor.fill();
				break;
		}
		putNewTask(newTask);
	}

	private void putNewTask(Runnable r) {
		try {
			graphicalTasksQueue.put(r);
		} catch (InterruptedException ignored) {}
	}

	private void visitDefinedMethodCall(FunctionCall functionCall) {
		Scope scope = knownMethods.get(functionCall.getIdentifier());
		int[] newLocalVariables = new int[scope.getNumberOfLocalVariables()];
		ArrayList<Node> arguments = functionCall.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			EvaluationBag result = calculationVisitor.calculate(arguments.get(i));
			newLocalVariables[i] = result.getValue();
		}
		saveCurrentContext();
		currentLocalVariables = newLocalVariables;
		executeScope(scope, false);
		restoreContext();
		incrementPointer();
	}

	@Override
	public void visitForConditionalJump(ForConditionalJump forConditionalJump) {
		int currentLoopIndex = currentLocalVariables[forConditionalJump.getIndex().readValue()];
		int bound = currentLocalVariables[forConditionalJump.getEnd().readValue()];
		if (currentLoopIndex < bound)
			incrementPointer();
		else
			setPointer(forConditionalJump.getInstructionPointer());
	}

	@Override
	public void visitJump(Jump jump) {
		setPointer(jump.getInstructionPointer());
	}

	@Override
	public void visitJumpIfNotTrue(JumpIfNotTrue jumpIfNotTrue) {
		EvaluationBag result = calculationVisitor.calculate(jumpIfNotTrue.getLogicalExpression());
		if (result.getBooleanValue())
			incrementPointer();
		else
			setPointer(jumpIfNotTrue.getInstructionPointer());
	}

}
