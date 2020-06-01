package gui.apps;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.apps.fileManager.FilePane;
import gui.desktop.DesktopPane;
import information.Information;
import items.Device;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileManager extends App {

    Device device;
    DesktopPane window;
    Path path;

    JLabel pwd;

    public FileManager(DesktopPane window, Device device){

        this.device = device;
        this.window = window;
        path = new Path(Information.client.connectedDevice);

        width = 800;
        height = 600;
        title = "FileManager";
            init();

    }

    @Override
    protected void init() {
        super.init();
        this.setLayout(null);

        pwd = new JLabel("/Hier/ist/der/Pfad");
        pwd.setLocation(10, 10);
        this.add(pwd);

        try {
            loadDirectory(device.getRootDirectory());
        } catch (NoDirectoryException | UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){
                    JPopupMenu menu = popupMenu();
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void loadDirectory(File dir) throws UnknownMicroserviceException, InvalidServerResponseException, NoDirectoryException {
        for(Component c : this.getContentPane().getComponents()){
            if(c instanceof FilePane){
                this.remove(c);
            }
        }

        int y = 10;


        // Verzeichnis mit dem Namen .. um zum vorherigen Ordner zurück zu kommen
        if(dir.getUuid() != null){
            FilePane filePane = new FilePane(File.getParentDir(dir), "..");
            filePane.setWidth(this.getWidth() - 30);
            filePane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON1){
                        try {
                            File parentDir = filePane.getFile();
                            path.setDirectory(parentDir);
                            loadDirectory(parentDir);
                        } catch (UnknownMicroserviceException | NoDirectoryException | InvalidServerResponseException exception) {
                            exception.printStackTrace();
                        }

                    }
                }
            });
            filePane.setLocation(15, y);
            y += 30;
            this.add(filePane);
        }

        for(File f: dir.getFiles()){
            FilePane b = new FilePane(f);
            b.setWidth(this.getWidth() - 30);
            if(f.isDirectory()){
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            try {
                                dirAction(f);
                            } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException unknownMicroserviceException) {
                                unknownMicroserviceException.printStackTrace();
                            }
                        }
                        if(e.getButton() == MouseEvent.BUTTON3){
                            JPopupMenu menu = dirPopupMenu(f);
                            menu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
            }else {
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            fileAction(f);
                        }
                        if(e.getButton() == MouseEvent.BUTTON3){
                            JPopupMenu menu = filePopupMenu(f);
                            menu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
            }
            b.setLocation(15, y);
            y += 30;
            this.add(b);
        }
        Dimension d = this.getSize();
        this.setSize(1, 1);
        this.setSize(d);
        pwd.setText(path.getPwd());
        pwd.repaint();

    }

    private void fileAction(File f){
        window.startTextEditor(f);
    }

    protected void dirAction(File dir) throws NoDirectoryException, InvalidServerResponseException, UnknownMicroserviceException {
        loadDirectory(dir);
        path.setDirectory(dir);
    }

    protected JPopupMenu dirPopupMenu(File dir){
        JPopupMenu popupMenu = popupMenu(dir, "directory");
        return popupMenu;
    }

    protected JPopupMenu filePopupMenu(File file){
        JPopupMenu popupMenu = popupMenu(file, "file");
        return popupMenu;
    }

    protected JPopupMenu popupMenu(File f, String type){
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem rename = new JMenuItem("rename");
        rename.addActionListener(actionEvent -> {
            try {
                String newName = JOptionPane.showInputDialog("New Name", f.getName());
                f.rename(newName);
                loadDirectory(path.getCurrentDirectory());
            } catch (InvalidServerResponseException | UnknownMicroserviceException | NoDirectoryException e) {
                e.printStackTrace();
            }
        });
        popupMenu.add(rename);

        JMenuItem delete = new JMenuItem("delete");
        delete.addActionListener(actionEvent -> {
            int result = JOptionPane.showConfirmDialog(null,
                    "Should this " + type + " really be deleted?", null, JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                try {
                    f.delete();
                    loadDirectory(path.getCurrentDirectory());
                } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException e) {
                    e.printStackTrace();
                }
            }
        });
        popupMenu.add(delete);

        return popupMenu;
    }


    protected JPopupMenu popupMenu(){
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem newFile = new JMenuItem("new File");
        newFile.addActionListener(actionEvent -> {
            String name = JOptionPane.showInputDialog("Name");
            try {
                File f = File.createFile(name, "", path.getCurrentDirectory().getUuid(), path.getDevice());
                loadDirectory(path.getCurrentDirectory());
                window.startTextEditor(f);
            } catch (InvalidServerResponseException | UnknownMicroserviceException | NoDirectoryException e) {
                e.printStackTrace();
            }
        });

        JMenuItem newDir = new JMenuItem("new Directory");
        newDir.addActionListener(actionEvent -> {
            String name = JOptionPane.showInputDialog("Name");
            try {
                File f = File.createDirectory(name, path.getCurrentDirectory().getUuid(), path.getDevice());
                loadDirectory(path.getCurrentDirectory());
            } catch (InvalidServerResponseException | UnknownMicroserviceException | NoDirectoryException e) {
                e.printStackTrace();
            }
        });

        JMenu newObject = new JMenu("new");
        newObject.add(newDir);
        newObject.add(newFile);

        popupMenu.add(newObject);
        return popupMenu;
    }
}
