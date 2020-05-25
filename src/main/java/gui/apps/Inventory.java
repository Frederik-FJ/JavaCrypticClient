package gui.apps;

import connection.Client;
import gui.App;

import javax.swing.*;
import java.awt.*;

public class Inventory extends App {

    Client client;
    JDesktopPane window;

    public Inventory(Client client, JDesktopPane window){
        super();
        this.client = client;
        this.window = window;

        this.width = 300;
        this.height = 300;

        init();
    }

    private void init(){
        setBackground(Color.WHITE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        //List<HardwareElement> items = client.getInventory();

    }



    @Override
    public void handleCommand(String command) {

    }

    @Override
    public void getFocus() {
        super.getFocus();
    }
}
