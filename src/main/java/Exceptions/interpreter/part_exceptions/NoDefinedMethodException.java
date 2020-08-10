package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoDefinedMethodException extends InterpreterException {
    public NoDefinedMethodException(String message, int line) {
        super(message, line);
    }
    public NoDefinedMethodException(String message) {
        super(message);
    }
}
