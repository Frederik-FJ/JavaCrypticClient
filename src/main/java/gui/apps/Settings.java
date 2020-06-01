package gui.apps;

import connection.Client;
import gui.App;
import javax.swing.*;

public class Settings extends App {

    JDesktopPane window;



    public Settings(JDesktopPane window){
        super();
        this.window = window;

        this.width = 300;
        this.height = 300;

        this.init();
    }

    protected void init(){
        this.setSize(width, height);
        this.setTitle("Settings");
        this.moveToFront();
        this.setVisible(true);

    }


    @Override
    public void handleCommand(String command) {

    }

}
