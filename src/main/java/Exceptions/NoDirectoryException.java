package Exceptions;

import util.file.File;

public class NoDirectoryException extends Exception{

    public NoDirectoryException(File file) throws InvalidServerResponseException, UnknownMicroserviceException {
        super(file.getName() + " is no Directory" + " (File-UUID: " + file.getUuid() + ")");
    }
}
