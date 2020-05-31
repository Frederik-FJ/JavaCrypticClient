package gui.apps;

import gui.App;
import gui.util.CommandArea;
import information.Information;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Arrays;

public class Terminal extends App {

    JDesktopPane window;

    CommandArea commandArea;

    public Terminal(JDesktopPane window) {
        super();
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
        commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" + Information.client.pwd + "$ ");
        this.add(commandArea);
    }

    @Override
    public void handleCommand(String command) {
        try {
            if((command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit"))/* && client.connected*/){
                this.setClosed(true);
                return;
            }
            String result = Information.client.processCommand(command);
            commandArea.println(result);
            commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" + Information.client.pwd + "$ ");
        } catch (Exception e) {
            e.printStackTrace();
            commandArea.println(Arrays.toString(e.getStackTrace()));
            commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" + Information.client.pwd + "$ ");
        }
    }

    @Override
    public void getFocus() {
        super.getFocus();
        this.setSize(width, height);
        commandArea.getFocus();
    }

}
