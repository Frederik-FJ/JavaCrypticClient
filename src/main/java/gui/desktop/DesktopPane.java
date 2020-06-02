package gui.desktop;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.apps.*;
import information.Information;
import items.Device;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DesktopPane extends JDesktopPane{

    Desktop window;
    Taskbar taskbar;

    JButton terminal;
    JButton settings;
    JButton shop;
    JButton controlCenter;
    JButton fileManager;

    Image settingsIconImage;
    Image terminalIconImage;
    Image shopImage;
    Image controlCenterImage;
    Image fileManagerImage;
    Image textEditorImage;

    ImageIcon settingsIcon;
    ImageIcon terminalIcon;
    ImageIcon shopIcon;
    ImageIcon controlCenterIcon;
    ImageIcon fileManagerIcon;
    ImageIcon textEditorIcon;

    Settings sets;


    public DesktopPane(Desktop window, Taskbar taskbar){
        this.window = window;
        this.taskbar = taskbar;


        // prepare Icons for for the Apps
        try {
            terminalIconImage = ImageIO.read(new File(Information.path + "apps/terminal/icon.png"));
            settingsIconImage = ImageIO.read(new File(Information.path + "apps/settings/icon.png"));
            shopImage = ImageIO.read(new File(Information.path + "apps/shop/icon.png"));
            controlCenterImage = ImageIO.read(new File(Information.path + "apps/control_center/icon.png"));
            fileManagerImage = ImageIO.read(new File(Information.path + "apps/file_manager/icon.png"));
            textEditorImage = ImageIO.read(new File(Information.path + "apps/text_editor/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image newPic = terminalIconImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        terminalIcon = new ImageIcon(newPic);

        newPic = settingsIconImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        settingsIcon = new ImageIcon(newPic);

        newPic = shopImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        shopIcon = new ImageIcon(newPic);

        newPic = controlCenterImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        controlCenterIcon = new ImageIcon(newPic);

        newPic = fileManagerImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        fileManagerIcon = new ImageIcon(newPic);

        newPic = textEditorImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        textEditorIcon = new ImageIcon(newPic);




        this.init();

    }

    private void init(){
        initComputer();

    }

    public void initComputer(){

        JPopupMenu menu = new JPopupMenu();

        JMenuItem shutdown = new JMenuItem("shutdown");
        JMenuItem disconnect = new JMenuItem("disconnect");

        shutdown.addActionListener(actionEvent -> {
            try {
                Information.client.connectedDevice.shutdown();
                window.disconnect();
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
        });
        disconnect.addActionListener(actionEvent -> {
            window.disconnect();
        });

        menu.add(shutdown);
        menu.add(disconnect);

        JButton deviceOptions = new JButton(settingsIcon);

        deviceOptions.addActionListener(actionEvent -> {
            menu.show(deviceOptions, deviceOptions.getX(), deviceOptions.getY());
        });

        taskbar.add(deviceOptions);

        this.setOpaque(false);


        //Add icons to the Terminal
        terminal = new JButton();
        terminal.setIcon(terminalIcon);
        terminal.setSize(50, 50);
        terminal.setLocation(50, 50);
        terminal.addActionListener(actionEvent -> startTerminal());
        this.add(terminal);

        settings = new JButton();
        settings.setIcon(settingsIcon);
        settings.setSize(50, 50);
        settings.setLocation(50, 150);
        settings.addActionListener(actionEvent -> startSettings());
        this.add(settings);

        shop = new JButton();
        shop.setIcon(shopIcon);
        shop.setSize(50, 50);
        shop.setLocation(50, 250);
        shop.addActionListener(actionEvent -> startShop());
        this.add(shop);

        controlCenter = new JButton();
        controlCenter.setIcon(controlCenterIcon);
        controlCenter.setSize(50, 50);
        controlCenter.setLocation(50, 350);
        controlCenter.addActionListener(actionEvent -> window.disconnect());
        this.add(controlCenter);

        fileManager = new JButton();
        fileManager.setIcon(fileManagerIcon);
        fileManager.setSize(50, 50);
        fileManager.setLocation(50, 450);
        fileManager.addActionListener(actionEvent -> startFileManager(Information.client.connectedDevice));
        this.add(fileManager);
    }


    public void startTerminal(){
        // open terminal
        Terminal ter = new Terminal(this);
        startApp(ter, terminalIcon);
    }


    public void startSettings(){
        //if settings is already opened it won't open again
        for(JInternalFrame f :this.getAllFrames()){
            if(f instanceof Settings) return;
        }
        //open settings
        sets = new Settings(this);
        startApp(sets, settingsIcon);
    }

    public void startShop(){
        // open shop
        Shop shop = new Shop(this);
        startApp(shop, shopIcon);
    }

    public void startFileManager(Device device){
        FileManager fileManager = new FileManager(this, device);
        startApp(fileManager, fileManagerIcon);
    }

    public void startTextEditor(util.File file){
        TextEditor textEditor = new TextEditor(file, this);
        startApp(textEditor, textEditorIcon);
    }

    public void startApp(App app, ImageIcon icon){
        ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        app.setFrameIcon(scaledIcon);
        this.add(app);
        app.getFocus();

        JButton b = new JButton(icon);
        b.addActionListener(actionEvent -> app.getFocus());
        taskbar.addApp(b);

        app.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                taskbar.removeApp(b);
                taskbar.repaint();
            }
        });
    }


}
