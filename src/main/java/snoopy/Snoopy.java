package snoopy;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import snoopy.command.CommandType;
import snoopy.exception.SnoopyException;
import snoopy.parser.Parser;
import snoopy.storage.Storage;
import snoopy.task.Task;
import snoopy.task.TaskList;
import snoopy.ui.Ui;

/**
 * Processes Snoopy commands for both the text and graphical user interfaces.
 */
public class Snoopy {
    private static final String DISPLAY_NAME = "Snoopy";
    private static final String WELCOME_MESSAGE = "Hi! I'm Snoopy, your happy little helper.\n"
            + "What can I do for you?";

    private final Storage storage;
    private final TaskList tasks;
    private final String startupError;
    private boolean shouldExit;

    /**
     * Creates Snoopy using the standard data-file location.
     */
    public Snoopy() {
        this(new Storage());
    }

    /**
     * Creates Snoopy with the supplied storage implementation.
     *
     * @param storage Storage used to load and save tasks.
     */
    public Snoopy(Storage storage) {
        assert storage != null : "Storage must be provided";
        this.storage = storage;

        TaskList loadedTasks;
        String loadError = null;
        try {
            loadedTasks = storage.load();
        } catch (SnoopyException exception) {
            loadedTasks = new TaskList();
            loadError = " OOPS! " + exception.getMessage();
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            loadError = " OOPS! I couldn't load the saved tasks.";
        }
        assert loadedTasks != null : "Storage must return a task list";
        tasks = loadedTasks;
        startupError = loadError;
    }

    /**
     * Runs the text interface until the user enters {@code bye}.
     *
     * @param args Command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Snoopy snoopy = new Snoopy();
        Ui ui = new Ui();
        ui.showWelcome(snoopy.getWelcomeMessage());
        while (ui.hasNextCommand() && !snoopy.shouldExit()) {
            ui.showResponse(snoopy.getResponse(ui.readCommand()));
        }
    }

    /**
     * Returns the greeting shown when the text interface starts.
     *
     * @return Greeting and any storage-loading error.
     */
    public String getWelcomeMessage() {
        if (startupError == null) {
            return WELCOME_MESSAGE;
        }
        return WELCOME_MESSAGE + "\n\n" + startupError.stripLeading();
    }

    /**
     * Returns Snoopy's greeting for the graphical interface.
     *
     * @return Graphical greeting and any storage-loading error.
     */
    public String getGuiWelcomeMessage() {
        return getWelcomeMessage();
    }

    /**
     * Returns the chatbot name displayed by the graphical interface.
     *
     * @return Snoopy's display name.
     */
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * Processes one command and returns the response to show the user.
     *
     * @param input Command entered by the user.
     * @return Snoopy's response, which can contain multiple lines.
     */
    public String getResponse(String input) {
        String command = Parser.normalizeInput(input);
        CommandType commandType = Parser.parseCommandType(command);
        assert commandType == CommandType.UNKNOWN
                || command.startsWith(commandType.getKeyword())
                : "Recognized commands must start with their keyword";

        try {
            return executeCommand(command, commandType);
        } catch (SnoopyException exception) {
            return " OOPS! " + exception.getMessage();
        } catch (IOException exception) {
            return " OOPS! I couldn't save the task list.";
        }
    }

    /**
     * Dispatches a recognized command to its command-specific handler.
     *
     * @param command Complete trimmed command.
     * @param commandType Type identified from the command.
     * @return Response to show the user.
     * @throws SnoopyException If the command arguments are invalid.
     * @throws IOException If a task-list change cannot be saved.
     */
    private String executeCommand(String command, CommandType commandType)
            throws SnoopyException, IOException {
        switch (commandType) {
            case BYE:
                shouldExit = true;
                return " Bye. Hope to see you again soon!";
            case LIST:
                return getTaskListResponse();
            case UNMARK:
                return unmarkTask(command, commandType);
            case MARK:
                return markTask(command, commandType);
            case DELETE:
                return deleteTask(command, commandType);
            case FIND:
                return getFindResponse(command, commandType);
            case UPDATE:
                return updateTask(command, commandType);
            case TODO:
            case DEADLINE:
            case EVENT:
                return saveNewTask(Parser.parseTask(command, commandType));
            case UNKNOWN:
                throw new SnoopyException(
                        "Sorry, I don't recognize that command. Available commands: "
                                + CommandType.getCommandSummary() + ".");
            default:
                throw new IllegalStateException("Unexpected command type: " + commandType);
        }
    }

