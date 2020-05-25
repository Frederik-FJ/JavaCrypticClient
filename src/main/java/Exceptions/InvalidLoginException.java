package Exceptions;

public class InvalidLoginException extends Exception{
    public InvalidLoginException(){
        new Error("Invalid Login Credentials").printStackTrace();
    }
}
