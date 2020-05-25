package gui.apps;

import connection.Client;
import gui.App;
import gui.util.CommandArea;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Arrays;

public class Terminal extends App {

    JDesktopPane window;
    Client client;

    CommandArea commandArea;

    public Terminal(JDesktopPane window, Client client) {
        super();
        this.client = client;
        this.window = window;

        width = 800;
        height = 600;
        init();
    }

    private void init(){

        this.setBackground(new Color(0xffffff));
        String title = "Terminal";

        this.setTitle(title);
        this.setSize(width, height);
        this.moveToFront();
        this.setLocation(window.getX()/2, window.getY()/2);
        this.setVisible(true);
        try {
            this.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        commandArea = new CommandArea(this);
        commandArea.print("[" + client.user + "]$ ");
        this.add(commandArea);
    }

    @Override
    public void handleCommand(String command) {
        try {
            if((command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit"))/* && client.connected*/){
                this.setClosed(true);
                return;
            }
            String result = client.processCommand(command);
            commandArea.println(result);
            commandArea.print("[" + client.user + "]$ ");
        } catch (Exception e) {
            e.printStackTrace();
            commandArea.println(Arrays.toString(e.getStackTrace()));
            commandArea.print("[" + client.user + "]");
        }
    }

    @Override
    public void getFocus() {
        super.getFocus();
        this.setSize(width, height);
        commandArea.getFocus();
    }

}
