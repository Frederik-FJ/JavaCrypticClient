package Exceptions.interpreter;

public class LoopException extends InterpreterException{
    public LoopException(String message, int line) {
        super(message, line);
    }
    public LoopException(String message) {
        super(message);
    }
}
