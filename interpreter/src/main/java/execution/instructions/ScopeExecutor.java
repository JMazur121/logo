package execution.instructions;

import execution.dispatching.GraphicExecutor;
import execution.expressions.CalculationVisitor;
import execution.expressions.EvaluationBag;
import instructions.*;
import scope.Scope;
import tree.ReadableArgument;
import visitors.InstructionVisitor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class ScopeExecutor implements InstructionVisitor{

	private Stack<ExecutionContext> contextStack;
	private int currentInstructionPointer;
	private int[] currentLocalVariables;
	private GraphicExecutor graphicExecutor;
	private Queue<Runnable> graphicalTasksQueue;
	private Map<String, Integer> globalVariables;
	private Map<String, Scope> knownMethods;
	private CalculationVisitor calculationVisitor;

	public ScopeExecutor(GraphicExecutor graphicExecutor, Queue<Runnable> graphicalTasksQueue,
						 Map<String, Integer> globalVariables, Map<String, Scope> knownMethods) {
		contextStack = new Stack<>();
		this.graphicExecutor = graphicExecutor;
		this.graphicalTasksQueue = graphicalTasksQueue;
		this.globalVariables = globalVariables;
		this.knownMethods = knownMethods;
		calculationVisitor = new CalculationVisitor(globalVariables);
	}

	public void executeScope(Scope scope) {
		currentInstructionPointer = 0;
		int sizeOfVariablesTable = scope.getNumberOfLocalVariables();
		if (sizeOfVariablesTable > 0) {
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
	}

	private void saveCurrentContext() {
		contextStack.push(new ExecutionContext(currentInstructionPointer + 1, currentLocalVariables));
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
