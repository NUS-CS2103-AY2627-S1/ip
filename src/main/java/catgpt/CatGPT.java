package catgpt;

import java.util.List;

/**
 * Coordinates CatGPT's user interface, parser, task list, and storage.
 */
public class CatGPT {
    private static final String DATA_FILE_PATH = "./data/catgpt.txt";

    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private final TaskList tasks;
    private final String startupErrorMessage;

    /**
     * Creates CatGPT using its default data-file location.
     */
    public CatGPT() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates CatGPT and loads tasks from the specified data file.
     *
     * @param filePath Path of the data file to load and save.
     */
    public CatGPT(String filePath) {
        this(new Storage(filePath));
    }

    /**
     * Creates CatGPT with a supplied storage implementation.
     *
     * @param storage Storage used to load and save tasks.
     */
    CatGPT(Storage storage) {
        assert storage != null : "Storage must not be null";
        this.storage = storage;
        ui = new Ui();
        parser = new Parser();

        TaskList loadedTasks;
        String loadErrorMessage = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (CatGPTException error) {
            loadedTasks = new TaskList();
            loadErrorMessage = ui.formatError(error.getMessage());
        }
        tasks = loadedTasks;
        startupErrorMessage = loadErrorMessage;
    }

    /**
     * Processes user commands until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (startupErrorMessage != null) {
            ui.showResponse(startupErrorMessage);
        }
        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                ui.showGoodbye();
                break;
            }

            try {
                Parser.ParsedCommand command = parser.parse(input);
                ui.showResponse(execute(command));
                if (command.getType() == Parser.CommandType.BYE) {
                    break;
                }
            } catch (CatGPTException error) {
                ui.showError(error.getMessage());
            }
        }
    }

    /**
     * Processes one user command and returns the response for a graphical UI.
     *
     * @param input User command to process.
     * @return User-facing response to the command.
     */
    public String getResponse(String input) {
        return getCommandResult(input).response();
    }

    /**
     * Processes one graphical-interface command and reports whether CatGPT should exit.
     *
     * @param input User command to process.
     * @return Command response and exit state.
     */
    public CommandResult getCommandResult(String input) {
        try {
            Parser.ParsedCommand command = parser.parse(input);
            String response = execute(command);
            boolean shouldExit = command.getType() == Parser.CommandType.BYE;
            return new CommandResult(response, shouldExit);
        } catch (CatGPTException error) {
            return new CommandResult(ui.formatError(error.getMessage()), false);
        }
    }

    /**
     * Returns CatGPT's greeting for a graphical UI.
     *
     * @return Greeting shown when the application starts.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns the storage error encountered during startup, if one occurred.
     *
     * @return Formatted startup error, or {@code null} if loading succeeded.
     */
    public String getStartupErrorMessage() {
        return startupErrorMessage;
    }

    /**
     * Executes one parsed command.
     *
     * @param command Parsed command to execute.
     * @return User-facing response to the command.
     * @throws CatGPTException If command execution or storage fails.
     */
    private String execute(Parser.ParsedCommand command) throws CatGPTException {
        assert command != null : "Command must be parsed before execution";
        return switch (command.getType()) {
            case BYE -> ui.getGoodbyeMessage();
            case LIST -> ui.formatTaskList(tasks);
            case MARK -> changeTaskStatus(command, true);
            case UNMARK -> changeTaskStatus(command, false);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case SORT -> sortTasks();
            case TODO, DEADLINE, EVENT -> addTask(command);
        };
    }

    private String addTask(Parser.ParsedCommand command) throws CatGPTException {
        Task task = parser.parseTask(command);
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (CatGPTException error) {
            tasks.delete(tasks.size() - 1);
            throw error;
        }
        return ui.formatTaskAdded(task, tasks.size());
    }

    private String changeTaskStatus(Parser.ParsedCommand command, boolean isDone)
            throws CatGPTException {
        int taskIndex = parser.parseTaskIndex(command, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            storage.save(tasks);
        } catch (CatGPTException error) {
            restoreTaskStatus(task, wasDone);
            throw error;
        }
        return ui.formatTaskStatusChanged(task, isDone);
    }

    /**
     * Restores a task's completion state after persistence fails.
     *
     * @param task Task whose state must be restored.
     * @param wasDone Completion state before the attempted change.
     */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    private String deleteTask(Parser.ParsedCommand command) throws CatGPTException {
        int taskIndex = parser.parseTaskIndex(command, tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (CatGPTException error) {
            tasks.restore(taskIndex, deletedTask);
            throw error;
        }
        return ui.formatTaskDeleted(deletedTask, tasks.size());
    }

    private String findTasks(Parser.ParsedCommand command) throws CatGPTException {
        String keyword = parser.parseKeyword(command);
        TaskList matchingTasks = tasks.find(keyword);
        return ui.formatMatchingTasks(matchingTasks);
    }

    private String sortTasks() throws CatGPTException {
        List<Task> previousOrder = tasks.snapshot();
        tasks.sortByDescription();
        try {
            storage.save(tasks);
        } catch (CatGPTException error) {
            tasks.restoreOrder(previousOrder);
            throw error;
        }
        return ui.formatSortedTaskList(tasks);
    }

    /**
     * Starts CatGPT using its default data-file location.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new CatGPT(DATA_FILE_PATH).run();
    }
}
