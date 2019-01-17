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
import java.util.Collection;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScopeExecutor implements InstructionVisitor {

	private Stack<ExecutionContext> contextStack;
	private int currentInstructionPointer;
	private int[] currentLocalVariables;
	private GraphicExecutor graphicExecutor;
	private BlockingQueue<Runnable> graphicalTasksQueue;
	private Map<String, Integer> globalVariables;
	private Map<String, Scope> knownMethods;
	private CalculationVisitor calculationVisitor;
	private AtomicBoolean isWorkToDo;

	private Multimap<String, Pair<Integer, BaseTask>> embeddedTasks;

	public ScopeExecutor(GraphicExecutor graphicExecutor, BlockingQueue<Runnable> graphicalTasksQueue,
						 Map<String, Integer> globalVariables, Map<String, Scope> knownMethods,
						 AtomicBoolean isWorkToDo) {
		contextStack = new Stack<>();
		this.graphicExecutor = graphicExecutor;
		this.graphicalTasksQueue = graphicalTasksQueue;
		this.globalVariables = globalVariables;
		this.knownMethods = knownMethods;
		this.isWorkToDo = isWorkToDo;
		calculationVisitor = new CalculationVisitor(globalVariables);
		currentLocalVariables = null;
		buildEmbeddedTasks();
	}

	private void buildEmbeddedTasks() {
		embeddedTasks = HashMultimap.create();
		embeddedTasks.put("naprzod", new Pair<>(1, (args, executor) -> () -> executor.drawAlong(args[0])));
		embeddedTasks.put("wstecz", new Pair<>(1, (args, executor) -> () -> executor.drawAlong(-args[0])));
		embeddedTasks.put("prawo", new Pair<>(1, (args, executor) -> () -> executor.rotate(args[0])));
		embeddedTasks.put("lewo", new Pair<>(1, (args, executor) -> () -> executor.rotate(-args[0])));
		embeddedTasks.put("czysc", new Pair<>(0, (args, executor) -> executor::clear));
		embeddedTasks.put("podnies", new Pair<>(0, (args, executor) -> executor::drawerUp));
		embeddedTasks.put("opusc", new Pair<>(0, (args, executor) -> executor::drawerDown));
		embeddedTasks.put("zamaluj", new Pair<>(0, (args, executor) -> executor::fill));
		embeddedTasks.put("obrysForemnego", new Pair<>(2, (args, executor) -> () -> executor.strokePolygon(args[0], args[1])));
		embeddedTasks.put("pelnyForemny", new Pair<>(2, (args, executor) -> () -> executor.fillPolygon(args[0], args[1])));
		embeddedTasks.put("obrysElipsy", new Pair<>(2, (args, executor) -> () -> executor.strokeEllipse(args[0], args[1])));
		embeddedTasks.put("pelnaElipsa", new Pair<>(2, (args, executor) -> () -> executor.fillEllipse(args[0], args[1])));
		embeddedTasks.put("okrag", new Pair<>(1, (args, executor) -> () -> executor.strokeCircle(args[0])));
		embeddedTasks.put("kolo", new Pair<>(1, (args, executor) -> () -> executor.fillCircle(args[0])));
		embeddedTasks.put("przesun", new Pair<>(2, (args, executor) -> () -> executor.translateDrawer(args[0], args[1])));
		embeddedTasks.put("skok", new Pair<>(2, (args, executor) -> () -> executor.moveDrawer(args[0], args[1])));
		embeddedTasks.put("wypisz", new Pair<>(1, (args, executor) -> () -> executor.print(Integer.toString(args[0]))));
		embeddedTasks.put("kolorPisaka", new Pair<>(3, (args, executor) -> () -> executor.setStroke(args[0], args[1], args[2])));
		embeddedTasks.put("kolorMalowania", new Pair<>(3, (args, executor) -> () -> executor.setFill(args[0], args[1], args[2])));
	}

	public void executeScope(Scope scope, boolean firstCall) throws InterpreterException {
		currentInstructionPointer = 0;
		int sizeOfVariablesTable = scope.getNumberOfLocalVariables();
		if (firstCall && sizeOfVariablesTable > 0) {
			currentLocalVariables = new int[sizeOfVariablesTable];
			calculationVisitor.setLocalVariables(currentLocalVariables);
		}
		ArrayList<BaseInstruction> instructions = scope.getInstructions();
		while (isWorkToDo.get() && currentInstructionPointer < instructions.size()) {
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

	private boolean areNegative(int[] args) {
		for (int arg : args) {
			if (arg < 0)
				return true;
		}
		return false;
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
			else {
				callEmbeddedMethod(functionCall);
				incrementPointer();
			}
		}
		else
			visitDefinedMethodCall(functionCall);
	}

	private void callEmbeddedMethod(FunctionCall call) throws InterpreterException {
		ArrayList<Node> arguments = call.getArguments();
		Collection<Pair<Integer, BaseTask>> methods = embeddedTasks.get(call.getIdentifier());
		BaseTask taskToDo = null;
		for (Pair<Integer, BaseTask> task : methods) {
			if (task.getKey() == arguments.size()) {
				taskToDo = task.getValue();
			}
		}
		if (taskToDo == null)
			throw new InterpreterException(wrongNumberOfArgumentsMessage(call.getIdentifier()));
		int[] args = new int[arguments.size()];
		calcArguments(arguments, args, 0);
		if (!"wypisz".equals(call.getIdentifier()) && !"przesun".equals(call.getIdentifier()) && areNegative(args))
			throw new InterpreterException(wrongArgumentsTypeMessage(call.getIdentifier()));
		queueNewTask(taskToDo.newTask(args, graphicExecutor));
	}

	private String wrongNumberOfArgumentsMessage(String functionName) {
		return String.format("Interpreter error - wrong number of arguments for \"%s\" call", functionName);
	}

	private String wrongArgumentsTypeMessage(String functionName) {
		return String.format("Interpreter error - wrong arguments type for \"%s\" call", functionName);
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
		calculationVisitor.setLocalVariables(newLocalVariables);
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
