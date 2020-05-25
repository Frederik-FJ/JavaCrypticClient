package Exceptions;

public class NoResponseTimeoutException extends Exception{

    public NoResponseTimeoutException(){
        new Error("No Response - Timeout").printStackTrace();
    }
}
