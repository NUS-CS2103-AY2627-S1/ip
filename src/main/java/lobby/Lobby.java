package lobby;

import java.io.IOException;

import lobby.exception.LobbyException;
import lobby.parser.Parser;
import lobby.storage.Storage;
import lobby.task.Task;
import lobby.task.TaskList;
import lobby.ui.ResponseFormatter;
import lobby.ui.Ui;

/**
 * Starts the Lobby chatbot application.
 */
public class Lobby {
    /**
     * Carries response text, error status, and whether the input should remain available for correction.
     *
     * @param text response shown to the user.
     * @param isError whether the command failed.
     * @param shouldRetainInput whether a recognized command failed and should remain editable.
     */
    public record Response(String text, boolean isError, boolean shouldRetainInput) {
    }

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;
    private final Storage.LoadResult loadResult;
    private final ResponseFormatter responseFormatter;

    /**
     * Creates a Lobby application backed by the given task file.
     *
     * @param filePath location used to load and save tasks.
     */
    public Lobby(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();
        this.loadResult = storage.load();
        this.tasks = new TaskList(loadResult.tasks());
        this.responseFormatter = new ResponseFormatter();
    }

    /**
     * Runs the command loop until the user exits or the input stream ends.
     */
    public void run() {
        ui.showWelcome();
        showLoadWarning();
        String command;
        while ((command = ui.readCommand()) != null) {
            ui.startResponse();
            Parser.Command commandType = parser.parseCommand(command);
            ui.showResponse(getResponse(command));
            ui.endResponse();
            if (commandType == Parser.Command.BYE) {
                return;
            }
        }
        ui.startResponse();
        ui.showFarewell();
    }

    /**
     * Processes one user command and returns Lobby's response.
     *
     * @param command complete command entered by the user.
     * @return Lobby's response to the command
     */
    public String getResponse(String command) {
        return processCommand(command).text();
    }

    /**
     * Executes a command once and returns its text together with its success or failure status.
     *
     * @param command complete command entered by the user.
     * @return response with an error flag for validation and storage failures
     */
    public Response processCommand(String command) {
        String normalizedCommand = command.trim();
        Parser.Command commandType = parser.parseCommand(normalizedCommand);
        try {
            String text = switch (commandType) {
                case BYE -> " Bye. Hope to see you again soon!";
                case LIST -> responseFormatter.formatTaskList(" Here are the tasks in your list:", tasks);
                case FIND -> handleFind(normalizedCommand);
                case MARK -> handleTaskCompletion(normalizedCommand, true);
                case UNMARK -> handleTaskCompletion(normalizedCommand, false);
                case TODO -> addTask(parser.parseTodo(normalizedCommand));
                case DEADLINE -> addTask(parser.parseDeadline(normalizedCommand));
                case EVENT -> addTask(parser.parseEvent(normalizedCommand));
                case DELETE -> handleDelete(normalizedCommand);
                default -> throw new LobbyException(
                        "Please use todo, deadline, event, list, find, mark, unmark, delete, or bye.");
            };
            return new Response(text, false, false);
        } catch (LobbyException e) {
            return new Response(" " + e.getMessage(), true, commandType != Parser.Command.UNKNOWN);
        }
    }

    /**
     * Returns the greeting and any warning produced while loading saved tasks.
     *
     * @return startup message for the graphical UI
     */
    public String getStartupMessage() {
        String greeting = "Hello! I'm Lobby." + System.lineSeparator() + "What can I do for you?";
        if (loadResult.readFailed()) {
            return greeting + System.lineSeparator()
                    + "I couldn't read data/lobby.txt, so I started with an empty task list.";
        }
        if (loadResult.skippedLines() > 0) {
            String lineWord = loadResult.skippedLines() == 1 ? "line" : "lines";
            return greeting + System.lineSeparator() + "I skipped " + loadResult.skippedLines()
                    + " invalid " + lineWord + " while loading data/lobby.txt.";
        }
        return greeting;
    }

