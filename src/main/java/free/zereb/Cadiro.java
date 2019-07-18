package free.zereb;

import free.zereb.util.GlobalHotKeys;
import free.zereb.utils.ArgumentHandler;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cadiro{

    public static String league = "Legion";
    public JFrame frame = new JFrame("Cadiro");
    public JLabel labelDpsInfo = new JLabel();
    public JLabel labelPricecheck = new JLabel();

    private Cadiro(){
        SystemTray systemTray;
       if (SystemTray.isSupported()){
           systemTray = SystemTray.getSystemTray();
           Image image = Toolkit.getDefaultToolkit().getImage("/icon.png");
           PopupMenu trayPopupMenu = new PopupMenu();
           MenuItem action = new MenuItem("League name");
           action.addActionListener(e -> JOptionPane.showMessageDialog(null, league));
           trayPopupMenu.add(action);

           MenuItem close = new MenuItem("Exit");
           close.addActionListener(e -> System.exit(0));
           trayPopupMenu.add(close);

           TrayIcon trayIcon = new TrayIcon(image, "Cadiro", trayPopupMenu);
           trayIcon.setImageAutoSize(true);

           try {
               systemTray.add(trayIcon);
           }catch (AWTException e){
               e.printStackTrace();
           }


       }

        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
        }catch (NativeHookException e){
            System.out.println(e.getMessage());
        }

        frame.setUndecorated(true);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));


        frame.getContentPane().add(labelDpsInfo);
        frame.getContentPane().add(labelPricecheck);
        frame.setAlwaysOnTop(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                labelDpsInfo.setText("");
                labelPricecheck.setText("");
                frame.setVisible(false);
            }
        });

        GlobalScreen.addNativeKeyListener(new GlobalHotKeys(this));
    }



    public static void main(String[] args) {
        new ArgumentHandler()
                .setArgument("-l", a -> league = a[1].replaceAll("//", " "))
                .setArgument("-h", a -> System.out.println("-l <League name>"))
                .runArgs(args);
        System.out.println("league: " + league);
        SwingUtilities.invokeLater(Cadiro::new);
    }
}
