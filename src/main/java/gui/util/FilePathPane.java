package gui.util;

import Exceptions.NoDirectoryException;
import gui.apps.fileManager.FileManager;
import information.Information;
import util.file.File;
import util.path.DirectoryPath;
import util.path.Path;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FilePathPane extends Panel {

    File file;
    Path path;

    JButton open;
    JLabel pathName;

    public FilePathPane(File file) {
        this.file = file;
        this.path = file.getPath();
        init();
    }

    public FilePathPane(Path path) {
        this.path = path;
        this.file = path.getCurrentFile();
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        pathName = new JLabel(path.getPwd());
        open = new JButton("open");
        open.addActionListener(actionEvent -> open());

        this.add(open, BorderLayout.EAST);
        this.add(pathName, BorderLayout.CENTER);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu popupMenu = getPopupMenu();
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void open() {
        if (file.isDirectory()) {
            FileManager fileManager;
            try {
                fileManager = new FileManager(new DirectoryPath(path));
                Information.Desktop.startFileManager(fileManager);
            } catch (NoDirectoryException e) {
                e.printStackTrace();
                JOptionPane.showInternalMessageDialog(Information.Desktop, "Error with the connection to the server");
            }
        } else {
            Information.Desktop.startTextEditor(file);
        }
    }

    private JPopupMenu getPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem open = new JMenuItem("open");
        open.addActionListener(actionEvent -> open());
        popupMenu.add(open);

        if (!file.isDirectory()) {
            JMenuItem openDir = new JMenuItem("open parent dir");
            FileManager fileManager = null;
            try {
                fileManager = new FileManager(new DirectoryPath(file.getParentDir().getPath()));
            } catch (NoDirectoryException e) {
                e.printStackTrace();
            }
            FileManager finalFileManager = fileManager;
            openDir.addActionListener(actionEvent -> Information.Desktop.startFileManager(finalFileManager));
            popupMenu.add(openDir);
        }


        return popupMenu;
    }
}
