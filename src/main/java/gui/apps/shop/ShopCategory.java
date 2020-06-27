package gui.apps.shop;

import com.google.gson.Gson;
import gui.App;
import items.HardwareElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopCategory extends JPanel {

    App parentApp;
    Map<String, Map<String, Object>> subcategories;
    Map items;
    String type;

    JScrollPane scrollPane;
    JPanel panel = new JPanel();
    JPanel subcategoriesPanel;
    ShoppingCart shoppingCart;

    public ShopCategory(App parentApp, Map<String, Map<String, Object>> subcategories, Map items, ShoppingCart shoppingCart, String type) {
        this.parentApp = parentApp;
        this.subcategories = subcategories;
        this.items = items;
        this.shoppingCart = shoppingCart;

        this.type = type;

        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        subcategoriesPanel = new JPanel();
        List<JButton> buttons = new ArrayList<>();

        if (items.keySet().size() > 0) {
            System.out.println();
            JButton b = new JButton();
            b.setText("Generell");
            b.setSize(200, 100);
            b.addActionListener(actionEvent -> showItems());
            buttons.add(b);
        }
        for (String s : this.subcategories.keySet()) {
            JButton b = new JButton();
            b.setText(s);
            b.setSize(200, 100);
            b.addActionListener(actionEvent -> showItemsFromSubcategories(s));
            buttons.add(b);
        }

        subcategoriesPanel.setLayout(new BoxLayout(subcategoriesPanel, BoxLayout.Y_AXIS));
        for (JButton b : buttons) {
            subcategoriesPanel.add(b);
        }
        add(subcategoriesPanel, BorderLayout.WEST);

        panel.setSize(this.getWidth() - 100, this.getHeight());
        panel.setLayout(null);
        scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

    }

    private void showItemsFromSubcategories(String subcategory) {
        Gson gson = new Gson();
        panel.removeAll();
        String ret = "";
        int counter = 0;
        Map sub = subcategories.get(subcategory);
        for (String s : (Set<String>) sub.keySet()) {
            if (!s.equals("items")) continue;
            Map subcat = (Map) sub.get(s);
            System.out.println(subcategory + gson.toJson(subcat));
            for (String item : (Set<String>) subcat.keySet()) {
                JButton b = new JButton();
                b.setText(item + "--------------> Price:" + ((Map) subcat.get(item)).get("price"));
                b.setSize(panel.getWidth(), 100);
                HardwareElement element = new HardwareElement(type, item);
                b.addActionListener(actionEvent -> shoppingCart.addElement(element, shoppingCart.elementExists(element) ? shoppingCart.getNumber(element) + 1 : 1));
                shoppingCart.repaint();
                b.setLocation(0, 100 * counter);
                counter++;
                panel.add(b);
                panel.repaint();
                scrollPane.repaint();
            }
        }

        //add(panel, BorderLayout.CENTER);
    }

    private void showItems() {
        int counter = 0;
        for (String item : (Set<String>) items.keySet()) {
            JButton b = new JButton();
            b.setText(item + "--------------> Price:" + ((Map) items.get(item)).get("price"));
            b.setSize(panel.getWidth(), 100);
            b.setLocation(0, 100 * counter);
            counter++;
            panel.add(b);
            HardwareElement element = new HardwareElement(type, item);
            b.addActionListener(actionEvent -> shoppingCart.addElement(element, shoppingCart.elementExists(element) ? shoppingCart.getNumber(element) + 1 : 1));
            panel.repaint();
            scrollPane.repaint();
        }
    }

    /**
     * resizes the size of the Buttons
     */
    public void ownRepaint() {
        panel.setSize(this.getWidth(), this.getHeight());
        for (Component c : panel.getComponents()) {
            c.setSize(panel.getWidth(), 100);
        }
        repaint();
    }
}
