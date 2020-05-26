package gui;

import javax.swing.*;
import java.awt.*;
import gui.desktop.Desktop;

public class ControlCenter extends JPanel{

    Desktop window;

    public ControlCenter(Desktop window){
        this.window = window;
        this.setLayout(new FlowLayout());

        JButton button = new JButton("connect");
        button.addActionListener(actionEvent -> window.connect());
        this.add(button);


    }
}
