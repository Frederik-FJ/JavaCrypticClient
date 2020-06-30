package Exceptions.file;

public class UnknownFileSourceException extends Exception{
    public UnknownFileSourceException(String fileUuid){
        super("Unknown File Uuid -->" + fileUuid);
    }
}
