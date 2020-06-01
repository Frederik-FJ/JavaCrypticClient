package gui.apps;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.desktop.DesktopPane;
import gui.util.Path;
import information.Information;
import items.Device;
import items.File;

import javax.swing.*;
import java.awt.*;

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
        this.setLayout(new FlowLayout());

        pwd = new JLabel("/Hier/ist/der/Pfad");
        this.add(pwd);

        try {
            loadDirectory(device.getRootDirectory());
        } catch (NoDirectoryException | UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }
    }

    private void loadDirectory(File dir) throws UnknownMicroserviceException, InvalidServerResponseException, NoDirectoryException {
        for(Component c : this.getContentPane().getComponents()){
            if(c instanceof JButton){
                this.remove(c);
            }
        }
        for(File f: dir.getFiles()){
            JButton b = new JButton(f.getName());
            if(f.isDirectory()){
                b.addActionListener((actionEvent) -> {
                    try {
                        path.setDirectory(f);
                        loadDirectory(f);
                    } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException e) { e.printStackTrace(); }
                });
            }else {
                b.addActionListener((actionEvent -> {
                    window.startTextEditor(f);
                }));
            }
            this.add(b);
        }
        Dimension d = this.getSize();
        this.setSize(1, 1);
        this.setSize(d);
        pwd.setText(path.getPwd());
        pwd.repaint();
        if(pwd.getText().length() > 1){
            JButton b = new JButton("back");
            b.addActionListener(actionEvent -> {
                try {
                    File parentDir = File.getParentDir(dir);
                    path.setDirectory(parentDir);
                    loadDirectory(parentDir);
                } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException e) {
                    e.printStackTrace();
                }
            });
            this.add(b);
        }
    }


}
