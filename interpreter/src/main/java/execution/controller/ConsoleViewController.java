package execution.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

}
