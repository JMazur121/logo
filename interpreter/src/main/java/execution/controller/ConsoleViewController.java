package execution.controller;

import execution.utils.ResizableCanvas;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ConsoleViewController implements Initializable {

	public TextField drawerPositionField;
	public TextField mousePositionField;
	public ComboBox latencyComboBox;
	public ColorPicker drawerColourPicker;
	public ColorPicker fillColourPicker;
	public Button endButton;
	public Button submitButton;
	public TextField responseField;
	public TextArea codeTextArea;
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
			alert.setContentText("Opuszczasz tryb pracy z konsolą. Poczekaj na zamknięcie okna");
			alert.showAndWait();
			closeConsole();
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

	private void closeConsole() {

	}

}
