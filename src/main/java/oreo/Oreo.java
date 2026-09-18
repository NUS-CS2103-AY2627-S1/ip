package oreo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import oreo.command.Command;
import oreo.core.AppContext;
import oreo.core.CommandFactory;
import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.TaskList;
import oreo.storage.Storage;
import oreo.ui.Ui;

/**
 * A simple command-line chatbot that stores and displays user-entered tasks.
 */
public class Oreo {
    private final Storage storage;
    private final Parser parser;
    private final Ui ui;
    private final TaskList tasks;
    private final AppContext context;
    private boolean wasLastCommandError;

    /** Creates an Oreo application using the default task file. */
    public Oreo() {
        storage = new Storage(Path.of("data", "oreo.txt"));
        parser = new Parser(new CommandFactory());
        ui = new Ui();
        tasks = new TaskList(storage.load());
        context = new AppContext(tasks, ui, storage);
    }

    /** Creates an Oreo application using the supplied UI implementation. */
    public Oreo(Ui ui) {
        storage = new Storage(Path.of("data", "oreo.txt"));
        parser = new Parser(new CommandFactory());
        this.ui = ui;
        tasks = new TaskList(storage.load());
        context = new AppContext(tasks, ui, storage);
    }

    /** Processes one command and returns the response shown by Oreo. */
    public String processCommand(String input) {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        PrintStream previous = System.out;
        wasLastCommandError = false;
        try {
            System.setOut(new PrintStream(response));
            try {
                Command command = parser.parse(input.trim());
                command.execute(context);
            } catch (OreoException e) {
                wasLastCommandError = true;
                ui.showError(e.getMessage());
            }
        } finally {
            System.setOut(previous);
        }
        return response.toString().stripTrailing();
    }

    /** Returns whether the most recently processed command produced an input error. */
    public boolean wasLastCommandError() {
        return wasLastCommandError;
    }

    /** Runs the command loop until an exit command is received. */
    public void run() {
        ui.showGreeting();

        // Reads commands from standard input.
        String userInput = "";
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            userInput = ui.readCommand();

            try {
                Command command = parser.parse(userInput);
                command.execute(context);
                isExit = command.isExit();
            } catch (OreoException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /** Starts the command-line application. */
    public static void main(String[] args) {
        new Oreo().run();
    }

}
