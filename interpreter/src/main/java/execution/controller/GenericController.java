package execution.controller;

import execution.utils.ResizableCanvas;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
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

	protected Image drawerImage;
	protected ResizableCanvas drawerCanvas;
	protected ResizableCanvas backgroundCanvas;
	protected Map<String, Color> definedColours;
	protected GraphicExecutionController executionController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createCanvas();
		executionController = new GraphicExecutionController(drawerCanvas.getGraphicsContext2D(), backgroundCanvas.getGraphicsContext2D(),
				definedColours, this);
		drawerCanvas.setOnMouseMoved(event -> mousePositionField.setText(String.format("(%d,%d)", (int) event.getX(), (int) event.getY())));
		definedColours = new HashMap<>();
		drawerColourPicker.setValue(Color.BLACK);
		fillColourPicker.setValue(Color.WHITE);
		drawerColourPicker.setOnAction(event -> backgroundCanvas.getGraphicsContext2D().setStroke(drawerColourPicker.getValue()));
		fillColourPicker.setOnAction(event -> backgroundCanvas.getGraphicsContext2D().setFill(fillColourPicker.getValue()));
		drawerCanvas.widthProperty().addListener((observable, oldValue, newValue) -> executionController.clear());
		drawerCanvas.heightProperty().addListener((observable, oldValue, newValue) -> executionController.clear());
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

	public void setDrawerImage(Image image) {
		drawerImage = image;
		executionController.setImage(image);
	}

	public void clearCanvas() {
		double w = backgroundCanvas.getWidth();
		double h = backgroundCanvas.getHeight();
		GraphicsContext context = backgroundCanvas.getGraphicsContext2D();
		context.fillRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
		setDrawerPosition(w / 2, h / 2);
		drawerCanvas.getGraphicsContext2D().drawImage(drawerImage, w / 2 - drawerImage.getWidth() / 2, h / 2 - drawerImage.getHeight() / 2);
	}

	public void setDrawerPosition(double x, double y) {
		drawerPositionField.setText(String.format("(%d,%d)", (int) x, (int) y));
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
		backgroundCanvas.getGraphicsContext2D().setStroke(Color.BLACK);
		backgroundCanvas.getGraphicsContext2D().setFill(Color.WHITE);
	}

	public abstract void setCloseRequestHandler();

	public abstract void close();

}
