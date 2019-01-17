package execution.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ScriptViewController extends GenericController {

	public Button openFileButton;
	private boolean firstFile;

	public void setSpecialHandlers() {
		openFileButton.disabledProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue)
				endButton.setDisable(true);
			else
				endButton.setDisable(false);
		});
		endButton.setDisable(true);
		firstFile = true;
		endButton.getScene().getWindow().setOnCloseRequest(event -> {
			showAlert("Closing", "Wait for the window to get closed", Alert.AlertType.INFORMATION);
			close();
		});
	}

	@Override
	public void close() {
		isWorkToDo.set(false);
		firstFile = false;
		indicator.setVisible(true);
		latency.set(0);
		parserExecutor.stop();
		tasksQueue.offer(() -> {});
		tasksQueue.offer(() -> {});
		consumer.stop();
		indicator.setVisible(false);
	}

	public void endPressed(ActionEvent event) {
		close();
		tasksQueue.clear();
		openFileButton.setDisable(false);
	}

	public void openFile(ActionEvent event) {
		executionController.clear();
		final FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
		if (file != null) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				if (!firstFile) {
					parserExecutor.reinitialize();
					consumer.reinitialize();
				}
				openFileButton.setDisable(true);
				isWorkToDo.set(true);
				consumer.start();
				parserExecutor.nextStream(inputStream);
			} catch (FileNotFoundException e) {
				showAlert("File not found", "Unable to open selected file", Alert.AlertType.ERROR);
			}
		}
	}

	private void configureFileChooser(final FileChooser chooser) {
		chooser.setTitle("Select script");
		chooser.setInitialDirectory(
				new File(System.getProperty("user.home"))
		);
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
	}

}
