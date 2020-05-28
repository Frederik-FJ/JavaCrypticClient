package gui.controlCenter;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import items.Wallet;

import javax.swing.*;
import java.awt.*;

public class WalletCategory extends JPanel{

    JPanel walletList;
    JTextArea walletInfo;

    public WalletCategory(){
        this.setLayout(new BorderLayout());

        walletList = new JPanel();
        walletInfo = new JTextArea();

        JButton b = new JButton("reload");
        b.addActionListener((actionEvent -> reload()));
        walletList.add(b);


        this.add(walletInfo, BorderLayout.CENTER);
        this.add(walletList, BorderLayout.WEST);

        reload();

    }

    public void reload(){
        try {
            walletInfo.setText(String.valueOf(new Wallet().getMorphcoins()/1000.0));
        } catch (UnknownMicroserviceException | InvalidServerResponseException e){
            e.printStackTrace();
        }
        repaint();
    }
}
