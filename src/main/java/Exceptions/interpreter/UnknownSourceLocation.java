package Exceptions.interpreter;

public class UnknownSourceLocation extends InterpreterException {
    public UnknownSourceLocation(String message, int line) {
        super(message, line);
    }

    public UnknownSourceLocation(String message) {
        super(message);
    }
}
