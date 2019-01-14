package execution;

import execution.controller.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {

	public static final String MAIN_VIEW_URL = "/views/main.fxml";
	public static final String WINDOW_TITLE = "Logo-interpreter";
	public static final String PICTURE_URL = "f1.png";
	public static final int PICTURE_WIDTH = 20;
	public static final int PICTURE_HEIGHT = 50;

	@Override
	public void start(Stage primaryStage) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			new File(classLoader.getResource(PICTURE_URL).getFile());
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Błąd odczytu");
			alert.setHeaderText("Nie można otworzyć zasobu");
			alert.setContentText("Nie udało się załadować obrazka rysownika!");
			alert.showAndWait();
			System.exit(1);
		}
		Image drawerImage = new Image(PICTURE_URL, PICTURE_WIDTH, PICTURE_HEIGHT, false, true);
		FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW_URL));
		Parent root = loader.load();
		primaryStage.setTitle(WINDOW_TITLE);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		MainViewController controller = loader.getController();
		controller.setDrawerImage(drawerImage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
