package execution.instructions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import exceptions.InterpreterException;
import execution.dispatching.BaseTask;
import execution.dispatching.GraphicExecutor;
import execution.expressions.CalculationVisitor;
import execution.expressions.EvaluationBag;
import instructions.*;
import javafx.util.Pair;
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

	private Multimap<String, Pair<Integer, BaseTask>> embeddedTasks;

	public ScopeExecutor(GraphicExecutor graphicExecutor, BlockingQueue<Runnable> graphicalTasksQueue,
						 Map<String, Integer> globalVariables, Map<String, Scope> knownMethods) {
		contextStack = new Stack<>();
		this.graphicExecutor = graphicExecutor;
		this.graphicalTasksQueue = graphicalTasksQueue;
		this.globalVariables = globalVariables;
		this.knownMethods = knownMethods;
		calculationVisitor = new CalculationVisitor(globalVariables);
		currentLocalVariables = null;
		buildEmbeddedTasks();
	}

	private void buildEmbeddedTasks() {
		embeddedTasks = HashMultimap.create();
		embeddedTasks.put("naprzod", new Pair<>(1, (identifier, args, executor) -> () -> executor.drawAlong(args[0])));
		embeddedTasks.put("wstecz", new Pair<>(1, (identifier, args, executor) -> () -> executor.drawAlong(-args[0])));
		embeddedTasks.put("prawo", new Pair<>(1, (identifier, args, executor) -> () -> executor.rotate(args[0])));
		embeddedTasks.put("lewo", new Pair<>(1, (identifier, args, executor) -> () -> executor.rotate(-args[0])));
		embeddedTasks.put("czysc", new Pair<>(0, (identifier, args, executor) -> executor::clear));
		embeddedTasks.put("podnies", new Pair<>(0, (identifier, args, executor) -> executor::drawerUp));
		embeddedTasks.put("opusc", new Pair<>(0, (identifier, args, executor) -> executor::drawerDown));
		embeddedTasks.put("zamaluj", new Pair<>(0, (identifier, args, executor) -> executor::fill));
		embeddedTasks.put("kolorPisaka", new Pair<>(1, (identifier, args, executor) -> () -> executor.setStroke(identifier)));
		embeddedTasks.put("kolorPisaka", new Pair<>(3, (identifier, args, executor) -> () -> executor.setStroke(args[0], args[1], args[2])));
		embeddedTasks.put("kolorMalowania", new Pair<>(1, (identifier, args, executor) -> () -> executor.setFill(identifier)));
		embeddedTasks.put("kolorMalowania", new Pair<>(3, (identifier, args, executor) -> () -> executor.setFill(args[0], args[1], args[2])));
		embeddedTasks.put("paleta", new Pair<>(4, (identifier, args, executor) -> () -> executor.defineColour(identifier, args[0], args[1], args[2])));
		embeddedTasks.put("foremny", new Pair<>(1, (identifier, args, executor) -> () -> executor.fillPolygon(args[0])));
		embeddedTasks.put("okrag", new Pair<>(1, (identifier, args, executor) -> () -> executor.strokeCircle(args[0])));
		embeddedTasks.put("kolo", new Pair<>(1, ((identifier, args, executor) -> () -> executor.fillCircle(args[0]))));
		embeddedTasks.put("skok", new Pair<>(2, (identifier, args, executor) -> () -> executor.moveDrawer(args[0], args[1])));
		embeddedTasks.put("wypisz", new Pair<>(1, (identifier, args, executor) -> () -> executor.print(Integer.toString(args[0]))));
	}

	public static boolean isNotExpectedLength(int[] args, int expectedSize) {
		return args.length != expectedSize;
	}

	public static boolean areNotPositive(int[] args) {
		for (int arg : args) {
			if (arg < 0)
				return true;
		}
		return false;
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

	private void callEmbeddedMethodWithArguments(FunctionCall call) throws InterpreterException {
		ArrayList<Node> arguments = call.getArguments();
		int[] args = new int[arguments.size()];
		calcArguments(arguments, args, 0);
		String id = call.getIdentifier();
		Runnable newTask;
		if (arguments.size() == 1) {
			if (args[0] < 0)
				throw new InterpreterException(wrongArgumentsTypeMessage(id));
			switch (id) {
				case "naprzod":
					newTask = () -> graphicExecutor.drawAlong(args[0]);
					break;
				case "wstecz":
					newTask = () -> graphicExecutor.drawAlong(-args[0]);
					break;
				case "prawo":
					newTask = () -> graphicExecutor.rotate(args[0]);
					break;
			}
		}
		queueNewTask(newTask);
	}

	private String checkForIdentifierNode(Node node, String methodID) throws InterpreterException {
		if (!node.isArgumentNode())
			throw new InterpreterException(wrongArgumentsTypeMessage(methodID));
		ArgumentNode argument = (ArgumentNode) node;
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
			case "czysc":
				newTask = () -> graphicExecutor.clear();
				break;
			case "podnies":
				newTask = () -> graphicExecutor.drawerUp();
				break;
			case "opusc":
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
		} catch (InterruptedException ignored) {
		}
	}

	private void calcArguments(ArrayList<Node> args, int[] tab, int argsOffset) {
		for (int i = argsOffset; i < args.size(); i++) {
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
