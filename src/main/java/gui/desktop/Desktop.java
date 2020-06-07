package gui.desktop;

import gui.ControlCenter;

import javax.swing.*;
import java.awt.*;

public class Desktop extends JPanel {

    DesktopPane pane;
    Taskbar taskbar;

    JFrame window;

    public Desktop(JFrame window){
        this.window = window;
        this.setLayout(new BorderLayout());

        disconnect();

    }

    private void initComputer(){
        taskbar = new Taskbar(pane);
        pane = new DesktopPane(this, taskbar);

        this.add(pane, BorderLayout.CENTER);
        this.add(taskbar, BorderLayout.WEST);
    }

    public void connect(){
        this.removeAll();

        repaint();

        initComputer();


        this.revalidate();
    }

    public void disconnect(){
        this.removeAll();

        ControlCenter controlCenter = new ControlCenter(this);
        this.add(controlCenter, BorderLayout.CENTER);

        this.revalidate();
    }




}
