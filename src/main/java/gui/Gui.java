package gui;

import Exceptions.InvalidLoginException;
import Exceptions.InvalidServerAddressException;
import Exceptions.InvalidServerResponseException;
import connection.Client;
import connection.WebSocketClient;
import gui.desktop.Desktop;
import information.Information;
import util.items.Shop;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class Gui extends JFrame {

    Container pane;
    WebSocketClient webClient;


    public Gui() {


        setTitle("CrypticClient");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String server;

        try {
            server = chooseServer();
        } catch (InvalidServerAddressException e) {
            e.printStackTrace();
            server = "wss://ws.test.cryptic-game.net";
        }

        Information.client = new Client(server);
        webClient = Information.client.getClient();

        // new Thread wait for inputs in the console
        new Thread(() -> Information.client.init()).start();

        login();

        try {
            Shop.rootCategory = Shop.reloadCategories();
        } catch (InvalidServerResponseException e) {
            e.printStackTrace();
        }

        initialize();
        Information.gui = this;
        setVisible(true);

    }

    public void initialize() {

        this.pane = getContentPane();
        pane.removeAll();
        pane.setLayout(new BorderLayout());

        Desktop desktop = new Desktop(this);
        pane.add(desktop);
    }


    //JOptionPane for choosing the server
    private String chooseServer() throws InvalidServerAddressException {
        String[] options = {"main", "test"};
        String server = (String) JOptionPane.showInputDialog(null, "Welcher Server", "Serverauswahl",
                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        // set Properties
        Information.server = server;
        Information.properties = new Properties();
        try {
            final File file = new File(server + "Server.properties");
            if (file.exists())
                Information.properties.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (server.equalsIgnoreCase("main")) {
            return "wss://ws.cryptic-game.net";
        } else if (server.equalsIgnoreCase("test")) {
            return "wss://ws.test.cryptic-game.net";
        } else {
            throw new InvalidServerAddressException();
        }
    }


    public void login() {

        while (!Information.client.isOnline()) {
            Thread.onSpinWait();
        }

        // load with data from properties
        if (Information.properties.containsKey("uname") && Information.properties.containsKey("pw")) {
            try {
                Information.client.login(Information.properties.getProperty("uname"), Information.properties.getProperty("pw"));
                return;
            } catch (InvalidLoginException ignore) {

            } catch (InvalidServerResponseException e) {
                return;
            }
        }

        // load with userInput if no data from properties available
        String uname = JOptionPane.showInputDialog("Username");
        String pw = JOptionPane.showInputDialog("Password");

        try {
            Information.client.login(uname, pw);
            Information.properties.setProperty("uname", uname);
            Information.properties.setProperty("pw", pw);
        } catch (InvalidLoginException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Falsche Logindaten", "Fehler", JOptionPane.ERROR_MESSAGE);
            login();
        } catch (InvalidServerResponseException e) {
            e.printStackTrace();
        }
    }
}
