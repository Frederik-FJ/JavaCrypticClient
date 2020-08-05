package util.interpreter;


import Exceptions.interpreterExceptions.InvalidVariableNameException;
import gui.util.OutputApp;
import util.file.File;
import util.interpreter.functions.Functions;
import util.items.Device;
import util.network.Network;
import util.service.Bruteforce;
import util.service.Miner;
import util.service.Portscan;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Interpreter {

    OutputApp outputApp;
    Device sourceDevice;
    Functions functions;

    List<Variable> vars = new ArrayList<>();


    static Map<String, Class<?>> classes = new HashMap<>();
    static Map<String, Method> methods = new HashMap<>();

    static {


        classes.put("Device", Device.class);
        classes.put("File", File.class);
        classes.put("Network", Network.class);
        classes.put("Bruteforce", Bruteforce.class);
        classes.put("Portscan", Portscan.class);
        classes.put("Miner", Miner.class);

        try {
            Class<?> sysClass = Functions.class;

            methods.put("println", sysClass.getMethod("println", Object.class));
            methods.put("print", sysClass.getMethod("print", Object.class));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
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
                throw e;
            }
        }
        for (Variable var: vars) {
            System.out.println(var);
        }
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
            System.err.println(command + "-->" + s);
        }
        System.out.println();
/*
        // TODO auf Klammern beim Punkt trennen achten (oben angefangen, noch testen); checken, was in der Liste steht & verbessern
        for (String s: command.split("\\.")) {
            value = interpretSingleCommand(value, s);
            System.out.println(value);
        }
 */
        for (String s : parts) {
            value = interpretSingleCommand(value, s);
            System.out.println(value);
            System.out.println(s);
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
            for (String s : methods.keySet()) {
                if (cmd.split("\\(")[0].equals(s)) {
                    String arg = cmd.split("\\(", 2)[1];
                    arg = arg.substring(0, arg.length()-1);
                    System.out.println("ARG --> " + arg + "\nCMD --> " + cmd);
                    try {
                        methods.get(s).invoke(this.functions, interpretCommand(arg));
                        System.err.println(arg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            return classes.getOrDefault(cmd, null);
        }

        // extras for Device
        if (value == Device.class) {
            if (cmd.equals("this")) {
                return sourceDevice;
            }
        }

        // Preparation for Methods
        String methodName = cmd.split("\\(")[0];
        System.out.println(cmd);
        String tmp = cmd.split("\\(")[1];
        String[] arguments = tmp.substring(0, tmp.length()-1).split(",");

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
                if (value instanceof Class) {
                    method = ((Class<?>) value).getMethod(methodName);
                    return method.invoke(null);
                } else {
                    method = value.getClass().getMethod(methodName);
                    return method.invoke(value);
                }
            }
            // with arguments
            else {
                if (value instanceof Class) {
                    method = ((Class<?>) value).getMethod(methodName, argTypes);
                    return method.invoke(null, args);
                }else {
                    method = value.getClass().getMethod(methodName, argTypes);
                    return method.invoke(value, args);
                }
            }
        } catch (NoSuchMethodException e) {
            System.err.println("Die Methode " + methodName + " wurde nicht gefundend");
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        return null;
    }

    private void interpretVar(String line) throws InvalidVariableNameException {
        String name = line.split("=")[0].strip().split(" ")[1];
        String value = line.split("=")[1];

        if (name.contains("'") || name.contains("\"") || name.contains(".")) {
            throw new InvalidVariableNameException(name);
        }
        Variable var = new Variable(name, interpretCommand(value));
        vars.add(var);
    }


    public OutputApp getOutputApp() {
        return outputApp;
    }
}
