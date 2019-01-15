package execution.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import java.io.IOException;

public class MainViewController {

	public Button scriptButton;
	public Button consoleButton;
	public Button exitButton;
	public static final String CONSOLE_VIEW_URL = "/views/console.fxml";
	public static final String SCRIPT_VIEW_URL = "/views/script.fxml";

	@Setter
	private Image drawerImage;

	public void exitPressed(ActionEvent event) {
		System.exit(0);
	}

	public void workWithScriptPressed(ActionEvent event) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource(SCRIPT_VIEW_URL));
			Parent root = loader.load();
			ScriptViewController controller = loader.getController();
			stage.setScene(new Scene(root));
			stage.setTitle("Praca ze skryptem");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(((Node) event.getSource()).getScene().getWindow());
			stage.show();
			controller.setSpecialHandlers();
			controller.setDrawerImage(drawerImage);
			controller.clearCanvas();
		} catch (IOException e) {
			showOpeningErrorDialog();
		}
	}

	public void workWithConsolePressed(ActionEvent event) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource(CONSOLE_VIEW_URL));
			Parent root = loader.load();
			ConsoleViewController viewController = loader.getController();
			stage.setScene(new Scene(root));
			stage.setTitle("Praca z konsolą");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(((Node) event.getSource()).getScene().getWindow());
			stage.show();
			viewController.setSpecialHandlers();
			viewController.setDrawerImage(drawerImage);
			viewController.clearCanvas();
		} catch (IOException e) {
			showOpeningErrorDialog();
		}
	}

	private void showOpeningErrorDialog() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Błąd odczytu");
		alert.setHeaderText("Nie można otworzyć okna");
		alert.setContentText("Nie udało się załadować pliku opisującego okno pracy z konsolą!");
		alert.showAndWait();
	}

}
