package gui.controlCenter;

import Exceptions.DeviceNotOnlineException;
import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import gui.desktop.Desktop;
import information.Information;
import items.Device;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputerCategory extends JPanel {

    Desktop desktop;

    JPanel deviceList;
    JPanel infoField;
    Gson gson = new Gson();

    JButton powerButton;

    Map<String, Device> devices = new HashMap<>();
    List<String> deviceNames = new ArrayList<>();

    public ComputerCategory(Desktop desktop){
        this.desktop = desktop;
        this.setLayout(new BorderLayout());



        try {
            for(Map map :(List<Map>) Information.client.getDevices().get("devices")){
                devices.put((String) map.get("name"), new Device((String) map.get("uuid")));
                deviceNames.add((String) map.get("name"));
            }
        } catch (InvalidServerResponseException | UnknownMicroserviceException | ClassCastException e) {
            this.add(new JLabel("Invalid Server response"));
        }

        deviceList = new JPanel();
        //deviceList.setBackground(Color.BLACK);
        deviceList.setLayout(new BoxLayout(deviceList, BoxLayout.Y_AXIS));
        for(String name : devices.keySet()){
            JButton b = new JButton(name);
            System.out.println(name);
            b.addActionListener((actionEvent) -> {
                try {
                    showInfos(devices.get(name));
                } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                    e.printStackTrace();
                }
            });
            deviceList.add(b);
            deviceList.add(b);
        }

        try {
            showInfos(devices.get(deviceNames.get(0)));
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }

        this.add(infoField, BorderLayout.CENTER);
        this.add(deviceList, BorderLayout.WEST);
    }

    private void showInfos(Device device) throws InvalidServerResponseException, UnknownMicroserviceException {
        infoField = new JPanel();
        infoField.setLayout(new FlowLayout());

        JButton connect = new JButton("connect");
        connect.addActionListener(actionEvent ->  connect(device));

        boolean state = device.isOnline();

        JLabel stateField = new JLabel(state?"online":"offline");

        powerButton = new JButton(state?"shutdown":"boot");
        powerButton.addActionListener(actionEvent -> {
            try {
                if (state) {
                    device.shutdown();
                } else {
                    device.boot();
                }
                showInfos(device);
                infoField.repaint();
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
        });

        infoField.add(connect);
        infoField.add(stateField);
        infoField.add(powerButton);
    }

    private void connect(Device device){
        try {
            Information.client.connect(device);
            desktop.connect();
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        } catch (DeviceNotOnlineException e){
            JOptionPane.showMessageDialog(null, "Device not online");
        }
    }
}
