package free.zereb;

import free.zereb.utils.Argument;
import free.zereb.utils.ArgumentHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import free.zereb.util.GlobalHotKeys;



public class Cadiro extends Application {

    public static String league = "Legion";

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
        System.out.println("hi");
        new ArgumentHandler()
                .setArgument("-l", a -> league = a[1])
                .setArgument("-h", a -> System.out.println("-l <League name>"))
                .runArgs(args);
        launch(args);
    }
}
