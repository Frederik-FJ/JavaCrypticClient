package gui;

import javax.swing.*;

public abstract class App extends JInternalFrame {


    protected int width;
    protected int height;

    public App(){
        super("App" , true, true, true, true);
    }


    public abstract void handleCommand(String command);

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height){
        this.height = height;
    }

    /**
     * Method to give the Focus to this App
     */
    public void getFocus(){
        this.pack();
        this.moveToFront();
        this.requestFocus();
    }



}
