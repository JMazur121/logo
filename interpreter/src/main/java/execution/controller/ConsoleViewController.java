package execution.controller;

import com.google.common.base.Strings;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;

public class ConsoleViewController extends GenericController {

	public Button submitButton;
	public TextArea codeTextArea;

	public void setSpecialHandlers() {
		endButton.getScene().getWindow().setOnCloseRequest(event -> {
			showAlert("Closing", "Wait for console-window to get closed", Alert.AlertType.INFORMATION);
			close();
		});
		submitButton.setDisable(true);

		codeTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if (Strings.isNullOrEmpty(newValue))
				submitButton.setDisable(true);
			else
				submitButton.setDisable(false);
		});
		consumer.start();
	}

	@Override
	public void close() {
		isWorkToDo.set(false);
		indicator.setVisible(true);
		latency.set(0);
		submitButton.setDisable(true);
		latencyComboBox.setDisable(true);
		endButton.setDisable(true);
		parserExecutor.stop();
		tasksQueue.offer(() -> {});
		tasksQueue.offer(() -> {});
		consumer.stop();
	}

	public void submitPressed(ActionEvent event) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(codeTextArea.getText().getBytes());
		showMessage("");
		parserExecutor.nextStream(inputStream);
	}

	public void endPressed(ActionEvent event) {
		close();
		Stage dialog = (Stage) ((Node) event.getTarget()).getScene().getWindow();
		dialog.close();
	}

}
