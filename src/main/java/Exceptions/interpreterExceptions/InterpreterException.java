package Exceptions.interpreterExceptions;

public class InterpreterException extends Exception{

    int line = 0;

    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(String message, int line) {
        super(message);
        this.line = line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
