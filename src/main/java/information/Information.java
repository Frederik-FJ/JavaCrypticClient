package information;

import connection.Client;
import connection.WebSocketClient;
import gui.desktop.DesktopPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Information {

    public static final String path = Information.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    public static Client client;
    public static WebSocketClient webSocketClient;

    public static String walletUuid = null;
    public static String walletPw = null;

    public static DesktopPane Desktop = null;

    public static ImageIcon fileIcon;
    public static ImageIcon dirIcon;

    static {
        try {
            fileIcon = new ImageIcon(ImageIO.read(new File(Information.path + "apps/file_manager/file.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            dirIcon = new ImageIcon(ImageIO.read(new File(Information.path + "apps/file_manager/dir.png")).getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
