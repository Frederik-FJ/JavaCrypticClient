package Exceptions;

public class InvalidWalletException extends Exception {

    public InvalidWalletException() {
        new Error("Invalid Wallet Key").printStackTrace();
    }
}
