package gui.desktop;

import javax.swing.*;
import java.awt.*;


public class Taskbar extends JToolBar {

    DesktopPane desktop;

    public Taskbar(DesktopPane desktop){
        this.desktop = desktop;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton options = new JButton("Options");
        options.addActionListener((actionEvent) -> desktop.startSettings());
        //this.add(options);
    }
}
