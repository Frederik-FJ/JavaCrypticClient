package gui.apps.shop;

import util.items.HardwareElement;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShoppingCart extends JPanel {

    Map<HardwareElement, Integer> elements;


    public ShoppingCart(Map<HardwareElement, Integer> elements) {
        this.elements = elements;
        init();
    }

    private void init() {
        this.removeAll();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        List<ShoppingCartItem> items = new ArrayList<>();

        try {
            for (HardwareElement e : elements.keySet()) {
                ShoppingCartItem item = new ShoppingCartItem(e, elements.get(e));
                items.add(item);
                this.add(item);
            }
            JButton submit = new JButton("store");
            submit.addActionListener(actionEvent -> store(items));

            add(submit);
        } catch (NullPointerException ignored) {
        }
    }


    private void store(List<ShoppingCartItem> items) {
        for (ShoppingCartItem i : items) {
            if (i.getInputNumber() > 0) {
                this.addElement(i.getElement(), i.getInputNumber());
            } else {
                this.removeElement(i.getElement());
            }

        }
    }


    public void addElement(HardwareElement element, int number) {
        elements.put(element, number);
    }

    public void removeElement(HardwareElement element) {
        elements.remove(element);
    }

    public int getNumber(HardwareElement element) {
        return elements.get(element);
    }

    public Set<HardwareElement> getElements() {
        return elements.keySet();
    }

    public Map<HardwareElement, Integer> getShoppingCart() {
        return elements;
    }

    public boolean elementExists(HardwareElement element) {
        return elements.containsKey(element);
    }

    @Deprecated
    public Map buy() {

        return null;
    }

    @Override
    public void repaint() {
        init();
        super.repaint();
    }
}
