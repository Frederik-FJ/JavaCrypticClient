package gui.apps.service.manager;

import util.service.Service;

import javax.swing.*;
import java.awt.*;

public class ServicePane extends JPanel {

    Service service;

    JLabel name;
    JLabel status;

    public ServicePane(Service service){
        this.service = service;

        init();
    }

    private void init(){
        this.setLayout(new BorderLayout());
        this.setHeight(30);

        name = new JLabel(service.getName());
        status = new JLabel("on");

        this.add(name, BorderLayout.CENTER);
        this.add(status, BorderLayout.EAST);
    }

    public void setWidth(int width){
        this.setSize(width, this.getHeight());
    }

    public void setHeight(int height){
        this.setSize(this.getWidth(), height);
    }
}
