package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoClassException extends InterpreterException {
    public NoClassException(String message, int line) {
        super(message, line);
    }
    public NoClassException(String message) {
        super(message);
    }
}
