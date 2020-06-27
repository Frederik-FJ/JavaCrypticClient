package gui.apps;

import gui.App;
import information.Information;

import javax.swing.*;
import java.awt.*;

public class Settings extends App {

    JDesktopPane window;


    public Settings(JDesktopPane window) {
        super();
        this.window = window;

        this.width = 300;
        this.height = 300;
        this.title = "settings";

        this.init();
    }

    protected void init() {
        super.init();
        this.setLayout(new FlowLayout());

        JLabel deviceName = new JLabel(Information.client.connectedDevice.getName());
        this.add(deviceName);

        JTextField uuid = new JTextField(Information.client.connectedDevice.getUuid());
        this.add(uuid);

    }


    @Override
    public void handleCommand(String command) {

    }

}
