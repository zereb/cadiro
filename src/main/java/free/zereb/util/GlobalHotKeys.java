package free.zereb.util;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import free.zereb.FXMLController;
import free.zereb.data.Item;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalHotKeys implements NativeKeyListener {
    private HashSet<Integer> mapper = new HashSet<>();
    private HashMap<String, Runnable> keyKombinations = new HashMap<>();

    public GlobalHotKeys(FXMLController controller){
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        keyKombinations.putIfAbsent("CtrlC", () -> {
            try {
                String clip = Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
                Item item = new Item(clip);
//                Item item = new Item(new String(Files.readAllBytes(Paths.get(getClass().getResource("/test/rare_claw").toURI()))));
                Poeprices poeprices = new Poeprices(item, controller);
                Platform.runLater(() -> {
                    controller.labelDpsInfo.setText(item.toString());
                    controller.stage.show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        mapper.add(e.getKeyCode());
        StringBuilder input = new StringBuilder();
        for (Integer a : mapper) {
            input.append(NativeKeyEvent.getKeyText(a));
        }
        keyKombinations.forEach((k, v) -> {
            if (k.equals(input.toString()))
                v.run();
        });
    }


    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        mapper.remove(e.getKeyCode());
    }
}
