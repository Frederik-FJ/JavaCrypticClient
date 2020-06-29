package gui.apps.fileManager;

import Exceptions.NoDirectoryException;
import util.file.File;
import util.path.Path;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PathPane extends Panel {

    List<JLabel> dirs = new ArrayList<>();
    FileManager fileManager;
    Path path;

    public PathPane(FileManager fileManager) {
        this.fileManager = fileManager;
        path = fileManager.directoryPath;

        init();
    }

    private void init() {
        this.setLayout(new FlowLayout());
        loadContent();
        path.addPathChangeListener(new Path.PathChangeListener() {
            @Override
            public void onPathChanged() {
                PathPane.this.loadContent();
            }
        });
    }

    public void loadContent() {
        dirs = new ArrayList<>();
        for (File dir: path.getPathFiles()) {
            JLabel dirLabel = new JLabel(dir.getName());
            if (dirLabel.getText() == null) {
                dirLabel.setText("Device(" + dir.getDevice().getName() + ")");
            }

            dirLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        try {
                            fileManager.setPath(dir);
                        } catch (NoDirectoryException noDirectoryException) {
                            noDirectoryException.printStackTrace();
                        }
                    }
                }
            });

            dirs.add(dirLabel);
        }
        reload();
    }

    public void reload() {
        for (Component c : this.getComponents()) {
            if (c instanceof JLabel) {
                this.remove(c);
            }
        }
        for (JLabel dir: dirs) {
            this.add(dir);
            this.add(new JLabel("-->"));
        }

        this.revalidate();
        this.repaint();
    }
}
