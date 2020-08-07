package util.interpreter;


import Exceptions.interpreterExceptions.InvalidVariableNameException;
import gui.util.OutputApp;
import util.interpreter.annotations.UsableClass;
import util.interpreter.classes.ClassStore;
import util.interpreter.classes.DeviceClass;
import util.interpreter.classes.FileClass;
import util.interpreter.functions.Functions;
import util.items.Device;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Interpreter {

    OutputApp outputApp;
    Device sourceDevice;
    Functions functions;

    List<Variable> vars = new ArrayList<>();


    static Map<String, ClassStore> classes = new HashMap<>();

    static Class<?>[] usableClasses = {
            DeviceClass.class,
            Functions.class,
            FileClass.class
    };

    static {

        for (Class<?> c : usableClasses) {
            classes.put(c.getAnnotation(UsableClass.class).name(), new ClassStore(c));
        }
    }

    public Interpreter(OutputApp outputApp, Device sourceDevice) {
        this.outputApp = outputApp;
        this.sourceDevice = sourceDevice;
        this.functions = new Functions(this);
    }

    public void interpret(String text) throws InvalidVariableNameException {
        for (int i = 0; i < text.split("\n").length; i++) {
            String line = text.split("\n")[i];
            try {
                for (String s : line.split(";")) {
                    if (s.strip().equals(""))
                        continue;
                    interpretCommand(s);
                }
            } catch (InvalidVariableNameException e) {
                e.setLine(i+1);
                outputApp.toNextFreeLine();
                outputApp.println(e.getClass().getSimpleName() + " at line " + e.getLine() + " due to: \n\t" + e.getMessage());
                throw e;
            }
        }
        /*
        for (Variable var: vars) {
            System.out.println(var);
        }
        */
    }

    public Object interpretCommand(String command) throws InvalidVariableNameException {

        if (command.strip().startsWith("var ")) {
            interpretVar(command);
            return null;
        }
        Object value = null;
        List<String> parts = new ArrayList<>();
        int br1 = 0;
        int br2 = 0;
        StringBuilder tmp = new StringBuilder();
        for (char c : command.toCharArray()) {
            if (c == '(')
                br1++;
            if (c == ')')
                br1--;
            if (c == '{')
                br2++;
            if (c == '}')
                br2--;

            if (c == '.' && br1 == 0 && br2 == 0) {
                parts.add(tmp.toString());
                tmp = new StringBuilder();
                continue;
            }
            tmp.append(c);
        }
        parts.add(tmp.toString());

        for (String s : parts) {
            value = interpretSingleCommand(value, s);
        }
        return value;
    }

    private Object interpretSingleCommand(Object value, String cmd) throws InvalidVariableNameException {

        cmd = cmd.strip();
        // First command in a chain of commands
        if (value == null) {
            // Checking for Vars
            for (Variable var : vars) {
                if (var.getName().equals(cmd)) {
                    return var.getContent();
                }
            }

            //Type-Forms
            try {
                return Double.parseDouble(cmd);
            } catch (NumberFormatException ignore) { }

            if ((cmd.startsWith("'") && cmd.endsWith("'")) || (cmd.startsWith("\"") && cmd.endsWith("\""))) {
                return cmd.substring(1, cmd.length()-1);
            }

            for (String s : classes.keySet()) {
                // for Classes used for static methods
                if (s.equals(cmd)) {
                    return classes.get(s);
                }
                // Constructor
                if (cmd.split("\\(")[0].equals(s)) {
                    // Preparation for calling the constructor
                    String tmp = cmd.split("\\(")[1];
                    String[] arguments = tmp.substring(0, tmp.length()-1).split(",");

                    Object[] args = new Object[arguments.length];
                    Class<?>[] argTypes = new Class[args.length];

                    for (int i = 0; i < arguments.length; i++) {
                        args[i] = interpretCommand(arguments[i]);
                        argTypes[i] = args[i].getClass();
                    }

                    // Finding and calling the constructor
                    try {
                        Constructor<?> constructor = classes.get(s).getConstructor(argTypes);
                        return constructor.newInstance(args);
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            // for defined methods
            ClassStore func = classes.get("Functions");
            Map<String, List<Method>> functions = func.getMethods();
            for (String s : functions.keySet()) {
                if (cmd.split("\\(")[0].equals(s)) {
                    List<Method> methods = functions.get(s);
                    String arg = cmd.split("\\(", 2)[1];
                    System.out.println("ARG --> " + arg + "\nCMD --> " + cmd);
                    // without arguments
                    if (arg.equals(")")) {
                        try {
                            return classes.get("Functions").getMethod(s).invoke(this.functions);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                    // with arguments
                    else {
                        System.out.println(arg);
                        String[] args = arg.substring(0, arg.length()-1).split(",");
                        Object[] arguments = new Object[args.length];
                        Class<?>[] argTypes = new Class<?>[args.length];

                        for (int i = 0; i < args.length; i++) {
                            arguments[i] = interpretCommand(args[i]);
                            argTypes[i] = arguments[i].getClass();
                        }
                        try {
                            return func.getMethod(s, argTypes).invoke(this.functions, arguments);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            return classes.getOrDefault(cmd, null);
        }


        // Methods
        if (cmd.contains("(") && cmd.contains(")")) {
            // Preparation for Methods
            String methodName = cmd.split("\\(")[0];
            System.out.println(cmd);
            String tmp = cmd.split("\\(")[1];
            String[] arguments = tmp.substring(0, tmp.length() - 1).split(",");

            Object[] args = new Object[arguments.length];
            Class<?>[] argTypes = new Class[args.length];

            for (int i = 0; i < args.length; i++) {
                args[i] = interpretCommand(arguments[i]);
                if (args[i] != null)
                    argTypes[i] = args[i].getClass();
            }


            // Finding and calling methods
            try {
                Method method;

                // without arguments
                if (arguments.length == 1 && arguments[0].equals("")) {
                    if (value instanceof ClassStore) {
                        method = ((ClassStore) value).getFunction(methodName);
                        return method.invoke(null);
                    } else {
                        method = classes.get(Functions.getUsableName(value.getClass())).getMethod(methodName);
                        return method.invoke(value);
                    }
                }
                // with arguments
                else {
                    if (value instanceof ClassStore) {
                        method = ((ClassStore) value).getFunction(methodName, argTypes);
                        return method.invoke(null, args);
                    } else {
                        method = classes.get(Functions.getUsableName(value.getClass())).getMethod(methodName, argTypes);
                        return method.invoke(value, args);
                    }
                }
            } catch (NoSuchMethodException e) {
                System.err.println("Die Methode " + methodName + " wurde nicht gefundend");
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }else  if (!cmd.contains("(") && !cmd.contains(")")) {
            //TODO fields
        }


        return null;
    }

    private void interpretVar(String line) throws InvalidVariableNameException {
        String name = line.split("=")[0].strip().split(" ")[1];
        String value = line.split("=")[1];

        if (name.contains("'") || name.contains("\"") || name.contains(".")) {
            throw new InvalidVariableNameException(name);
        }

        // overwrite old value
        for (Variable var : vars) {
            if (var.getName().equals(name)) {
                var.setContent(interpretCommand(value));
                return;
            }
        }

        // new Var
        Variable var = new Variable(name, interpretCommand(value));
        vars.add(var);
    }


    public OutputApp getOutputApp() {
        return outputApp;
    }

    public Device getSourceDevice() {
        return sourceDevice;
    }
}
