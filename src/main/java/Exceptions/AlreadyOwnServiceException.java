package Exceptions;

public class AlreadyOwnServiceException extends Exception{

    public AlreadyOwnServiceException(){
        new Exception("You already own a service with this name").printStackTrace();
    }
}
