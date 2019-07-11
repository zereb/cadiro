package free.zereb.util;

import free.zereb.Cadiro;
import free.zereb.data.Item;
import free.zereb.utils.Util;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class GlobalHotKeys implements NativeKeyListener{
    private HashSet<Integer> mapper = new HashSet<>();
    private HashMap<String, Runnable> keyKombinations = new HashMap<>();

    public GlobalHotKeys(Cadiro cadiro){
        keyKombinations.putIfAbsent("CtrlC", () -> {
                SwingUtilities.invokeLater(() -> {
                    Item item = new Item(getClipboard());
                    new Poeprices(item, cadiro);
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    cadiro.labelDpsInfo.setText(Util.swingLabelNewlines(item.toString()));
                    cadiro.frame.setLocation(p);
                    cadiro.frame.pack();
                    cadiro.frame.setVisible(true);
                });
        });
    }


    private String getClipboard() {
        String clip = null;
        try {
            if ((Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor)){
                clip = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            }
        }catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
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
