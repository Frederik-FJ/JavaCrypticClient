package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoTypeException extends InterpreterException {
    public NoTypeException(String message, int line) {
        super(message, line);
    }

    public NoTypeException(String message) {
        super(message);
    }
}
