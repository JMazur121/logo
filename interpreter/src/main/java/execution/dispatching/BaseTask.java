package execution.dispatching;

import exceptions.InterpreterException;

@FunctionalInterface
public interface BaseTask {

	Runnable newTask(String identifier, int[] args, GraphicExecutor executor) throws InterpreterException;

}
