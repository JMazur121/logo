package execution.controller;

import execution.dispatching.GraphicExecutor;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import lombok.Setter;
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

	public void restartDirections() {
		directionVersor = new Point2D(0, -1);
		currentPosition = new Point2D(drawerCanvas.getWidth() / 2, drawerCanvas.getHeight() / 2);
		currentAngle = 0;
		isDrawerDown = true;
	}

	private Point2D getLeftTopCornerOfDrawer() {
		return new Point2D(currentPosition.getX() - imageWidth / 2, currentPosition.getY() - imageHeight / 2);
	}

	private void rotate(double angle, double px, double py) {
		Rotate r = new Rotate(angle, px, py);
		drawerContext.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	private void drawRotatedImage(double angle, double tlpx, double tlpy) {
		drawerContext.save();
		rotate(angle, currentPosition.getX(), currentPosition.getY());
		drawerContext.drawImage(drawerImage, tlpx, tlpy);
		drawerContext.restore();
	}

	@Override
	public void drawAlong(int pathDirection) {
		Point2D translation = directionVersor.multiply(pathDirection);
		Point2D endPoint = currentPosition.add(translation);
		// TODO: 2019-01-15 Zrobić rzeczywiste rysowanie i aktualizowanie pozycji.
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
		drawRotatedImage(0, corner.getX(), corner.getY());
		controller.setDrawerPosition(currentPosition.getX(), currentPosition.getY());
	}

	@Override
	public void rotate(int angle) {
		drawerContext.clearRect(0, 0, drawerCanvas.getWidth(), drawerCanvas.getHeight());
		currentAngle += angle;
		// TODO: 2019-01-15 Dodać to rysowanie obróconych obrazków
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
		// TODO: 2019-01-15 Trzeba sie zastanowic czy damy rade to zaimplementować
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
		backgroundContext.fillOval(x, y, radius, radius);
	}

	@Override
	public void moveDrawer(int xTranslation, int yTranslation) {
		drawerContext.clearRect(0, 0, drawerCanvas.getWidth(), drawerCanvas.getHeight());
		// TODO: 2019-01-15 Dopisac rysowanie, które też może wymagać tego obrotu
		Point2D translation = new Point2D(xTranslation, yTranslation);

	}

	@Override
	public void nop() {
	}

	@Override
	public void print(String message) {
		controller.showMessage(message);
	}

}
