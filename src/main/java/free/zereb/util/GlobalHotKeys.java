package free.zereb.util;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import free.zereb.FXMLController;
import free.zereb.data.Item;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalHotKeys implements NativeKeyListener{
    private HashSet<Integer> mapper = new HashSet<>();
    private HashMap<String, Runnable> keyKombinations = new HashMap<>();
    private Point2D mousePosition;

    public GlobalHotKeys(FXMLController controller){
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        keyKombinations.putIfAbsent("CtrlC", () -> {
                Item item = new Item(getClipboard());
                new Poeprices(item, controller);
                Platform.runLater(() -> {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    controller.labelDpsInfo.setText(item.toString());
                    controller.stage.setX(p.x);
                    controller.stage.setY(p.y);
                    controller.stage.show();
                });
        });
    }


    private String getClipboard() {
        String clip = null;
        try {
           clip = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException | IOException ignored) {}
        return clip;
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
