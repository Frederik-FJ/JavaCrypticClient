package gui.desktop;

import connection.Client;

import javax.swing.*;
import java.awt.*;

public class Desktop extends JPanel {

    DesktopPane pane;
    Taskbar taskbar;

    Client client;
    JFrame window;

    public Desktop(JFrame window, Client client){
        this.client = client;
        this.window = window;


        init();

    }

    private void init(){
        this.setLayout(new BorderLayout());

        taskbar = new Taskbar(pane);
        pane = new DesktopPane(this, client, taskbar);

        this.add(pane, BorderLayout.CENTER);
        this.add(taskbar, BorderLayout.WEST);
    }




}
