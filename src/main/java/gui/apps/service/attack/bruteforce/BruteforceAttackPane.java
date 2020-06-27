package gui.apps.service.attack.bruteforce;

import Exceptions.InvalidServerResponseException;
import gui.apps.service.attack.AttackPane;
import information.Information;
import items.Device;
import util.service.Bruteforce;
import util.service.Portscan;
import util.service.Service;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.*;

public class BruteforceAttackPane extends AttackPane {

    Bruteforce bruteforce;

    JButton getRandom;

    JTextField deviceUuid;
    JTextField servicePort;

    JButton attack;

    JLabel statusView;
    JButton status;
    JButton stop;

    JComboBox<String> chooseService;

    Timer attackTimer = new Timer();


    Device targetDevice;


    public BruteforceAttackPane(Service s) {
        super();
        service = s;

        bruteforce = (Bruteforce) service;
        init();
    }

    private void init() {
        this.setLayout(null);

        title = new JLabel(service.getName().toUpperCase());
        title.setSize(100, 20);
        title.setLocation(this.relativeWidth(50) - title.getWidth() / 2, 10);
        this.add(title);

        getRandom = new JButton("Get random PC");
        getRandom.setSize(getRandom.getText().length() * 9 + 15, 20);
        getRandom.setLocation(relativeWidth(10), 40);
        getRandom.addActionListener(actionEvent -> getRandomDevice());
        this.add(getRandom);

        deviceUuid = new JTextField("Device UUID");
        deviceUuid.setSize(deviceUuid.getText().length() * 7, 20);
        deviceUuid.setLocation(relativeWidth(10), 70);
        this.add(deviceUuid);

        chooseService = new JComboBox<>(new String[]{"ssh", "telnet"});
        chooseService.setEditable(false);
        chooseService.setSize(100, 20);
        chooseService.setLocation(relativeWidth(10), 90);
        this.add(chooseService);

        servicePort = new JTextField("Service Port");
        servicePort.setSize(servicePort.getText().length() * 7, 20);
        servicePort.setLocation(relativeWidth(10) + chooseService.getWidth() + relativeWidth(10), 90);
        this.add(servicePort);

        attack = new JButton("attack");
        attack.setSize(attack.getText().length() * 9 + 20, 20);
        attack.setLocation(relativeWidth(10), 130);
        attack.addActionListener(actionEvent -> attack());
        this.add(attack);


        // on Resizing
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });

        if (bruteforce.inAttack()) {
            attackLayout();
            Map res;
            try {
                res = bruteforce.status();
            } catch (InvalidServerResponseException e) {
                e.printStackTrace();
                return;
            }
            deviceUuid.setText(res.get("target_device").toString());
            servicePort.setText(res.get("target_service").toString());
            targetDevice = new Device(res.get("target_device").toString());
        }
    }

    private void reload() {
        title.setLocation(relativeWidth(50) - title.getWidth() / 2, 10);
        getRandom.setLocation(relativeWidth(10), 40);
        attack.setLocation(relativeWidth(10), 130);
        chooseService.setLocation(relativeWidth(10), 90);
        servicePort.setLocation(relativeWidth(10) + chooseService.getWidth() + relativeWidth(10), 90);
        deviceUuid.setLocation(relativeWidth(10), 70);

        servicePort.setSize(servicePort.getText().length() * 7 + 10, 20);
        deviceUuid.setSize(deviceUuid.getText().length() * 7 + 10, 20);

        try {
            stop.setLocation(relativeWidth(70), relativeHeight(100) - stop.getHeight());
            status.setLocation(relativeWidth(20), relativeHeight(100) - status.getHeight());
            statusView.setLocation(relativeWidth(50) - statusView.getWidth() / 4, relativeHeight(85));
        } catch (NullPointerException ignore) {
        }


        revalidate();
        repaint();
    }

    private void getRandomDevice() {
        Device d = Device.getRandomDevice();
        assert d != null;
        for (Device device : Device.getHackedDevices()) {
            if (d.getUuid().equals(device.getUuid())) {
                getRandomDevice();
                return;
            }
        }


        deviceUuid.setText(d.getUuid());

        Portscan portscan = service.getDevice().getPortscanService();
        if (portscan == null) {
            JOptionPane.showInternalMessageDialog(this.app, "No Portscan service found");
            return;
        }
        for (Service s : portscan.run(d)) {
            try {
                if (s.getName().equalsIgnoreCase(Objects.requireNonNull(chooseService.getSelectedItem()).toString())) {
                    servicePort.setText(s.getRunningPort() + "");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        reload();
    }

    private void attack() {
        targetDevice = new Device(deviceUuid.getText());
        Portscan p = service.getDevice().getPortscanService();
        List<Service> services = p.run(targetDevice);
        Service targetService = null;
        for (Service s : services) {
            if (chooseService.getSelectedItem().toString().equals(s.getName())) {
                targetService = Service.toServiceType(s, s.getName());
                break;
            }
        }
        int port;
        try {
            port = Integer.parseInt(servicePort.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, servicePort.getText() + " is not a valid Port");
            return;
        }

        if (targetService == null || port != targetService.getRunningPort()) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, "No Service " + chooseService.getSelectedItem().toString() + " on port " + port + " found");
            assert targetService != null;
            System.out.println(targetService.getRunningPort());
            return;
        }
        try {
            bruteforce.attack(targetDevice, targetService);
        } catch (InvalidServerResponseException e) {
            e.printStackTrace();
            JOptionPane.showInternalMessageDialog(app, e.getMessage());
            return;
        }
        attackLayout();
    }

    private void attackLayout() {
        statusView = new JLabel();
        statusView.setSize(40, 20);
        statusView.setLocation(relativeWidth(50), relativeHeight(85));
        this.add(statusView);

        status = new JButton("status");
        status.setSize(70, 15);
        status.setLocation(relativeWidth(20), relativeHeight(100) - status.getHeight());
        status.addActionListener(actionEvent -> getStatus());
        this.add(status);

        stop = new JButton("stop");
        stop.setSize(70, 15);
        stop.setLocation(relativeWidth(70), relativeHeight(100) - stop.getHeight());
        stop.addActionListener(actionEvent -> stop());
        this.add(stop);

        attackTimer = new Timer();
        attackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getStatus();
            }
        }, 0, 5000);

        reload();
    }

    private void getStatus() {
        String temp = null;
        try {
            temp = bruteforce.status().get("progress").toString();
        } catch (InvalidServerResponseException e) {
            attackTimer.cancel();
            e.printStackTrace();
        }

        if (temp == null) return;
        double progress = Double.parseDouble(temp);
        double percent = (progress / 20 - 0.1) * 100;
        statusView.setText("Erfolgswahrscheinlichkeit: " + (double) ((int) (percent * 100)) / 100.0 + "%");
        statusView.setSize(statusView.getText().length() * 10, 20);
        reload();
    }

    private void stop() {
        attackTimer.cancel();
        this.remove(status);
        this.remove(stop);
        this.remove(statusView);
        statusView = null;
        status = null;
        stop = null;
        reload();

        Map result = bruteforce.stop();

        if ((boolean) result.get("access")) {
            Information.Desktop.startTerminal(targetDevice);
        } else {
            JOptionPane.showInternalMessageDialog(Information.Desktop, "Access denied");
        }
    }

}
