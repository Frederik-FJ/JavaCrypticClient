package gui.apps.fileManager;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.desktop.DesktopPane;
import information.Information;
import items.Device;
import util.*;
import util.file.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

        // Resize the FilePane when the FileManager resizes
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for(Component c: FileManager.this.getContentPane().getComponents()){
                    if(c instanceof FilePane){
                        FilePane fp = (FilePane) c;
                        fp.setWidth(FileManager.this.getWidth() - 30);
                    }
                }
            }
        });

        // Adding a Popup-Menu on Right-Click
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3){
                    JPopupMenu menu = popupMenu();
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // Reloading when F5 is released
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_F5){
                    try {
                        loadDirectory(path.getCurrentDirectory());
                    } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException unknownMicroserviceException) {
                        unknownMicroserviceException.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Loads the files and dirs of a dir into the file-Manager
     * @param dir the dir from which the dirs/files should be loaded
     */
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
                            b.setBackground(new Color(0x777777));
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

    /**
     * Action from a file if the filePane is left-clicked
     * @param f the file
     */
    protected void fileAction(File f){
        try{
            // execute the file if the ending of the file is .run
            if(f.getName().endsWith(".run")){
                f.toExecutionFile().execute();
                return;
            }
        }catch (InvalidServerResponseException | UnknownMicroserviceException e){
            e.printStackTrace();
        }

        // open the TextEditor
        window.startTextEditor(f);
    }

    /**
     * Action from a dire of the filePane is left-clicked
     * @param dir the directory
     */
    protected void dirAction(File dir) throws NoDirectoryException, InvalidServerResponseException, UnknownMicroserviceException {
        loadDirectory(dir);
        path.setDirectory(dir);
    }

    /**
     * The Popup-Menu for right-clicking a dir
     * @param dir The dir
     * @return returns the Popup-Menu
     */
    protected JPopupMenu dirPopupMenu(File dir){
        return popupMenu(dir, "directory");
    }

    /**
     * The Popup-Menu for right-clicking a file
     * @param file The file
     * @return returns the Popup-Menu
     */
    protected JPopupMenu filePopupMenu(File file){
       JPopupMenu options = popupMenu(file, "file");

       // adding the option to open the file in the Text-Editor
       JMenuItem open = new JMenuItem("open");
       open.addActionListener(actionEvent -> window.startTextEditor(file));
       options.add(open, 0);


       // Adding option to execute if the file ends with .run
        try {
            if(file.getName().endsWith(".run")){
                JMenuItem execute = new JMenuItem("execute");
                execute.addActionListener(actionEvent -> {
                        file.toExecutionFile().execute();
                });
                options.add(execute, 0);
            }
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }

        return options;
    }

    protected JPopupMenu popupMenu(File f, String type){
        JPopupMenu popupMenu = new JPopupMenu();

        popupMenu.addSeparator();

        JMenuItem rename = new JMenuItem("rename");
        rename.addActionListener(actionEvent -> {
            try {
                String newName = (String) JOptionPane.showInternalInputDialog(window, "New Name", null,
                        JOptionPane.PLAIN_MESSAGE, null, null, f.getName());
                loadDirectory(path.getCurrentDirectory());
            } catch (InvalidServerResponseException | UnknownMicroserviceException | NoDirectoryException e) {
                e.printStackTrace();
            }
        });
        popupMenu.add(rename);

        JMenuItem delete = new JMenuItem("delete");
        delete.addActionListener(actionEvent -> {
            int result = JOptionPane.showInternalConfirmDialog(window,
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
