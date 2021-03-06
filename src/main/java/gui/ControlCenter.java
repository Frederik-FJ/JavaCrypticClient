package gui;

import gui.controlCenter.ComputerCategory;
import gui.controlCenter.UserCategory;
import gui.controlCenter.WalletCategory;
import gui.desktop.Desktop;

import javax.swing.*;

public class ControlCenter extends JTabbedPane {

    Desktop desktop;

    ComputerCategory computerCategory;
    WalletCategory walletCategory;
    UserCategory userCategory;


    public ControlCenter(Desktop desktop) {
        this.desktop = desktop;

        computerCategory = new ComputerCategory(desktop);
        this.addTab("Computer", computerCategory);

        /*walletCategory = new WalletCategory();
        this.addTab("Wallet", walletCategory);*/

        userCategory = new UserCategory();
        this.addTab("User", userCategory);
    }


}
