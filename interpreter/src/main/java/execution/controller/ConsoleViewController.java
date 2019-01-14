package execution.controller;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ConsoleViewController implements Initializable {

	public TextField drawerPositionField;
	public TextField mousePositionField;
	public ComboBox latencyComboBox;
	public ColorPicker drawerColourPicker;
	public ColorPicker fillColourPicker;
	public Button endButton;
	public Button submitButton;
	public TextField responseField;
	public TextArea codeTextArea;
	public GridPane centerPane;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void setCloseRequestHandler() {
		endButton.getScene().getWindow().setOnCloseRequest(event -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Zakończenie");
			alert.setHeaderText(null);
			alert.setContentText("Opuszczasz tryb pracy z konsolą. Poczekaj na zamknięcie okna");
			alert.showAndWait();
			closeConsole();
		});
	}

	private void closeConsole() {

	}

}
