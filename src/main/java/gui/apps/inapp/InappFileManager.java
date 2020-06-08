package gui.apps.inapp;

import gui.apps.fileManager.FileManager;
import items.Device;
import util.file.File;

import javax.swing.*;

public class InappFileManager extends FileManager {

    File choseFile = null;

    JButton submit;
    boolean submitted = false;

    public InappFileManager(Device device) {
        super(null, device);

        submit = new JButton("ok");
        submit.setLocation(this.getWidth() - 70, this.getHeight()- 30);
        submit.setSize(40, 20);
        submit.addActionListener(actionEvent -> submitted=true);

        this.add(submit);
    }

    @Override
    protected void fileAction(File f) {
        this.choseFile = f;
    }

    public File getChoseFile(){
        return choseFile;
    }

}
