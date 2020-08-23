package information;

import connection.Client;
import connection.WebSocketClient;
import gui.Gui;
import gui.desktop.DesktopPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class Information {

    public static Properties properties;
    public static Gui gui;
    public static Client client;
    public static WebSocketClient webSocketClient;

    public static String walletUuid = null;
    public static String walletPw = null;

    public static volatile DesktopPane Desktop = null;

    public static ImageIcon fileIcon;
    public static ImageIcon dirIcon;

    public static String server = "";

    static {
        try {
            fileIcon = new ImageIcon(ImageIO.read(Information.class.getResourceAsStream("/apps/file_manager/file.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dirIcon = new ImageIcon(ImageIO.read(Information.class.getResourceAsStream("/apps/file_manager/dir.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
