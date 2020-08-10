package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoConstructorException extends InterpreterException {
    public NoConstructorException(String message, int line) {
        super(message, line);
    }
    public NoConstructorException(String message) {
        super(message);
    }
}
