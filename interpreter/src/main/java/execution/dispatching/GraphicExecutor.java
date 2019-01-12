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
	void fill();
	void polygon(double[] xPoints, double[] yPoints);
	void strokeRound(int radius);
	void drawCircle(int radius);
	void moveDrawer(int xTranslation, int yTranslation);

}
