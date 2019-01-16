package execution.controller;

import execution.instructions.ParserExecutor;
import execution.utils.ResizableCanvas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class GenericController implements Initializable {

	public TextField drawerPositionField;
	public TextField mousePositionField;
	public ComboBox<Long> latencyComboBox;
	public ColorPicker drawerColourPicker;
	public ColorPicker fillColourPicker;
	public Button endButton;
	public TextField responseField;
	public GridPane centerPane;
	public ProgressIndicator indicator;

	protected Image drawerImage;
	protected ResizableCanvas drawerCanvas;
	protected ResizableCanvas backgroundCanvas;
	protected Map<String, Color> definedColours;
	protected GraphicExecutionController executionController;

	protected AtomicLong latency;
	protected AtomicBoolean isWorkToDo;
	protected BlockingQueue<Runnable> tasksQueue;
	protected ParserExecutor parserExecutor;
	protected GraphicalTaskConsumer consumer;

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
		setupLatencyBox();
		isWorkToDo = new AtomicBoolean(true);
		tasksQueue = new LinkedBlockingQueue<>();
		parserExecutor = new ParserExecutor(executionController, tasksQueue, isWorkToDo);
		consumer = new GraphicalTaskConsumer(latency, isWorkToDo, tasksQueue);
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

	public void showAlert(String title, String message, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void setupLatencyBox() {
		ObservableList<Long> latencies =
				FXCollections.observableArrayList(
						0L,
						100L,
						200L,
						500L,
						1000L
				);
		latencyComboBox.setItems(latencies);
		latencyComboBox.setValue(500L);
		latency = new AtomicLong(500L);
		latencyComboBox.setOnAction(event -> {
			Long selected = latencyComboBox.getValue();
			if (selected == null)
				latencyComboBox.setValue(latency.get());
			else
				latency.set(selected);
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
		backgroundCanvas.getGraphicsContext2D().setStroke(Color.BLACK);
		backgroundCanvas.getGraphicsContext2D().setFill(Color.WHITE);
	}

	public abstract void setSpecialHandlers();

	public abstract void close();

}
