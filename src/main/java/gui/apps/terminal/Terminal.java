package gui.apps.terminal;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import information.Information;
import items.Device;
import util.Path;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.util.Arrays;

public class Terminal extends App {

    JDesktopPane window = Information.Desktop;
    Path path;

    Device device;

    CommandArea commandArea;

    public Terminal(Device device) {
        super();

        this.device = device;

        width = 800;
        height = 600;
        title = "Terminal - " + device.getName();
        path = new Path(device);
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
        commandArea.print("[" + Information.client.user + "@" + device.getName() + "]" + path.getPwd() + "$ ");
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
            commandArea.print("[" + Information.client.user + "@" + device.getName() + "]" + path.getPwd() + "$ ");
        } catch (Exception e) {
            e.printStackTrace();
            commandArea.println(Arrays.toString(e.getStackTrace()));
            commandArea.print("[" + Information.client.user + "@" + device.getName() + "]" + path.getPwd() + "$ ");
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

        boolean fileManager = command.replace(" ", "").equalsIgnoreCase("filemanager");
        boolean terminal = command.replace(" ", "").equalsIgnoreCase("terminal");
        boolean attack = command.replace(" ", "").equalsIgnoreCase("attack");
        boolean serviceManager = command.replace(" ", "").equalsIgnoreCase("servicemanager");

        boolean ls = command.equals("ls");
        boolean cd = command.startsWith("cd");
        boolean pwd = command.equals("pwd");

        // open programs
        if(fileManager){
            Information.Desktop.startFileManager(device);
        }

        if(terminal){
            Information.Desktop.startTerminal(device);
        }

        if(attack){
            Information.Desktop.startServiceAttacker(device);
        }

        if(serviceManager){
            Information.Desktop.startServiceManager(device);
        }


        // path cmd
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
