package execution.instructions;

import execution.dispatching.GraphicExecutor;
import parser.Parser;
import scope.Scope;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParserExecutor {

	private ExecutorService executor;
	private Parser parser;
	private Map<String, Integer> globalVariables;
	private Map<String, Scope> knownMethods;
	private GraphicExecutor graphicExecutor;
	private Queue<Runnable> graphicalTasksQueue;

	public ParserExecutor(GraphicExecutor graphicExecutor, Queue<Runnable> graphicalTasksQueue) {
		executor = Executors.newSingleThreadExecutor();
		globalVariables = new HashMap<>();
		knownMethods = new HashMap<>();
		parser = new Parser(globalVariables, knownMethods);
		this.graphicExecutor = graphicExecutor;
		this.graphicalTasksQueue = graphicalTasksQueue;
	}



	public void stop() {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS))
				executor.shutdownNow();
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
	}


}
