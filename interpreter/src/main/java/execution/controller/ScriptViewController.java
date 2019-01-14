package execution.controller;

import javafx.scene.control.*;

public class ScriptViewController extends GenericController {

	public Button openFileButton;

	public void setCloseRequestHandler() {
		endButton.getScene().getWindow().setOnCloseRequest(event -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Zakończenie");
			alert.setHeaderText(null);
			alert.setContentText("Opuszczasz tryb pracy ze skryptem. Poczekaj na zamknięcie okna");
			alert.showAndWait();
			close();
		});
	}

	@Override
	public void close() {

	}

}
