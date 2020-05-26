package information;

import connection.Client;

public class Information {

    public static final String path = Information.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    public static Client client;
}
