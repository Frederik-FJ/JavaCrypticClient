package gui.apps.service.attack.portscan;

import gui.apps.service.attack.AttackPane;
import gui.apps.service.manager.ServicePane;
import util.items.Device;
import util.service.Portscan;
import util.service.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PortscanAttackPane extends AttackPane {

    JButton activate;
    JTextField deviceUuid;

    Portscan portscan;

    JScrollPane ports = new JScrollPane();

    public PortscanAttackPane(Service s) {
        super();
        this.service = s;
        portscan = (Portscan) service;
        init();
    }

    private void init() {
        this.setLayout(null);

        title = new JLabel(service.getName().toUpperCase());
        title.setSize(100, 20);
        title.setLocation(relativeWidth(50) - title.getWidth() / 2, 10);
        this.add(title);

        deviceUuid = new JTextField("Device UUID");
        deviceUuid.setSize(deviceUuid.getText().length() * 9, 20);
        deviceUuid.setLocation(relativeWidth(10), 40);
        this.add(deviceUuid);

        activate = new JButton("run");
        activate.setSize(activate.getText().length() * 10 + 20, 20);
        activate.setLocation(deviceUuid.getX() + deviceUuid.getWidth() + relativeWidth(10), 40);
        activate.addActionListener(actionEvent -> run());
        this.add(activate);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });

        ports.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for (Component c : ports.getComponents()) {
                    if (c instanceof ServicePane) {
                        ((ServicePane) c).setWidth(PortscanAttackPane.this.getWidth() - 15);
                    }
                    if (c instanceof JLabel) {
                        c.setLocation(relativeWidth(50) - c.getWidth() / 2, c.getY());
                    }
                    ports.revalidate();
                    ports.repaint();
                }
            }
        });

        reload();
    }

    private void reload() {

        title.setLocation(relativeWidth(50) - title.getWidth() / 2, 10);
        deviceUuid.setSize(deviceUuid.getText().length() * 9, 20);
        deviceUuid.setLocation(relativeWidth(10), 40);
        activate.setLocation(deviceUuid.getX() + deviceUuid.getWidth() + relativeWidth(10), 40);

        ports.setSize(this.getWidth(), this.getHeight() - 80);
        ports.setLocation(0, 80);

        this.revalidate();
        this.repaint();
    }

    private void run() {
        for (Component c : ports.getComponents()) {
            if (c instanceof JLabel || c instanceof ServicePane) {
                ports.remove(c);
            }
        }
        ports.setLayout(null);

        JLabel deviceLabel = new JLabel(new Device(deviceUuid.getText()).getName());
        deviceLabel.setSize(deviceLabel.getText().length() * 9, 20);
        deviceLabel.setLocation(relativeWidth(50) - deviceLabel.getWidth() / 2, 0);
        ports.add(deviceLabel);

        int y = 30;
        for (Service s : portscan.run(new Device(deviceUuid.getText()))) {
            ServicePane pane = new ServicePane(s);
            pane.add(new JLabel(s.getRunningPort() + "-->"), BorderLayout.WEST);
            pane.setSize(ports.getWidth() - 15, 30);
            pane.setLocation(10, y);
            y += 32;
            ports.add(pane);
        }

        this.add(ports);

        reload();
    }
}
