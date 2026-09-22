package oz;

import java.time.LocalDate;
import java.util.List;

import oz.exception.OzException;
import oz.formatter.TaskResponseFormatter;
import oz.parser.CommandParser;
import oz.parser.ParsedCommand;
import oz.parser.TaskParser;
import oz.parser.TaskTarget;
import oz.parser.TaskTargetParser;
import oz.service.TaskService;
import oz.storage.Storage;
import oz.task.Task;
import oz.task.TaskDateTime;

/**
 * Main entry point and controller for the Oz chatbot.
 */
public class Oz {
    /** Default task file shared by the console and graphical entry points. */
    public static final String DEFAULT_STORAGE_PATH = "data/oz.txt";

    /** Error shown when a command word is unknown. */
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command. Check your blueprint syntax.";

    /** Error shown when list receives arguments. */
    private static final String LIST_ARGUMENTS_MESSAGE = "The list command does not take arguments.";

    /** Error shown when the bye command receives arguments. */
    private static final String BYE_ARGUMENTS_MESSAGE = "The bye command does not take arguments.";

    /** Usage message for date-filtered task listing. */
    private static final String ON_USAGE_MESSAGE = "Use: on <date> (e.g., on 2019-10-15 or on 2/12/2019).";

    /** Error shown when find receives no keyword. */
    private static final String EMPTY_FIND_KEYWORD_MESSAGE = "The keyword for find cannot be empty.";

    /** Service coordinating task state and persistence. */
    private final TaskService taskService;

    /** Flag indicating whether an exit command was issued. */
    private boolean isExit = false;

    /**
     * Constructs an Oz chatbot instance configured with the specified storage file
     * path.
     *
     * @param filePath Path to the task storage file.
     */
    public Oz(String filePath) {
        this.taskService = new TaskService(new Storage(filePath));
    }

    /**
     * Returns true if the chatbot has received an exit command.
     *
     * @return True if the exit command has been received; false otherwise.
     */
    public boolean isExit() {
        return this.isExit;
    }

    /**
     * Processes a user command and returns the response message.
     *
     * @param fullCommand Full command string entered by the user.
     * @return Result containing the response message and its presentation type.
     */
    public CommandResult getResponse(String fullCommand) {
        if (fullCommand == null || fullCommand.isBlank()) {
            return new CommandResult("Please enter a command.", ResponseType.BLANK);
        }

        try {
            ParsedCommand command = CommandParser.parse(fullCommand);
            return executeCommand(command.commandWord(), command.arguments());
        } catch (OzException exception) {
            return new CommandResult("Confound it! " + exception.getMessage(), ResponseType.ERROR);
        }
    }

