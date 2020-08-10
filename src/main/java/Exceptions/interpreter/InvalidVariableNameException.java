package Exceptions.interpreter;

public class InvalidVariableNameException extends InterpreterException{

    public InvalidVariableNameException(String name) {
        super("The Name '" + name + "' is not allowed");
    }
}
