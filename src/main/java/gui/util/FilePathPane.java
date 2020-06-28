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

import java.util.ArrayList;
import java.util.List;

public class FilePathPane extends Panel {

    File file;
    Path path;

    JButton open;
    JLabel pathName;

    boolean openParentDir = false;

    boolean fileChanging = false;

    List<FileChangeListener> fileChangeListener = new ArrayList<>();

    public FilePathPane(File file) {
        this.file = file;
        this.path = file.getPath();
        init();
    }

    public void openAsParentDir(boolean openParentDir) {
        this.openParentDir = openParentDir;
    }

    public void setFileChanging(boolean fileChanging) {
        this.fileChanging = fileChanging;
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
        if (openParentDir){
            try {
                FileManager fileManager = new FileManager(new DirectoryPath(path.getCurrentFile().getParentDir()));
                Information.Desktop.startFileManager(fileManager);
            } catch (NoDirectoryException e) {
                e.printStackTrace();
            }
            return;
        }
        if (file.isDirectory()) {
            FileManager fileManager;
            try {
                fileManager = new FileManager(new DirectoryPath(path));
                Information.Desktop.startFileManager(fileManager);
            } catch (NoDirectoryException e) {
                e.printStackTrace();
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

        popupMenu.addSeparator();

        if (fileChanging) {
            JMenuItem changeFile = new JMenuItem("change file");
            changeFile.addActionListener(actionEvent -> changeFile());
            popupMenu.add(changeFile);
        }




        return popupMenu;
    }

    private void changeFile() {
        for (FileChangeListener l: fileChangeListener) {
            l.beforeFileChanging();
        }
        //TODO option for changing the file

        for (FileChangeListener l: fileChangeListener) {
            l.onFileChanged();
        }
    }

    public void addFileChangeListener(FileChangeListener fileChangeListener) {
        this.fileChangeListener.add(fileChangeListener);
    }

    public static abstract class FileChangeListener {
        public abstract void onFileChanged();
        public abstract void beforeFileChanging();
    }
}
