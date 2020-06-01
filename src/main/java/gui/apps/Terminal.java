package gui.apps;

import gui.App;
import gui.apps.terminal.CommandArea;
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
        title = "Terminal";
        init();
    }

    protected void init(){
        super.init();


        try {
            this.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        commandArea = new CommandArea(this);
        commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" + Information.client.path.getPwd() + "$ ");
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
            commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" +  Information.client.path.getPwd() + "$ ");
        } catch (Exception e) {
            e.printStackTrace();
            commandArea.println(Arrays.toString(e.getStackTrace()));
            commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" +  Information.client.path.getPwd() + "$ ");
        }
    }

    @Override
    public void getFocus() {
        super.getFocus();
        commandArea.getFocus();
    }

}
