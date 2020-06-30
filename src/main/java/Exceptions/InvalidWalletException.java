package Exceptions;

public class InvalidWalletException extends Exception {

    public InvalidWalletException() {
        super("Invalid Wallet Key");
    }
}
