package gui;

import Exceptions.InvalidLoginException;
import Exceptions.InvalidServerAddressException;
import Exceptions.InvalidServerResponseException;
import connection.Client;
import connection.WebSocketClient;
import gui.desktop.Desktop;

import javax.swing.*;
import java.awt.*;


public class Gui extends JFrame {

    Container pane;
    WebSocketClient webClient;
    Client client;


    public Gui(){


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

        this.client = new Client(server);
        webClient = client.getClient();

        // new Thread wait for inputs in the console
        new Thread(() -> client.init()).start();

        login();
        initialize();
        setVisible(true);

    }

    private void initialize(){

        this.pane = getContentPane();
        pane.setLayout(new BorderLayout());

        Desktop desktop = new Desktop(this, client);
        pane.add(desktop);
    }


    //JOptionPane for choosing the server
    private String chooseServer() throws InvalidServerAddressException {
        String[] options = {"main", "test"};
        String server = (String) JOptionPane.showInputDialog(null, "Welcher Server", "Serverauswahl",
                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if(server.equalsIgnoreCase("main")){
            return "wss://ws.cryptic-game.net";
        }else if(server.equalsIgnoreCase("test")){
            return "wss://ws.test.cryptic-game.net";
        }else{
            throw new InvalidServerAddressException();
        }
    }


    public void login(){
        String uname = JOptionPane.showInputDialog("Username");
        String pw = JOptionPane.showInputDialog("Password");

        try {
            client.login(uname, pw);
        } catch (InvalidLoginException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Falsche Logindaten", "Fehler", JOptionPane.ERROR_MESSAGE);
            login();
        } catch (InvalidServerResponseException e) {
            e.printStackTrace();
        }
    }





}
