package bott;

import bott.parser.Parser;
import bott.storage.Storage;
import bott.task.Task;
import bott.task.TaskList;
import bott.ui.Ui;

/**
 * The main entry point for the Bott chatbot. Holds the task list and the
 * supporting Ui, Storage, and Parser collaborators, and turns each line of
 * user input into a response.
 *
 * <p>Two interfaces drive Bott: {@link #run()} for the console (reading
 * from and printing to the terminal), and {@link #getResponse(String)} for
 * the JavaFX GUI (one input line in, one response string out).
 */
public class Bott {

    private static final String SAVED_TASKS_FILE_PATH = "./data/bott.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates a new Bott instance, loading any tasks previously saved to
     * {@code filePath}.
     *
     * @param filePath Path of the file tasks are loaded from and saved to.
     */
    public Bott(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /** Creates a new Bott instance backed by the default save file. */
    public Bott() {
        this(SAVED_TASKS_FILE_PATH);
    }

    /** Runs Bott's read-execute loop until the user types "bye". */
    public void run() {
        ui.showWelcome();
        String input = ui.readCommand();
        while (!isExitCommand(input)) {
            try {
                ui.showMessage(executeCommand(input));
            } catch (BottException exception) {
                ui.showError(exception.getMessage());
            }
            input = ui.readCommand();
        }
        ui.showGoodbye();
    }

    /**
     * Returns Bott's response to a single line of user input. Used by the
     * GUI, where each response becomes a dialog bubble. Unlike {@link #run()},
     * errors are returned as text rather than printed, and "bye" produces
     * the farewell message (the GUI closes its own window).
     *
     * @param input Line of input entered by the user.
     * @return Bott's response.
     */
    public String getResponse(String input) {
        if (isExitCommand(input)) {
            return ui.getGoodbyeMessage();
        }
        try {
            return executeCommand(input);
        } catch (BottException exception) {
            return ui.getErrorMessage(exception.getMessage());
        }
    }

    /** Returns Bott's greeting, shown when the GUI starts. */
    public String getGreeting() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns whether {@code input} is the command to exit Bott.
     *
     * @param input Line of input entered by the user.
     */
    public static boolean isExitCommand(String input) {
        return input.equals("bye");
    }

    /** Starts Bott's console interface, backed by {@link #SAVED_TASKS_FILE_PATH}. */
    public static void main(String[] args) {
        new Bott(SAVED_TASKS_FILE_PATH).run();
    }

    /**
     * Executes a single command entered by the user, other than "bye"
     * (which the caller handles separately).
     *
     * @param input Command entered by the user.
     * @return Bott's response to the command.
     * @throws BottException If {@code input} is not a recognized command,
     *         or is missing information the command needs.
     */
    private String executeCommand(String input) throws BottException {
        String command = Parser.getCommandWord(input);
        String args = Parser.getArguments(input);

        switch (command) {
            case "list":
                return ui.getTaskListMessage(tasks.getTasks());
            case "mark":
                return setTaskStatus("mark", args, true);
            case "unmark":
                return setTaskStatus("unmark", args, false);
            case "delete":
                return deleteTask(args);
            case "find":
                return findTasks(args);
            case "todo":
                return addTask(Parser.parseTodo(args));
            case "deadline":
                return addTask(Parser.parseDeadline(args));
            case "event":
                return addTask(Parser.parseEvent(args));
            case "duration":
                return addTask(Parser.parseFixedDuration(args));
            default:
                throw new BottException(
                    "That's not an order I recognize: \"" +
                        command +
                        "\". Try: list, todo, deadline, event, duration, find, mark, unmark," +
                        " delete, or bye."
                );
        }
    }

    /**
     * Stores a newly created task and returns Bott's acknowledgement.
     *
     * @param task Newly created task to store.
     * @return Acknowledgement that the task was added.
     * @throws BottException If the updated task list cannot be saved.
     */
    private String addTask(Task task) throws BottException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return ui.getTaskAddedMessage(task, tasks.size());
    }

    /**
     * Removes the task named in a "delete" command's arguments and returns
     * Bott's acknowledgement.
     *
     * @param args Text after the "delete" command word.
     * @return Acknowledgement that the task was removed.
     * @throws BottException If {@code args} does not name an existing task,
     *         or the updated task list cannot be saved.
     */
    private String deleteTask(String args) throws BottException {
        int taskNumber = Parser.parseTaskNumber("delete", args, tasks.size());
        Task removedTask = tasks.remove(taskNumber);
        storage.save(tasks.getTasks());
        return ui.getTaskDeletedMessage(removedTask, tasks.size());
    }

    /**
     * Returns the tasks whose description matches a "find" command's keyword.
     *
     * @param args Text after the "find" command word.
     * @return Message listing the matching tasks.
     * @throws BottException If {@code args} has no keyword.
     */
    private String findTasks(String args) throws BottException {
        String keyword = Parser.parseFind(args);
        return ui.getMatchingTasksMessage(tasks.find(keyword));
    }

    /**
     * Marks or unmarks the task named in a "mark"/"unmark" command's
     * arguments and returns Bott's response.
     *
     * @param commandName Command word the user typed, "mark" or "unmark",
     *         used to phrase error messages.
     * @param args Text after the command word.
     * @param isDone Whether the task should be marked as done.
     * @return Acknowledgement that the task's status changed.
     * @throws BottException If {@code args} does not name an existing task,
     *         or the updated task list cannot be saved.
     */
    private String setTaskStatus(String commandName, String args, boolean isDone)
        throws BottException {
        // Only the "mark" and "unmark" switch branches call this helper, and
        // it embeds commandName in its error text and pairs it with isDone;
        // any other value would mean a future edit misrouted a command here.
        assert commandName.equals("mark") || commandName.equals("unmark")
            : "expected mark/unmark, got: " + commandName;
        int taskNumber = Parser.parseTaskNumber(
            commandName,
            args,
            tasks.size()
        );
        Task task = tasks.get(taskNumber);
        String message;
        if (isDone) {
            task.markAsDone();
            message = ui.getTaskMarkedMessage(task);
        } else {
            task.markAsNotDone();
            message = ui.getTaskUnmarkedMessage(task);
        }
        storage.save(tasks.getTasks());
        return message;
    }
}
