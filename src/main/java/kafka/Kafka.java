package kafka;

import java.io.IOException;

import kafka.command.CommandType;
import kafka.exception.CorruptedTaskDataException;
import kafka.exception.KafkaException;
import kafka.parser.DeadlineSnoozeRequest;
import kafka.parser.EventSnoozeRequest;
import kafka.parser.RenameRequest;
import kafka.parser.SnoozeRequest;
import kafka.parser.TaskParser;
import kafka.storage.InstanceLock;
import kafka.storage.TaskStorage;
import kafka.task.RenameResult;
import kafka.task.SnoozeResult;
import kafka.task.Task;
import kafka.task.TaskList;
import kafka.ui.Ui;

/**
 * Coordinates the user interface, command parser, task storage, and task list.
 */
public class Kafka {
    private static final String UNSUPPORTED_SNOOZE_ERROR =
            "This snooze request is not supported.";

    private final Ui ui;
    private final TaskStorage taskStorage;
    private TaskList tasks;
    private boolean isLoaded;
    private boolean isRecoveryPending;

    /**
     * Creates Kafka with its usual {@code data/kafka.txt} storage file.
     */
    public Kafka() {
        this(new TaskStorage());
    }

    /**
     * Creates Kafka with the specified task storage service.
     *
     * @param taskStorage Storage service Kafka should use for this session.
     */
    Kafka(TaskStorage taskStorage) {
        this.ui = new Ui();
        this.taskStorage = taskStorage;
        this.tasks = new TaskList();
        this.isLoaded = false;
    }

