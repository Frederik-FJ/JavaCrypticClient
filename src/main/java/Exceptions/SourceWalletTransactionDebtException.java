package Exceptions;

public class SourceWalletTransactionDebtException extends Exception {

    public SourceWalletTransactionDebtException() {
        new Error("Source wallet would make debt").printStackTrace();
    }
}
