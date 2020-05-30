package gui.controlCenter;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import items.Wallet;

import javax.swing.*;
import java.awt.*;

public class WalletCategory extends JPanel{

    JPanel walletList;
    JPanel walletInfo;

    JLabel currency;
    JTextField walletUuid;
    JTextField walletPw;

    public WalletCategory(){
        this.setLayout(new BorderLayout());

        //wallet list
        walletList = new JPanel();

        JButton b = new JButton("reload");
        b.addActionListener((actionEvent -> reload()));
        walletList.add(b);

        //wallet info
        walletInfo = new JPanel();
        walletInfo.setLayout(null);

        currency = new JLabel();
        currency.setSize(300, 20);
        currency.setLocation(50 ,50);

        walletUuid = new JTextField("uuid");
        if(Information.walletUuid != null)
            walletUuid.setText(Information.walletUuid);
        walletUuid.setSize(200, 20);
        walletUuid.setLocation(50, 100);

        walletPw = new JTextField("pw");
        if(Information.walletPw != null)
            walletPw.setText(Information.walletPw);
        walletPw.setSize(200, 20);
        walletPw.setLocation(50, 150);

        walletInfo.add(currency);
        walletInfo.add(walletPw);
        walletInfo.add(walletUuid);


        this.add(walletInfo, BorderLayout.CENTER);
        this.add(walletList, BorderLayout.WEST);

        reload();
    }

    public void reload(){

        Information.walletUuid = walletUuid.getText();
        Information.walletPw = walletPw.getText();
        try {
            currency.setText(String.valueOf(new Wallet().getMorphcoins()/1000.0));
        } catch (UnknownMicroserviceException e){
            e.printStackTrace();
        }
        walletInfo.repaint();
    }
}
