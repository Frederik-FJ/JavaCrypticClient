package gui.apps.terminal;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import Exceptions.file.MissingFileException;
import Exceptions.file.UnknownFileSourceException;
import gui.App;
import gui.apps.fileManager.FileManager;
import gui.apps.walletApp.WalletApp;
import gui.util.OutputApp;
import information.Information;
import util.items.Device;
import util.file.File;
import util.file.WalletFile;
import util.path.DirectoryPath;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Objects;

public class Terminal extends App implements OutputApp {

    DirectoryPath directoryPath;

    Device device;

    CommandArea commandArea;

    public Terminal(Device device) {
        super();

        this.device = device;

        width = 800;
        height = 600;
        title = "Terminal - " + device.getName();
        directoryPath = new DirectoryPath(device);
        init();
    }

    protected void init() {
        super.init();


        try {
            this.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        commandArea = new CommandArea(this);
        commandArea.print("[" + Information.client.user + "@" + device.getName() + "]" + directoryPath.getPwd() + "$ ");
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
            commandArea.print("[" + Information.client.user + "@" + device.getName() + "]" + directoryPath.getPwd() + "$ ");
        } catch (Exception e) {
            e.printStackTrace();
            commandArea.println(Arrays.toString(e.getStackTrace()));
            commandArea.print("[" + Information.client.user + "@" + device.getName() + "]" + directoryPath.getPwd() + "$ ");
        }
    }

    @Override
    public void getFocus() {
        super.getFocus();
        commandArea.getFocus();
    }

    public CommandArea getCommandArea() {
        return commandArea;
    }

    /**
     * @param command The command from the console as String
     * @return The output of the command
     */
    public String processCommand(String command) {
        String notFound = "Unknown command";

        if (command.equals("")) {
            return "";
        }

        String[] params = command.split(" ");

        boolean fileManager = command.replace(" ", "").equalsIgnoreCase("filemanager");
        boolean terminal = command.replace(" ", "").equalsIgnoreCase("terminal");
        boolean attack = command.replace(" ", "").equalsIgnoreCase("attack");
        boolean serviceManager = command.replace(" ", "").equalsIgnoreCase("servicemanager");
        boolean walletApp = command.replace(" ", "").startsWith("wallet");
        boolean miner = command.replace(" ", "").equalsIgnoreCase("miner");

        boolean ls = command.equals("ls");
        boolean cd = command.startsWith("cd");
        boolean pwd = command.equals("pwd");

        boolean run = command.startsWith("run");

        boolean usePath = command.contains("/");

        // open programs
        if (fileManager) {
            FileManager f = Information.Desktop.startFileManager(device);

            // loading the app
            Dimension d = f.getSize();
            f.setSize(1, 1);
            f.setSize(d);
            return "starting fileManager";
        }

        if (miner) {
            Information.Desktop.startMinerApp(device);
            return "starting MinerApp";
        }

        if (terminal) {
            Information.Desktop.startTerminal(device);
            return "starting terminal";
        }

        if (attack) {
            Information.Desktop.startServiceAttacker(device);
            return "starting attacker";
        }

        if (serviceManager) {
            Information.Desktop.startServiceManager(device);
            return "starting serviceManager";
        }

        if (walletApp) {
            if (params.length == 1) {
                try {
                    WalletApp w = new WalletApp(this.device);
                    Information.Desktop.startApp(w, null);
                    return "starting Wallet-App";
                } catch (UnknownFileSourceException | MissingFileException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }

            try {
                WalletFile walletFile = new WalletFile(Objects.requireNonNull(this.getFileFromPath(command.split(" ")[1])));
                Information.Desktop.startWalletApp(walletFile);
                return "starting Wallet-App";
            } catch (NullPointerException e) {
                e.printStackTrace();
                return "Invalid File";
            }
        }


        // path cmd
        if (ls) {
            return directoryPath.listFiles();
        }

        if (cd) {
            try {
                if (params.length < 2) {
                    return directoryPath.changeDirectory("/");
                }
                return directoryPath.changeDirectory(params[1]);
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }

        }

        if (pwd) {
            directoryPath.updatePwd();
            return directoryPath.getPwd();
        }


        if (run) {
            if (params[0].equals("run-interpret"))
                getFileFromPath(params[1]).toExecutionFile().executeWithInterpreter();
            else
                getFileFromPath(params[1]).toExecutionFile().execute();
        }

        if (usePath) {

            directoryPath = new DirectoryPath(this.directoryPath);
            String[] dirs = command.split("/");
            for (String dir : dirs) {
                if (dir.contains(" ") && !dir.contains("\"")) {
                    dir = dir.split(" ")[0];
                }
                if (dir.equals(".")) continue;
                try {
                    for (File f : directoryPath.getCurrentFile().getFiles()) {
                        if (dir.equals(f.getName())) {
                            if (!f.isDirectory()) {
                                if (command.replace(" ", "").endsWith("--new"))
                                    f.toExecutionFile().execute();
                                else
                                    f.toExecutionFile().executeFromTerminal(this);
                                return "";
                            }
                            break;
                        }
                    }
                } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException e) {
                    e.printStackTrace();
                }
                try {
                    directoryPath.changeDirectory(dir);
                } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                    e.printStackTrace();
                    JOptionPane.showInternalMessageDialog(Information.Desktop, "Error");
                    break;
                }
            }
        }
        return notFound;
    }

    private File getFileFromPath(String path) {
        DirectoryPath p = new DirectoryPath(this.directoryPath);
        String[] dirs = path.split("/");
        for (String dir : dirs) {
            if (dir.contains(" ") && !dir.contains("\"")) {
                dir = dir.split(" ")[0];
            }
            if (dir.equals(".")) continue;
            try {
                for (File f : p.getCurrentFile().getFiles()) {
                    if (dir.equals(f.getName())) {
                        if (!f.isDirectory())
                            return f;
                        break;
                    }
                }
            } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException e) {
                e.printStackTrace();
            }
            try {
                p.changeDirectory(dir);
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
                JOptionPane.showInternalMessageDialog(Information.Desktop, "Error");
                break;
            }
        }
        return null;
    }

    @Override
    public void println(Object o) {
        commandArea.println(o);
    }

    @Override
    public void print(Object o) {
        commandArea.print(o);
    }

    @Override
    public void toNextFreeLine() {
        commandArea.toNextFreeLine();
    }
}
