package org.zereb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.zereb.util.GlobalHotKeys;


public class Cadiro extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            GlobalScreen.registerNativeHook();
        }catch (NativeHookException e){
            System.out.println(e.getMessage());
        }

        Platform.setImplicitExit(false);

        FXMLController controller = new FXMLController(primaryStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/scene.fxml"));
        loader.setControllerFactory(t -> controller);
        GlobalScreen.addNativeKeyListener(new GlobalHotKeys(controller));

        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(80);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(loader.load()));


    }

    public static void main(String[] args) {
        launch(args);
    }
}
