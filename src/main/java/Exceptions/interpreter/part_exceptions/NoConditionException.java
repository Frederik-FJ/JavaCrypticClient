package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoConditionException extends InterpreterException {
    public NoConditionException(String message, int line) {
        super(message, line);
    }
    public NoConditionException(String message) {
        super(message);
    }
}
