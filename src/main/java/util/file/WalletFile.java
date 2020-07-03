package util.file;

import information.Information;
import util.items.Device;
import util.Wallet;

import javax.swing.*;

public class WalletFile extends File {

    Wallet wallet;

    public WalletFile(File file) {
        this(file.uuid, file.parentDirUuid, file.isDirectory, file.device);
    }

    public WalletFile(String uuid, String parentDirUuid, boolean isDirectory, Device device) {
        super(uuid, parentDirUuid, isDirectory, device);

        init();
    }


    private void init() {
        String[] content = this.getContent().split(" ");
        try {
            wallet = new Wallet(content[0], content[1]);
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, "There is a mistake in the File " + this.getName());
        }
    }

    public Wallet getWallet() {
        return wallet;
    }
}
