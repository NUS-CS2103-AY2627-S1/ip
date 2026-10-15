package elora;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.Map;

import elora.task.Task;
import elora.task.TaskFactory;
import elora.task.TaskList;

/**
 * Entry point and main command loop for the Elora chatbot: a personal
 * assistant that tracks todos, deadlines, and events entered via a
 * simple text command language. Command execution is exposed both as a
 * blocking console loop ({@link #run()}) and as a single-shot
 * {@link #getResponse(String)} call, so the same logic can back either a
 * CLI or a GUI front end.
 */
public class Elora {
    private static final String DATA_FILE_PATH = "data" + File.separator + "elora.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final TaskFactory taskFactory = new TaskFactory();
    private final Map<CommandType, CommandHandler> commandHandlers = buildCommandHandlers();

    /**
     * Creates an Elora instance, loading any previously saved tasks from
     * the given file path.
     *
     * @param filePath Relative path to the save file.
     */
    public Elora(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Creates an Elora instance using the default save file location.
     */
    public Elora() {
        this(DATA_FILE_PATH);
    }

    /**
     * Runs the main command loop: greets the user, then repeatedly reads,
     * executes, and prints the result of one command, until the user
     * types "bye".
     */
    public void run() {
        System.out.println(ui.welcomeMessage());
        printStartupWarningsIfAny();
        boolean isExit = false;
        while (!isExit) {
            isExit = runOneCommand();
        }
        ui.closeScanner();
    }

    /**
     * Prints a warning to the console if the save file had lines that
     * couldn't be understood on load.
     */
    private void printStartupWarningsIfAny() {
        if (!storage.getLoadWarnings().isEmpty()) {
            System.out.println(ui.corruptedLinesWarning(storage.getLoadWarnings()));
        }
    }

    /**
     * Reads one line of console input, executes it, and prints the
     * result (or the error message, if it failed), framed by divider
     * lines.
     *
     * @return true if that command was "bye", signalling the loop to stop.
     */
    private boolean runOneCommand() {
        String input = ui.readCommand();
        ui.showLine();
        boolean isExit = false;
        try {
            System.out.println(executeCommand(input));
            isExit = isExitCommand(input);
        } catch (EloraException e) {
            System.out.println(e.getMessage());
        }
        ui.showLine();
        return isExit;
    }

    /**
     * Returns Elora's initial greeting, for a GUI to display as its
     * first message. If the save file had lines that couldn't be
     * understood, a warning listing them is appended so the user knows
     * that data wasn't silently lost.
     *
     * @return The welcome message.
     */
    public String getWelcomeMessage() {
        String welcome = ui.chatWelcomeMessage();
        if (storage.getLoadWarnings().isEmpty()) {
            return welcome;
        }
        return welcome + "\n\n" + ui.corruptedLinesWarning(storage.getLoadWarnings());
    }

    /**
     * Executes a single line of user input and returns Elora's reply,
     * for a GUI to display. Errors are reported as an error
     * {@link Response} rather than thrown, so the GUI can style them
     * differently and catch the user's eye.
     *
     * @param input The full line of user input.
     * @return Elora's reply to that input, marked as an error if one occurred.
     */
    public Response getResponse(String input) {
        try {
            return Response.of(executeCommand(input));
        } catch (EloraException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * Returns whether the given input is the command that ends the
     * session, so a GUI knows when to close.
     *
     * @param input The full line of user input.
     * @return true if the input is the "bye" command.
     */
    public boolean isExitCommand(String input) {
        return Parser.parseCommandType(Parser.getCommandWord(input)) == CommandType.BYE;
    }

    /**
     * Parses a single line of user input and looks up (then runs) the
     * handler registered for its command type.
     *
     * @param input The full line of user input.
     * @return Elora's reply to that input.
     * @throws EloraException If the input can't be understood or acted on.
     */
    private String executeCommand(String input) throws EloraException {
        String arguments = Parser.getArguments(input);
        CommandType commandType = Parser.parseCommandType(Parser.getCommandWord(input));
        CommandHandler handler = commandHandlers.get(commandType);
        if (handler == null) {
            throw new EloraException(
                    "Hold on - I don't recognize that one yet. Could you rephrase it?");
        }
        return handler.handle(arguments);
    }

    /**
     * Builds the lookup table from command type to the method that
     * handles it, used by {@link #executeCommand(String)} in place of a
     * long switch statement.
     *
     * @return The command type to handler mapping.
     */
    private Map<CommandType, CommandHandler> buildCommandHandlers() {
        Map<CommandType, CommandHandler> handlers = new EnumMap<>(CommandType.class);
        handlers.put(CommandType.BYE, arguments -> ui.goodbyeMessage());
        handlers.put(CommandType.LIST, arguments -> ui.taskListMessage(tasks));
        handlers.put(CommandType.MARK, this::handleMark);
        handlers.put(CommandType.UNMARK, this::handleUnmark);
        handlers.put(CommandType.DELETE, this::handleDelete);
        handlers.put(CommandType.TODO, this::handleTodo);
        handlers.put(CommandType.DEADLINE, this::handleDeadline);
        handlers.put(CommandType.EVENT, this::handleEvent);
        handlers.put(CommandType.ON, this::handleOn);
        handlers.put(CommandType.FIND, this::handleFind);
        handlers.put(CommandType.SORT, arguments -> handleSort());
        return handlers;
    }

    /**
     * Handles "mark INDEX": marks the given task as done.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the index is missing, invalid, or out of range.
     */
    private String handleMark(String arguments) throws EloraException {
        Task task = tasks.get(parseTaskIndex(arguments,
                "Hold on - which task should I mark done? Give me a number, like mark 2."));
        task.markAsDone();
        storage.save(tasks.getAll());
        return ui.taskMarkedMessage(task);
    }

    /**
     * Handles "unmark INDEX": marks the given task as not done.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the index is missing, invalid, or out of range.
     */
    private String handleUnmark(String arguments) throws EloraException {
        Task task = tasks.get(parseTaskIndex(arguments,
                "Hold on - which task should I unmark? Give me a number, like unmark 2."));
        task.markAsNotDone();
        storage.save(tasks.getAll());
        return ui.taskUnmarkedMessage(task);
    }

    /**
     * Handles "delete INDEX": removes the given task from the list.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the index is missing, invalid, or out of range.
     */
    private String handleDelete(String arguments) throws EloraException {
        Task removed = tasks.remove(parseTaskIndex(arguments,
                "Hold on - which task should I delete? Give me a number, like delete 2."));
        storage.save(tasks.getAll());
        return ui.taskDeletedMessage(removed, tasks.size());
    }

    /**
     * Handles "todo DESCRIPTION": adds a description-only task.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the description is missing, invalid, or a duplicate.
     */
    private String handleTodo(String arguments) throws EloraException {
        return addAndReport(taskFactory.createTodo(arguments));
    }

    /**
     * Handles "deadline DESCRIPTION /by DATE": adds a task due by a date.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the description, /by, or date is missing or invalid,
     *     or the resulting task is a duplicate.
     */
    private String handleDeadline(String arguments) throws EloraException {
        return addAndReport(taskFactory.createDeadline(arguments));
    }

    /**
     * Handles "event DESCRIPTION /from START /to END": adds a task spanning
     * a start and end time.
     *
     * @param arguments The text after the command word.
     * @return The confirmation message.
     * @throws EloraException If the description, /from, or /to is missing or
     *     invalid, the event ends before it starts, or it's a duplicate.
     */
    private String handleEvent(String arguments) throws EloraException {
        return addAndReport(taskFactory.createEvent(arguments));
    }

    /**
     * Adds a newly built task to the list (rejecting an exact duplicate),
     * persists the list, and returns the standard "task added" message.
     * Shared by the todo/deadline/event handlers, which only differ in
     * how they build the task.
     *
     * @param task The task to add.
     * @return The confirmation message.
     * @throws EloraException If an equal task is already in the list, or
     *     the list couldn't be saved.
     */
    private String addAndReport(Task task) throws EloraException {
        addTaskIfNotDuplicate(task);
        storage.save(tasks.getAll());
        return ui.taskAddedMessage(task, tasks.size());
    }

    /**
     * Handles "on DATE": lists tasks occurring on the given date.
     *
     * @param arguments The text after the command word.
     * @return The formatted tasks-on-date message.
     * @throws EloraException If the date is missing or invalid.
     */
    private String handleOn(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException("Hold on - which date? Try: on 2019-10-15");
        }
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(arguments);
        } catch (DateTimeParseException e) {
            throw new EloraException(
                    "Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
        }
        return ui.tasksOnDateMessage(targetDate, tasks.getTasksOnDate(targetDate));
    }

    /**
     * Handles "find KEYWORD": lists tasks whose description matches the keyword.
     *
     * @param arguments The text after the command word.
     * @return The formatted matching-tasks message.
     * @throws EloraException If the keyword is missing.
     */
    private String handleFind(String arguments) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException("Hold on - what should I search for? Try: find book");
        }
        return ui.matchingTasksMessage(tasks.findTasks(arguments));
    }

