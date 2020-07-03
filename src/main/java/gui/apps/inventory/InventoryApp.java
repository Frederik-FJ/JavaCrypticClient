package gui.apps.inventory;

import gui.App;

import javax.swing.*;
import java.awt.*;

public class InventoryApp extends App {

    JTabbedPane tabbedPane = new JTabbedPane();

    public InventoryApp() {
        init();
    }

    @Override
    protected void init() {
        title = "Inventory";
        width = 400;
        height = 300;

        super.init();
        this.setLayout(new BorderLayout());

        InventoryPanel inventory = new InventoryPanel();
        tabbedPane.addTab("inventory", inventory);

        TradePanel tradePanel = new TradePanel();
        tabbedPane.addTab("trade", tradePanel);

        this.add(tabbedPane, BorderLayout.CENTER);
    }
}
