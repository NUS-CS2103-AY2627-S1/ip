package edith;

import java.io.IOException;

import edith.command.Command;
import edith.exception.EdithException;
import edith.parser.Parser;
import edith.storage.Storage;
import edith.task.TaskList;
import edith.ui.Ui;

/** Entry point for the Edith chatbot. */
public class Edith {
    private final StringBuilder responseBuffer;
    private final Ui ui;
    private boolean isExit;
    private boolean hasLoadingError;
    private TaskList tasks;

    /** Creates an Edith application with a console user interface. */
    public Edith() {
        this.ui = new Ui();
        this.responseBuffer = null;
    }

    private Edith(StringBuilder responseBuffer) {
        this.responseBuffer = responseBuffer;
        this.ui = new Ui(line -> responseBuffer.append(line).append(System.lineSeparator()));
        this.tasks = loadTasks();
    }

    /**
     * Creates an Edith instance whose responses can be displayed by a graphical interface.
     *
     * @return a chatbot configured to collect each response as text
     */
    public static Edith createForGui() {
        return new Edith(new StringBuilder());
    }

    /** Returns a loading error to display when the graphical window opens, if one occurred. */
    public String getStartupError() {
        if (responseBuffer == null) {
            throw new IllegalStateException("getStartupError is available only on GUI-configured instances.");
        }
        return responseBuffer.toString().stripTrailing();
    }

    /** Starts Edith's command-processing loop. */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();
        if (hasLoadingError) {
            return;
        }
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            try {
                Command parsedCommand = Parser.parse(command);
                parsedCommand.execute(tasks, ui);
                isExit = parsedCommand.isExit();
            } catch (EdithException e) {
                ui.showError(e);
            } finally {
                ui.showDivider();
            }
        }
    }

    /**
     * Processes one command and returns the text that Edith would display.
     *
     * @param input one complete command entered by the user
     * @return Edith's response without a trailing line separator
     */
    public String getResponse(String input) {
        if (responseBuffer == null) {
            throw new IllegalStateException("getResponse is available only on GUI-configured instances.");
        }
        responseBuffer.setLength(0);
        if (hasLoadingError) {
            ui.showLoadingError();
            return responseBuffer.toString().stripTrailing();
        }
        try {
            Command parsedCommand = Parser.parse(input);
            parsedCommand.execute(tasks, ui);
            isExit = parsedCommand.isExit();
        } catch (EdithException e) {
            ui.showError(e);
        }
        return responseBuffer.toString().stripTrailing();
    }

    /**
     * Returns whether the most recently processed command asked Edith to exit.
     *
     * @return {@code true} after a successful {@code bye} command
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts Edith using its default console and storage configuration.
     *
     * @param args command-line arguments, which Edith does not use
     */
    public static void main(String[] args) {
        new Edith().run();
    }

    /**
     * Loads saved tasks, starting with an empty list if the file does not exist or cannot be read.
     *
     * @return the restored tasks, or an empty list after a loading error
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(Storage.loadTasks());
        } catch (IOException e) {
            hasLoadingError = true;
            ui.showLoadingError();
            return new TaskList();
        }
    }
}
