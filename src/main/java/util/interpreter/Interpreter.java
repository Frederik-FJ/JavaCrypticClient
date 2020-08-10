package util.interpreter;


import Exceptions.interpreter.InterpreterException;
import Exceptions.interpreter.InvalidVariableNameException;
import Exceptions.interpreter.NotFoundException;
import Exceptions.interpreter.SyntaxException;
import Exceptions.interpreter.part_exceptions.*;
import gui.util.OutputApp;
import util.interpreter.annotations.UsableClass;
import util.interpreter.classes.ClassStore;
import util.interpreter.classes.DeviceClass;
import util.interpreter.classes.FileClass;
import util.interpreter.classes.Functions;
import util.interpreter.classes.service.BruteforceClass;
import util.interpreter.elements.Variable;
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

    Interpreter parentInterpreter = null;


    static Map<String, ClassStore> classes = new HashMap<>();

    static Class<?>[] usableClasses = {
            DeviceClass.class,
            Functions.class,
            FileClass.class,
            BruteforceClass.class
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

    public Interpreter(Interpreter parentInterpreter) {
        this.parentInterpreter = parentInterpreter;
        this.sourceDevice = parentInterpreter.sourceDevice;
        this.outputApp = parentInterpreter.outputApp;
        this.functions = new Functions(this);
    }

    public void interpret(String text) throws InterpreterException {
        int br1 = 0;
        int br2 = 0;
        int line = 1;
        StringBuilder cmd = new StringBuilder();
        try {
            for (char i : text.toCharArray()) {
                if (i == '(')
                    br1++;
                if (i == ')')
                    br1--;
                if (i == '{')
                    br2++;
                if (i == '}')
                    br2--;

                if (i == '\n')
                    line++;

                if (br1 < 0) {
                    throw new SyntaxException("missing (", line);
                }

                if (br2 < 0) {
                    throw new SyntaxException("missing {", line);
                }

                if ((i == ';' || i == '}') && br1 == 0 && br2 == 0) {
                    interpretCommand(cmd.toString());
                    cmd = new StringBuilder();
                    continue;
                }
                cmd.append(i);
            }

            if (br1 != 0) {
                throw new SyntaxException("missing )");
            }

            if (br2 != 0) {
                throw new SyntaxException("missing }");
            }
        } catch (InterpreterException e) {
            if (e.getLine() == 0)
                e.setLine(line);
            outputApp.toNextFreeLine();
            outputApp.println(e.getClass().getSimpleName() + " at line " + e.getLine() + " due to: \n\t" + e.getMessage());
            throw e;
        }

        /*
        for (Variable var: vars) {
            System.out.println(var);
        }
        */
    }

    public Object interpretCommand(String command) throws InterpreterException {

        Object value = null;

        if (command.strip().startsWith("var ")) {
            return interpretVar(command);
        }

        List<String> parts = new ArrayList<>();
        int br1 = 0;
        int br2 = 0;
        boolean str1 = false;
        boolean str2 = false;
        StringBuilder tmp = new StringBuilder();
        char last = '.';
        for (char c : command.toCharArray()) {

            // String
            if (str1) {
                tmp.append(c);
                if (c == '"') {
                    str1 = false;
                    last = c;
                }
                continue;

            }

            if (str2) {
                tmp.append(c);
                if (c == '\'') {
                    str2 = false;
                    last = c;
                }
                continue;
            }

            if (c == '"')
                str1 = true;
            if (c == '\'')
                str2 = true;

            // Brackets
            if (c == '(')
                br1++;
            if (c == ')')
                br1--;
            if (c == '{')
                br2++;
            if (c == '}')
                br2--;

            // interpret main command
            if (br1 == 0 && br2 == 0 && !str1 && !str2) {
                // Condition
                if (c == '=') {
                    if (last == '=') {
                        parts = new ArrayList<>();
                        tmp = new StringBuilder();
                        tmp.append(command);
                        break;
                    }
                }
                if (c == '<' || c == '>' || c == '!') {
                    parts = new ArrayList<>();
                    tmp = new StringBuilder();
                    tmp.append(command);
                    break;
                }

                // Split commands with '.'s
                if (c == '.') {
                    parts.add(tmp.toString());
                    tmp = new StringBuilder();
                    last = c;
                    continue;
                }
            }
            tmp.append(c);
            last = c;
        }
        parts.add(tmp.toString());

        for (String s : parts) {
            value = interpretSingleCommand(value, s);
        }
        return value;
    }

    private Object interpretSingleCommand(Object value, String cmd) throws InterpreterException {

        cmd = cmd.strip();
        //System.err.println(cmd);
        // First command in a chain of commands
        if (value == null) {
            // Checking for Vars
            for (Variable var : vars) {
                if (var.getName().equals(cmd)) {
                    return var.getContent();
                }
            }


            try {
                return interpretTypeForms(cmd);
            } catch (NoTypeException ignore) {
            }

            try {
                return interpretClassOrConstructor(cmd);
            } catch (NoClassException ignore) {
            }

            try {
                return interpretDefinedMethods(cmd);
            } catch (NoDefinedMethodException ignore) {
            }

            try {
                return interpretConditions(cmd);
            } catch (NoConditionException ignore) {
            }


            if (!cmd.strip().equals("")) {
                throw new NotFoundException("The value '" + cmd + "' wasn't found");
            }
            return null;
        }


        // Methods
        if (cmd.contains("(") && cmd.contains(")")) {
            try {
                return interpretMethods(value, cmd);
            } catch (NoMethodException e) {
                if (!e.getMessage().equals("")) {
                    throw e;
                }
            }
        } else if (!cmd.contains("(") && !cmd.contains(")")) {
            //TODO fields
        }


        return null;
    }

    private Object interpretVar(String line) throws InterpreterException {
        String name = line.split("=")[0].strip().split(" ")[1];
        String value = line.split("=", 2)[1];

        if (name.contains("'") || name.contains("\"") || name.contains(".")) {
            throw new InvalidVariableNameException(name);
        }

        // overwrite old value
        for (Variable var : vars) {
            if (var.getName().equals(name)) {
                var.setContent(interpretCommand(value));
                return value;
            }
        }

        // vars from parent Interpreter
        if (parentInterpreter != null) {
            Interpreter localParentInterpreter = parentInterpreter;

            List<Interpreter> parents = new ArrayList<>();
            parents.add(parentInterpreter);
            while (localParentInterpreter.hasParentInterpreter()) {
                parents.add((localParentInterpreter = localParentInterpreter.getParentInterpreter()));
            }

            for (Interpreter i : parents) {
                for (Variable var : i.vars) {
                    if (var.getName().equals(name)) {
                        var.setContent(interpretCommand(value));
                        return value;
                    }
                }
            }
        }

        // new Var
        Variable var = new Variable(name, interpretCommand(value));
        vars.add(var);
        return value;
    }

    private Object interpretConditions(String cmd) throws InterpreterException {

        //Condition connections
        if (cmd.contains("&&") || cmd.contains("||")) {

            int br1 = 0;
            StringBuilder tmp = new StringBuilder();
            for (char c : cmd.toCharArray()) {
                if (c == '(')
                    br1++;
                if (c == ')')
                    br1--;

                if (br1 == 0 && c == ')' && (tmp.toString().contains("==") || tmp.toString().contains("<")
                        || tmp.toString().contains(">") || tmp.toString().contains("!="))) {
                    cmd = cmd.replace(tmp.toString() + ')', interpretCommand(tmp.substring(1)).toString());
                    tmp = new StringBuilder();
                    continue;
                }

                if (br1 > 0)
                    tmp.append(c);
            }

            // Split with ||
            String[] or = cmd.split("\\|\\|");

            //Split with &&
            String[][] and = new String[or.length][];
            for (int i = 0; i < or.length; i++) {
                and[i] = or[i].split("&&");
            }

            // compute values and compute them with &&
            boolean[] valuesAfterAnd = new boolean[or.length];
            for (int i = 0; i < or.length; i++) {
                boolean val = true;
                for (String s : and[i]) {
                    if (!((boolean) interpretCommand(s))) {
                        val = false;
                        break;
                    }
                }
                valuesAfterAnd[i] = val;
            }

            // compute values with ||
            boolean finalValue = false;
            for (boolean i : valuesAfterAnd) {
                if (i) {
                    finalValue = true;
                    break;
                }
            }

            return finalValue;
        }

        // Conditions
        if (cmd.contains("==") || cmd.contains("!") || cmd.contains("<") || cmd.contains(">")) {
            if (cmd.contains("=")) {
                if (cmd.contains("===") || cmd.contains("!==")) {
                    String[] parts = cmd.split("===");
                    int value1 = interpretCommand(parts[0]).hashCode();
                    int value2 = interpretCommand(parts[1]).hashCode();
                    if (cmd.contains("===")) {
                        return value1 == value2;
                    } else {
                        return value1 != value2;
                    }
                } else if (cmd.contains("==") || cmd.contains("!=")) {
                    String[] parts = cmd.split("==");
                    Object value1 = interpretCommand(parts[0]);
                    Object value2 = interpretCommand(parts[1]);
                    if (cmd.contains("==")) {
                        return value1.equals(value2);
                    } else {
                        return !value1.equals(value2);
                    }
                }
            } else if (cmd.contains("<")) {
                String[] parts = cmd.replace("=", "").split("<");
                Double value1 = (Double) interpretCommand(parts[0]);
                Double value2 = (Double) interpretCommand(parts[1]);
                if (cmd.contains("=")) {
                    return value1 <= value2;
                } else {
                    return value1 < value2;
                }
            } else if (cmd.contains(">")) {
                String[] parts = cmd.replace("=", "").split(">");
                Double value1 = (Double) interpretCommand(parts[0]);
                Double value2 = (Double) interpretCommand(parts[1]);
                if (cmd.contains("=")) {
                    return value1 >= value2;
                } else {
                    return value1 > value2;
                }
            }
        }

        if (cmd.startsWith("!")) {
            return !((boolean) interpretCommand(cmd.substring(1)));
        }

        throw new NoConditionException("");
    }

    private Object interpretClassOrConstructor(String cmd) throws InterpreterException {
        for (String s : classes.keySet()) {
            // for Classes used for static methods
            if (s.equals(cmd)) {
                return classes.get(s);
            }
            // Constructor
            if (cmd.split("\\(")[0].equals(s)) {
                // Preparation for calling the constructor
                String tmp = cmd.split("\\(", 2)[1];
                String[] arguments = split(Collections.singletonList(','), tmp.substring(0, tmp.length() - 1)).toArray(new String[0]);;

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
        throw new NoClassException("");
    }

    private Object interpretDefinedMethods(String cmd) throws InterpreterException {
        ClassStore func = classes.get("Functions");
        Map<String, List<Method>> functions = func.getMethods();
        for (String s : functions.keySet()) {
            if (cmd.split("\\(")[0].equals(s)) {
                String arg = cmd.split("\\(", 2)[1];
                System.out.println("ARG --> " + arg + "\nCMD --> " + cmd);

                // without arguments
                if (arg.equals(")")) {
                    try {
                        return func.getMethod(s).invoke(this.functions);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                // with arguments
                else {
                    System.out.println(arg);
                    String[] args = split(Collections.singletonList(','), arg.substring(0, arg.length() - 1)).toArray(new String[0]);
                    System.out.println(Arrays.toString(args));
                    Object[] arguments = new Object[args.length];
                    Class<?>[] argTypes = new Class<?>[args.length];

                    for (int i = 0; i < args.length; i++) {
                        arguments[i] = interpretCommand(args[i]);
                        System.out.println(arguments[i]);
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
        throw new NoDefinedMethodException("");
    }

    private Object interpretTypeForms(String cmd) throws NoTypeException {
        try {
            return Double.parseDouble(cmd);
        } catch (NumberFormatException ignore) {
        }

        if ((cmd.startsWith("'") && cmd.endsWith("'")) || (cmd.startsWith("\"") && cmd.endsWith("\""))) {
            return cmd.substring(1, cmd.length() - 1);
        }

        if (cmd.equals("true") || cmd.equals("True")) {
            return true;
        }

        if (cmd.equals("false") || cmd.equals("False")) {
            return false;
        }
        throw new NoTypeException("");
    }

    private Object interpretMethods(Object value, String cmd) throws InterpreterException {
        // Preparation for Methods
        String methodName = cmd.split("\\(")[0];
        System.out.println(cmd);
        String tmp = cmd.split("\\(", 2)[1];
        String[] arguments = split(Collections.singletonList(','), tmp.substring(0, tmp.length() - 1)).toArray(new String[0]);

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
            throw new NoMethodException("Die Methode " + methodName + " mit den Parameter " + Arrays.toString(argTypes)
                    + " wurde auf dem Objekt der Klasse " + Functions.getUsableName(value.getClass()) +
                    " nicht gefundend");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new NoMethodException("");
    }

    public OutputApp getOutputApp() {
        return outputApp;
    }

    public Device getSourceDevice() {
        return sourceDevice;
    }

    public boolean hasParentInterpreter() {
        return parentInterpreter != null;
    }

    public Interpreter getParentInterpreter() {
        return parentInterpreter;
    }

    public List<Variable> getVars() {
        return vars;
    }

    private List<String> split(List<Character> splitter, String command) {
        List<String> parts = new ArrayList<>();
        int br1 = 0;
        int br2 = 0;
        boolean str1 = false;
        boolean str2 = false;
        StringBuilder tmp = new StringBuilder();

        rootLoop:
        for (char c : command.toCharArray()) {

            // String
            if (str1) {
                tmp.append(c);
                if (c == '"') {
                    str1 = false;
                }
                continue;

            }

            if (str2) {
                tmp.append(c);
                if (c == '\'') {
                    str2 = false;
                }
                continue;
            }

            if (c == '"')
                str1 = true;
            if (c == '\'')
                str2 = true;

            // Brackets
            if (c == '(')
                br1++;
            if (c == ')')
                br1--;
            if (c == '{')
                br2++;
            if (c == '}')
                br2--;

            // interpret main command
            if (br1 == 0 && br2 == 0 && !str1 && !str2) {
                // Condition
                for (char split : splitter) {
                    if (c == split) {
                        parts.add(tmp.toString());
                        tmp = new StringBuilder();
                        continue rootLoop;
                    }
                }
            }
            tmp.append(c);
        }
        parts.add(tmp.toString());
        return parts;
    }


}
