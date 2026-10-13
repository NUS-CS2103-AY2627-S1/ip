package baymax;

import java.io.IOException;

import baymax.exception.BaymaxException;
import baymax.parser.Parser;
import baymax.storage.Storage;
import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.Task;
import baymax.task.TaskList;
import baymax.task.Todo;
import baymax.ui.Ui;

/**
 * Coordinates Baymax's command processing and task state.
 *
 * <p>The command-processing method is deliberately independent of a particular
 * user interface. The console UI and the JavaFX UI can therefore share exactly
 * the same parsing and task-management behavior.</p>
 */
public class Baymax {
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Describes the text response to a command and how an interface should present it.
     *
     * @param message the response text to display
     * @param shouldExit whether the command requested interface termination
     * @param isError whether the response describes a user-facing error
     */
    public record CommandResponse(String message, boolean shouldExit, boolean isError) {
    }

    /**
     * Creates Baymax using the application's default data file.
     */
    public Baymax() {
        this("./data/Baymax.txt");
    }

    /**
     * Creates Baymax using a specified data file.
     *
     * @param filePath the file used to load and save tasks
     */
    public Baymax(String filePath) {
        storage = new Storage(filePath);
        tasks = storage.load();
        assert tasks != null : "Storage.load should always return a TaskList.";
    }

    /**
     * Processes one command and returns the response for an interface to display.
     *
     * @param command the user's command
     * @return the command response and exit status
     */
    public CommandResponse processCommand(String command) {
        try {
            command = Parser.normalizeCommand(command);
            Parser.CommandType commandType = Parser.getCommandType(command);
            boolean changesTasks = switch (commandType) {
                case LIST, FIND, BYE -> false;
                default -> true;
            };
            if (storage.isReadOnly() && changesTasks) {
                return createConcernResponse(" Sorry, your care plan is read-only because loading failed. "
                        + "Repair the data file and restart Baymax before making changes.");
            }

            return switch (commandType) {
                case BYE -> new CommandResponse(
                        " I am satisfied with my care. Until next time.", true, false);
                case LIST -> new CommandResponse(formatTaskList(
                        " Here is your current care plan:", tasks), false, false);
                case MARK -> processMark(command, commandType);
                case UNMARK -> processUnmark(command, commandType);
                case DELETE -> processDelete(command, commandType);
                case FIND -> processFind(command);
                case TODO -> processTodo(command);
                case DEADLINE -> processDeadline(command);
                case EVENT -> processEvent(command);
            };
        } catch (BaymaxException exception) {
            return createConcernResponse(exception.getMessage());
        }
    }

    /**
     * Saves the current task list, or leaves the file untouched in read-only mode.
     *
     * @throws IOException if the task list cannot be written
     */
    public void saveTasks() throws IOException {
        if (storage.isReadOnly()) {
            // All mutations are blocked in this mode, so closing has nothing new to save.
            return;
        }
        storage.save(tasks);
    }

    /** Returns a warning when stored tasks could not be loaded safely. */
    public String getLoadWarning() {
        return storage.getLoadWarning();
    }

    private CommandResponse processMark(
            String command, Parser.CommandType commandType) {
        assert commandType == Parser.CommandType.MARK
                : "processMark should only be called for mark commands.";
        int taskIndex = Parser.parseTaskIndex(command, commandType);
        if (isValidTaskIndex(taskIndex)) {
            tasks.get(taskIndex).markAsDone();
            return new CommandResponse(formatTaskChange(
                    " Excellent. This task is complete:", tasks.get(taskIndex)), false, false);
        }
        return createConcernResponse(" Sorry, that task is not in your care plan.");
    }

    private CommandResponse processUnmark(
            String command, Parser.CommandType commandType) {
        assert commandType == Parser.CommandType.UNMARK
                : "processUnmark should only be called for unmark commands.";
        int taskIndex = Parser.parseTaskIndex(command, commandType);
        if (isValidTaskIndex(taskIndex)) {
            tasks.get(taskIndex).markAsUndone();
            return new CommandResponse(formatTaskChange(
                    " Understood. This task still requires care:", tasks.get(taskIndex)), false, false);
        }
        return createConcernResponse(" Sorry, that task is not in your care plan.");
    }

