package gui.apps;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import gui.desktop.DesktopPane;
import util.File;

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

    DesktopPane pane;


    public TextEditor(File file, DesktopPane pane){
        this.file = file;
        this.pane = pane;

        this.width = 400;
        this.height = 300;
        title = "TextEditor";
        init();
    }

    @Override
    protected void init(){
        super.init();
        //this.setLayout(new BorderLayout());

        InternalFrameAdapter saveOnClose;

        this.addInternalFrameListener(saveOnClose = new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            try {
                if(!textArea.getText().equals(file.getContent())){
                    int save = JOptionPane.showConfirmDialog(null, "Save File?", null, JOptionPane.YES_NO_OPTION);
                    if(save == JOptionPane.YES_OPTION){
                        save();
                    }
                }
            } catch (InvalidServerResponseException | UnknownMicroserviceException exception) {
                JOptionPane.showMessageDialog(null, "Error occurred during the conversation with the Server");
            }
            }
        });

        JMenu fileMenu = new JMenu("File");

        JMenuItem save = new JMenuItem("save");
        //JMenuItem saveAs = new JMenuItem("save as");
        JMenuItem rename = new JMenuItem("rename");
        JMenuItem delete = new JMenuItem("delete");

        save.addActionListener(actionEvent -> save());
        rename.addActionListener(actionEvent -> {
            try {
                String newName = JOptionPane.showInputDialog("New Name", file.getName());
                file.rename(newName);
            } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
                e.printStackTrace();
            }
        });
        delete.addActionListener(actionEvent -> {
            int result = JOptionPane.showConfirmDialog(null,
                    "Should this file really be deleted?", null, JOptionPane.YES_NO_OPTION);
            try {
                if(result == JOptionPane.YES_OPTION) {
                    file.delete();
                    TextEditor.this.removeInternalFrameListener(saveOnClose);
                    TextEditor.this.setClosed(true);
                }
            } catch (UnknownMicroserviceException | InvalidServerResponseException | PropertyVetoException e) {
                e.printStackTrace();
            }
        });

        fileMenu.add(save);
        //fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(rename);
        fileMenu.add(delete);

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        this.add(textArea);

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S){
                    TextEditor.this.save();
                }
            }
        });

        load();


    }

    private void load(){
        try{
            textArea.setText(file.getContent());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            JOptionPane.showMessageDialog(null, "Error occurred during the conversation with the Server");
        }
    }

    public void save() {
        try{
            file.setContent(textArea.getText());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            JOptionPane.showMessageDialog(null, "Error occurred during the conversation with the Server");
        }
    }

    @Override
    public void getFocus(){
        super.getFocus();
        textArea.requestFocus();
    }
}
