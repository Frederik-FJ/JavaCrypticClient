package gui.apps.service.attack;

import gui.App;
import gui.apps.service.attack.hacked.HackedPane;
import util.items.Device;
import util.service.Service;

import javax.swing.*;
import java.awt.*;

public class ServiceAttack extends App {

    JTabbedPane content = new JTabbedPane();

    Device device;

    public ServiceAttack(Device device) {
        super();
        this.device = device;

        this.height = 300;
        this.width = 480;
        this.title = "Attack - " + device.getName();

        this.init();
    }

    @Override
    protected void init() {
        super.init();
        this.setLayout(new BorderLayout());

        for (Service s : Service.getServiceList(device)) {
            if (s.isAttackService()) {
                AttackPane attackPane = new AttackPane(s, this);
                content.addTab(s.getName(), attackPane);
            }
        }
        content.addTab("hacked", new HackedPane());

        this.add(content, BorderLayout.CENTER);
    }
}
