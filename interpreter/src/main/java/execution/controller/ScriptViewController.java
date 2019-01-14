package execution.controller;

import execution.utils.ResizableCanvas;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.ResourceBundle;

public class ScriptViewController implements Initializable {

	public Button openFileButton;
	public TextField drawerPositionField;
	public TextField mousePositionField;
	public ComboBox latencyComboBox;
	public ColorPicker drawerColourPicker;
	public ColorPicker fillColourPicker;
	public Button endButton;
	public TextField responseField;
	public GridPane centerPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void setCloseRequestHandler() {
		endButton.getScene().getWindow().setOnCloseRequest(event -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Zakończenie");
			alert.setHeaderText(null);
			alert.setContentText("Opuszczasz tryb pracy ze skryptem. Poczekaj na zamknięcie okna");
			alert.showAndWait();
			closeScriptView();
		});
	}

	private void closeScriptView() {

	}
	
}
