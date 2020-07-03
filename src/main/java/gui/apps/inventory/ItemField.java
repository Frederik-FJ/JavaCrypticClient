package gui.apps.inventory;

import gui.util.Panel;
import util.items.HardwareElement;

import javax.swing.*;
import java.awt.*;

public class ItemField extends Panel {

    JLabel name;
    JLabel numberField;

    HardwareElement item;
    int number;

    public ItemField(HardwareElement item, int number){
        this.item = item;
        this.number = number;

        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        setHeight(20);

        name = new JLabel(item.getName());
        numberField = new JLabel(number+"   ");

        this.add(name, BorderLayout.WEST);
        this.add(numberField, BorderLayout.EAST);
    }
}
