package gui.apps;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import util.File;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextEditor extends App {


    JTextArea textArea;
    JMenuBar menuBar;

    File file;


    public TextEditor(File file){
        this.file = file;

        this.width = 400;
        this.height = 300;
        title = "TextEditor";
        init();
    }

    @Override
    protected void init(){
        super.init();
        //this.setLayout(new BorderLayout());



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

        this.addInternalFrameListener(new InternalFrameAdapter() {
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
