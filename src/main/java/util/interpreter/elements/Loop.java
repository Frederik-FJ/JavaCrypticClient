package util.interpreter.elements;

import Exceptions.interpreterExceptions.InterpreterException;
import util.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loop {

    Interpreter interpreter;
    Interpreter loopInterpreter;

    List<Variable> vars = new ArrayList<>();
    String condition;
    String content;

    public Loop(Interpreter interpreter, String condition, String content, Variable... loopVars) {
        this.interpreter = interpreter;
        this.loopInterpreter = new Interpreter(interpreter);
        this.condition = condition;
        this.content = content;
        loopInterpreter.getVars().addAll(Arrays.asList(loopVars));

    }

    public void execute() throws InterpreterException {
        try {
            while((boolean) loopInterpreter.interpretCommand(condition)) {
                Interpreter interpreter = new Interpreter(this.loopInterpreter);
                interpreter.interpret(content);
            }
        } catch (ClassCastException e) {
            throw new InterpreterException("Cast Exception in Loop");
        }

    }
}