    /**
     * Starts Lobby using its default task file.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Lobby("data/lobby.txt").run();
    }

    /**
     * Prints a warning when some or all saved tasks could not be loaded.
     */
    private void showLoadWarning() {
        if (loadResult.readFailed()) {
            ui.showLoadingError();
        } else if (loadResult.skippedLines() > 0) {
            ui.showSkippedLinesWarning(loadResult.skippedLines());
        }
    }

    /**
     * Attempts to save a mutation and reports a recoverable error to the user.
     *
     * @return {@code true} when the save succeeded
     */
    private boolean trySaveTasks() {
        try {
            storage.save(tasks.asList());
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Finds tasks matching the validated search criteria and formats the result.
     */
    private String handleFind(String command) throws LobbyException {
        TaskList matches = tasks.find(parser.parseFind(command));
        if (matches.size() == 0) {
            return " No matching tasks found.";
        }
        return responseFormatter.formatTaskList(" Here are the matching tasks in your list:", matches);
    }

    /**
     * Updates and saves completion status, restoring the previous status if saving fails.
     */
    private String handleTaskCompletion(String command, boolean isMarkingDone) throws LobbyException {
        String commandWord = isMarkingDone ? "mark" : "unmark";
        int taskNumber = parseExistingTaskNumber(command, commandWord);
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        setTaskCompletion(taskNumber, isMarkingDone);
        if (!trySaveTasks()) {
            setTaskCompletion(taskNumber, wasDone);
            throw new LobbyException(getSaveErrorMessage());
        }
        return isMarkingDone
                ? responseFormatter.formatTaskMarked(task)
                : responseFormatter.formatTaskUnmarked(task);
    }

    /**
     * Reads a task number and rejects numbers outside the current list.
     */
    private int parseExistingTaskNumber(String command, String commandWord) throws LobbyException {
        int taskNumber = parser.parseTaskNumber(command, commandWord);
        if (!tasks.containsTaskNumber(taskNumber)) {
            throw new LobbyException("Please enter the number of a task in the list.");
        }
        return taskNumber;
    }

    /**
     * Sets completion status for either a user command or a rollback.
     */
    private void setTaskCompletion(int taskNumber, boolean isDone) {
        if (isDone) {
            tasks.mark(taskNumber);
        } else {
            tasks.unmark(taskNumber);
        }
    }

    /**
     * Adds and saves a task, removing it again if saving fails.
     */
    private String addTask(Task task) throws LobbyException {
        int originalSize = tasks.size();
        tasks.add(task);
        if (!trySaveTasks()) {
            // Rollback removes the last task, which must be the exact object this command appended.
            assert tasks.get(tasks.size()) == task : "The unsaved task must remain at the end of the list";
            tasks.delete(tasks.size());
            // Rejecting an addition must restore the task count seen before the command.
            assert tasks.size() == originalSize : "Add rollback must restore the original task count";
            throw new LobbyException(getSaveErrorMessage());
        }
        return responseFormatter.formatTaskAdded(task, tasks.size());
    }

    /**
     * Deletes and saves a task, restoring its original position if saving fails.
     */
    private String handleDelete(String command) throws LobbyException {
        int taskNumber = parseExistingTaskNumber(command, "delete");
        Task removedTask = tasks.delete(taskNumber);
        if (!trySaveTasks()) {
            tasks.add(taskNumber, removedTask);
            // Restoring the same object at its old position preserves task numbering and completion state.
            assert tasks.get(taskNumber) == removedTask : "Delete rollback must restore the original task position";
            throw new LobbyException(getSaveErrorMessage());
        }
        return responseFormatter.formatTaskDeleted(removedTask, tasks.size());
    }

    private String getSaveErrorMessage() {
        return "I couldn't save your changes. Please check that data/lobby.txt is writable.";
    }

}
