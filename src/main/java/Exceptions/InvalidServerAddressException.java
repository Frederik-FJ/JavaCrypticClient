package Exceptions;

public class InvalidServerAddressException extends Exception {

    public InvalidServerAddressException() {
        new Exception("Invalid Server Address").printStackTrace();
    }

}
