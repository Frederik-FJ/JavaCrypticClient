package Exceptions.interpreter.part_exceptions;

import Exceptions.interpreter.InterpreterException;

public class NoVarException extends InterpreterException {
    public NoVarException(String message, int line) {
        super(message, line);
    }
    public NoVarException(String message) {
        super(message);
    }
}