    /**
     * Marks the task selected by an unmark command as not done.
     *
     * @param command Complete unmark command.
     * @param commandType Unmark command metadata.
     * @return Confirmation shown to the user.
     * @throws SnoopyException If the task number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String unmarkTask(String command, CommandType commandType)
            throws SnoopyException, IOException {
        int taskIndex = getTaskIndex(command, commandType);
        Task task = tasks.get(taskIndex);
        updateTaskStatus(task, false);
        return formatLines(
                " OK, I've marked this task as not done yet:",
                "   " + task);
    }

    /**
     * Marks the task selected by a mark command as done.
     *
     * @param command Complete mark command.
     * @param commandType Mark command metadata.
     * @return Confirmation shown to the user.
     * @throws SnoopyException If the task number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String markTask(String command, CommandType commandType)
            throws SnoopyException, IOException {
        int taskIndex = getTaskIndex(command, commandType);
        Task task = tasks.get(taskIndex);
        updateTaskStatus(task, true);
        return formatLines(
                " Nice! I've marked this task as done:",
                "   " + task);
    }

    /**
     * Removes the task selected by a delete command.
     *
     * @param command Complete delete command.
     * @param commandType Delete command metadata.
     * @return Confirmation shown to the user.
     * @throws SnoopyException If the task number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String deleteTask(String command, CommandType commandType)
            throws SnoopyException, IOException {
        int taskIndex = getTaskIndex(command, commandType);
        Task removedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        return formatLines(
                " Noted. I've removed this task:",
                "   " + removedTask,
                " Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Extracts and validates the task number from a task-selection command.
     *
     * @param command Complete task-selection command.
     * @param commandType Command metadata containing the keyword.
     * @return Zero-based index of the selected task.
     * @throws SnoopyException If the task number is invalid.
     */
    private int getTaskIndex(String command, CommandType commandType) throws SnoopyException {
        String numberText = Parser.getArguments(command, commandType);
        return Parser.parseTaskIndex(numberText, tasks.size(), commandType);
    }

    /**
     * Checks whether the last command requested that the application close.
     *
     * @return {@code true} after a valid {@code bye} command.
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Formats every task for the list command.
     *
     * @return Response containing the task list.
     */
    private String getTaskListResponse() {
        return " Here are the tasks in your list:" + formatNumberedTasks(tasks);
    }

    /**
     * Changes a task's completion state and restores it if saving fails.
     *
     * @param task Task whose state should change.
     * @param shouldMarkDone Whether the task should be marked as done.
     * @throws IOException If the task list cannot be saved.
     */
    private void updateTaskStatus(Task task, boolean shouldMarkDone) throws IOException {
        boolean wasDone = task.isDone();
        if (shouldMarkDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        try {
            storage.save(tasks);
        } catch (IOException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
    }

    /**
     * Finds tasks whose descriptions contain the requested keyword.
     *
     * @param command Complete find command.
     * @param commandType Find command metadata.
     * @return Response containing matching tasks.
     * @throws SnoopyException If the keyword is empty.
     */
    private String getFindResponse(String command, CommandType commandType) throws SnoopyException {
        String keyword = Parser.getArguments(command, commandType);
        if (keyword.isEmpty()) {
            throw new SnoopyException(
                    "I need a scent to follow. Please provide a keyword to find.");
        }

        TaskList matchingTasks = tasks.find(keyword);
        return " I sniffed out these matching tasks:" + formatNumberedTasks(matchingTasks);
    }

    /**
     * Replaces a task's description without changing its type, dates, or completion state.
     *
     * @param command Complete update command.
     * @param commandType Update command metadata.
     * @return Confirmation shown to the user.
     * @throws SnoopyException If the task number or new description is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String updateTask(String command, CommandType commandType)
            throws SnoopyException, IOException {
        Parser.UpdateDetails updateDetails = Parser.parseUpdate(
                command, tasks.size(), commandType);
        Task task = tasks.get(updateDetails.getTaskIndex());
        task.updateDescription(updateDetails.getDescription());
        storage.save(tasks);
        return formatLines(
                " Got it. I've updated this task:",
                "   " + task);
    }

    /**
     * Formats tasks as a one-based numbered list, with each task on a new line.
     *
     * @param tasksToFormat Tasks to number in their existing order.
     * @return Numbered task lines, or an empty string when there are no tasks.
     */
    private static String formatNumberedTasks(TaskList tasksToFormat) {
        assert tasksToFormat != null : "Task list must be provided for formatting";
        String numberedTasks = IntStream.range(0, tasksToFormat.size())
                .mapToObj(index -> " " + (index + 1) + "." + tasksToFormat.get(index))
                .collect(Collectors.joining("\n"));
        return numberedTasks.isEmpty() ? "" : "\n" + numberedTasks;
    }

    /**
     * Persists a new task and creates its confirmation response.
     *
     * @param task Task to add and save.
     * @return Confirmation shown to the user.
     * @throws IOException If the task cannot be saved.
     */
    private String saveNewTask(Task task) throws IOException {
        assert task != null : "A new task must be created before it can be saved";
        tasks.add(task);
        assert tasks.get(tasks.size() - 1) == task : "The new task must be appended to the task list";
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        return formatLines(
                " Got it. I've added this task:",
                "   " + task,
                " Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Joins any number of response lines with newline characters.
     *
     * @param lines Response lines in display order.
     * @return Lines combined into one response.
     */
    private static String formatLines(String... lines) {
        assert lines != null : "Response lines must be provided";
        for (String line : lines) {
            assert line != null : "Response lines must not contain null values";
        }
        return String.join("\n", lines);
    }

}
