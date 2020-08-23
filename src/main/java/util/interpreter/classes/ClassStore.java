package util.interpreter.classes;

import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableField;
import util.interpreter.annotations.UsableFunction;
import util.interpreter.annotations.UsableMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ClassStore {

    Class<?> c;

    Map<String, List<Method>> functions = new HashMap<>();
    Map<String, Field> fields = new HashMap<>();
    Map<String, List<Method>> methods = new HashMap<>();
    List<Constructor<?>> constructors;

    public ClassStore(Class<?> c) {
        this.c = c;
        loadConstructors();
        loadMethodsFunctions();
        loadFields();
    }

    public Class<?> getStoredClass() {
        return c;
    }

    public List<Constructor<?>> getConstructors() {
        return constructors;
    }

    public Constructor<?> getConstructor(Class<?>... args) throws NoSuchMethodException {
        for (Constructor<?> c : constructors) {
            if (Functions.isEqual(c.getParameterTypes(), args)) {
                return c;
            }
        }
        for (Constructor<?> c : constructors) {
            if (Functions.isCallable(c.getParameterTypes(), args)) {
                return c;
            }
        }
        throw new NoSuchMethodException();
    }

    public Map<String, List<Method>> getMethods() {
        return methods;
    }

    public Method getMethod(String name, Class<?>... args) throws NoSuchMethodException {
        return getMethod(methods, name, args);
    }


    public Map<String, List<Method>> getFunctions() {
        return functions;
    }

    public Method getFunction(String name, Class<?>... args) throws NoSuchMethodException {
        return getMethod(functions, name, args);
    }

    private Method getMethod(Map<String, List<Method>> src, String name, Class<?>... args) throws NoSuchMethodException {
        if (!src.containsKey(name))
            throw new NoSuchMethodException();
        for (Method method : src.get(name)) {
            if (Functions.isEqual(method.getParameterTypes(), args)) {
                return method;
            }
        }
        for (Method method : src.get(name)) {
            if (Functions.isCallable(method.getParameterTypes(), args)) {
                return method;
            }
        }
        throw new NoSuchMethodException();
    }

    private void loadConstructors() {
        List<Constructor<?>> list = new ArrayList<>();
        for (Constructor<?> constructor : c.getConstructors()) {
            for (Annotation anno : constructor.getAnnotations()) {
                if (anno instanceof UsableConstructor) {
                    list.add(constructor);
                }
            }
        }
        constructors = list;
    }

    private void loadMethodsFunctions() {
        for (Method method : c.getMethods()) {
            for (Annotation anno : method.getAnnotations()) {
                if (anno instanceof UsableMethod) {
                    String name = ((UsableMethod) anno).name();
                    if (!methods.containsKey(name)) {
                        methods.put(name, new ArrayList<>());
                    }
                    methods.get(name).add(method);
                }
                if (anno instanceof UsableFunction) {
                    String name = ((UsableFunction) anno).name();
                    if (!functions.containsKey(name)) {
                        functions.put(name, new ArrayList<>());
                    }
                    functions.get(name).add(method);
                }
            }
        }
    }

    private void loadFields() {
        for (Field field : c.getFields()) {
            for (Annotation anno : field.getAnnotations()) {
                if (anno instanceof UsableField) {
                    fields.put(((UsableField) anno).name(), field);
                }
            }
        }
    }



}
