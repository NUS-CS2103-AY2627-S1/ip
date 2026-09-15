package choo;

import java.nio.file.Path;

import choo.exception.ChooException;
import choo.parser.ParsedCommand;
import choo.parser.Parser;
import choo.storage.Storage;
import choo.task.Task;
import choo.task.TaskList;
import choo.ui.Ui;

/**
 * Coordinates the CHOO chatbot's command-processing workflow.
 */
public class Choo {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "choo.txt");

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    /**
     * Creates CHOO using the default task-data file and console formatter.
     */
    public Choo() {
        this(new Storage(DEFAULT_DATA_FILE), new Ui());
    }

    /**
     * Creates CHOO with its persistence and user-interface collaborators.
     *
     * @param storage Storage used to load and save tasks.
     * @param ui User interface used for console interaction.
     */
    public Choo(Storage storage, Ui ui) {
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Choo().run();
    }

    /**
     * Runs the chatbot using the supplied storage location.
     *
     * @param storage Storage used to load and save tasks.
     */
    public static void run(Storage storage) {
        new Choo(storage, new Ui()).run();
    }

    /**
     * Processes commands until input ends or the user exits.
     */
    public void run() {
        this.ui.showWelcome();

        try {
            loadTasks();
        } catch (ChooException exception) {
            this.ui.showError(exception.getMessage());
            return;
        }

        while (this.ui.hasNextCommand()) {
            String command = this.ui.readCommand();
            try {
                CommandResult result = executeCommand(Parser.parse(command));
                this.ui.showResponse(result.response());
                if (result.shouldExit()) {
                    break;
                }
            } catch (ChooException exception) {
                this.ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Processes one GUI command and returns the response to display.
     *
     * @param input Raw command entered by the user.
     * @return User-visible response produced by the command.
     */
    public String getResponse(String input) {
        try {
            loadTasks();
            return executeCommand(Parser.parse(input)).response();
        } catch (ChooException exception) {
            return this.ui.formatError(exception.getMessage());
        }
    }

    private void loadTasks() throws ChooException {
        if (this.tasks == null) {
            this.tasks = new TaskList(this.storage.load());
        }
    }

    /**
     * Executes a parsed command and reports whether the session should end.
     *
     * @param parsedCommand Command and associated details to execute.
     * @return Response text and whether the command ends the console session.
     * @throws ChooException If the command cannot be completed.
     */
    private CommandResult executeCommand(ParsedCommand parsedCommand) throws ChooException {
        assert parsedCommand != null : "Parsed command must exist before execution";
        assert this.tasks != null : "Task list must be loaded before command execution";

        switch (parsedCommand.getType()) {
            case BYE:
                return new CommandResult(this.ui.formatBye(), true);
            case LIST:
                return new CommandResult(this.ui.formatTaskList(this.tasks.asList()), false);
            case SORT:
                return new CommandResult(sortTasksByDeadline(), false);
            case MARK:
                return new CommandResult(updateTaskStatus(parsedCommand.getTaskNumber(), true), false);
            case UNMARK:
                return new CommandResult(updateTaskStatus(parsedCommand.getTaskNumber(), false), false);
            case DELETE:
                return new CommandResult(deleteTask(parsedCommand.getTaskNumber()), false);
            case FIND:
                return new CommandResult(
                        this.ui.formatMatchingTasks(this.tasks.find(parsedCommand.getKeyword())), false);
            case ADD:
                return new CommandResult(addTask(parsedCommand.getTask()), false);
            default:
                throw new AssertionError("Unhandled command type: " + parsedCommand.getType());
        }
    }

    /**
     * Updates and saves a task's completion status, restoring it if saving fails.
     *
     * @param taskNumber One-based position of the task to update.
     * @param isMarking Whether the task should be marked as completed.
     * @return Confirmation describing the updated task.
     * @throws ChooException If the position is invalid or the change cannot be saved.
     */
    private String updateTaskStatus(int taskNumber, boolean isMarking) throws ChooException {
        Task task = this.tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        if (isMarking) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            this.storage.save(this.tasks.asList());
        } catch (ChooException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        return this.ui.formatTaskStatusChanged(task, isMarking);
    }

    /**
     * Deletes and saves a task, reinserting it at its original position if saving fails.
     *
     * @param taskNumber One-based position of the task to delete.
     * @return Confirmation describing the deleted task.
     * @throws ChooException If the position is invalid or the deletion cannot be saved.
     */
    private String deleteTask(int taskNumber) throws ChooException {
        Task removedTask = this.tasks.remove(taskNumber);
        try {
            this.storage.save(this.tasks.asList());
        } catch (ChooException exception) {
            this.tasks.add(taskNumber, removedTask);
            throw exception;
        }
        return this.ui.formatDeletedTask(removedTask, this.tasks.size());
    }

    /**
     * Adds and saves a task, removing it again if saving fails.
     *
     * @param task Task to add.
     * @return Confirmation describing the added task.
     * @throws ChooException If the addition cannot be saved.
     */
    private String addTask(Task task) throws ChooException {
        this.tasks.add(task);
        try {
            this.storage.save(this.tasks.asList());
        } catch (ChooException exception) {
            this.tasks.remove(this.tasks.size());
            throw exception;
        }
        return this.ui.formatAddedTask(task, this.tasks.size());
    }

    /**
     * Sorts deadlines chronologically and saves the reordered task list.
     *
     * @return Confirmation followed by the sorted task list.
     * @throws ChooException If the reordered task list cannot be saved.
     */
    private String sortTasksByDeadline() throws ChooException {
        TaskList sortedTasks = this.tasks.sortedByDeadline();
        this.storage.save(sortedTasks.asList());
        this.tasks = sortedTasks;
        return this.ui.formatSortedTasks(this.tasks.asList());
    }

    /**
     * Couples a command's response with whether it ends the console session.
     */
    private record CommandResult(String response, boolean shouldExit) {
    }
}
