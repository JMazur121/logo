package execution.controller;

import javafx.application.Platform;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GraphicalTaskConsumer {

	private AtomicLong latency;
	private AtomicBoolean isWorkToDo;
	private BlockingQueue<Runnable> tasksQueue;
	private ExecutorService executor;

	public GraphicalTaskConsumer(AtomicLong latency, AtomicBoolean isWorkToDo, BlockingQueue<Runnable> tasksQueue) {
		this.latency = latency;
		this.isWorkToDo = isWorkToDo;
		this.tasksQueue = tasksQueue;
		executor = Executors.newSingleThreadExecutor();
	}

	public void start() {
		executor.execute(() -> {
			while (isWorkToDo.get()) {
				try {
					Runnable nextTask = tasksQueue.take();
					Platform.runLater(nextTask);
					Thread.sleep(latency.get());
				} catch (InterruptedException ignored) {}
			}
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
