package execution.controller;

import execution.utils.ResizableCanvas;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class GenericController implements Initializable {

	public TextField drawerPositionField;
	public TextField mousePositionField;
	public ComboBox latencyComboBox;
	public ColorPicker drawerColourPicker;
	public ColorPicker fillColourPicker;
	public Button endButton;
	public TextField responseField;
	public GridPane centerPane;

	protected ResizableCanvas drawerCanvas;
	protected ResizableCanvas backgroundCanvas;
	protected Map<String, Color> definedColours;
	protected GraphicExecutionController executionController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createCanvas();
		executionController = new GraphicExecutionController(drawerCanvas.getGraphicsContext2D(), backgroundCanvas.getGraphicsContext2D(),
				definedColours,this);
		drawerCanvas.setOnMouseMoved(event -> mousePositionField.setText(String.format("(%d,%d)", (int) event.getX(), (int) event.getY())));
		definedColours = new HashMap<>();
		drawerColourPicker.setValue(Color.BLACK);
		fillColourPicker.setValue(Color.WHITE);
		drawerColourPicker.setOnAction(event -> backgroundCanvas.getGraphicsContext2D().setStroke(drawerColourPicker.getValue()));
		fillColourPicker.setOnAction(event -> backgroundCanvas.getGraphicsContext2D().setFill(fillColourPicker.getValue()));
	}

	public void showMessage(String message) {
		responseField.setText(message);
	}
	public void setStrokeColor(Color color) {
		drawerColourPicker.setValue(color);
	}
	public void setFillColor(Color color) {
		fillColourPicker.setValue(color);
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

	public abstract void setCloseRequestHandler();

	public abstract void close();

}
