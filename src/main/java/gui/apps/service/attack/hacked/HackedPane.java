package gui.apps.service.attack.hacked;

import gui.apps.service.attack.AttackPane;
import items.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HackedPane extends JScrollPane {

    public HackedPane(){
        init();
    }

    private void init(){
        this.setLayout(null);

        int y = 10;
        for(Device d: Device.getHackedDevices()){
            HackedDevicePane devicePane = new HackedDevicePane(d);
            devicePane.setWidth(this.getWidth() - 35);
            devicePane.setLocation(30, y);
            y += 35;
            this.add(devicePane);
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });

        reload();
    }

    private void reload(){
        for(Component c : this.getComponents()){
            if(c instanceof HackedDevicePane){
                HackedDevicePane pane = (HackedDevicePane) c;
                pane.setWidth(this.getWidth() - 35);
            }
        }

        this.revalidate();
        this.repaint();
    }
}
