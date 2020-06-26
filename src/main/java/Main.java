
import gui.Gui;
import information.Information;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main(){

        new Gui();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            try {
                Information.properties.store(new FileOutputStream(Information.path+Information.server+"Server.properties"), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
