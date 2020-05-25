package gui.apps;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import connection.Client;
import gui.App;
import gui.util.ShopCategory;
import gui.util.ShoppingCart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Shop extends App {

    Client client;
    JDesktopPane window;

    String outputString;
    JTabbedPane tabbedPane;

    Map<String, Map> outputMap;

    public Shop(JDesktopPane window, Client client){
        this.client = client;
        this.window = window;

        this.width = 800;
        this.height = 600;

        String title = "Shop";

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                if(!componentEvent.paramString().equals("COMPONENT_RESIZED (0,0 800x600)")) ownRepaint();
            }
        });

        this.setTitle(title);
        this.setSize(this.width, this.height);
        this.moveToFront();
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        //this.handleCommand("list");
        init();

    }

    private void init() {

        this.handleCommand("list");
        tabbedPane = new JTabbedPane();

        ShoppingCart shoppingCart = new ShoppingCart(new HashMap<>());

        Map ram = outputMap.get("RAM");
        ShopCategory ramCategory = new ShopCategory(this,
                (Map<String, Map<String, Object>>) ram.get("categories"), (Map) ram.get("items"),shoppingCart, "ram" +
                "");

        Map cpu = outputMap.get("Processor");
        ShopCategory cpuCategory = new ShopCategory(this,
                (Map<String, Map<String, Object>>) cpu.get("categories"), (Map) cpu.get("items"), shoppingCart, "cpu");

        Map cooler = outputMap.get("Cooler");
        ShopCategory coolerCategory = new ShopCategory(this,
                (Map<String, Map<String, Object>>) cooler.get("categories"), (Map) cooler.get("items"), shoppingCart, "processorCooler");

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(outputString);
        textArea.setLocation(0, 0);
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        tabbedPane.addTab("all", textArea);
        tabbedPane.addTab("RAM", ramCategory);
        tabbedPane.addTab("CPU", cpuCategory);
        tabbedPane.addTab("Cooler", coolerCategory);
        tabbedPane.addTab("Shopping cart", shoppingCart);

        this.add(tabbedPane, BorderLayout.CENTER);
    }


    private void getItems() throws InvalidServerResponseException, UnknownMicroserviceException {
        String ret = "";
        Gson gson = new Gson();

        this.outputMap = (Map<String, Map>) client.getShopItems().get("categories");
        System.out.println(gson.toJson(outputMap));
        for(String key : outputMap.keySet()){
            Map<String, Map> category = outputMap.get(key);
            ret += "\n" + key + ":";

            for(String item: (Set<String>) category.get("items").keySet()){
                ret += "\n\t" + item;
            }
            for(String sub: (Set<String>) category.get("categories").keySet()){
                Map<String, Map> subcategory = (Map<String, Map>) category.get("categories").get(sub);
                ret += "\n\t" + sub + ":";
                for(String item: (Set<String>) subcategory.get("items").keySet()){
                    ret += "\n\t\t" + item;
                }
            }
        }
        //System.out.println(ret + "test");
        outputString =  ret;
    }

    @Override
    public void handleCommand(String command) {
        if(command.equals("list") || command.equals("reload")){
            try {
                getItems();
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getFocus(){
        super.getFocus();
        this.setSize(width, height);
    }


    public void ownRepaint(){
        for(Component c: tabbedPane.getComponents()){
            if(c instanceof  ShopCategory){
                ((ShopCategory) c).ownRepaint();
            }
        }
        repaint();
    }
}
