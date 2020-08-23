package util.interpreter.elements;

import Exceptions.interpreter.InterpreterException;
import Exceptions.interpreter.LoopException;
import util.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IfStatement {

    Interpreter interpreter;
    Interpreter loopInterpreter;

    List<Variable> vars = new ArrayList<>();
    String condition;
    String content;
    String elseContent;

    public IfStatement(Interpreter interpreter, String condition, String content, String elseContent, Variable... loopVars) {
        this.interpreter = interpreter;
        this.loopInterpreter = new Interpreter(interpreter);
        this.condition = condition;
        this.content = content;
        this.elseContent = elseContent;
        loopInterpreter.getVars().addAll(Arrays.asList(loopVars));

    }

    public void execute() throws InterpreterException {
        try {
            if((boolean) loopInterpreter.interpretCommand(condition)) {
                Interpreter interpreter = new Interpreter(this.loopInterpreter);
                interpreter.interpret(content);
            } else {
                Interpreter interpreter = new Interpreter(this.loopInterpreter);
                interpreter.interpret(elseContent);
            }
        } catch (ClassCastException e) {
            System.out.println(condition);
            System.out.println(loopInterpreter.interpretCommand(condition));
            throw new LoopException("The condition wasn't a boolean");
        }
    }
}
