package Exceptions.file;

public class MissingFileException extends Exception{

    public MissingFileException(){
        super("A File is missing");
    }
}
