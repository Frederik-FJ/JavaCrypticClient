import gui.Gui;
import information.Information;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public Main() {

        new Gui();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Information.properties.store(new FileOutputStream(Information.path + Information.server + "Server.properties"), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void main(String[] args) {
        new Main();
    }
}
