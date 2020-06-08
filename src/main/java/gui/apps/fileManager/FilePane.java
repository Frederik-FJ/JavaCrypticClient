package gui.apps.fileManager;

import Exceptions.InvalidServerResponseException;
import Exceptions.NoDirectoryException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import util.file.File;

import javax.swing.*;
import java.awt.*;

public class FilePane extends JPanel{

    File file;

    boolean isDirectory;
    String name;

    ImageIcon icon;

    JLabel iconContainer;
    JLabel nameContainer;
    JLabel sizeContainer;

    public FilePane(File file){
        this.file = file;

        try {
            load();
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }

        init();
    }

    public FilePane(File file, String name){
        this.file = file;

        try {
            load();
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }

        this.name = name;
        init();
    }

    public void load() throws UnknownMicroserviceException, InvalidServerResponseException {
        this.isDirectory = file.isDirectory();
        this.name = file.getName();
    }

    private void init(){
        this.setLayout(new BorderLayout());
        this.setHeight(30);

        if(isDirectory){
            this.icon = Information.dirIcon;
        }else {
            this.icon = Information.fileIcon;
        }

        iconContainer = new JLabel(icon);
        nameContainer = new JLabel(name);
        try {
            sizeContainer = new JLabel(file.getContent().length()+ " byte");
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
        if(isDirectory){
            try {
                sizeContainer = new JLabel(file.getFiles().size() + " Files");
            } catch (UnknownMicroserviceException | InvalidServerResponseException | NoDirectoryException e) {
                e.printStackTrace();
            }
        }


        this.add(iconContainer, BorderLayout.WEST);
        this.add(nameContainer, BorderLayout.CENTER);
        this.add(sizeContainer, BorderLayout.EAST);
    }

    public void setWidth(int width){
        this.setSize(width, this.getHeight());
    }

    public void setHeight(int height){
        this.setSize(this.getWidth(), height);
    }

    public void setFileName(String name){
        this.name = name;
    }

    public File getFile(){
        return file;
    }


}
