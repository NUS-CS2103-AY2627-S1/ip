package alex;

import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import alex.exception.AlexException;
import alex.parser.CommandType;
import alex.parser.Parser;
import alex.storage.Storage;
import alex.storage.StorageException;
import alex.task.Task;
import alex.task.TaskList;
import alex.ui.Ui;

/**
 * Runs the Alex task manager.
 */
public class Alex {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;
    private final Clock clock;
    private final String loadingErrorMessage;

    /**
     * Creates Alex and loads its saved tasks.
     *
     * @param filePath path of the task data file.
     */
    public Alex(String filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /**
     * Creates Alex with a clock for evaluating time-dependent commands.
     *
     * @param filePath path of the task data file.
     * @param clock clock used to determine the current time.
     */
    Alex(String filePath, Clock clock) {
        this(new Storage(Path.of(filePath)), clock);
    }

    /**
     * Creates Alex with injected storage and a clock for testing.
     *
     * @param storage storage used to load and save tasks.
     * @param clock clock used to determine the current time.
     */
    Alex(Storage storage, Clock clock) {
        assert storage != null : "Storage cannot be null";
        assert clock != null : "Clock cannot be null";

        ui = new Ui();
        this.storage = storage;
        this.clock = clock;

        TaskList loadedTasks;
        String loadingErrorMessage = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (StorageException e) {
            loadedTasks = new TaskList();
            loadingErrorMessage = e.getMessage();
        }
        tasks = loadedTasks;
        this.loadingErrorMessage = loadingErrorMessage;
    }

    /**
     * Runs the command loop until the user exits or storage becomes unavailable.
     */
    public void run() {
        ui.showWelcome();
        if (loadingErrorMessage != null) {
            ui.showLoadingError(loadingErrorMessage);
            return;
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(command);

            try {
                String response = executeCommand(command, commandType);
                ui.showResponse(response);

                if (commandType == CommandType.BYE) {
                    break;
                }
            } catch (AlexException e) {
                ui.showError(e.getMessage());
            } catch (StorageException e) {
                ui.showError(e.getMessage());
                break;
            }
        }
    }

    /**
     * Processes a user command and returns Alex's response.
     *
     * @param input user command.
     * @return result containing Alex's response and command outcome.
     */
    public CommandResult getResponse(String input) {
        if (loadingErrorMessage != null) {
            return CommandResult.error(loadingErrorMessage
                    + "\nPlease repair or remove the data file, then restart Alex.");
        }

        String command = input.trim();
        CommandType commandType = Parser.parseCommandType(command);

        try {
            String response = executeCommand(command, commandType);
            return commandType == CommandType.BYE
                    ? CommandResult.exit(response)
                    : CommandResult.success(response);
        } catch (AlexException | StorageException e) {
            return CommandResult.error(e.getMessage());
        }
    }

    /**
     * Executes one parsed command and returns Alex's response.
     *
     * @param command full user command.
     * @param commandType parsed type of the command.
     * @return Alex's response.
     * @throws AlexException if the command is invalid.
     * @throws StorageException if the updated tasks cannot be saved.
     */
    private String executeCommand(String command, CommandType commandType)
            throws AlexException, StorageException {
        assert commandType == Parser.parseCommandType(command)
                : "Command type must match the command text";

        if (command.isBlank()) {
            throw new AlexException(
                    "You'll have to order something. I can't work with an empty cup.");
        }
        if (commandType != CommandType.UNKNOWN && !commandType.canAcceptArguments()) {
            Parser.validateNoArguments(command, commandType);
        }

        return switch (commandType) {
            case BYE -> "All right, closing your tab. Try not to leave your tasks on the table.";
            case LIST -> getTaskListResponse();
            case UPCOMING -> getUpcomingDeadlinesResponse();
            case MARK -> markTask(command);
            case UNMARK -> unmarkTask(command);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case TODO, DEADLINE, EVENT -> addTask(command, commandType);
            case UNKNOWN -> throw new AlexException(
                    "That's not on the menu. Try 'list', 'todo', 'deadline', or 'event'.");
        };
    }

    private String markTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.MARK, tasks.getSize());
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks();
        } catch (StorageException e) {
            setTaskCompletion(task, wasDone);
            throw e;
        }

        return "Done. One task served and off the counter:\n"
                + "   " + task;
    }

    private String unmarkTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.UNMARK, tasks.getSize());
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsUndone();
        try {
            saveTasks();
        } catch (StorageException e) {
            setTaskCompletion(task, wasDone);
            throw e;
        }

        return "Not finished? Fine. Back into the order queue it goes:\n"
                + "   " + task;
    }

    private String deleteTask(String command) throws AlexException, StorageException {
        int index = Parser.parseTaskIndex(command, CommandType.DELETE, tasks.getSize());
        Task removedTask = tasks.delete(index);
        try {
            saveTasks();
        } catch (StorageException e) {
            tasks.add(index, removedTask);
            throw e;
        }

        return "Canceled. I'll toss the order slip:\n"
                + "   " + removedTask + "\n"
                + "You've got " + tasks.getSize() + " item(s) left brewing.";
    }

    private String getTaskListResponse() {
        return formatTasks("Let me check the order slip. Here's what you've got:", tasks.getTasks());
    }

    private String getUpcomingDeadlinesResponse() {
        List<Task> upcomingDeadlines = tasks.findUpcomingDeadlines(LocalDateTime.now(clock));
        if (upcomingDeadlines.isEmpty()) {
            return "Nothing due in the next 24 hours. Slow shift, apparently.";
        }
        return formatTasks(
                "These are due in the next 24 hours. They're starting to steam:",
                upcomingDeadlines);
    }

    private String findTasks(String command) throws AlexException {
        String keyword = Parser.parseFindKeyword(command);
        List<Task> matchingTasks = tasks.find(keyword);
        if (matchingTasks.isEmpty()) {
            return "Nothing matching '" + keyword
                    + "'. Maybe it ordered under a different name.";
        }
        return formatTasks("Found these tucked behind the espresso machine:", matchingTasks);
    }

    private static String formatTasks(String heading, List<Task> tasksToFormat) {
        StringBuilder response = new StringBuilder(heading);

        for (int i = 0; i < tasksToFormat.size(); i++) {
            response.append("\n ")
                    .append(i + 1)
                    .append(".")
                    .append(tasksToFormat.get(i));
        }

        return response.toString();
    }

    private String addTask(String command, CommandType commandType)
            throws AlexException, StorageException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation commands can add tasks";

        Task task = Parser.parseTask(command, commandType);
        tasks.add(task);
        try {
            saveTasks();
        } catch (StorageException e) {
            tasks.delete(tasks.getSize() - 1);
            throw e;
        }

        String acknowledgement = switch (commandType) {
            case TODO -> "One task, house blend. Added to your order:";
            case DEADLINE -> "One deadline with an extra shot of urgency. Coming right up:";
            case EVENT -> "All right, one reservation for your schedule:";
            default -> throw new AssertionError("Unsupported task-creation command");
        };

        return acknowledgement + "\n"
                + "   " + task + "\n"
                + "You've got " + tasks.getSize() + " item(s) brewing.";
    }

    private void saveTasks() throws StorageException {
        storage.saveTasks(tasks.getTasks());
    }

    private static void setTaskCompletion(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }

    /**
     * Starts Alex using the default task data file.
     *
     * @param args command-line arguments, which are ignored.
     */
    public static void main(String[] args) {
        new Alex("data/alex.txt").run();
    }
}
