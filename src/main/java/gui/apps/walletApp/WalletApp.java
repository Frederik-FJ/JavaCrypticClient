package gui.apps.walletApp;

import Exceptions.file.MissingFileException;
import Exceptions.file.UnknownFileSourceException;
import gui.App;
import information.Information;
import util.items.Device;
import util.Wallet;
import util.file.File;
import util.file.WalletFile;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.Objects;

public class WalletApp extends App {

    Wallet wallet;

    WalletPane walletPane;
    WalletFile walletFile;

    JTabbedPane content = new JTabbedPane();

    public WalletApp(Wallet wallet) {
        this.wallet = wallet;

        init();
    }

    public WalletApp(WalletFile walletFile) {
        this.wallet = walletFile.getWallet();
        this.walletFile = walletFile;

        init();
    }

    public WalletApp(Device device) throws UnknownFileSourceException, MissingFileException {
        String key = device.getName() + "." + device.getUuid() + ".wallet";
        if (!Information.properties.containsKey(key)) {
            System.out.println(key);
            Information.properties.keySet().forEach(System.out::println);
            throw new MissingFileException();
        }
        this.walletFile = new WalletFile(
                Objects.requireNonNull(File.getFileByUuid(
                        Information.properties.get(key)
                                .toString(), device)));
        this.wallet = walletFile.getWallet();



        init();
    }

    @Override
    protected void init() {

        this.height = 300;
        this.width = 400;
        this.title = "Wallet";

        super.init();
        this.setLayout(new BorderLayout());

        if (walletFile != null) {
            walletPane = new WalletPane(walletFile);
            Information.properties.put(walletFile.getDevice().getName() + "." + walletFile.getDevice().getUuid() + ".wallet", walletFile.getUuid());
        } else {
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
