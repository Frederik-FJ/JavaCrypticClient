package gui.apps.service.attack.hacked;

import information.Information;
import items.Device;

import javax.swing.*;
import java.awt.*;

public class HackedDevicePane extends JPanel {

    Device device;

    JLabel name = new JLabel();
    JButton connect = new JButton("connect");

    public HackedDevicePane(Device device){
        this.device = device;
        init();
    }

    private void init(){
        this.setLayout(new BorderLayout());
        this.setHeight(30);

        name.setText(device.getName() + "\t" + "(" + device.getUuid() + ")");
        connect.addActionListener(actionEvent -> Information.Desktop.startTerminal(device));

        this.add(name, BorderLayout.CENTER);
        this.add(connect, BorderLayout.EAST);
    }

    public void setWidth(int width){
        this.setSize(width, this.getHeight());
    }

    public void setHeight(int height){
        this.setSize(this.getWidth(), height);
    }
}
