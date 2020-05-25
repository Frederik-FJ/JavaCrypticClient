package Exceptions;

public class InvalidSessionTokenException extends Exception{

    public InvalidSessionTokenException(){
        new Error("Invalid Session token").printStackTrace();
    }
}
