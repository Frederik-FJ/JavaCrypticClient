package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoLoopException extends InterpreterException {
    public NoLoopException(String message, int line) {
        super(message, line);
    }
    public NoLoopException(String message) {
        super(message);
    }
}
