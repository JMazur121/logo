package execution.instructions;

import exceptions.InterpreterException;
import exceptions.LexerException;
import exceptions.ParserException;
import execution.dispatching.GraphicExecutor;
import parser.Parser;
import scope.Scope;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParserExecutor {

	private ExecutorService executor;
	private Parser parser;
	private Map<String, Integer> globalVariables;
	private Map<String, Scope> knownMethods;
	private GraphicExecutor graphicExecutor;
	private BlockingQueue<Runnable> graphicalTasksQueue;
	private ScopeExecutor scopeExecutor;
	private AtomicBoolean isWorkToDo;

	public ParserExecutor(GraphicExecutor graphicExecutor, BlockingQueue<Runnable> graphicalTasksQueue, AtomicBoolean isWorkToDo) {
		executor = Executors.newSingleThreadExecutor();
		globalVariables = new HashMap<>();
		knownMethods = new HashMap<>();
		parser = new Parser(globalVariables, knownMethods);
		this.graphicExecutor = graphicExecutor;
		this.graphicalTasksQueue = graphicalTasksQueue;
		this.isWorkToDo = isWorkToDo;
		scopeExecutor = new ScopeExecutor(graphicExecutor, graphicalTasksQueue, globalVariables, knownMethods, isWorkToDo);
	}

	public void reinitialize() {
		executor = Executors.newSingleThreadExecutor();
	}

	public void nextStream(InputStream inputStream) {
		executor.execute(() -> {
			parser.handleStream(inputStream);
			scopeExecutor.reset();
			Scope nextScope;
			try {
				while (isWorkToDo.get() && (nextScope = parser.getNextScope()) != null) {
					try {
						if (nextScope.isFunctionDefinition())
							graphicalTasksQueue.offer(() -> graphicExecutor.print("Dodano definicje funkcji"));
						else
							scopeExecutor.executeScope(nextScope, true);
					} catch (Exception e) {
						graphicalTasksQueue.offer(() -> graphicExecutor.print("Exception : " + e.getMessage()));
						break;
					}
				}
			} catch (LexerException | ParserException e) {
				graphicalTasksQueue.offer(() -> graphicExecutor.print(e.getMessage()));
			}
			parser.reset();
		});
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
