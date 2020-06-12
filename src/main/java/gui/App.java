package gui;

import javax.swing.*;

public abstract class App extends JInternalFrame {


    protected int width;
    protected int height;
    protected String title;

    public App(){
        super("App" , true, true, true, true);
    }

    protected void init(){
        this.setTitle(title);
        this.setSize(width, height);
        this.moveToFront();
        this.setVisible(true);
    }


    public void handleCommand(String command){}

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
        this.revalidate();
        this.moveToFront();
        this.requestFocus();
    }



}