    /**
     * Starts a Kafka session.
     *
     * @param args Command-line arguments; Kafka does not currently use them.
     */
    public static void main(String[] args) {
        try (InstanceLock instanceLock = InstanceLock.acquire(new TaskStorage().getFilePath())) {
            new Kafka().run();
        } catch (KafkaException | IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Loads saved tasks and processes commands until the user says bye.
     */
    void run() {
        ui.showResponse(ui.formatGreeting());

        if (!canStartSession()) {
            ui.close();
            return;
        }

        while (true) {
            String input = ui.readCommand();
            CommandType command = CommandType.parseInput(input);
            if (command == CommandType.BYE) {
                break;
            }
            KafkaResponse response = getResponse(input);
            ui.showResponse(response.message());
        }

        ui.showResponse(ui.formatFarewell());
        ui.close();
    }

    /**
     * Loads stored tasks and safely handles a corrupted or unreadable file.
     *
     * @return {@code true} when Kafka can proceed to its command loop.
     */
    private boolean canStartSession() {
        try {
            ensureTasksLoaded();
            return true;
        } catch (CorruptedTaskDataException exception) {
            return canRecoverConsoleStorage(exception);
        } catch (KafkaException exception) {
            ui.showResponse(formatStorageError(exception));
            return false;
        }
    }

    /**
     * Prompts for recovery and reports whether the console session can continue.
     */
    private boolean canRecoverConsoleStorage(CorruptedTaskDataException exception) {
        isRecoveryPending = true;
        ui.showResponse(ui.formatError(exception.getMessage()));
        if (!ui.shouldOverwriteStorage(taskStorage.getFilePath())) {
            ui.showResponse(ui.formatStorageFileLocation(taskStorage.getFilePath()));
            return false;
        }

        KafkaResponse response = recoverStorage();
        ui.showResponse(response.message());
        return !response.isError();
    }

    /**
     * Dispatches a recognized command to the corresponding handler.
     *
     * @param command Recognized command type.
     * @param input Complete input containing any command arguments.
     * @param workingTasks Task list on which to apply the command.
     * @throws KafkaException If parsing, task handling, or saving fails.
     */
    private String processCommand(CommandType command, String input, TaskList workingTasks)
            throws KafkaException {
        assert isLoaded : "Tasks must be loaded before processing commands";
        assert command != null : "Command must be parsed before dispatch";
        assert command != CommandType.BYE
                : "BYE must be handled before command dispatch";
        return switch (command) {
            case LIST -> listTasks();
            case TODO -> addTodo(input, workingTasks);
            case DEADLINE -> addDeadline(input, workingTasks);
            case EVENT -> addEvent(input, workingTasks);
            case MARK -> markTask(input, workingTasks);
            case UNMARK -> unmarkTask(input, workingTasks);
            case DELETE -> deleteTask(input, workingTasks);
            case RENAME -> renameTask(input, workingTasks);
            case SNOOZE -> snoozeTask(input, workingTasks);
            case FIND -> findTasks(input);
            default -> handleUnknownCommand();
        };
    }

    /**
     * Displays all tasks in their current order.
     */
    private String listTasks() {
        return ui.formatTaskList(tasks);
    }

    /**
     * Parses and adds a todo.
     *
     * @param input Complete todo command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If parsing fails.
     */
    private String addTodo(String input, TaskList workingTasks) throws KafkaException {
        return addAndGetResponse(TaskParser.parseTodo(input), workingTasks);
    }

    /**
     * Parses and adds a deadline.
     *
     * @param input Complete deadline command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If parsing fails.
     */
    private String addDeadline(String input, TaskList workingTasks) throws KafkaException {
        return addAndGetResponse(TaskParser.parseDeadline(input), workingTasks);
    }

    /**
     * Parses and adds an event.
     *
     * @param input Complete event command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If parsing fails.
     */
    private String addEvent(String input, TaskList workingTasks) throws KafkaException {
        return addAndGetResponse(TaskParser.parseEvent(input), workingTasks);
    }

    /**
     * Adds a task to the working list and prepares its confirmation.
     *
     * @param task Parsed task to add.
     * @param workingTasks Task list to edit.
     */
    private String addAndGetResponse(Task task, TaskList workingTasks) {
        workingTasks.addTask(task);
        return ui.formatTaskAdded(task, workingTasks.size());
    }

    /**
     * Marks the task number supplied by the user.
     *
     * @param input Complete mark command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If the task number is invalid.
     */
    private String markTask(String input, TaskList workingTasks) throws KafkaException {
        int taskNumber = TaskParser.parseTaskNumber(input, CommandType.MARK.getKeyword());
        Task markedTask = workingTasks.markTask(taskNumber);
        return ui.formatTaskMarked(markedTask.display());
    }

    /**
     * Unmarks the task number supplied by the user.
     *
     * @param input Complete unmark command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If the task number is invalid.
     */
    private String unmarkTask(String input, TaskList workingTasks) throws KafkaException {
        int taskNumber = TaskParser.parseTaskNumber(input, CommandType.UNMARK.getKeyword());
        Task unmarkedTask = workingTasks.unmarkTask(taskNumber);
        return ui.formatTaskUnmarked(unmarkedTask.display());
    }

    /**
     * Deletes the task number supplied by the user.
     *
     * @param input Complete delete command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If the task number is invalid.
     */
    private String deleteTask(String input, TaskList workingTasks) throws KafkaException {
        int taskNumber = TaskParser.parseTaskNumber(input, CommandType.DELETE.getKeyword());
        Task deletedTask = workingTasks.deleteTask(taskNumber);
        return ui.formatTaskDeleted(deletedTask, workingTasks.size());
    }

    /**
     * Renames the task number supplied by the user.
     *
     * @param input Complete rename command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If the arguments or task number are invalid.
     */
    private String renameTask(String input, TaskList workingTasks) throws KafkaException {
        RenameRequest request = TaskParser.parseRename(input);
        RenameResult result = workingTasks.renameTask(request.taskNumber(), request.newName());
        return ui.formatTaskRenamed(result.oldDisplay(), result.newDisplay());
    }

    /**
     * Reschedules the deadline or event selected by the user.
     *
     * @param input Complete snooze command.
     * @param workingTasks Task list to edit.
     * @throws KafkaException If the arguments or selected task type are invalid.
     */
    private String snoozeTask(String input, TaskList workingTasks) throws KafkaException {
        SnoozeRequest request = TaskParser.parseSnooze(input);
        SnoozeResult result = applySnooze(request, workingTasks);
        return ui.formatTaskSnoozed(result.oldDisplay(), result.newDisplay());
    }

    /**
     * Applies a parsed deadline or event schedule change.
     *
     * @param request Parsed snooze request.
     * @param workingTasks Task list to edit.
     * @return Display snapshots from before and after rescheduling.
     * @throws KafkaException If the selected task has the wrong type.
     */
    private SnoozeResult applySnooze(SnoozeRequest request, TaskList workingTasks) throws KafkaException {
        if (request instanceof DeadlineSnoozeRequest deadlineRequest) {
            return workingTasks.snoozeDeadline(deadlineRequest.taskNumber(), deadlineRequest.newBy());
        }
        if (request instanceof EventSnoozeRequest eventRequest) {
            return workingTasks.snoozeEvent(
                    eventRequest.taskNumber(), eventRequest.newFrom(), eventRequest.newTo());
        }
        throw new KafkaException(UNSUPPORTED_SNOOZE_ERROR);
    }

    /**
     * Finds and displays tasks containing the keyword supplied by the user.
     * Searching does not change the task list, so no save is needed.
     *
     * @param input Complete find command.
     * @throws KafkaException If no search keyword was supplied.
     */
    private String findTasks(String input) throws KafkaException {
        String keyword = TaskParser.parseFindKeyword(input);
        return ui.formatMatchingTasks(tasks, tasks.findTasks(keyword));
    }

    /**
     * Tells the user that Kafka did not recognize their command.
     */
    private String handleUnknownCommand() {
        return ui.formatUnknownCommand();
    }

    /**
     * Processes one command and returns its display message and error status.
     *
     * @param input Complete command entered by the user.
     * @return Result containing the formatted message and its error status.
     */
    public KafkaResponse getResponse(String input) {
        String normalizedInput = input.stripLeading();
        CommandType command = CommandType.parseInput(normalizedInput);

        if (command == CommandType.BYE) {
            return new KafkaResponse(ui.formatFarewell(), false, KafkaResponse.Action.EXIT);
        }

        try {
            ensureTasksLoaded();
            String message = executeCommand(command, normalizedInput);
            boolean isError = command == CommandType.UNKNOWN;
            return new KafkaResponse(message, isError);
        } catch (CorruptedTaskDataException exception) {
            isRecoveryPending = true;
            return new KafkaResponse(formatStorageError(exception), true,
                    KafkaResponse.Action.CONFIRM_STORAGE_OVERWRITE);
        } catch (KafkaException exception) {
            String message = isLoaded ? ui.formatError(exception.getMessage()) : formatStorageError(exception);
            return new KafkaResponse(message, true);
        }
    }

    /**
     * Applies a command and publishes task changes only after they are saved successfully.
     * A copy keeps failed edits from changing the current in-memory tasks.
     */
    private String executeCommand(CommandType command, String input) throws KafkaException {
        TaskList workingTasks = command.isTaskModification() ? tasks.copy() : tasks;
        String message = processCommand(command, input, workingTasks);
        if (command.isTaskModification()) {
            taskStorage.save(workingTasks);
            tasks = workingTasks;
        }
        return message;
    }

    /**
     * Replaces a corrupted file with an empty list after the user confirms recovery.
     * A failed save leaves recovery pending so the user can retry.
     *
     * @return Recovery confirmation or an error explaining why recovery failed.
     */
    public KafkaResponse recoverStorage() {
        if (!isRecoveryPending) {
            return new KafkaResponse(ui.formatError("There is no corrupted file awaiting recovery."), true);
        }
        try {
            TaskList emptyTasks = new TaskList();
            taskStorage.save(emptyTasks);
            tasks = emptyTasks;
            isLoaded = true;
            isRecoveryPending = false;
            return new KafkaResponse(ui.formatStorageOverwritten(), false);
        } catch (KafkaException exception) {
            return new KafkaResponse(formatStorageError(exception), true);
        }
    }

    /**
     * Includes file repair instructions in the response shown by either interface.
     */
    private String formatStorageError(KafkaException exception) {
        return ui.formatError(exception.getMessage()) + "\n"
                + ui.formatStorageFileLocation(taskStorage.getFilePath());
    }

    /**
     * Loads saved tasks once before processing GUI commands.
     *
     * @throws KafkaException If the saved tasks cannot be loaded.
     */
    private void ensureTasksLoaded() throws KafkaException {
        if (isLoaded) {
            return;
        }
        tasks = taskStorage.load();
        isLoaded = true;
        isRecoveryPending = false;
    }

    /**
     * Returns the greeting to display when an interface starts.
     */
    public String greet() {
        return ui.formatGreeting();
    }
}
