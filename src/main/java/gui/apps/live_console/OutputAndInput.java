package gui.apps.live_console;

import Exceptions.interpreterExceptions.InterpreterException;
import gui.util.OutputApp;
import gui.util.Panel;
import util.interpreter.Interpreter;

import javax.swing.*;
import java.awt.*;

public class OutputAndInput extends Panel implements OutputApp {

    Interpreter interpreter;
    LiveConsole parent;

    JTextArea input = new JTextArea();
    JTextArea output = new JTextArea();
    JButton submit = new JButton("submit");
    JScrollPane outputContainer;

    public OutputAndInput(Interpreter interpreter, LiveConsole parent) {
        this.interpreter = interpreter;
        this.parent = parent;
        init();
    }


    private void init() {
        this.setLayout(new BorderLayout());

        output.setEditable(false);
        output.setLineWrap(true);
        output.setAutoscrolls(true);

        submit.addActionListener(actionEvent -> {
            String text = input.getText();
            if (!output.getText().endsWith("\n"))
                println("");
            for (String s : text.split("\n")) {
                println(">>> " + s);
            }
            execute(text);
        });

        Panel bottom = new Panel();
        bottom.setLayout(new BorderLayout());
        JPanel color = new JPanel();
        color.setPreferredSize(new Dimension((int) color.getPreferredSize().getWidth(), 2));
        color.setBackground(Color.BLUE);
        bottom.add(color, BorderLayout.NORTH);
        bottom.add(input, BorderLayout.CENTER);
        bottom.add(submit, BorderLayout.EAST);

        output.setTabSize(3);
        input.setTabSize(3);

        outputContainer = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(bottom, BorderLayout.SOUTH);
        this.add(outputContainer, BorderLayout.CENTER);
    }

    @Override
    public void println(Object o) {
        if (o == null)
            o = "null";
        output.setText(output.getText() + o.toString() + "\n");
    }

    @Override
    public void print(Object o) {
        if (o == null)
            o = "null";
        output.setText(output.getText() + o.toString());
    }

    @Override
    public void toNextFreeLine() {
        if (!output.getText().endsWith("\n"))
            println("");
    }

    protected void execute(String input) {
        try {
            interpreter.interpret(input);
        } catch (InterpreterException e) {
            parent.exceptionList.add(e);
        }
        parent.reload();
        this.input.setText("");
    }


}
