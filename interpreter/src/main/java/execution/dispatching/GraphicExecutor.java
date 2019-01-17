package execution.dispatching;

public interface GraphicExecutor {

	void drawAlong(int pathDirection);
	void clear();
	void rotate(int angle);
	void drawerUp();
	void drawerDown();
	void setStroke(int r, int g, int b);
	void setFill(int r, int g, int b);
	void fill();
	void fillPolygon(int numberOfPoints, int sideLength);
	void strokePolygon(int numberOfPoints, int sideLength);
	void strokeCircle(int radius);
	void fillCircle(int radius);
	void strokeEllipse(int width, int height);
	void fillEllipse(int width, int height);
	void translateDrawer(int xTranslation, int yTranslation);
	void moveDrawer(int x, int y);
	void print(String message);

}
