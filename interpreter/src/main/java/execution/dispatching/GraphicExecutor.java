package execution.dispatching;

public interface GraphicExecutor {

	void drawAlong(int pathDirection);
	void clear();
	void rotate(int angle);
	void drawerUp();
	void drawerDown();
	void setStroke(String colourName);
	void setStroke(int r, int g, int b);
	void setFill(String colourName);
	void setFill(int r, int g, int b);
	void defineColour(String name, int r, int g, int b);
	void fill();
	void fillPolygon(int numberOfPoints, int sideLength);
	void strokePolygon(int numberOfPoints, int sideLength);
	void strokeCircle(int radius);
	void fillCircle(int radius);
	void moveDrawer(int xTranslation, int yTranslation);
	void nop();
	void print(String message);

}
