package gui.apps.service.attack;

import gui.App;
import gui.apps.service.attack.bruteforce.BruteforceAttackPane;
import gui.apps.service.attack.hacked.HackedPane;
import gui.apps.service.attack.portscan.PortscanAttackPane;
import util.service.Bruteforce;
import util.service.Portscan;
import util.service.Service;

import javax.swing.*;
import java.awt.*;

public class AttackPane extends JPanel {

    protected Service service;
    protected JLabel title;

    protected App app;


    public AttackPane (Service s, App app){
        this.service = s;

        init();
    }

    protected AttackPane(){

    }

    private void init(){
        this.setLayout(new BorderLayout());

        if(service == null){
            this.add(new HackedPane(), BorderLayout.CENTER);
        }

        if(service instanceof Bruteforce){
            this.add(new BruteforceAttackPane(service), BorderLayout.CENTER);
        }else if(service instanceof Portscan){
            this.add(new PortscanAttackPane(service), BorderLayout.CENTER);
        }else {
            this.setLayout(new FlowLayout());
            this.add(new JLabel("Unknown Attack Service"), BorderLayout.CENTER);
        }
    }

    public String getTitle(){
        return title.getText();
    }


    protected int relativeHeight(int percent){
        return (int) (this.getHeight()*((double) percent/100));
    }

    protected int relativeWidth(int percent){
        return (int) (this.getWidth()*((double) percent/100));
    }
}
