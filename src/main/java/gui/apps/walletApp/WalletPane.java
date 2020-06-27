package gui.apps.walletApp;

import Exceptions.InvalidWalletException;
import gui.util.FilePathPane;
import gui.util.Panel;
import information.Information;
import util.User;
import util.Wallet;
import util.file.WalletFile;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

public class WalletPane extends Panel {

    Timer timer;

    Wallet wallet;
    WalletFile walletFile;

    JLabel title;
    FilePathPane filePath;
    JLabel amountField;
    JLabel uuidField;

    double amount;
    String uuid;
    User owner;

    public WalletPane(Wallet wallet){
        this.wallet = wallet;

        init();
    }

    public WalletPane(WalletFile walletFile){
        this.walletFile = walletFile;
        this.wallet = walletFile.getWallet();

        init();
    }

    private void init(){
        this.setLayout(null);

        try {
            amount = wallet.getMorphcoins();
        } catch (InvalidWalletException e) {
            e.printStackTrace();
            JOptionPane.showInternalMessageDialog(Information.Desktop, "Invalid Wallet");
            return;
        }

        uuid = wallet.getUuid();
        owner = wallet.getOwner();

        title = new JLabel("Wallet von " + owner.getUserName());
        title.setSize(130, 25);
        title.setLocation(relativeWidth(50) - title.getWidth()/2, 10);
        this.add(title);

        if(walletFile != null){
            filePath = new FilePathPane(walletFile);
            this.add(filePath);
        }


        amountField = new JLabel(amount + " Mc");
        amountField.setSize(amountField.getText().length()*8+10, 20);
        amountField.setLocation(relativeWidth(10), 50);
        this.add(amountField);

        uuidField = new JLabel("UUID: " + uuid);
        uuidField.setSize(uuidField.getText().length()*8+10, 20);
        uuidField.setLocation(relativeWidth(60), 50);
        this.add(uuidField);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reload();
            }
        });

        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reloadMorphcoins();
            }
        }, 5000, 5000);



    }

    private void reload(){
        title.setLocation(relativeWidth(50) - 50, 10);
        uuidField.setLocation(relativeWidth(60), 100);
        amountField.setLocation(relativeWidth(10), 100);
        if(filePath != null){
            filePath.setSize(this.getWidth()-relativeWidth(10), 30);
            filePath.setLocation(relativeWidth(10), 50);
        }

        revalidate();
        repaint();
    }

    private void reloadMorphcoins(){
        try {
            amount = wallet.getMorphcoins();
        } catch (InvalidWalletException e) {
            e.printStackTrace();
        }
        amountField.setText(amount + "Mc");
        amountField.setSize(amountField.getText().length()*8+10, 20);


        reload();
    }

    public void close(){
        timer.cancel();
    }

}
