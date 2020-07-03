package gui.apps.inventory;

import gui.util.Panel;
import util.items.HardwareElement;
import util.items.HardwareInventory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

public class InventoryPanel extends Panel {


    JPanel content;
    JScrollPane scrollPane;

    public InventoryPanel() {
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());


        content = new JPanel();
        scrollPane = new JScrollPane(content);
        content.setLayout(null);

        Map<HardwareElement, Integer> items = HardwareInventory.getInventory();
        int y = 0;
        for (HardwareElement e: items.keySet()) {
            ItemField field = new ItemField(e, items.get(e));
            field.setWidth(content.getWidth());
            field.setLocation(0, y);
            content.add(field);

            y += 30;
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });
        reload();

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void reload() {
        int contentHeight = 0;
        for(Component c : content.getComponents()){
            if(c instanceof ItemField){
                ((Panel) c).setWidth(this.getWidth()-20);
            }
            contentHeight += c.getHeight()+10;
        }

        content.setPreferredSize(new Dimension(this.getWidth()-20, contentHeight));

        this.revalidate();
        this.repaint();
    }
}
