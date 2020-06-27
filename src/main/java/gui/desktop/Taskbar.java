package gui.desktop;

import javax.swing.*;
import java.awt.*;


public class Taskbar extends JToolBar {

    DesktopPane desktop;

    public Taskbar(DesktopPane desktop) {
        this.desktop = desktop;
        this.setOrientation(JToolBar.VERTICAL);
    }

    public void addApp(Component component) {
        this.add(component);
    }

    public void removeApp(Component component) {
        this.remove(component);
    }


}
