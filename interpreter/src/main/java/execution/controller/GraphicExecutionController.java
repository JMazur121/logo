package execution.controller;

import execution.dispatching.GraphicExecutor;
import execution.utils.IntPair;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import lombok.Setter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GraphicExecutionController implements GraphicExecutor {

	private GraphicsContext drawerContext;
	private GraphicsContext backgroundContext;
	private Point2D directionVersor;
	private Point2D currentPosition;
	private int currentAngle;
	private boolean isDrawerDown;
	private Map<String, Color> definedColours;

	private GenericController controller;
	private Image drawerImage;
	private double imageWidth;
	private double imageHeight;
	private Canvas drawerCanvas;
	private Canvas backgroundCanvas;

	public GraphicExecutionController(GraphicsContext drawerContext, GraphicsContext backgroundContext,
									  Map<String, Color> definedColours, GenericController controller) {
		this.drawerContext = drawerContext;
		this.backgroundContext = backgroundContext;
		this.definedColours = definedColours;
		this.controller = controller;
		drawerCanvas = drawerContext.getCanvas();
		backgroundCanvas = backgroundContext.getCanvas();
		restartDirections();
	}

	public void setImage(Image image) {
		drawerImage = image;
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
	}

	private void restartDirections() {
		directionVersor = new Point2D(0, -1);
		currentPosition = new Point2D(drawerCanvas.getWidth() / 2, drawerCanvas.getHeight() / 2);
		currentAngle = 0;
		isDrawerDown = true;
	}

	private void setCurrentPositionInController() {
		controller.setDrawerPosition(currentPosition.getX(), currentPosition.getY());
	}

	private Point2D getLeftTopCornerOfDrawer() {
		return new Point2D(currentPosition.getX() - imageWidth / 2, currentPosition.getY() - imageHeight / 2);
	}

	private void rotate(double angle, double px, double py, GraphicsContext context) {
		Rotate r = new Rotate(angle, px, py);
		context.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	private void drawRotatedImage() {
		drawerContext.save();
		rotate(currentAngle, currentPosition.getX(), currentPosition.getY(), drawerContext);
		Point2D corner = getLeftTopCornerOfDrawer();
		drawerContext.drawImage(drawerImage, corner.getX(), corner.getY());
		drawerContext.restore();
	}

	private double toRadians(double angle) {
		return angle * (Math.PI / 180);
	}

	private Point2D rot(double x, double y, double angle) {
		double cosAngle = cos(toRadians(angle));
		double sinAngle = sin(toRadians(angle));
		double newX = x * cosAngle - y * sinAngle;
		double newY = x * sinAngle + y * cosAngle;
		return new Point2D(newX, newY);
	}

	private void updateDirectionVersor() {
		directionVersor = rot(0, -1, currentAngle);
	}

	@Override
	public void drawAlong(int pathDirection) {
		if (isDrawerDown) {
			Point2D translation = directionVersor.multiply(pathDirection);
			Point2D begin = currentPosition;
			currentPosition = currentPosition.add(translation);
			drawerContext.clearRect(0, 0, drawerCanvas.getWidth(), drawerCanvas.getHeight());
			drawRotatedImage();
			backgroundContext.strokeLine(begin.getX(), begin.getY(), currentPosition.getX(), currentPosition.getY());
			setCurrentPositionInController();
		}
	}

	@Override
	public void clear() {
		restartDirections();
		backgroundContext.setStroke(Color.BLACK);
		backgroundContext.setFill(Color.WHITE);
		controller.setStrokeColor(Color.BLACK);
		controller.setFillColor(Color.WHITE);
		backgroundContext.clearRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
		backgroundContext.strokeRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
		Point2D corner = getLeftTopCornerOfDrawer();
		drawerContext.clearRect(0, 0, drawerCanvas.getWidth(), drawerCanvas.getHeight());
		drawerContext.drawImage(drawerImage, corner.getX(), corner.getY());
		setCurrentPositionInController();
	}

	@Override
	public void rotate(int angle) {
		drawerContext.clearRect(0, 0, drawerCanvas.getWidth(), drawerCanvas.getHeight());
		currentAngle += angle;
		drawRotatedImage();
		updateDirectionVersor();
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
			print("There is no colour with name \"" + colourName + "\"");
		else {
			backgroundContext.setStroke(color);
			controller.setStrokeColor(color);
		}
	}

	@Override
	public void setStroke(int r, int g, int b) {
		if (r > 255 || g > 255 || b > 255)
			print("RGB values cannot exceed 255");
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
			print("There is no colour with name \"" + colourName + "\"");
		else {
			backgroundContext.setFill(color);
			controller.setFillColor(color);
		}
	}

	@Override
	public void setFill(int r, int g, int b) {
		if (r > 255 || g > 255 || b > 255)
			print("RGB values cannot exceed 255");
		else {
			Color color = Color.rgb(r, g, b);
			backgroundContext.setFill(color);
			controller.setFillColor(color);
		}
	}

	@Override
	public void defineColour(String name, int r, int g, int b) {
		if (r > 255 || g > 255 || b > 255)
			print("RGB values cannot exceed 255");
		else {
			Color color = Color.rgb(r, g, b);
			definedColours.put(name, color);
		}
	}

	@Override
	public void fill() {
		WritableImage image = backgroundCanvas.snapshot(null, null);
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = image.getPixelWriter();
		int x = (int) currentPosition.getX();
		int y = (int) currentPosition.getY();
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		Color targetColor = reader.getColor(x, y);
		Color currentFill = (Color)backgroundContext.getFill();
		Stack<IntPair> stack = new Stack<>();
		stack.push(new IntPair(x, y));
		while (!stack.isEmpty()) {
			IntPair point = stack.pop();
			x = point.x;
			y = point.y;
			if (filled(reader, x, y, targetColor)) {
				continue;
			}
			writer.setColor(x, y, currentFill);
			push(stack, x - 1, y, width, height);
			push(stack, x, y + 1, width, height);
			push(stack, x + 1, y, width, height);
			push(stack, x, y - 1, width, height);
		}
		backgroundContext.drawImage(image, 0, 0);
	}

	private void push(Stack<IntPair> stack, int x, int y, int w, int h) {
		if (x < 0 || x > w ||
				y < 0 || y > h) {
			return;
		}
		stack.push(new IntPair(x, y));
	}

	private boolean filled(PixelReader reader, int x, int y, Color toFill) {
		Color color = reader.getColor(x, y);
		return !color.equals(toFill);
	}

	private void calcPolygonPoints(double[] xPoints, double[] yPoints, int numberOfPoints, int sideLength) {
		double internalAngle = 360 / numberOfPoints;
		double radius = (sideLength / 2) * (1 / sin(toRadians(internalAngle / 2)));
		double startingAngle = (numberOfPoints % 2 == 0) ? 45 : 90;
		for (int i = 0; i < numberOfPoints; i++) {
			double currentAngle = startingAngle + i * internalAngle;
			xPoints[i] = (radius * cos(toRadians(currentAngle))) + currentPosition.getX();
			yPoints[i] = -(radius * sin(toRadians(currentAngle))) + currentPosition.getY();
		}
	}

	@Override
	public void fillPolygon(int numberOfPoints, int sideLength) {
		if (numberOfPoints < 3)
			print("Too few point for polygon");
		else if (sideLength < 0)
			print("Side length for polygon must be positive");
		else {
			double[] xPoints = new double[numberOfPoints];
			double[] yPoints = new double[numberOfPoints];
			calcPolygonPoints(xPoints, yPoints, numberOfPoints, sideLength);
			backgroundContext.fillPolygon(xPoints, yPoints, numberOfPoints);
		}
	}

	@Override
	public void strokePolygon(int numberOfPoints, int sideLength) {
		if (numberOfPoints < 3)
			print("Too few point for polygon");
		else if (sideLength < 0)
			print("Side length for polygon must be positive");
		else {
			double[] xPoints = new double[numberOfPoints];
			double[] yPoints = new double[numberOfPoints];
			calcPolygonPoints(xPoints, yPoints, numberOfPoints, sideLength);
			backgroundContext.strokePolygon(xPoints, yPoints, numberOfPoints);
		}
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
		backgroundContext.fillOval(x, y, radius, radius);
	}

	@Override
	public void moveDrawer(int xTranslation, int yTranslation) {
		drawerContext.clearRect(0, 0, drawerCanvas.getWidth(), drawerCanvas.getHeight());
		currentPosition = currentPosition.add(xTranslation, yTranslation);
		drawRotatedImage();
		setCurrentPositionInController();
	}

	@Override
	public void strokeEllipse(int width, int height) {
		double x = currentPosition.getX() - width / 2;
		double y = currentPosition.getY() - height / 2;
		backgroundContext.save();
		rotate(currentAngle, currentPosition.getX(), currentPosition.getY(), backgroundContext);
		backgroundContext.strokeOval(x, y, width, height);
		backgroundContext.restore();
	}

	@Override
	public void fillEllipse(int width, int height) {
		double x = currentPosition.getX() - width / 2;
		double y = currentPosition.getY() - height / 2;
		backgroundContext.save();
		rotate(currentAngle, currentPosition.getX(), currentPosition.getY(), backgroundContext);
		backgroundContext.fillOval(x, y, width, height);
		backgroundContext.restore();
	}

	@Override
	public void print(String message) {
		controller.showMessage(message);
	}

}
