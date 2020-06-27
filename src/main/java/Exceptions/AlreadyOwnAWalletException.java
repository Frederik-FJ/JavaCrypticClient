package Exceptions;

public class AlreadyOwnAWalletException extends Exception {

    public AlreadyOwnAWalletException() {
        new Exception("You already own a wallet").printStackTrace();
    }
}
