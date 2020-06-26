package gui.apps.walletApp;

import gui.App;
import util.Wallet;
import util.file.WalletFile;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class WalletApp extends App {

    Wallet wallet;

    WalletPane walletPane;
    WalletFile walletFile;

    JTabbedPane content = new JTabbedPane();

    public WalletApp(Wallet wallet){
        this.wallet = wallet;

        init();
    }

    public WalletApp(WalletFile walletFile){
        this.wallet = walletFile.getWallet();
        this.walletFile = walletFile;

        init();
    }

    @Override
    protected void init() {

        this.height = 300;
        this.width = 400;
        this.title = "Wallet";

        super.init();
        this.setLayout(new BorderLayout());

        if(walletFile != null){
            walletPane = new WalletPane(walletFile);
        }else{
            walletPane = new WalletPane(wallet);
        }
        content.addTab("Wallet", walletPane);


        this.add(content, BorderLayout.CENTER);


        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                walletPane.close();
            }
        });
    }
}
