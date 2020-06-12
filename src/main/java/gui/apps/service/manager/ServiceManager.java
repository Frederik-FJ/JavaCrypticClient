package gui.apps.service.manager;

import gui.App;
import gui.desktop.DesktopPane;
import information.Information;
import items.Device;
import util.service.Service;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ServiceManager extends App{

    Device device;
    DesktopPane desktopPane;

    public ServiceManager(Device device, DesktopPane desktopPane){
        super();

        this.desktopPane = desktopPane;
        this.device = device;

        this.height = 300;
        this.width = 400;
        this.title = "Service Manager - " + device.getName();

        this.init();
    }

    protected void init(){
        super.init();
        this.setLayout(null);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for(Component c: ServiceManager.this.getContentPane().getComponents()){
                    if(c instanceof ServicePane){
                        ServicePane servicePane = (ServicePane) c;
                        servicePane.setWidth(ServiceManager.this.getWidth() - 30);
                    }
                }
            }
        });

        loadServices();
    }

    private void loadServices(){
        int y = 10;
        for(Service service : Service.getServiceList(device)){
            ServicePane servicePane = new ServicePane(service);
            servicePane.setWidth(this.getWidth() - 30);
            servicePane.setLocation(15, y);
            y += 30;
            this.add(servicePane);
        }
    }
}
