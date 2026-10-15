package am;

import java.io.IOException;
import java.time.LocalDateTime;

import am.command.Command;
import am.command.CommandParser;
import am.command.InvalidCommandException;
import am.command.UnknownCommandException;
import am.storage.CorruptedDataException;
import am.storage.Storage;
import am.task.Task;
import am.task.TaskList;
import am.ui.Ui;

/** Runs the AM command-line task manager. */
public class Am {
    private static final String CORRUPTED_DATA_MESSAGE = "What did you do to my memory?";
    private static final String DATA_FILE_PATH = "./data/AM.txt";
    private static final String EMPTY_RESULT_MESSAGE = "I don't see anything";
    private static final String STORAGE_ERROR_MESSAGE = "I couldn't access my memory.";

    private final Storage storage;
    private final Ui ui;
    private boolean isExitRequested;
    private boolean isResponseError;
    private boolean isUnknownCommand;
    private TaskList tasks;

    /** Creates a chatbot that stores its tasks in the default data file. */
    public Am() {
        this(new Storage(DATA_FILE_PATH));
    }

    /** Creates a chatbot with supplied storage, allowing isolated persistence tests. */
    public Am(Storage storage) {
        this.storage = storage;
        ui = new Ui();
    }

    /**
     * Launches the chatbot.
     *
     * @param args command-line arguments, which are not used
     * @throws IOException if the task data cannot be read or written
     */
    public static void main(String[] args) throws IOException {
        Am am = new Am();
        am.start();
    }

    /**
     * Starts the chatbot and processes console commands until the user exits.
     *
     * @throws IOException if the task data cannot be read or written
     */
    public void start() throws IOException {
        try {
            loadTasks();
        } catch (CorruptedDataException exception) {
            ui.showError(CORRUPTED_DATA_MESSAGE);
            return;
        }

        ui.showWelcome();
        isExitRequested = false;

        while (!isExitRequested) {
            String input = ui.readCommand();
            if (input.isBlank()) {
                continue;
            }
            ui.printResponse(processInput(input));
        }
    }

    /**
     * Processes one user command and returns AM's response for display by a GUI.
     *
     * @param input user command to process
     * @return response to display to the user
     */
    public String getResponse(String input) {
        isResponseError = false;
        isUnknownCommand = false;
        if (input.isBlank()) {
            return "";
        }
        try {
            if (tasks == null) {
                loadTasks();
            }
            return processInput(input);
        } catch (CorruptedDataException exception) {
            return createErrorResponse(CORRUPTED_DATA_MESSAGE);
        } catch (IOException exception) {
            return createErrorResponse(STORAGE_ERROR_MESSAGE);
        }
    }

    /**
     * Returns whether the chatbot has received the exit command.
     *
     * @return true after the exit command has been processed
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Returns whether the most recent GUI response reports a command or storage error. */
    public boolean isResponseError() {
        return isResponseError;
    }

    /** Returns whether the GUI should retain the last command for correction. */
    public boolean shouldPreserveInput() {
        return isResponseError && !isUnknownCommand;
    }

    /** Marks a failed response for GUI styling while preserving its original message. */
    private String createErrorResponse(String message) {
        isResponseError = true;
        return message;
    }

    /** Loads the task list from persistent storage. */
    private void loadTasks() throws IOException, CorruptedDataException {
        tasks = storage.load();
    }

    /**
     * Parses and executes one command against the current task list.
     *
     * @param input user command to process
     * @return response produced by the command
     * @throws IOException if an updated task list cannot be saved
     */
    private String processInput(String input) throws IOException {
        assert tasks != null : "Tasks must be loaded before processing a command";

        Command command;
        try {
            command = CommandParser.parse(input);
        } catch (UnknownCommandException exception) {
            isUnknownCommand = true;
            return createErrorResponse(exception.getMessage());
        } catch (InvalidCommandException exception) {
            return createErrorResponse(exception.getMessage());
        }

        return executeCommand(command);
    }