    /**
     * Handles "sort": sorts the task list by date and persists the new order.
     *
     * @return The formatted, sorted task list message.
     * @throws EloraException If the sorted list couldn't be saved.
     */
    private String handleSort() throws EloraException {
        tasks.sortByDate();
        storage.save(tasks.getAll());
        return ui.taskListSortedMessage(tasks);
    }

    /**
     * Parses a 1-based task number argument (as used by mark/unmark/delete)
     * into a validated 0-based index into the current task list.
     *
     * @param arguments The raw argument text following the command word.
     * @param missingIndexMessage The error to report if arguments is empty,
     *     since that message differs by command (e.g. "which task should I
     *     mark done?" vs "...delete?").
     * @return The validated, 0-based task index.
     * @throws EloraException If the argument is missing, not a number, or
     *     out of range for the current task list.
     */
    private int parseTaskIndex(String arguments, String missingIndexMessage) throws EloraException {
        if (arguments.isEmpty()) {
            throw new EloraException(missingIndexMessage);
        }
        int index;
        try {
            index = Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw new EloraException(
                    "Hold on - \"" + arguments + "\" doesn't look like a task number to me.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new EloraException(
                    "Hold on - I don't see a task numbered " + arguments
                    + ". Take another look at your list?");
        }
        return index;
    }

    /**
     * Adds a task to the list, unless an equal task (same type,
     * description, and any type-specific fields like a due date) is
     * already present.
     *
     * @param task The task to add.
     * @throws EloraException If an equal task is already in the list.
     */
    private void addTaskIfNotDuplicate(Task task) throws EloraException {
        if (tasks.getAll().contains(task)) {
            throw new EloraException(
                    "Hold on - that exact task is already on your list. No need to add it twice.");
        }
        tasks.add(task);
    }

    /**
     * A single command's execution logic: takes the raw argument text
     * following the command word and returns Elora's reply. Implemented
     * either by a handleX() method reference or, for trivial commands
     * with no real logic, an inline lambda.
     */
    @FunctionalInterface
    private interface CommandHandler {
        /**
         * Executes this command against the current task list.
         *
         * @param arguments The text after the command word.
         * @return The reply to show the user.
         * @throws EloraException If the command can't be completed.
         */
        String handle(String arguments) throws EloraException;
    }

    /**
     * Launches the console version of the application.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new Elora(DATA_FILE_PATH).run();
    }
}
