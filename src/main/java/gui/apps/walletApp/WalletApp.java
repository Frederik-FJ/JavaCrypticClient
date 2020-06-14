package gui.apps.walletApp;

import gui.App;
import util.Wallet;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class WalletApp extends App {

    Wallet wallet;

    WalletPane walletPane;

    JTabbedPane content = new JTabbedPane();

    public WalletApp(Wallet wallet){
        this.wallet = wallet;

        this.height = 300;
        this.width = 400;
        this.title = "Wallet";

        init();

        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                walletPane.close();
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.setLayout(new BorderLayout());

        walletPane = new WalletPane(wallet);
        content.addTab("Wallet", walletPane);


        this.add(content, BorderLayout.CENTER);
    }
}
