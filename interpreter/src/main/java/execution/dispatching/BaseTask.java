package execution.dispatching;

import exceptions.InterpreterException;

@FunctionalInterface
public interface BaseTask {

	Runnable newTask(int[] args, GraphicExecutor executor) throws InterpreterException;

}
