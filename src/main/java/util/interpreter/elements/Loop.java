package util.interpreter.elements;

import Exceptions.interpreter.InterpreterException;
import Exceptions.interpreter.LoopException;
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

    Variable counter = new Variable("counter", 0.0);

    public Loop(Interpreter interpreter, String condition, String content, Variable... loopVars) {
        this.interpreter = interpreter;
        this.loopInterpreter = new Interpreter(interpreter);
        this.condition = condition;
        this.content = content;
        loopInterpreter.getVars().add(counter);
        loopInterpreter.getVars().addAll(Arrays.asList(loopVars));

    }

    public void execute() throws InterpreterException {
        try {
            while((boolean) loopInterpreter.interpretCommand(condition)) {
                Interpreter interpreter = new Interpreter(this.loopInterpreter);
                interpreter.interpret(content);
                try {
                    Double current = (Double) counter.getContent();
                    if (current.intValue() != current) {
                        throw new ClassCastException("");
                    }
                    counter.setContent(current + 1);
                } catch (ClassCastException e) {
                    throw new LoopException("The counter cannot be set on a non-Integer value");
                }
            }
        } catch (ClassCastException e) {
            System.out.println(condition);
            System.out.println(loopInterpreter.interpretCommand(condition));
            throw new LoopException("The condition wasn't a boolean");
        }
    }
}
