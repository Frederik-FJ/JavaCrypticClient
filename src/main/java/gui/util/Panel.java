package gui.util;

import javax.swing.*;

public class Panel extends JPanel {

    public Panel() {
    }

    public void setWidth(int width) {
        this.setSize(width, this.getHeight());
    }

    public void setHeight(int height) {
        this.setSize(this.getWidth(), height);
    }

    public int relativeHeight(int percent) {
        return (int) (this.getHeight() * ((double) percent / 100));
    }

    public int relativeWidth(int percent) {
        return (int) (this.getWidth() * ((double) percent / 100));
    }

    public void reload() {
        this.revalidate();
        this.repaint();
    }
}
