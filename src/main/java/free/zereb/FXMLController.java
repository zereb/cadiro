package free.zereb;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    @FXML
    public Label labelDpsInfo;
    public Stage stage;
    public Label labelPoeprices;

    public double mouseX;
    public double mouseY;

    FXMLController(Stage primaryStage) {
        this.stage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void close(MouseEvent mouseEvent) {
        labelPoeprices.setText("");
        labelDpsInfo.setText("");
        stage.close();
    }
}
