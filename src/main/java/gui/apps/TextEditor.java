package gui.apps;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.desktop.DesktopPane;
import gui.util.FilePathPane;
import util.file.File;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

public class TextEditor extends App {


    JTextArea textArea;
    JMenuBar menuBar;

    File file;
    FilePathPane filePath;

    DesktopPane pane;


    public TextEditor(File file, DesktopPane pane) {
        this.file = file;
        this.pane = pane;
        filePath = new FilePathPane(file.getParentDir());

        this.width = 400;
        this.height = 300;
        title = "TextEditor";
        title = "TextEditor - " + file.getName();

        init();
    }

    @Override
    protected void init() {
        super.init();
        this.setLayout(new BorderLayout());

        InternalFrameAdapter saveOnClose;

        // Add Option to save the file on closing the TextEditor
        this.addInternalFrameListener(saveOnClose = new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (!textArea.getText().equals(file.getContent())) {
                    int save = JOptionPane.showInternalConfirmDialog(pane, "Save File?", null, JOptionPane.YES_NO_OPTION);
                    if (save == JOptionPane.YES_OPTION) {
                        save();
                    }
                }

            }
        });

        // Adding a JMenu
        JMenu fileMenu = new JMenu("File");

        JMenuItem save = new JMenuItem("save");
        //JMenuItem saveAs = new JMenuItem("save as");
        JMenuItem rename = new JMenuItem("rename");
        JMenuItem delete = new JMenuItem("delete");

        // Adding Actions to the Items
        save.addActionListener(actionEvent -> save());
        rename.addActionListener(actionEvent -> {
            try {
                String newName = (String) JOptionPane.showInternalInputDialog(pane, "New Name", null,
                        JOptionPane.PLAIN_MESSAGE, null, null, file.getName());
                file.rename(newName);
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
        });
        delete.addActionListener(actionEvent -> {
            int result = JOptionPane.showInternalConfirmDialog(pane,
                    "Should this file really be deleted?", null, JOptionPane.YES_NO_OPTION);
            try {
                if (result == JOptionPane.YES_OPTION) {
                    file.delete();
                    TextEditor.this.removeInternalFrameListener(saveOnClose);
                    TextEditor.this.setClosed(true);
                }
            } catch (UnknownMicroserviceException | InvalidServerResponseException | PropertyVetoException e) {
                e.printStackTrace();
            }
        });

        // Add the Items to the JMenuBar
        fileMenu.add(save);
        //fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(rename);
        fileMenu.add(delete);

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        // init the TextArea
        textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        this.add(textArea, BorderLayout.CENTER);

        this.add(filePath, BorderLayout.NORTH);

        // Add Key-Listener for Strg+S
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    TextEditor.this.save();
                }
            }
        });

        load();


    }

    /**
     * load the content of a file into the TextArea from the TextEditor
     */
    private void load() {
        textArea.setText(file.getContent());
    }

    /**
     * save the content from the textArea into the file
     */
    public void save() {
        if (textArea.getText().length() > 255) {
            int confirmed = JOptionPane.showInternalConfirmDialog(pane, "Your Text is too long. The " +
                            "file can maximally contain 255 chars. Your Text contains " + textArea.getText().length() +
                            " chars. Do you want to store your Text with the first 255 chars?", "Too long",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                textArea.setText(textArea.getText().substring(0, 255));
            } else {
                return;
            }
        }
        try {
            file.setContent(textArea.getText());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            JOptionPane.showMessageDialog(null, "Error occurred during the conversation with the Server");
        }
    }

    @Override
    public void getFocus() {
        super.getFocus();
        textArea.requestFocus();
    }
}
