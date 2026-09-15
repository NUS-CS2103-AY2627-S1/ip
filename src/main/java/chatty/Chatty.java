package chatty;

import java.nio.file.Path;

import chatty.exception.ChattyException;
import chatty.parser.CommandType;
import chatty.parser.Parser;
import chatty.storage.Storage;
import chatty.task.RecurringTask;
import chatty.task.Task;
import chatty.task.TaskList;
import chatty.ui.Ui;

/**
 * Coordinates Chatty's user interface, command parsing, task list, and storage.
 */
public class Chatty {
    /** User interface used to read commands and display responses. */
    private final Ui ui;

    /** Storage used to load and save tasks. */
    private final Storage storage;

    /** Tasks managed during the current session. */
    private TaskList tasks;

    /** Warning produced when saved tasks cannot be loaded during startup. */
    private String startupWarning;

    /**
     * Creates Chatty with a task data file at the given path.
     *
     * @param filePath relative path of the task data file.
     */
    public Chatty(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    /** Runs Chatty until input ends or the user enters {@code bye}. */
    public void run() {
        ui.showGreeting();
        if (startupWarning != null) {
            ui.showResponse(startupWarning);
        }
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            ui.showResponse(getResponse(input));
            ui.showLine();
            if (shouldExit(input)) {
                return;
            }
        }
    }

    /**
     * Loads saved tasks, or starts with an empty list if loading fails.
     *
     * @return loaded tasks, or an empty task list when loading fails.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (ChattyException exception) {
            startupWarning = ui.formatError(exception.getMessage()
                    + " Task changes are disabled to protect your saved data."
                    + " Repair or move the data file, then restart Chatty.");
            return new TaskList();
        }
    }

    /**
     * Returns a warning produced while loading saved tasks, if loading failed.
     *
     * @return formatted startup warning, or {@code null} when loading succeeded.
     */
    public String getStartupWarning() {
        return startupWarning;
    }

    /**
     * Processes one user command and returns the response for any user interface.
     *
     * @param rawInput command entered by the user.
     * @return response produced after processing the command.
     */
    public String getResponse(String rawInput) {
        String input = rawInput.strip();
        CommandType command = Parser.parseCommand(input);
        try {
            return processCommand(input, command);
        } catch (ChattyException exception) {
            return ui.formatError(exception.getMessage());
        }
    }

    /**
     * Executes a parsed command and returns its formatted response.
     *
     * @param input normalized user input.
     * @param command command represented by the input.
     * @return formatted response produced by the command.
     * @throws ChattyException if the command arguments or storage operation are invalid.
     */
    private String processCommand(String input, CommandType command) throws ChattyException {
        requireWritableSession(command);
        switch (command) {
            case BYE:
                return ui.formatExit();
            case LIST:
                return ui.formatTaskList(tasks.getTasks());
            case FIND:
                return ui.formatMatchingTasks(tasks.find(Parser.parseFindKeyword(input)), tasks.getTasks());
            case MARK:
                return markTask(input);
            case UNMARK:
                return unmarkTask(input);
            case DELETE:
                return deleteTask(input);
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                // Fallthrough
            case RECURRING:
                return addTask(input, command);
            default:
                throw new ChattyException("OOPS!!! I don't recognise that command. "
                        + "Try todo, deadline, event, recurring, list, find, mark, unmark, "
                        + "delete, or bye.");
        }
    }

    /**
     * Prevents task changes after a failed startup load to protect saved data.
     *
     * @param command command about to be executed.
     * @throws ChattyException if a mutating command is attempted in a read-only session.
     */
    private void requireWritableSession(CommandType command) throws ChattyException {
        if (startupWarning == null) {
            return;
        }
        switch (command) {
            case TODO, DEADLINE, EVENT, RECURRING, MARK, UNMARK, DELETE:
                throw new ChattyException("OOPS!!! Task changes are disabled because saved data could not be loaded."
                        + " Repair or move the data file, then restart Chatty.");
            default:
                break;
        }
    }

    /**
     * Marks the selected task as done, saves the task list, and formats the response.
     *
     * @param input normalized mark command.
     * @return formatted task-marked response.
     * @throws ChattyException if the task number or storage operation is invalid.
     */
    private String markTask(String input) throws ChattyException {
        Task markedTask = tasks.mark(Parser.parseTaskNumber(input, CommandType.MARK));
        saveTasks();
        if (markedTask instanceof RecurringTask recurringTask) {
            return ui.formatRecurringTaskAdvanced(recurringTask);
        }
        return ui.formatTaskMarked(markedTask);
    }

    /**
     * Marks the selected task as not done, saves the task list, and formats the response.
     *
     * @param input normalized unmark command.
     * @return formatted task-unmarked response.
     * @throws ChattyException if the task number or storage operation is invalid.
     */
    private String unmarkTask(String input) throws ChattyException {
        Task unmarkedTask = tasks.unmark(Parser.parseTaskNumber(input, CommandType.UNMARK));
        saveTasks();
        return ui.formatTaskUnmarked(unmarkedTask);
    }

    /**
     * Deletes the selected task, saves the task list, and formats the response.
     *
     * @param input normalized delete command.
     * @return formatted task-deleted response.
     * @throws ChattyException if the task number or storage operation is invalid.
     */
    private String deleteTask(String input) throws ChattyException {
        Task deletedTask = tasks.delete(Parser.parseTaskNumber(input, CommandType.DELETE));
        saveTasks();
        return ui.formatTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Adds a parsed task, saves the task list, and formats the response.
     *
     * @param input normalized add command.
     * @param command type of task to add.
     * @return formatted task-added response.
     * @throws ChattyException if the task details or storage operation are invalid.
     */
    private String addTask(String input, CommandType command) throws ChattyException {
        Task addedTask = Parser.parseTask(input, command);
        tasks.add(addedTask);
        saveTasks();
        return ui.formatTaskAdded(addedTask, tasks.size());
    }

    /**
     * Saves the current task list.
     *
     * @throws ChattyException if the task list cannot be saved.
     */
    private void saveTasks() throws ChattyException {
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (ChattyException saveException) {
            try {
                tasks = new TaskList(storage.loadTasks());
            } catch (ChattyException loadException) {
                throw new ChattyException(saveException.getMessage()
                        + " I also couldn't restore the last saved task list.");
            }
            throw saveException;
        }
    }

    /**
     * Returns whether the supplied input is the command that closes Chatty.
     *
     * @param rawInput command entered by the user.
     * @return {@code true} when the normalized command is {@code bye}.
     */
    public boolean shouldExit(String rawInput) {
        return Parser.parseCommand(rawInput.strip()) == CommandType.BYE;
    }

    /**
     * Starts Chatty using its default relative data-file path.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Chatty(Path.of("data", "chatty.txt")).run();
    }
}
