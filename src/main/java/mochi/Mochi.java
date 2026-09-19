package mochi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import mochi.parser.Parser;
import mochi.storage.Storage;
import mochi.task.Task;
import mochi.task.TaskList;
import mochi.ui.Ui;

/**
 * Coordinates Mochi's user interface, parser, task list, and storage.
 */
public class Mochi {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final List<String> loadingWarnings;
    private final boolean isLoadingFailed;
    private boolean isExitRequested;

    /**
     * Creates Mochi and loads its saved tasks.
     *
     * @param filePath Path of the file used to load and save tasks.
     */
    public Mochi(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        List<String> warnings;
        boolean isLoadingFailed;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
            warnings = storage.getLoadWarnings();
            isLoadingFailed = false;
        } catch (IOException e) {
            loadedTasks = new TaskList();
            warnings = new ArrayList<>();
            isLoadingFailed = true;
        }
        tasks = loadedTasks;
        loadingWarnings = warnings;
        this.isLoadingFailed = isLoadingFailed;
        isExitRequested = false;
    }

    /**
     * Runs the command loop until the user exits.
     */
    public void run() {
        ui.showWelcome();
        showLoadingMessages();

        while (!isExitRequested) {
            String input = ui.readCommand();
            ui.showSeparator();
            ui.showResponse(getResponse(input));
        }
    }

    /**
     * Processes one user command and returns Mochi's response.
     *
     * @param input Raw command entered by the user.
     * @return Mochi's response to the command.
     */
    public String getResponse(String input) {
        try {
            return execute(Parser.parse(input));
        } catch (MochiException e) {
            return ui.getErrorResponse(e);
        }
    }

    /**
     * Returns whether Mochi has received an exit command.
     *
     * @return True if the application should exit.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns messages about problems encountered while loading saved tasks.
     *
     * @return An immutable list of loading error and warning messages.
     */
    public List<String> getLoadingMessages() {
        ArrayList<String> messages = new ArrayList<>();
        if (isLoadingFailed) {
            messages.add(ui.getLoadingErrorResponse());
        }
        for (String warning : loadingWarnings) {
            messages.add(ui.getLoadingWarningResponse(warning));
        }
        return List.copyOf(messages);
    }

    private String execute(Parser.Command command) throws MochiException {
        assert command != null : "Parser must return a command";

        switch (command.getType()) {
            case LIST:
                return ui.getTaskListResponse(tasks);
            case ADD:
                assert command.getTask() != null : "Add commands must contain a task";
                tasks.add(command.getTask());
                return saveTasks() + ui.getTaskAddedResponse(command.getTask(), tasks.size());
            case DELETE:
                Task deletedTask = tasks.delete(command.getTaskNumber());
                return saveTasks() + ui.getTaskDeletedResponse(deletedTask, tasks.size());
            case MARK:
                Task markedTask = tasks.mark(command.getTaskNumber());
                return saveTasks() + ui.getTaskMarkedResponse(markedTask);
            case UNMARK:
                Task unmarkedTask = tasks.unmark(command.getTaskNumber());
                return saveTasks() + ui.getTaskUnmarkedResponse(unmarkedTask);
            case FIND:
                assert command.getKeyword() != null && !command.getKeyword().isBlank()
                        : "Find commands must contain a keyword";
                return ui.getMatchingTasksResponse(tasks.find(command.getKeyword()));
            case BYE:
                isExitRequested = true;
                return ui.getGoodbyeResponse();
            default:
                throw new AssertionError("Unsupported command type");
        }
    }

    private void showLoadingMessages() {
        if (isLoadingFailed) {
            ui.showLoadingError();
        }
        for (String warning : loadingWarnings) {
            ui.showLoadingWarning(warning);
        }
    }

    private String saveTasks() {
        try {
            storage.saveTasks(tasks.getTasks());
            return "";
        } catch (IOException e) {
            return ui.getSavingErrorResponse() + System.lineSeparator();
        }
    }

    /**
     * Starts Mochi using the default task data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Mochi(Path.of("data", "duke.txt")).run();
    }
}
