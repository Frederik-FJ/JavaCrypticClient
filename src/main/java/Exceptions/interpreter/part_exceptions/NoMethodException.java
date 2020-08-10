package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoMethodException extends InterpreterException {

    public NoMethodException(String message, int line) {
        super(message, line);
    }

    public NoMethodException(String message) {
        super(message);
    }
}
