package Exceptions.interpreterExceptions;

public class NotFoundException extends InterpreterException{
    public NotFoundException(String message, int line) {
        super(message, line);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
