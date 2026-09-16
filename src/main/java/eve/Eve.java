package eve;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import eve.command.Command;
import eve.task.TaskList;

/**
 * Eve is a task-tracking chatbot. It turns each line of user input into a
 * {@link Command} via {@link Parser}, executes it against the current
 * {@link TaskList}, and saves changes via {@link Storage}. Tasks are loaded
 * from disk when an Eve instance is created.
 *
 * <p>This class is shared by both front ends: {@link #run} drives the
 * command-line loop (all output goes through {@link Ui} to the console, as
 * before), while {@link #getResponse} lets the JavaFX GUI (see
 * {@link eve.gui.MainWindow}) process one line of input at a time and get
 * back the chatbot's reply as a string instead of printed console output.
 */
public class Eve {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit = false;
    private boolean lastResponseWasError = false;

    /**
     * Creates an Eve instance, loading any previously saved tasks.
     *
     * @param filePath where tasks are loaded from and saved to, e.g. "data/eve.txt".
     */
    public Eve(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (EveException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the command-line chatbot: prints the greeting, then repeatedly
     * reads, parses, and executes one command per line until "bye" is
     * entered.
     */
    public void run() {
        ui.showWelcome();

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (EveException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Parses and executes one line of input, and returns the chatbot's
     * reply as plain text (with the console-only "____" divider lines
     * stripped out), for a GUI to display.
     *
     * @param input one line of user input, e.g. "todo read book".
     * @return the chatbot's response.
     */
    public String getResponse(String input) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        lastResponseWasError = false;
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (EveException e) {
            ui.showError(e.getMessage());
            lastResponseWasError = true;
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString().replaceAll("(?m)^_{5,}\\R?", "").strip();
    }

    /** Returns whether the last command processed by {@link #getResponse} was "bye". */
    public boolean isExit() {
        return isExit;
    }

    /** Returns whether the last response from {@link #getResponse} was an error message. */
    public boolean isLastResponseError() {
        return lastResponseWasError;
    }

    /**
     * Returns Eve's greeting and command summary, for the GUI to show as
     * its first chat message.
     *
     * @return the greeting and command summary.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Runs the command-line chatbot.
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        new Eve("data/eve.txt").run();
    }
}
