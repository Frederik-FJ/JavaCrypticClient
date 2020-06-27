package gui.controlCenter;

import gui.util.Panel;
import information.Information;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class UserCategory extends Panel {

    JLabel name;
    JButton reLogin;
    JButton logout;

    public UserCategory() {
        init();
    }

    private void init() {
        this.setLayout(null);

        name = new JLabel(Information.client.user);
        reLogin = new JButton("login with other account");
        logout = new JButton("logout and quit");

        reLogin.addActionListener(actionEvent -> {
            Information.client.logout();
            Information.properties.remove("uname");
            Information.properties.remove("pw");
            Information.gui.login();
            Information.gui.initialize();
            Information.gui.revalidate();
            Information.gui.repaint();
        });

        logout.addActionListener(actionEvent -> {
            Information.client.logout();
            Information.properties.remove("uname");
            Information.properties.remove("pw");
            System.exit(1);
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });

        this.add(reLogin);
        this.add(logout);
        this.add(name);
        this.reload();
    }

    public void reload() {
        name.setSize(name.getText().length() * 8, 20);
        name.setLocation(this.relativeWidth(50) - (name.getWidth() / 2), this.relativeHeight(1) + name.getHeight());

        reLogin.setSize(reLogin.getText().length() * 8, 15);
        reLogin.setLocation(this.relativeWidth(10), this.relativeHeight(30));

        logout.setSize(logout.getText().length() * 8, 15);
        logout.setLocation(this.relativeWidth(10), reLogin.getY() + reLogin.getHeight() + 20);

        this.revalidate();
        this.repaint();
    }
}
