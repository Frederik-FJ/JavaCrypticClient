package gui.apps.live_console;

import Exceptions.interpreter.InterpreterException;
import gui.App;
import gui.util.OutputApp;
import util.file.ExecutionFile;
import util.interpreter.Interpreter;
import util.items.Device;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class LiveConsole extends App implements OutputApp {

    Device device;

    protected List<InterpreterException> exceptionList = new ArrayList<>();


    OutputAndInput outputAndInput;
    volatile Interpreter interpreter;

    public LiveConsole(ExecutionFile executionFile) {
        this.device = executionFile.getDevice();
        init();
        outputAndInput.execute(executionFile.getContent());
    }

    public LiveConsole(Device device) {
        this.device = device;
        init();
    }

    protected void init() {
        width = 400;
        height = 300;
        title = "Live Console";
        super.init();

        this.interpreter = new Interpreter(this, device);
        this.outputAndInput = new OutputAndInput(interpreter, this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Output & Input", outputAndInput);

        this.add(tabbedPane);

    }

    protected void reload() {

    }

    @Override
    public void println(Object o) {
        outputAndInput.println(o);
    }

    @Override
    public void print(Object o) {
        outputAndInput.print(o);
    }

    @Override
    public void toNextFreeLine() {
        outputAndInput.toNextFreeLine();
    }
}