    /**
     * Routes a recognized command word to its handler.
     *
     * @param command Command word entered by the user.
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command is unknown or its arguments are invalid.
     */
    private CommandResult executeCommand(String command, String details) throws OzException {
        switch (command) {
            case "bye":
                return exit(details);
            case "list":
                return listTasks(details);
            case "on":
                return listTasksOn(details);
            case "find":
                return findTasks(details);
            case "mark":
                return markTask(details);
            case "unmark":
                return unmarkTask(details);
            case "todo":
                return addTodo(details);
            case "deadline":
                return addDeadline(details);
            case "event":
                return addEvent(details);
            case "recurring":
                return addRecurringEvent(details);
            case "delete":
                return deleteTask(details);
            default:
                throw new OzException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Records the exit request and returns the farewell message.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If arguments are supplied to the bye command.
     */
    private CommandResult exit(String details) throws OzException {
        if (!details.isBlank()) {
            throw new OzException(BYE_ARGUMENTS_MESSAGE);
        }
        this.isExit = true;
        return new CommandResult("Farewell! Back to my contraptions. *oink*", ResponseType.BYE);
    }

    /**
     * Lists all tasks after validating that no arguments were supplied.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult listTasks(String details) throws OzException {
        if (!details.isBlank()) {
            throw new OzException(LIST_ARGUMENTS_MESSAGE);
        }

        String response = TaskResponseFormatter.formatTaskList("Here is the master task list:\n",
                this.taskService.getTasks());
        return new CommandResult(response, ResponseType.LIST);
    }

    /**
     * Lists tasks occurring on the date supplied by the user.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult listTasksOn(String details) throws OzException {
        if (details.isBlank()) {
            throw new OzException(ON_USAGE_MESSAGE);
        }

        TaskDateTime targetDateTime = TaskDateTime.parse(details);
        LocalDate targetDate = targetDateTime.toLocalDate();
        String dateHeader = TaskDateTime.formatDate(targetDate);

        List<Task> matchingTasks = this.taskService.findTasksOn(targetDate);

        if (matchingTasks.isEmpty()) {
            return new CommandResult("No tasks found for " + dateHeader + ".",
                    ResponseType.LIST);
        }

        String response = TaskResponseFormatter.formatTasksOnDate(
                "Tasks occurring on " + dateHeader + ":\n",
                matchingTasks, targetDate);
        return new CommandResult(response, ResponseType.LIST);
    }

    /**
     * Finds tasks containing the supplied keyword.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult findTasks(String details) throws OzException {
        if (details.isBlank()) {
            throw new OzException(EMPTY_FIND_KEYWORD_MESSAGE);
        }

        List<Task> matchingTasks = this.taskService.findTasksByKeyword(details);

        if (matchingTasks.isEmpty()) {
            return new CommandResult("No matching tasks found in the ledger.", ResponseType.FIND);
        }

        String response = TaskResponseFormatter.formatTaskList("Matching tasks located:\n",
                matchingTasks);
        return new CommandResult(response, ResponseType.FIND);
    }

    /**
     * Marks the selected task as done and saves the updated list.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult markTask(String details) throws OzException {
        TaskTarget target = TaskTargetParser.parseMarkTarget(details, this.taskService.size());
        Task task = this.taskService.markTask(target.taskIndex(), target.occurrenceDate());
        return new CommandResult(TaskResponseFormatter.formatMarkedTask(
                task, target.occurrenceDate()),
                ResponseType.CHANGE_MARK);
    }

    /**
     * Marks the selected task as not done and saves the updated list.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult unmarkTask(String details) throws OzException {
        TaskTarget target = TaskTargetParser.parseUnmarkTarget(details, this.taskService.size());
        Task task = this.taskService.unmarkTask(target.taskIndex(), target.occurrenceDate());
        return new CommandResult(TaskResponseFormatter.formatUnmarkedTask(
                task, target.occurrenceDate()),
                ResponseType.CHANGE_MARK);
    }

    /**
     * Validates and adds a todo task.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult addTodo(String details) throws OzException {
        return addTask(TaskParser.parseTodo(details));
    }

    /**
     * Parses and adds a deadline task.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult addDeadline(String details) throws OzException {
        return addTask(TaskParser.parseDeadline(details));
    }

    /**
     * Parses and adds an event task.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult addEvent(String details) throws OzException {
        return addTask(TaskParser.parseEvent(details));
    }

    /**
     * Parses and adds a same-day event that repeats at a weekly interval.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the recurring event arguments are invalid.
     */
    private CommandResult addRecurringEvent(String details) throws OzException {
        return addTask(TaskParser.parseRecurringEvent(details));
    }

    /**
     * Deletes the selected task and saves the updated list.
     *
     * @param details Arguments supplied after the command word.
     * @return Response message and command type.
     * @throws OzException If the command arguments are invalid.
     */
    private CommandResult deleteTask(String details) throws OzException {
        int index = TaskTargetParser.parseIndex(details, this.taskService.size());
        Task removedTask = this.taskService.delete(index);
        return new CommandResult(TaskResponseFormatter.formatDeletedTask(
                removedTask, this.taskService.size()), ResponseType.DELETE);
    }

    /**
     * Adds a validated task, saves the list, and reports the new task count.
     *
     * @param task Task to add.
     * @return Confirmation message and the add command type.
     * @throws OzException If saving the updated task list to storage fails.
     */
    private CommandResult addTask(Task task) throws OzException {
        Task addedTask = this.taskService.add(task);
        return new CommandResult(TaskResponseFormatter.formatAddedTask(
                addedTask, this.taskService.size()), ResponseType.ADD);
    }

    /**
     * Entry point for running the Oz application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Oz oz = new Oz(DEFAULT_STORAGE_PATH);
        new ConsoleUi(oz).run();
    }
}
