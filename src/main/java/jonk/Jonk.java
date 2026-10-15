package jonk;

import java.time.format.DateTimeParseException;

/**
 * Coordinates user interaction, command parsing, task management, and storage.
 */
public class Jonk {
    private static final String FILE_PATH = "./data/jonk.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String startupError;

    /**
     * Creates a Jonk chatbot backed by the default data file.
     */
    public Jonk() {
        this(FILE_PATH);
    }

    /**
     * Creates a Jonk chatbot backed by the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Jonk(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        String loadError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (JonkException e) {
            loadError = e.getMessage();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        startupError = loadError;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();
        if (startupError != null) {
            ui.showError(startupError);
        }

        String input = ui.readCommand();
        while (!input.equals("bye")) {
            ui.showSeparator();
            ui.showResponse(getResponse(input));
            ui.showSeparator();
            input = ui.readCommand();
        }

        ui.showGoodbye();
    }

    /**
     * Starts Jonk using its default task data file.
     *
     * @param args Command-line arguments, which Jonk does not use.
     */
    public static void main(String[] args) {
        new Jonk(FILE_PATH).run();
    }

    /**
     * Returns the greeting displayed when the GUI opens.
     *
     * @return Greeting and any error encountered while loading saved tasks.
     */
    public String getWelcomeMessage() {
        if (startupError == null) {
            return ui.formatGreeting();
        }
        return ui.formatGreeting() + "\n" + startupError;
    }

    /**
     * Processes a command and returns the response for display in any user interface.
     *
     * @param input Complete command entered by the user.
     * @return Response to display to the user.
     */
    public String getResponse(String input) {
        if (input.equals("bye")) {
            return ui.formatFarewell();
        }

        try {
            return executeCommand(input);
        } catch (JonkException e) {
            return e.getMessage();
        } catch (DateTimeParseException e) {
            return "Navigation dates must use yyyy-MM-dd format.";
        }
    }

    /**
     * Delegates one user command to the appropriate application operation.
     *
     * @param input Complete command entered by the user.
     * @throws JonkException If the command is invalid or a storage operation fails.
     */
    private String executeCommand(String input) throws JonkException {
        String commandWord = Parser.parseCommandWord(input);

        return switch (commandWord) {
            case "help" -> {
                if (!input.trim().equals("help")) {
                    throw new JonkException(
                            "Extra signal detected. Type help on its own to open the mission guide.");
                }
                yield ui.formatHelp();
            }
            case "list" -> {
                if (!input.equals("list")) {
                    throw unclearSignalException();
                }
                yield ui.formatTaskList(tasks.asList());
            }
            case "mark" -> updateTaskStatus(input, true);
            case "unmark" -> updateTaskStatus(input, false);
            case "todo", "deadline", "event" -> addTask(input);
            case "delete" -> deleteTask(input);
            case "find" -> findTasks(input);
            default -> throw unclearSignalException();
        };
    }

    /**
     * Creates the shared response for an unrecognized transmission.
     *
     * @return Error that directs the user to the mission guide.
     */
    private JonkException unclearSignalException() {
        return new JonkException("Signal unclear. Type help to open the mission guide.");
    }

    /**
     * Finds and formats tasks whose descriptions contain the supplied keyword.
     *
     * @param input Find command entered by the user.
     * @return Matching-task response.
     * @throws JonkException If the command does not contain a keyword.
     */
    private String findTasks(String input) throws JonkException {
        String keyword = Parser.parseKeyword(input);
        return ui.formatMatchingTasks(tasks.find(keyword));
    }

    /**
     * Updates a task's completion status and restores it if saving fails.
     *
     * @param input Mark or unmark command entered by the user.
     * @param isDone New completion status.
     * @return Task-status response.
     * @throws JonkException If the task number is invalid or saving fails.
     */
    private String updateTaskStatus(String input, boolean isDone) throws JonkException {
        int taskNumber = Parser.parseTaskNumber(input);
        Task task = tasks.get(taskNumber);
        boolean previousStatus = task.isDone();

        setTaskStatus(task, isDone);
        try {
            storage.save(tasks.asList());
        } catch (JonkException e) {
            setTaskStatus(task, previousStatus);
            throw e;
        }

        return ui.formatTaskStatusUpdated(task, isDone);
    }

    /**
     * Adds a parsed task and removes it again if saving fails.
     *
     * @param input Add-task command entered by the user.
     * @return Task-added response.
     * @throws JonkException If the command is invalid or saving fails.
     */
    private String addTask(String input) throws JonkException {
        Task newTask = Parser.parseTask(input);
        tasks.add(newTask);

        try {
            storage.save(tasks.asList());
        } catch (JonkException e) {
            tasks.removeLast();
            throw e;
        }

        return ui.formatTaskAdded(newTask, tasks.size());
    }

    /**
     * Deletes a task and restores it to its former position if saving fails.
     *
     * @param input Delete command entered by the user.
     * @return Task-deleted response.
     * @throws JonkException If the task number is invalid or saving fails.
     */
    private String deleteTask(String input) throws JonkException {
        int taskNumber = Parser.parseTaskNumber(input);
        Task removedTask = tasks.delete(taskNumber);

        try {
            storage.save(tasks.asList());
        } catch (JonkException e) {
            tasks.restore(taskNumber, removedTask);
            throw e;
        }

        return ui.formatTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Applies a completion status using the behavior provided by {@link Task}.
     *
     * @param task Task whose status should change.
     * @param isDone Whether the task should be complete.
     */
    private void setTaskStatus(Task task, boolean isDone) {
        assert task != null : "Task to update should not be null";
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }
}