    private CommandResponse processDelete(
            String command, Parser.CommandType commandType) {
        assert commandType == Parser.CommandType.DELETE
                : "processDelete should only be called for delete commands.";
        int taskIndex = Parser.parseTaskIndex(command, commandType);
        if (isValidTaskIndex(taskIndex)) {
            Task removedTask = tasks.remove(taskIndex);
            return new CommandResponse(formatDeletedTask(removedTask), false, false);
        }
        return createConcernResponse(" Sorry, that task is not in your care plan.");
    }

    private CommandResponse processFind(String command) {
        String keyword = Parser.parseFindKeyword(command);
        assert keyword != null && !keyword.isBlank()
                : "Parser should return a non-blank find keyword.";
        return new CommandResponse(formatTaskList(
                " I found these tasks in your care plan:", tasks.find(keyword)), false, false);
    }

    private CommandResponse processTodo(String command) {
        String description = Parser.parseTodoDescription(command);
        Task task = new Todo(description);
        tasks.add(task);
        return new CommandResponse(formatAddedTask(task), false, false);
    }

    private CommandResponse processDeadline(String command) {
        Parser.DeadlineDetails deadline = Parser.parseDeadline(command);
        Task task = new Deadline(deadline.description(), deadline.dueDate());
        tasks.add(task);
        return new CommandResponse(formatAddedTask(task), false, false);
    }

    private CommandResponse processEvent(String command) {
        Parser.EventDetails event = Parser.parseEvent(command);
        Task task = new Event(
                event.description(), event.startDate(), event.endDate());
        tasks.add(task);
        return new CommandResponse(formatAddedTask(task), false, false);
    }

    private boolean isValidTaskIndex(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size();
    }

    private CommandResponse createConcernResponse(String message) {
        assert message != null && !message.isBlank()
                : "A concern response should contain an explanation.";
        return new CommandResponse(
                " I have some concerns." + System.lineSeparator() + message,
                false,
                true);
    }

    private String formatTaskList(String heading, TaskList taskList) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < taskList.size(); i++) {
            response.append(System.lineSeparator())
                    .append(" ")
                    .append(i + 1)
                    .append(".")
                    .append(taskList.get(i));
        }
        return response.toString();
    }

    private String formatTaskChange(String heading, Task task) {
        return heading + System.lineSeparator() + "   " + task;
    }

    private String formatAddedTask(Task task) {
        return " I have added this task to your care plan:" + System.lineSeparator()
                + "   " + task + System.lineSeparator()
                + formatTaskCount();
    }

    private String formatDeletedTask(Task task) {
        return " This task is no longer under my care:" + System.lineSeparator()
                + "   " + task + System.lineSeparator()
                + formatTaskCount();
    }

    private String formatTaskCount() {
        String taskNoun = tasks.size() == 1 ? "task" : "tasks";
        return " You now have " + tasks.size() + " " + taskNoun + " under my care.";
    }

    /**
     * Starts Baymax's text user interface.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        Baymax baymax = new Baymax();
        Ui ui = new Ui();

        ui.showWelcome();
        if (!baymax.getLoadWarning().isEmpty()) {
            ui.showResponse(baymax.getLoadWarning());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            CommandResponse response = baymax.processCommand(command);
            ui.showResponse(response.message());

            if (response.shouldExit()) {
                try {
                    baymax.saveTasks();
                } catch (IOException exception) {
                    ui.showSaveError();
                    ui.showResponse(exception.getMessage());
                    ui.showSeparator();
                    continue;
                }
                ui.close();
                ui.showSeparator();
                return;
            }

            ui.showSeparator();
        }
        try {
            baymax.saveTasks();
        } catch (IOException exception) {
            ui.showSaveError();
            ui.showResponse(exception.getMessage());
        }
        ui.close();
    }
}
