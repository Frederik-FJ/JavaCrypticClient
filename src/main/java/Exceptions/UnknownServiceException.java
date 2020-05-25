package Exceptions;

public class UnknownServiceException extends Exception{

    public UnknownServiceException(){
        new Error("Unknown service ").printStackTrace();
    }
}
