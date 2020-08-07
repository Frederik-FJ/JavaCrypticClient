package Exceptions.interpreterExceptions;

public class SyntaxException extends InterpreterException {
    public SyntaxException(String message) {
        super(message);
    }

    public SyntaxException(String message, int line) {
        super(message, line);
    }
}
