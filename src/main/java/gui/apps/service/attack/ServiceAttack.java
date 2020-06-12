package gui.apps.service.attack;

import gui.App;
import items.Device;
import util.service.Service;

import javax.swing.*;
import java.awt.*;

public class ServiceAttack extends App {

    JTabbedPane content = new JTabbedPane();

    Device device;

    public ServiceAttack(Device device){
        super();
        this.device = device;

        this.height = 300;
        this.width = 400;
        this.title = "Attack - " + device.getName();

        this.init();
    }

    @Override
    protected void init() {
        super.init();
        this.setLayout(new BorderLayout());

        for(Service s: Service.getServiceList(device)){
            if(s.isAttackService()){
                AttackPane attackPane = new AttackPane(s, this);
                content.addTab(s.getName(), attackPane);
            }
        }
        this.add(content, BorderLayout.CENTER);
    }
}
