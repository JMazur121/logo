package execution.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ScriptViewController extends GenericController {

	public Button openFileButton;

	public void setSpecialHandlers() {
		endButton.setDisable(true);
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

	public void endPressed(ActionEvent event) {

	}

	public void openFile(ActionEvent event) {
		executionController.clear();
		final FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
		if (file != null) {
			
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
