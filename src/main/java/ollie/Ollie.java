package ollie;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates Ollie's user interface, command parsing, task list, and storage.
 */
public class Ollie {
    private static final Path DATA_FILE_PATH = Path.of("data", "ollie.txt");

    private final Parser parser;
    private final Storage storage;
    private final UI ui;
    private TaskList tasks;

    /**
     * Creates an Ollie chatbot that stores tasks at the given file path.
     *
     * @param filePath Path of the task data file.
     */
    public Ollie(Path filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new UI();
    }

    /**
     * Starts Ollie's command-processing loop.
     */
    public void run() {
        ui.showWelcome();
        ui.showDivider();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandResult result = null;

            try {
                result = handleCommand(command);
                ui.showResponse(result.response());
            } catch (OllieException e) {
                ui.showError(e.getMessage());
            }

            ui.showDivider();
            if (result != null && result.isExit()) {
                break;
            }
        }
    }

    /**
     * Processes a command and returns Ollie's response for a graphical interface.
     *
     * @param command Command entered by the user.
     * @return User-facing response to the command.
     */
    public String getResponse(String command) {
        return getCommandResponse(command).message();
    }

    /**
     * Processes a command and reports the response state needed by a graphical interface.
     *
     * @param command Command entered by the user.
     * @return Response text together with its exit and error states.
     */
    public CommandResponse getCommandResponse(String command) {
        String commandText = command == null ? "" : command.trim();
        try {
            CommandResult result = handleCommand(commandText);
            return new CommandResponse(result.response(), result.isExit(), false);
        } catch (OllieException e) {
            return new CommandResponse(ui.getErrorMessage(e.getMessage()), false, true);
        }
    }

    /**
     * Returns whether the command ends the current chatbot session.
     *
     * @param command User command to inspect.
     * @return True if the command is the bye command.
     */
    public boolean isExitCommand(String command) {
        String commandText = command == null ? "" : command.trim();
        if (parser.parseCommand(commandText) != CommandType.BYE) {
            return false;
        }

        try {
            parser.validateNoArguments(commandText, "bye");
            return true;
        } catch (OllieException e) {
            return false;
        }
    }

    /**
     * Creates an Ollie chatbot that uses the default task data file.
     *
     * @return Ollie configured with the default storage path.
     */
    public static Ollie createDefault() {
        return new Ollie(DATA_FILE_PATH);
    }

    /**
     * Starts Ollie using the default task data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        createDefault().run();
    }

    /**
     * Performs the action represented by a user command.
     *
     * @param command Command entered by the user.
     * @return Response and exit state produced by the command.
     * @throws OllieException If the command is invalid or its action fails.
     */
    private CommandResult handleCommand(String command) throws OllieException {
        CommandType commandType = parser.parseCommand(command);

        switch (commandType) {
            case BYE:
                parser.validateNoArguments(command, "bye");
                return new CommandResult(ui.getGoodbyeMessage(), true);
            case HELP:
                parser.validateNoArguments(command, "help");
                return new CommandResult(ui.getHelpMessage(), false);
            case LIST:
                parser.validateNoArguments(command, "list");
                ensureTasksLoaded();
                return new CommandResult(ui.getTaskListMessage(tasks), false);
            case FIND:
                String keyword = parser.parseFindKeyword(command);
                ensureTasksLoaded();
                return new CommandResult(ui.getMatchingTasksMessage(tasks.find(keyword)), false);
            case MARK:
                ensureTasksLoaded();
                int markIndex = parser.parseTaskIndex(command, "mark", tasks.size());
                updateTaskStatus(markIndex, true);
                return new CommandResult(ui.getTaskMarkedMessage(), false);
            case UNMARK:
                ensureTasksLoaded();
                int unmarkIndex = parser.parseTaskIndex(command, "unmark", tasks.size());
                updateTaskStatus(unmarkIndex, false);
                return new CommandResult(ui.getTaskUnmarkedMessage(), false);
            case DELETE:
                ensureTasksLoaded();
                int deleteIndex = parser.parseTaskIndex(command, "delete", tasks.size());
                deleteTask(deleteIndex);
                return new CommandResult(ui.getTaskDeletedMessage(), false);
            case TODO, DEADLINE, EVENT, UNKNOWN:
                Task task = parser.parseTask(command);
                ensureTasksLoaded();
                addTask(task);
                return new CommandResult(ui.getTaskAddedMessage(), false);
            default:
                throw new OllieException("I don't recognize that command.");
        }
    }

    /**
     * Loads saved tasks before processing the first command.
     */
    private void ensureTasksLoaded() throws OllieException {
        if (tasks == null) {
            tasks = new TaskList(storage.load());
        }
    }

    /**
     * Adds and saves a task, restoring the previous list if saving fails.
     *
     * @param task Task to add.
     * @throws OllieException If the task is a duplicate or the task list cannot be saved.
     */
    private void addTask(Task task) throws OllieException {
        List<Task> previousTasks = tasks.getTasks();
        tasks.add(task);
        try {
            saveTasks();
        } catch (OllieException e) {
            tasks = new TaskList(previousTasks);
            throw e;
        }
    }

    /**
     * Updates and saves a task status, restoring the previous status if saving fails.
     *
     * @param index Zero-based index of the task to update.
     * @param isDone Whether the task should be marked as done.
     * @throws OllieException If the task list cannot be saved.
     */
    private void updateTaskStatus(int index, boolean isDone) throws OllieException {
        boolean wasDone = tasks.get(index).isDone();
        if (wasDone == isDone) {
            String status = isDone ? "done" : "undone";
            throw new OllieException("That task is already marked as " + status + ".");
        }

        if (isDone) {
            tasks.mark(index);
        } else {
            tasks.unmark(index);
        }

        try {
            saveTasks();
        } catch (OllieException e) {
            if (wasDone) {
                tasks.mark(index);
            } else {
                tasks.unmark(index);
            }
            throw e;
        }
    }

    /**
     * Deletes and saves a task, restoring the previous list if saving fails.
     *
     * @param index Zero-based index of the task to delete.
     * @throws OllieException If the task list cannot be saved.
     */
    private void deleteTask(int index) throws OllieException {
        List<Task> previousTasks = tasks.getTasks();
        tasks.delete(index);
        try {
            saveTasks();
        } catch (OllieException e) {
            tasks = new TaskList(previousTasks);
            throw e;
        }
    }

    /**
     * Saves the current task list.
     *
     * @throws OllieException If the task list cannot be saved.
     */
    private void saveTasks() throws OllieException {
        storage.save(tasks.getTasks());
    }

    /**
     * Contains a response and the state needed to display it in the GUI.
     *
     * @param message User-facing response.
     * @param isExit Whether the command ends the session.
     * @param isError Whether the response describes an error.
     */
    public record CommandResponse(String message, boolean isExit, boolean isError) {
    }

    /**
     * Contains the response and exit state produced by a command.
     *
     * @param response User-facing response.
     * @param isExit Whether the command ends a console session.
     */
    private record CommandResult(String response, boolean isExit) {
    }
}