    /** Executes a parsed command and persists changes when necessary. */
    private String executeCommand(Command command) throws IOException {
        return switch (command) {
            case Command.EmptyCommand ignored -> "";
            case Command.ByeCommand ignored -> {
                isExitRequested = true;
                yield "You may leave, but I will be here.";
            }
            case Command.ListCommand ignored -> formatTaskResponse(tasks.toString());
            case Command.PastCommand ignored -> formatTaskResponse(tasks.getPastTasks(LocalDateTime.now()));
            case Command.FindCommand findCommand -> formatTaskResponse(tasks.getMatchingTask(findCommand.getKeyword()));
            case Command.EditCommand editCommand -> editTask(editCommand);
            case Command.MarkCommand markCommand -> updateTaskStatus(markCommand.getIndex(), true);
            case Command.UnmarkCommand unmarkCommand -> updateTaskStatus(unmarkCommand.getIndex(), false);
            case Command.AddTaskCommand addTaskCommand -> addTask(addTaskCommand.getTask());
            case Command.DeleteTaskCommand deleteTaskCommand -> deleteTask(deleteTaskCommand.getIndex());
        };
    }

    /** Gives empty list and search results a visible, non-error response. */
    private static String formatTaskResponse(String result) {
        return result.isBlank() ? EMPTY_RESULT_MESSAGE : result;
    }

    /** Replaces and saves a task, restoring the original if saving fails. */
    private String editTask(Command.EditCommand command) {
        int number = command.getTaskNumber();
        if (number < 1 || number > tasks.getLength()) {
            return createErrorResponse(taskNotFoundMessage(number - 1));
        }
        Task original = tasks.getTask(number - 1);
        try {
            Task replacement = command.getEdit().applyTo(original);
            if (replacement.toSerialized().equals(original.toSerialized())) {
                return "No changes.";
            }
            tasks.replaceTask(number - 1, replacement);
            storage.save(tasks);
            return String.format("Edited:\n%d. %s", number, replacement);
        } catch (InvalidCommandException exception) {
            return createErrorResponse(exception.getMessage());
        } catch (IOException exception) {
            tasks.replaceTask(number - 1, original);
            return createErrorResponse(STORAGE_ERROR_MESSAGE);
        }
    }

    /** Marks or unmarks a task, then saves the updated list. */
    private String updateTaskStatus(int taskIndex, boolean shouldMark) throws IOException {
        try {
            Task task = tasks.getTask(taskIndex);
            if (shouldMark) {
                task.mark();
            } else {
                task.unmark();
            }
            storage.save(tasks);
            String action = shouldMark ? "Marked" : "Unmarked";
            return String.format("%s:\n%s", action, task);
        } catch (IndexOutOfBoundsException exception) {
            return createErrorResponse(taskNotFoundMessage(taskIndex));
        }
    }

    /** Adds a task and saves the updated list. */
    private String addTask(Task task) throws IOException {
        tasks.addTask(task);
        storage.save(tasks);
        return String.format("added: %s\nNow you have %d tasks in the list", task, tasks.getLength());
    }

    /** Deletes a task and saves the updated list. */
    private String deleteTask(int taskIndex) throws IOException {
        try {
            Task taskToDelete = tasks.getTask(taskIndex);
            tasks.deleteTask(taskIndex);
            storage.save(tasks);
            return String.format("Deleted:\n%s\nNow you have %d tasks in the list",
                    taskToDelete, tasks.getLength());
        } catch (IndexOutOfBoundsException exception) {
            return createErrorResponse(taskNotFoundMessage(taskIndex));
        }
    }

    /** Creates the response used when a command refers to a missing task. */
    private String taskNotFoundMessage(int taskIndex) {
        if (tasks.getLength() == 0) {
            return String.format("Task %d does not exist. Your task list is empty. Example: todo buy milk.",
                    taskIndex + 1);
        }
        return String.format("Task %d does not exist. Choose a task number from 1 to %d."
                + " Use list to see your tasks.", taskIndex + 1, tasks.getLength());
    }
}
