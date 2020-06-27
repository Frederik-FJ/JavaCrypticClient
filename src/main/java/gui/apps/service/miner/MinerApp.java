package gui.apps.service.miner;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import gui.App;
import information.Information;
import items.Device;
import util.Wallet;
import util.service.Miner;

import javax.swing.*;
import java.awt.*;

public class MinerApp extends App {
    Miner miner;
    Device device;

    JTextField walletUuid;

    JSlider percentSlider;

    JLabel percent;
    JButton setPower;

    public MinerApp(Device device, Miner miner) {
        this.miner = miner;
        this.device = device;

        width = 300;
        height = 200;
        title = "Miner";

        init();
    }

    public static MinerApp startMinerApp(Device device) {
        Miner miner = device.getMinerService();
        if (miner == null) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, "You need to register a Miner service first");
            return null;
        }
        return new MinerApp(device, miner);
    }

    @Override
    protected void init() {
        super.init();
        this.setLayout(new FlowLayout());
        walletUuid = new JTextField();
        walletUuid.setText(miner.getWallet().getUuid());

        walletUuid.addActionListener(actionEvent -> setWalletUuid());

        percent = new JLabel();
        percent.setText(miner.getPower() + "");

        percentSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, miner.getPower());
        percentSlider.addChangeListener(changeEvent -> setPower(percentSlider.getValue()));

        setPower = new JButton("set power");
        setPower.addActionListener(actionEvent -> setPower());

        this.add(walletUuid);
        this.add(percent);
        this.add(percentSlider);
        //this.add(setPower);
    }

    private void setPower() {
        try {
            int power = Integer.parseInt(percent.getText());
            if (power > 100 || power < 0) {
                throw new NumberFormatException();
            }
            miner.setPower(power);
        } catch (NumberFormatException e) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, percent.getText() + " is not a valid number");
        }
    }

    private void setPower(int power) {
        if (power > 100 || power < 0) {
            return;
        }
        miner.setPower(power);
        reload();
    }

    private void setWalletUuid() {
        try {
            miner.connectWallet(new Wallet(walletUuid.getText()));
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            JOptionPane.showInternalMessageDialog(Information.Desktop, "Invalid Wallet-UUID");
        }
        walletUuid.setText(miner.getWallet().getUuid());
    }

    private void reload() {

        walletUuid.setText(miner.getWallet().getUuid());
        percent.setText(miner.getPower() + "");
        percentSlider.setValue(miner.getPower());

        this.revalidate();
        this.repaint();
    }
}
