package free.zereb;

import com.google.gson.Gson;
import free.zereb.util.GlobalHotKeys;
import free.zereb.utils.ArgumentHandler;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cadiro{

    public static String league = "";
    public JFrame frame = new JFrame("Cadiro");
    public JLabel labelDpsInfo = new JLabel();
    public JLabel labelName = new JLabel();
    public JLabel labelPricecheck = new JLabel();
    public Gson gson = new Gson();

    private Cadiro(){
        SystemTray systemTray;
       if (SystemTray.isSupported()){
           systemTray = SystemTray.getSystemTray();
           Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"));
           PopupMenu trayPopupMenu = new PopupMenu();
           MenuItem action = new MenuItem("League name");
           action.addActionListener(e -> JOptionPane.showMessageDialog(null, league));
           trayPopupMenu.add(action);

           MenuItem close = new MenuItem("Exit");
           close.addActionListener(e -> System.exit(0));
           trayPopupMenu.add(close);

           TrayIcon trayIcon = new TrayIcon(image, "Cadiro", trayPopupMenu);
           trayIcon.setImageAutoSize(true);
           System.out.println(systemTray.getTrayIconSize());

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



        frame.getContentPane().add(labelName);
        frame.getContentPane().add(labelDpsInfo);
        frame.getContentPane().add(labelPricecheck);
        frame.setAlwaysOnTop(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                labelDpsInfo.setText("");
                labelPricecheck.setText("");
                frame.setVisible(false);
                System.gc();
            }
        });

        try {
            GlobalHotKeys globalHotKeys = new GlobalHotKeys(this);
            GlobalScreen.addNativeKeyListener(globalHotKeys);
            GlobalScreen.addNativeMouseWheelListener(globalHotKeys);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        new ArgumentHandler()
                .setArgument("-l", a -> league = a[1].replaceAll("//", " "))
                .setArgument("-h", a -> System.out.println("-l <League name> use // for <space>"))
                .runArgs(args);
        System.out.println("league: " + league);
        SwingUtilities.invokeLater(Cadiro::new);
    }
}
