package gui.apps.terminal;

import gui.App;
import gui.util.OutputApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class CommandArea extends JPanel implements OutputApp {

    App app;

    JTextArea textAreaRead;
    JPanel commandLine;
    JTextField commandInput;
    JButton commitButton;

    public CommandArea(App app) {
        this.app = app;
        init();
    }

    public void init() {

        this.setSize(app.getSize());
        this.setBackground(Color.BLACK);
        this.setLocation(0, 0);

        this.setLayout(new BorderLayout());

        //textArea for for reading outputs and commands
        textAreaRead = new JTextArea();
        textAreaRead.setEditable(false);
        textAreaRead.setSelectedTextColor(Color.BLUE);
        textAreaRead.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        textAreaRead.setLineWrap(true);
        textAreaRead.setAutoscrolls(true);
        textAreaRead.setLocation(0, 0);
        JScrollPane jsp = new JScrollPane(textAreaRead);
        jsp.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        // FocusListener to give the focus to the CommandLine
        jsp.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                CommandArea.this.getFocus();
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
            }
        });

        textAreaRead.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                CommandArea.this.getFocus();
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
            }
        });
        this.add(jsp, BorderLayout.CENTER);

        // CommandLine for the input of commands
        commandLine = new JPanel();
        commandLine.setLayout(new BorderLayout());

        commandInput = new JTextField();

        commandInput.addActionListener(actionEvent -> execute());
        commandInput.requestFocus();
        commandLine.add(commandInput, BorderLayout.CENTER);

        commitButton = new JButton();
        commitButton.setText("send");
        commitButton.addActionListener(actionEvent -> execute());
        commandLine.add(commitButton, BorderLayout.EAST);


        this.add(commandLine, BorderLayout.SOUTH);

    }

    /**
     * write in the textArea
     *
     * @param content String to write
     */
    public void print(Object content) {
        if (content == null)
            content = "null";
        textAreaRead.setText(textAreaRead.getText() + content.toString());
    }

    /**
     * write in the textArea
     *
     * @param content String to write
     */
    public void println(Object content) {
        if (content == null)
            content = "null";
        textAreaRead.setText(textAreaRead.getText() + content.toString() + "\n");
    }

    private void execute() {
        execute(commandInput.getText());
        commandInput.setText("");
        commandInput.requestFocus();
    }

    public void execute(String command) {
        print(command + "\n");
        app.handleCommand(command);
    }

    /**
     * Method to give the Focus to the commandInputField
     */
    public void getFocus() {
        this.requestFocus();
        commandInput.requestFocus();
    }
}
