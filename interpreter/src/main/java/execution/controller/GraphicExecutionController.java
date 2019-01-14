package execution.controller;

import execution.dispatching.GraphicExecutor;
import javafx.scene.canvas.GraphicsContext;

public class GraphicExecutionController implements GraphicExecutor {

	private GraphicsContext drawerContext;
	private GraphicsContext backgroundContext;

	public GraphicExecutionController(GraphicsContext drawerContext, GraphicsContext backgroundContext) {
		this.drawerContext = drawerContext;
		this.backgroundContext = backgroundContext;
	}

	@Override
	public void drawAlong(int pathDirection) {

	}

	@Override
	public void clear() {

	}

	@Override
	public void rotate(int angle) {

	}

	@Override
	public void drawerUp() {

	}

	@Override
	public void drawerDown() {

	}

	@Override
	public void setStroke(String colourName) {

	}

	@Override
	public void setStroke(int r, int g, int b) {

	}

	@Override
	public void setFill(String colourName) {

	}

	@Override
	public void setFill(int r, int g, int b) {

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

	}

	@Override
	public void fillCircle(int radius) {

	}

	@Override
	public void moveDrawer(int xTranslation, int yTranslation) {

	}

	@Override
	public void nop() {

	}

	@Override
	public void print(String message) {

	}

}
