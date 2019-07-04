package org.zereb;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.zereb.data.Item;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    @FXML
    public Label labelDpsInfo;
    public Stage stage;

    public FXMLController(Stage primaryStage) {
        this.stage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void close(MouseEvent mouseEvent) {
        stage.close();
    }
}
