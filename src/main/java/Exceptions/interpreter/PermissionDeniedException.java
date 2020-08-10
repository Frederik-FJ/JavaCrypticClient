package Exceptions.interpreter;

public class PermissionDeniedException extends InterpreterException{
    public PermissionDeniedException(String message, int line) {
        super(message, line);
    }

    public PermissionDeniedException(String message) {
        super(message);
    }
}
