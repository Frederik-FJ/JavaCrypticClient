package gui.apps.service.attack.hacked;

import gui.util.Panel;
import items.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HackedPane extends JScrollPane {

    Panel panel = new Panel();

    public HackedPane() {
        init();
    }

    private void init() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this.getVerticalScrollBar().setUnitIncrement(16);

        int y = 10;
        for (Device d : Device.getHackedDevices()) {
            HackedDevicePane devicePane = new HackedDevicePane(d);
            devicePane.setWidth(this.getWidth() - 30 - 20);
            devicePane.setLocation(30, y);
            y += 35;
            panel.add(devicePane);
        }

        this.getViewport().add(panel);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });

        reload();
    }

    private void reload() {
        for (Component c : panel.getComponents()) {
            if (c instanceof HackedDevicePane) {
                HackedDevicePane pane = (HackedDevicePane) c;
                pane.setWidth(this.getWidth() - 35);
            }
        }

        this.revalidate();
        this.repaint();
    }
}
