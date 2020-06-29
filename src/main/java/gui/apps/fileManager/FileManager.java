package gui.apps.fileManager;

import Exceptions.NoDirectoryException;
import gui.App;
import items.Device;
import util.file.File;
import util.path.DirectoryPath;
import util.path.Path;

import java.awt.event.*;

public class FileManager extends App {

    Device device;
    DirectoryPath directoryPath;

    FileManagerMainPane mainPane;
    PathPane pathPane;


    public FileManager(Device device) {
        this.device = device;
        directoryPath = new DirectoryPath(device);

        init();
    }

    public FileManager(DirectoryPath path) {
        this.device = path.getDevice();
        directoryPath = path;
        init();

    }

    public void setPath(Path p) throws NoDirectoryException {
        setPath(p.getCurrentFile());
    }

    public void setPath(File f) throws NoDirectoryException {
        mainPane.setPath(f);
    }

    @Override
    protected void init() {

        width = 800;
        height = 600;
        title = "FileManager";

        super.init();
        this.setLayout(null);

        mainPane = new FileManagerMainPane(directoryPath);
        mainPane.setLocation(0 ,50);
        mainPane.setSize(this.getWidth(), this.getHeight()-50);
        this.add(mainPane);


        pathPane = new PathPane(this);
        pathPane.setLocation(0, 0);
        pathPane.setSize(this.getWidth(), 50);
        this.add(pathPane);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });
    }

    private void reload(){
        mainPane.setSize(this.getWidth(), this.getHeight()-50);
        pathPane.setSize(this.getWidth(), 50);

        this.revalidate();
        this.repaint();
    }

}
