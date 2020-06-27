package gui.apps;

import gui.App;

import javax.swing.*;
import java.awt.*;

public class Inventory extends App {

    JDesktopPane window;

    public Inventory(JDesktopPane window) {
        super();
        this.window = window;

        this.width = 300;
        this.height = 300;

        init();
    }

    protected void init() {
        setBackground(Color.WHITE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        //List<HardwareElement> items = client.getInventory();

    }


    @Override
    public void handleCommand(String command) {

    }

    @Override
    public void getFocus() {
        super.getFocus();
    }
}
