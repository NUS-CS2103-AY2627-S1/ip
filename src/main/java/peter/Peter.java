package peter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import peter.command.Command;
import peter.storage.Storage;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Coordinates the task list, storage, and console UI for the chatbot.
 */
public class Peter {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /** Loading failure retained so it can be displayed after the welcome message. */
    private final PeterException loadingError;

    /** Set once a command asks the application to stop. */
    private boolean isExitRequested;

    /**
     * Creates the chatbot and loads tasks from the given data file.
     *
     * @param filePath path to the task data file.
     */
    public Peter(String filePath) {
        this.storage = new Storage(filePath);
        this.ui = new Ui();

        TaskList loadedTasks;
        PeterException loadError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (PeterException e) {
            loadedTasks = new TaskList();
            loadError = e;
        }
        this.tasks = loadedTasks;
        this.loadingError = loadError;

        // run() and the graphical interface both refuse to run commands once
        // loading has failed, so that a half-loaded list is never saved back
        // over the user's data file. That is only safe if a failed load left
        // no tasks behind at all.
        assert loadingError == null || tasks.size() == 0 : "a failed load must leave the task list empty";
    }

    /**
     * Starts the console interaction loop.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError.getMessage());
            ui.showDivider();
            return;
        }

        // The extra hasNextCommand check stops the loop when input runs out
        // without a bye command, which happens when input is piped in.
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showDivider();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (PeterException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showDivider();
            }
        }
    }

    /**
     * Returns the greeting to show when the chatbot starts.
     *
     * <p>Unlike the console welcome, this omits the banner and dividers, which
     * only make sense in a fixed-width terminal.
     *
     * @return greeting text.
     */
    public String getGreeting() {
        return capture(Ui::showGreeting);
    }

    /**
     * Returns whether the saved tasks failed to load at startup.
     */
    public boolean hasLoadingError() {
        return loadingError != null;
    }

    /**
     * Returns the message explaining why the saved tasks could not be loaded.
     *
     * @return loading error message, or an empty string if loading succeeded.
     */
    public String getLoadingErrorMessage() {
        return hasLoadingError() ? loadingError.getMessage() : "";
    }

    /**
     * Runs one command and returns what it would have printed.
     *
     * <p>This is the entry point for a caller that displays responses itself,
     * such as a graphical interface. Errors are reported in the returned text
     * rather than thrown, so every input produces something to display.
     *
     * @param input complete command entered by the user.
     * @return text of the response.
     */
    public String getResponse(String input) {
        return capture(ui -> {
            try {
                Command command = Parser.parse(input);
                command.execute(tasks, ui, storage);
                isExitRequested = command.isExit();
            } catch (PeterException e) {
                ui.showError(e.getMessage());
            }
        });
    }

    /**
     * Returns whether the last command executed asked the application to stop.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Runs a UI action against a buffer and returns everything it wrote.
     *
     * <p>The buffer is read back as UTF-8 so that the captured text does not
     * depend on the platform's default character encoding.
     *
     * @param action action to perform on the buffering UI.
     * @return captured text, without its trailing newline.
     */
    private static String capture(Consumer<Ui> action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        action.accept(new Ui(new PrintStream(buffer, true, StandardCharsets.UTF_8)));
        return buffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Starts the chatbot using the default data file.
     *
     * <p>The path is relative, so tasks are saved under the directory the
     * program is launched from.
     *
     * @param args ignored; the data file location is fixed.
     */
    public static void main(String[] args) {
        new Peter("data/peter.txt").run();
    }
}
