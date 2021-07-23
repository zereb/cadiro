package free.zereb.util;

import free.zereb.Cadiro;
import free.zereb.data.Item;
import free.zereb.utils.ItemParser;
import free.zereb.utils.Util;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class GlobalHotKeys implements NativeKeyListener, NativeMouseWheelListener {
    private final HashSet<Integer> mapper = new HashSet<>();
    private final HashMap<String, Runnable> keyCombinations = new HashMap<>();
    private final Robot robot;

    public GlobalHotKeys(Cadiro cadiro) throws AWTException {
        robot = new Robot();
        keyCombinations.putIfAbsent("CtrlC", () -> SwingUtilities.invokeLater(() -> {
            cadiro.frame.setResizable(false);
            Item item = ItemParser.parse(getClipboard());

            if (item.rarity.equals("Rare"))
                new Poeprices(item, cadiro);
            else {
                new PoeTrade(item, cadiro);
            }
            Point p = MouseInfo.getPointerInfo().getLocation();
            cadiro.labelName.setText(item.name + " Quality: " + item.quality);
            cadiro.labelDpsInfo.setText(Util.swingLabelNewlines(item.getDamage()));
            cadiro.labelPricecheck.setText("price check for " + Cadiro.league + " ...");
            p.x -= cadiro.frame.getWidth();
            cadiro.frame.setLocation(p);
            cadiro.frame.pack();
            cadiro.frame.setVisible(true);
        }));

        sendToChat("F4", "/hideout");
        sendToChat("F2", "/remaining");

        //15 left mouse autoclicks on Ctrl + Shift + X
        Runnable cliks = () -> {
            for (int i = 0; i < 15; i++) {
                robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                robot.delay(50);
                robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                robot.delay(50);
            }
        };

        keyCombinations.putIfAbsent("ShiftCtrlX", () -> new Thread(cliks).start());



    }


    private void sendToChat(String key, String seq){
        keyCombinations.putIfAbsent(key, () ->{
            char[] hideout = seq.toUpperCase().toCharArray();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            for (char c : hideout) {
                robot.keyPress(c);
                robot.keyRelease(c);
            }
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        });
    }

    private String getClipboard() {
        String clip = null;
        try {
            if (Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor)){
                clip = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            }
        }catch (UnsupportedFlavorException | IOException ignored) {
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
        keyCombinations.forEach((k, v) -> {
            if (k.equals(input.toString()))
                v.run();
        });
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        mapper.remove(e.getKeyCode());
    }


    //emulate arrow keys with Ctrl + mouseWheel
    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
        if (!mapper.contains(29)) return;
        System.out.println(nativeMouseWheelEvent.getWheelRotation());;
        if (nativeMouseWheelEvent.getWheelRotation() == -1) {
            robot.keyPress(KeyEvent.VK_LEFT);
            robot.keyRelease(KeyEvent.VK_LEFT);
            System.out.println("left");
        }
        if (nativeMouseWheelEvent.getWheelRotation() == 1) {
            robot.keyPress(KeyEvent.VK_RIGHT);
            robot.keyRelease(KeyEvent.VK_RIGHT);
            System.out.println("right");
        }

    }
}
