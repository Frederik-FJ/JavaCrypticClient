package information;

import connection.Client;
import connection.WebSocketClient;

public class Information {

    public static final String path = Information.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    public static Client client;
    public static WebSocketClient webSocketClient;

    public static String walletUuid = null;
    public static String walletPw = null;
}
