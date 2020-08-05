package util.interpreter.functions;

import util.interpreter.Interpreter;

public class Functions {

    Interpreter interpreter;

    public Functions(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void print(Object o) {
        interpreter.getOutputApp().print(o);
    }

    public void println(Object o) {
        interpreter.getOutputApp().println(o);
    }
}
