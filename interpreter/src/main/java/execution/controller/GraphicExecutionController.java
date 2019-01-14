package execution.controller;

import execution.dispatching.GraphicExecutor;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.Map;

public class GraphicExecutionController implements GraphicExecutor {

	private GraphicsContext drawerContext;
	private GraphicsContext backgroundContext;
	private Point2D directionVersor;
	private Point2D currentPosition;
	private int currentAngle;
	private boolean isDrawerDown;
	private Map<String, Color> definedColours;

	private GenericController controller;

	public GraphicExecutionController(GraphicsContext drawerContext, GraphicsContext backgroundContext,
									  Map<String, Color> definedColours, GenericController controller) {
		this.drawerContext = drawerContext;
		this.backgroundContext = backgroundContext;
		this.definedColours = definedColours;
		this.controller = controller;
		restartDirections();
	}

	public void restartDirections() {
		Canvas canvas = drawerContext.getCanvas();
		directionVersor = new Point2D(0, -1);
		currentPosition = new Point2D(canvas.getWidth() / 2, canvas.getHeight() / 2);
		currentAngle = 0;
		isDrawerDown = true;
	}

	@Override
	public void drawAlong(int pathDirection) {
		Point2D translation = directionVersor.multiply(pathDirection);
		Point2D endPoint = currentPosition.add(translation);

	}

	@Override
	public void clear() {
		restartDirections();
		backgroundContext.setStroke(Color.BLACK);
		backgroundContext.setFill(Color.WHITE);
		controller.setStrokeColor(Color.BLACK);
		controller.setFillColor(Color.WHITE);
	}

	@Override
	public void rotate(int angle) {

	}

	@Override
	public void drawerUp() {
		isDrawerDown = false;
	}

	@Override
	public void drawerDown() {
		isDrawerDown = true;
	}

	@Override
	public void setStroke(String colourName) {
		Color color = definedColours.get(colourName);
		if (color == null)
			print("Nie zdefiniowano koloru o nazwie \"" + colourName + "\"");
		else {
			backgroundContext.setStroke(color);
			controller.setStrokeColor(color);
		}
	}

	@Override
	public void setStroke(int r, int g, int b) {
		if (r > 255 || g > 255 || b > 255)
			print("Wartości składowych RGB nie mogą przekraczać 255");
		else {
			Color color = Color.rgb(r, g, b);
			backgroundContext.setStroke(color);
			controller.setStrokeColor(color);
		}
	}

	@Override
	public void setFill(String colourName) {
		Color color = definedColours.get(colourName);
		if (color == null)
			print("Nie zdefiniowano koloru o nazwie \"" + colourName + "\"");
		else {
			backgroundContext.setFill(color);
			controller.setFillColor(color);
		}
	}

	@Override
	public void setFill(int r, int g, int b) {
		if (r > 255 || g > 255 || b > 255)
			print("Wartości składowych RGB nie mogą przekraczać 255");
		else {
			Color color = Color.rgb(r, g, b);
			backgroundContext.setFill(color);
			controller.setFillColor(color);
		}
	}

	@Override
	public void defineColour(String name, int r, int g, int b) {
	}

	@Override
	public void fill() {

	}

	@Override
	public void fillPolygon(int numberOfPoints) {

	}

	@Override
	public void strokeCircle(int radius) {
		double x = currentPosition.getX() - radius;
		double y = currentPosition.getY() - radius;
		backgroundContext.strokeOval(x, y, radius, radius);
	}

	@Override
	public void fillCircle(int radius) {
		double x = currentPosition.getX() - radius;
		double y = currentPosition.getY() - radius;
		backgroundContext.fillOval(x, y ,radius, radius);
	}

	@Override
	public void moveDrawer(int xTranslation, int yTranslation) {

	}

	@Override
	public void nop() {}

	@Override
	public void print(String message) {
		controller.showMessage(message);
	}

}
