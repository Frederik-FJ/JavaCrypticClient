package gui.desktop;

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

    Icon settingsIcon;
    Icon terminalIcon;

    Settings sets;


    public DesktopPane(Desktop window, Taskbar taskbar){
        this.window = window;
        this.taskbar = taskbar;


        // prepare Icons for for the Apps
        try {
            terminalIconImage = ImageIO.read(new File(Information.path + "apps/terminal/icon.png"));
            settingsIconImage = ImageIO.read(new File(Information.path + "apps/settings/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image newPic = terminalIconImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        terminalIcon = new ImageIcon(newPic);

        newPic = settingsIconImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        settingsIcon = new ImageIcon(newPic);



        this.init();

    }

    private void init(){
        initComputer();

    }

    public void initComputer(){


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
        shop.setText("Shop");
        shop.setSize(50, 50);
        shop.setLocation(50, 250);
        shop.addActionListener(actionEvent -> startShop());
        this.add(shop);

        controlCenter = new JButton();
        controlCenter.setText("Shop");
        controlCenter.setSize(50, 50);
        controlCenter.setLocation(50, 350);
        controlCenter.addActionListener(actionEvent -> window.disconnect());
        this.add(controlCenter);

        fileManager = new JButton();
        fileManager.setText("fileManager");
        fileManager.setSize(50, 50);
        fileManager.setLocation(50, 450);
        fileManager.addActionListener(actionEvent -> startFileManager(Information.client.connectedDevice));
        this.add(fileManager);
    }


    public void startTerminal(){
        // open terminal
        Terminal ter = new Terminal(this);
        this.add(ter);
        ter.getFocus();

        // Add an icon to the taskbar
        JButton b = new JButton(terminalIcon);
        b.addActionListener(actionEvent -> ter.getFocus());
        taskbar.add(b);

        // If the Terminal is closing, the button disappears from the Taskbar
        ter.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
                taskbar.remove(b);
                taskbar.repaint();
            }
        });
    }


    public void startSettings(){
        //if settings is already opened it won't open again
        for(JInternalFrame f :this.getAllFrames()){
            if(f instanceof Settings) return;
        }
        //open settings
        sets = new Settings(this);
        this.add(sets);
        sets.getFocus();

        // Add an icon to the taskbar
        JButton b = new JButton(settingsIcon);
        b.addActionListener(actionEvent -> sets.getFocus());
        taskbar.add(b);

        // If the Terminal is closing, the button disappears from the Taskbar
        sets.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
                taskbar.remove(b);
                taskbar.repaint();
            }
        });
    }

    public void startShop(){
        // open shop
        Shop shop = new Shop(this);
        this.add(shop);
        shop.getFocus();

        //Add a icon to the taskbar
        JButton b = new JButton("Shop");
        b.addActionListener(actionEvent -> shop.getFocus());
        taskbar.add(b);

        // If the Terminal is closing, the button disappears from the Taskbar
        shop.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
                taskbar.remove(b);
                taskbar.repaint();
            }
        });
    }

    public void startFileManager(Device device){
        FileManager fileManager = new FileManager(this, device);
        this.add(fileManager);
        fileManager.getFocus();

        JButton b = new JButton("FileManager");
        b.addActionListener(actionEvent -> fileManager.getFocus());
        taskbar.add(b);

        fileManager.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                taskbar.remove(b);
                taskbar.repaint();
            }
        });
    }

    public void startTextEditor(items.File file){
        TextEditor textEditor = new TextEditor(file);
        this.add(textEditor);
        textEditor.getFocus();

        JButton b = new JButton("TextEditor");
        b.addActionListener(actionEvent -> textEditor.getFocus());
        taskbar.add(b);

        textEditor.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                taskbar.remove(b);
                taskbar.repaint();
            }
        });
    }


}
