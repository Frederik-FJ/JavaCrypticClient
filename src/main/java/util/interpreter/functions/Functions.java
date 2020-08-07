package util.interpreter.functions;

import util.interpreter.Interpreter;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableField;
import util.interpreter.annotations.UsableMethod;
import util.interpreter.classes.ClassStore;
import util.interpreter.classes.DeviceClass;

@UsableClass(name = "Functions")
public class Functions {

    Interpreter interpreter;
    ClassStore classStore;

    public Functions(Interpreter interpreter) {
        this.interpreter = interpreter;
        currentDevice = new DeviceClass(interpreter.getSourceDevice());
    }

    @UsableMethod(name = "print")
    public void print(Object o) {
        interpreter.getOutputApp().print(o);
    }

    @UsableMethod(name = "println")
    public void println(Object o) {
        interpreter.getOutputApp().println(o);
    }

    @UsableMethod(name = "currentDevice")
    public DeviceClass getCurrentDevice() {
        return currentDevice;
    }

    @UsableField(name = "currentDevice", isStatic = false)
    public DeviceClass currentDevice = null;


    // Functions for Interpreter
    public static boolean isEqual(Class<?>[] first, Class<?>[] second) {
        if (first.length != second.length) {
            return false;
        }
        for (int i = 0; i < first.length; i++) {
            if (!first[i].getName().equals(second[i].getName())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCallable(Class<?>[] params, Class<?>[] args) {
        if (params.length != args.length) {
            return false;
        }
        for (int i = 0; i < params.length; i++) {
            if (!params[i].isAssignableFrom(args[i])) {
                return false;
            }
        }
        return true;
    }

    public static String getUsableName(Class<?> cl) {
        return cl.getAnnotation(UsableClass.class).name();
    }
}
