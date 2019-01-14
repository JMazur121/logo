package execution;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static final String MAIN_VIEW_URL = "/views/main.fxml";
	public static final String WINDOW_TITLE = "Logo-interpreter";

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW_URL));
		Parent root = loader.load();
		primaryStage.setTitle(WINDOW_TITLE);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
