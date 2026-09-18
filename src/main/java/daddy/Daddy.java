package daddy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import daddy.command.Command;
import daddy.exception.DaddyException;
import daddy.parser.Parser;
import daddy.storage.Storage;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Coordinates Daddy's commands, task storage, and presentation interfaces.
 */
public class Daddy {
    /** Stores the tasks available during the current application session. */
    private final TaskList tasks;
    /** Saves tasks in the current file and migrates tasks from the legacy file when needed. */
    private final Storage storage;
    /** Converts user-entered command text into executable commands. */
    private final Parser parser;
    /** Stores messages produced while loading saved tasks. */
    private final List<String> loadingMessages;
    /** Records whether the user has entered the command that ends this session. */
    private boolean isExitRequested;
    /** Records whether the most recently processed command produced a user-facing error. */
    private boolean hasLastResponseError;

    /**
     * Creates Daddy using the standard project-relative task data files.
     */
    public Daddy() {
        this(new Storage(Path.of("data", "daddy.txt"), Path.of("data", "duke.txt")));
    }

    /**
     * Creates Daddy using supplied storage, primarily for alternate interfaces and tests.
     *
     * @param storage the storage used to load and save tasks
     */
    public Daddy(Storage storage) {
        this.tasks = new TaskList();
        this.storage = storage;
        this.parser = new Parser();
        this.loadingMessages = List.copyOf(storage.loadInto(tasks));
        this.isExitRequested = false;
        this.hasLastResponseError = false;
    }

    /**
     * Starts Daddy's command-line application.
     *
     * @param args command-line arguments, which Daddy does not use
     */
    public static void main(String[] args) {
        Daddy daddy = new Daddy();
        Ui ui = new Ui();
        daddy.showWelcome(ui);
        daddy.runChatLoop(ui);
    }

    /**
     * Returns the welcome and loading messages for a graphical conversation.
     *
     * @return the formatted welcome and loading messages
     */
    public String getWelcomeMessage() {
        return captureOutput(this::showWelcome);
    }

    /**
     * Processes one user command and returns Daddy's formatted response.
     *
     * @param input the complete command entered by the user
     * @return the response produced for the command
     */
    public String getResponse(String input) {
        return captureOutput(ui -> processCommand(input, ui));
    }

    /**
     * Returns whether the user has entered the command that ends this session.
     *
     * @return whether an exit command has been processed
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns whether the most recently processed command produced an error response.
     *
     * @return whether the latest command failed validation or execution
     */
    public boolean hasLastResponseError() {
        return hasLastResponseError;
    }

    /**
     * Returns the current tasks for a graphical interface to present.
     *
     * @return an unmodifiable snapshot of the current tasks in list order
     */
    public List<Task> getTasks() {
        List<Task> taskSnapshot = new ArrayList<>();
        for (Task task : tasks) {
            taskSnapshot.add(task);
        }
        return List.copyOf(taskSnapshot);
    }

    /**
     * Repeatedly reads and processes console commands until an exit command is received.
     *
     * @param ui the console interface used to read and display messages
     */
    private void runChatLoop(Ui ui) {
        while (!isExitRequested) {
            if (!ui.hasNextCommand()) {
                isExitRequested = true;
                ui.showExit();
                break;
            }
            processCommand(ui.readCommand(), ui);
        }
        ui.close();
    }

    /**
     * Displays the greeting and any task-loading messages through an interface.
     *
     * @param ui the interface used to display startup messages
     */
    private void showWelcome(Ui ui) {
        ui.showGreeting();
        for (String message : loadingMessages) {
            ui.showError(message);
        }
    }

    /**
     * Parses and executes one command, displaying a user-friendly error when necessary.
     *
     * @param input the complete command entered by the user
     * @param ui the interface used to display the command response
     */
    private void processCommand(String input, Ui ui) {
        hasLastResponseError = false;
        try {
            Command command = parser.parseCommand(input);
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
        } catch (DaddyException exception) {
            hasLastResponseError = true;
            ui.showError(exception.getMessage());
        }
    }

    /**
     * Captures messages shown through a temporary interface as one multi-line response.
     *
     * @param action the work that produces messages using the temporary interface
     * @return the captured response lines joined by the system line separator
     */
    private String captureOutput(Consumer<Ui> action) {
        List<String> lines = new ArrayList<>();
        action.accept(new Ui(lines::add));
        return String.join(System.lineSeparator(), lines);
    }
}
