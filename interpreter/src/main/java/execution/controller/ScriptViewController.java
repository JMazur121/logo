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

	private ResizableCanvas drawerCanvas;
	private ResizableCanvas backgroundCanvas;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createCanvas();
		drawerCanvas.setOnMouseMoved(event -> mousePositionField.setText(String.format("(%d,%d)", (int) event.getX(), (int) event.getY())));
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

	private void createCanvas() {
		drawerCanvas = new ResizableCanvas();
		backgroundCanvas = new ResizableCanvas();
		centerPane.getColumnConstraints().get(0).setHalignment(HPos.CENTER);
		centerPane.getRowConstraints().get(0).setValignment(VPos.CENTER);
		centerPane.add(drawerCanvas, 0, 0);
		centerPane.add(backgroundCanvas, 0, 0);
		drawerCanvas.widthProperty().bind(
				centerPane.widthProperty());
		drawerCanvas.heightProperty().bind(
				centerPane.heightProperty().subtract(5));
		backgroundCanvas.widthProperty().bind(
				centerPane.widthProperty());
		backgroundCanvas.heightProperty().bind(
				centerPane.heightProperty().subtract(5));
		drawerCanvas.toFront();
	}

	private void closeScriptView() {

	}
	
}
