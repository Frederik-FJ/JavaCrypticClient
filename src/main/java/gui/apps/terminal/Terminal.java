package gui.apps.terminal;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.apps.terminal.CommandArea;
import information.Information;
import util.Path;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Arrays;

public class Terminal extends App {

    JDesktopPane window;
    Path path;

    CommandArea commandArea;

    public Terminal(JDesktopPane window) {
        super();
        this.window = window;

        width = 800;
        height = 600;
        title = "Terminal";
        path = new Path(Information.client.connectedDevice);
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
            // if command is exit or quit close the Terminal
            if ((command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit"))) {
                this.setClosed(true);
                return;
            }
            String result = processCommand(command);
            // if command returns something, write it
            if (!result.equals(""))
                commandArea.println(result);
            // print the new Line
            commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" + path.getPwd() + "$ ");
        } catch (Exception e) {
            e.printStackTrace();
            commandArea.println(Arrays.toString(e.getStackTrace()));
            commandArea.print("[" + Information.client.user + "@" + Information.client.device + "]" + path.getPwd() + "$ ");
        }
    }

    @Override
    public void getFocus() {
        super.getFocus();
        commandArea.getFocus();
    }

    public CommandArea getCommandArea(){
        return commandArea;
    }

    /**
     *
     * @param command The command from the console as String
     * @return The output of the command
     */
    public String processCommand(String command){
        String notFound = "Unknown command";

        String[] params = command.split(" ");

        boolean ls = command.equals("ls");
        boolean cd = command.startsWith("cd");
        boolean pwd = command.equals("pwd");

        if(ls){
            return path.listFiles();
        }

        if(cd){
            try{
                if(params.length < 2){
                    return path.changeDirectory("/");
                }
                return path.changeDirectory(params[1]);
            } catch (InvalidServerResponseException | UnknownMicroserviceException e){
                e.printStackTrace();
            }

        }

        if(pwd){
            path.updatePwd();
            return path.getPwd();
        }
        return notFound;
    }

}
