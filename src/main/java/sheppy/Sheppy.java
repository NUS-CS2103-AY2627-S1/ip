package sheppy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import sheppy.storage.Storage;
import sheppy.task.Task;
import sheppy.task.TaskList;

/** Coordinates Sheppy's user interface, parser, task list, and storage. */
public class Sheppy {
    /** The default location used to save tasks. */
    private static final String DEFAULT_FILE_PATH = "data/tasks.txt";

    /** Suggests how to start when there are no tasks to display. */
    private static final String EMPTY_LIST_MESSAGE =
            "Your meadow is empty! Add a task with todo, deadline, or event.";

    /** Stores tasks between application sessions. */
    private final Storage storage;

    /** Contains the tasks in the current session. */
    private final TaskList tasks;

    /** Prevents overwriting a file that failed to load. */
    private final boolean storageLoaded;

    /** Contains a loading error to show after startup, or an empty string. */
    private final String startupMessage;

    /** Creates Sheppy using the default task-data file. */
    public Sheppy() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates Sheppy using a specified task-data file.
     *
     * @param filePath the path of the task-data file
     */
    public Sheppy(String filePath) {
        storage = new Storage(filePath);

        TaskList loadedTasks;
        String loadingMessage = "";
        try {
            loadedTasks = storage.load();
        } catch (SheppyException exception) {
            loadedTasks = new TaskList();
            loadingMessage = formatError(exception.getMessage());
        }
        tasks = loadedTasks;
        startupMessage = loadingMessage;
        storageLoaded = loadingMessage.isEmpty();
    }

    /**
     * Starts Sheppy's command loop.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Sheppy sheppy = new Sheppy();
        ui.showWelcome();
        if (!sheppy.getStartupMessage().isEmpty()) {
            ui.showResponse(sheppy.getStartupMessage());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showResponse(sheppy.getResponse(command));
            if (sheppy.isExitCommand(command)) {
                return;
            }
        }
    }

    /**
     * Returns any message produced while loading saved tasks.
     *
     * @return the startup error, or an empty string if loading succeeded
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Returns Sheppy's response to one user command.
     *
     * @param command the command entered by the user
     * @return Sheppy's response, including any validation error
     */
    public String getResponse(String command) {
        try {
            command = Parser.normalize(command);
            if (command.isEmpty()) {
                throw new SheppyException("please enter a command, such as list.");
            }
            CommandType commandType = Parser.parseCommand(command);
            if (!storageLoaded && (commandType == CommandType.LIST || commandType == CommandType.FIND)) {
                return formatError("your tasks are unavailable because the data file could not be loaded. "
                        + "Back up and repair the file, then restart Sheppy.");
            }
            return commandType.changesTasks()
                    ? executeChange(commandType, command)
                    : executeCommand(commandType, command);
        } catch (SheppyException exception) {
            return formatError(exception.getMessage());
        }
    }

    /** Executes and saves a change, restoring its snapshot if either operation fails. */
    private String executeChange(CommandType commandType, String command) throws SheppyException {
        if (!storageLoaded) {
            throw new SheppyException("your data could not be loaded. Back up and repair the data file, "
                    + "then restart Sheppy before changing tasks.");
        }
        TaskList.Snapshot snapshot = tasks.snapshot();
        try {
            if (commandType == CommandType.SORT && tasks.size() == 0) {
                return "Nothing to sort yet. " + EMPTY_LIST_MESSAGE;
            }
            String response = executeCommand(commandType, command);
            storage.save(tasks);
            return response;
        } catch (SheppyException exception) {
            snapshot.restore();
            throw exception;
        }
    }

    /** Dispatches a validated command to the operation responsible for it. */
    private String executeCommand(CommandType commandType, String command) throws SheppyException {
        return switch (commandType) {
            case BYE -> "Baa-bye! Keep your thoughts cozy and your tasks tidy.";
            case LIST -> formatTasks("Here are the tasks in your list:", tasks.asList());
            case FIND -> findTasks(command);
            case SORT -> sortTasks();
            case MARK -> updateTaskStatus(command, true);
            case UNMARK -> updateTaskStatus(command, false);
            case DELETE -> deleteTask(command);
            case TODO, DEADLINE, EVENT -> addTask(Parser.parseTask(command));
            case UNKNOWN -> throw Parser.unknownCommand();
        };
    }

    /**
     * Checks whether a command tells Sheppy to exit.
     *
     * @param command the command entered by the user
     * @return true if the command is {@code bye}
     */
    public boolean isExitCommand(String command) {
        return Parser.parseCommand(command) == CommandType.BYE;
    }

    /** Adds a task and returns a confirmation for the pending change. */
    private String addTask(Task task) {
        tasks.add(task);
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + formatTaskCount();
    }

    /** Updates a task's completion status and returns a confirmation. */
    private String updateTaskStatus(String command, boolean markDone) throws SheppyException {
        int taskNumber = Parser.parseTaskNumber(command);
        Task task = tasks.updateStatus(taskNumber, markDone);
        String confirmation = markDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return confirmation + "\n  " + task;
    }

    /** Deletes a task and returns a confirmation. */
    private String deleteTask(String command) throws SheppyException {
        int taskNumber = Parser.parseTaskNumber(command);
        Task deletedTask = tasks.remove(taskNumber);
        return "Noted. I've removed this task:\n"
                + "  " + deletedTask + "\n"
                + formatTaskCount();
    }

    /** Displays matching tasks or explains that the search returned no matches. */
    private String findTasks(String command) throws SheppyException {
        List<Task> matches = tasks.find(Parser.parseFindKeyword(command));
        return matches.isEmpty()
                ? "No matching tasks found. Try another keyword!"
                : formatTasks("Here are the matching tasks in your list:", matches);
    }

    /** Sorts tasks alphabetically and returns the sorted list. */
    private String sortTasks() {
        tasks.sortByDescription();
        return formatTasks("All sorted! Here are your tasks in alphabetical order:", tasks.asList());
    }

    /** Formats the task count using the appropriate singular or plural noun. */
    private String formatTaskCount() {
        return "Now you have " + tasks.size() + (tasks.size() == 1 ? " task" : " tasks") + " in the list.";
    }

    /** Formats a numbered collection of tasks under a heading. */
    private String formatTasks(String heading, List<Task> displayedTasks) {
        String lineSeparator = System.lineSeparator();
        String formattedTasks = IntStream.range(0, displayedTasks.size())
                .mapToObj(index -> (index + 1) + "." + displayedTasks.get(index))
                .collect(Collectors.joining(lineSeparator));
        return formattedTasks.isEmpty()
                ? heading + lineSeparator + EMPTY_LIST_MESSAGE
                : heading + lineSeparator + formattedTasks;
    }

    /** Formats an exception message using Sheppy's error prefix. */
    private String formatError(String message) {
        return "Baa-error: " + message;
    }
}
