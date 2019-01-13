package execution.instructions;

import exceptions.InterpreterException;
import execution.dispatching.GraphicExecutor;
import execution.expressions.CalculationVisitor;
import execution.expressions.EvaluationBag;
import instructions.*;
import scope.Scope;
import tree.ArgumentNode;
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

	public void executeScope(Scope scope, boolean firstCall) throws InterpreterException {
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
	public void visitFunctionCall(FunctionCall functionCall) throws InterpreterException {
		if (functionCall.isEmbeddedMethodCall()) {
			if ("stop".equals(functionCall.getIdentifier()))
				setPointer(Integer.MAX_VALUE);
			else if (functionCall.hasArguments()) {
				String id = functionCall.getIdentifier();
				if (isColourChangeOperation(id))
					callEmbeddedColourChange(functionCall);
				else
					callEmbeddedMethodWithArguments(functionCall);
				incrementPointer();
			}
			else {
				callEmbeddedMethodWithNoArguments(functionCall);
				incrementPointer();
			}
		}
		else
			visitDefinedMethodCall(functionCall);
	}

	private String checkForIdentifierNode(Node node, String methodID) throws InterpreterException {
		if (!node.isArgumentNode())
			throw new InterpreterException(wrongArgumentsTypeMessage(methodID));
		ArgumentNode argument = (ArgumentNode)node;
		if (!argument.getArgument().isDictionaryArgument())
			throw new InterpreterException(wrongArgumentsTypeMessage(methodID));
		return argument.getArgument().readKey();
	}

	private void callEmbeddedColourChange(FunctionCall call) throws InterpreterException {
		String id = call.getIdentifier();
		Runnable newTask;
		ArrayList<Node> arguments = call.getArguments();
		if ("paleta".equals(id)) {
			if (arguments.size() != 4)
				throw new InterpreterException(wrongNumberOfArgumentsMessage(id));
			String colourName = checkForIdentifierNode(arguments.get(0), id);
			int[] colourTab = new int[3];
			calcArguments(arguments, colourTab, 1);
			newTask = () -> graphicExecutor.defineColour(colourName, colourTab[0], colourTab[1], colourTab[2]);
		}
		else {
			if (arguments.size() == 1) {
				String colourName = checkForIdentifierNode(arguments.get(0), id);
				if ("kolorPisaka".equals(id))
					newTask = () -> graphicExecutor.setStroke(colourName);
				else
					newTask = () -> graphicExecutor.setFill(colourName);
			}
			else if (arguments.size() == 3) {
				int[] colourTab = new int[3];
				calcArguments(arguments, colourTab, 0);
				if ("kolorPisaka".equals(id))
					newTask = () -> graphicExecutor.setStroke(colourTab[0], colourTab[1], colourTab[2]);
				else
					newTask = () -> graphicExecutor.setFill(colourTab[0], colourTab[1], colourTab[2]);
			}
			else
				throw new InterpreterException(wrongNumberOfArgumentsMessage(id));
		}
		queueNewTask(newTask);
	}

	private boolean isColourChangeOperation(String id) {
		return "kolorPisaka".equals(id) || "kolorMalowania".equals(id) || "paleta".equals(id);
	}

	private String wrongNumberOfArgumentsMessage(String functionName) {
		return String.format("Interpreter error - wrong number of arguments for \"%s\" call", functionName);
	}

	private String wrongArgumentsTypeMessage(String functionName) {
		return String.format("Interpreter error - wrong arguments type for \"%s\" call", functionName);
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
		queueNewTask(newTask);
	}

	private void queueNewTask(Runnable r) {
		try {
			graphicalTasksQueue.put(r);
		} catch (InterruptedException ignored) {}
	}

	private void calcArguments(ArrayList<Node> args, int[] tab, int argsOffset) {
		for (int i=argsOffset; i < args.size(); i++) {
			EvaluationBag result = calculationVisitor.calculate(args.get(i));
			tab[i] = result.getValue();
		}
	}

	private void visitDefinedMethodCall(FunctionCall functionCall) throws InterpreterException {
		Scope scope = knownMethods.get(functionCall.getIdentifier());
		int[] newLocalVariables = new int[scope.getNumberOfLocalVariables()];
		ArrayList<Node> arguments = functionCall.getArguments();
		calcArguments(arguments, newLocalVariables, 0);
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
