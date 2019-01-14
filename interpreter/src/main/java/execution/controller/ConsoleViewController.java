package execution.controller;

import javafx.scene.control.*;

public class ConsoleViewController extends GenericController {

	public Button submitButton;
	public TextArea codeTextArea;

	public void setCloseRequestHandler() {
		endButton.getScene().getWindow().setOnCloseRequest(event -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Zakończenie");
			alert.setHeaderText(null);
			alert.setContentText("Opuszczasz tryb pracy z konsolą. Poczekaj na zamknięcie okna");
			alert.showAndWait();
			close();
		});
	}

	@Override
	public void close() {

	}

}
