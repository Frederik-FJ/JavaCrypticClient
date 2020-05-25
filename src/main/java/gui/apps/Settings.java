package gui.apps;

import connection.Client;
import gui.App;
import javax.swing.*;

public class Settings extends App {

    Client client;
    JDesktopPane window;



    public Settings(JDesktopPane window, Client client){
        super();
        this.client = client;
        this.window = window;

        this.width = 300;
        this.height = 300;

        this.init();
    }

    private void init(){
        this.setSize(width, height);
        this.setTitle("Settings");
        this.moveToFront();
        this.setVisible(true);

    }


    @Override
    public void handleCommand(String command) {

    }

    @Override
    public void getFocus() {
        super.getFocus();
        this.setSize(width, height);
    }
}
