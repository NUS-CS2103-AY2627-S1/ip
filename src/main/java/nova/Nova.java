package nova;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The chatbot application. Processes user commands, updates the task list and
 * persists it to disk via {@link Storage}. Both the text-based interface and
 * the JavaFX GUI feed user input into {@link #executeCommand(String)}.
 */
public class Nova {
    /** The greeting shown when the application starts. */
    public static final String MESSAGE_GREETING = "Nova here. What can I do for you?";

    private static final String MESSAGE_BYE = "Signing off. Your tasks are saved.";
    private static final String MESSAGE_ADDED = "Added to the list:";
    private static final String MESSAGE_REMOVED = "Removed:";
    private static final String MESSAGE_MARKED = "Done and dusted:";
    private static final String MESSAGE_UNMARKED = "Back to pending:";
    private static final String MESSAGE_FIND_HEADER = "Found these:";
    private static final String MESSAGE_TASK_COUNT_PREFIX = "Now you have ";

    private static final String PARAMETER_BY = "/by";
    private static final String PARAMETER_FROM = "/from";
    private static final String PARAMETER_TO = "/to";

    /** The command words Nova understands, in the order the help text lists them. */
    private static final List<String> COMMAND_WORDS = List.of("todo", "deadline", "event", "list", "find",
            "mark", "unmark", "delete", "archive", "help", "bye");

    private final Storage storage;
    private final TaskList taskList;
    private final String startupWarning;

    /**
     * Creates a chatbot that loads its tasks from the default save file.
     */
    public Nova() {
        this(new Storage());
    }

    /**
     * Creates a chatbot that loads its tasks from the given storage.
     *
     * @param storage the storage to load tasks from and save tasks to
     */
    public Nova(Storage storage) {
        assert storage != null : "Storage must not be null";
        this.storage = storage;
        this.taskList = new TaskList(storage.load());
        this.startupWarning = storage.consumeErrorMessage();
    }

    /**
     * Returns the command words Nova understands, so the GUI can complete them.
     *
     * @return the command words
     */
    public static List<String> getCommandWords() {
        return COMMAND_WORDS;
    }

    /**
     * Returns any warning about the save file that was found while starting up.
     *
     * @return the warning text, or an empty string when the save file read cleanly
     */
    public String getStartupWarning() {
        return this.startupWarning;
    }

    /**
     * Returns how many tasks the list holds.
     *
     * @return the number of tasks
     */
    public int getTaskCount() {
        return this.taskList.size();
    }

    /**
     * Returns how many tasks are done, which the GUI shows next to the total.
     *
     * @return the number of completed tasks
     */
    public int getDoneCount() {
        return (int) this.taskList.getAll().stream().filter(Task::isDone).count();
    }

    /**
     * Processes one user command and returns the outcome of that command.
     *
     * @param input the raw command entered by the user
     * @return the reply text and the kind of reply it is
     */
    public CommandResult executeCommand(String input) {
        Parser parser = new Parser(input);
        String command = parser.getCommandWord();
        if (command.isEmpty()) {
            return CommandResult.error(Ui.MESSAGE_EMPTY_INPUT);
        }
        switch (command) {
            case "bye":
                return handleBye(parser);
            case "list":
                return handleList(parser);
            case "todo":
                return handleTodo(parser);
            case "deadline":
                return handleDeadline(parser);
            case "event":
                return handleEvent(parser);
            case "mark":
            case "unmark":
            case "delete":
                return handleIndexedCommand(command, parser);
            case "find":
                return handleFind(parser);
            case "archive":
                return handleArchive(parser);
            case "help":
                return handleHelp(parser);
            default:
                return CommandResult.error(Ui.MESSAGE_INVALID_COMMAND);
        }
    }

    /**
     * Returns whether the given input is the bye command.
     *
     * @param input the command entered by the user
     * @return true if the command exits the chatbot
     */
    public boolean isExitCommand(String input) {
        return new Parser(input).getCommandWord().equals("bye");
    }

    /**
     * Runs the text-based chatbot loop until the user types "bye".
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Nova nova = new Nova();
        Ui ui = new Ui();
        Scanner scanner = new Scanner(System.in);
        ui.greet(nova.getStartupWarning());
        while (true) {
            String input = scanner.nextLine();
            CommandResult result = nova.executeCommand(input);
            if (!result.text().isEmpty()) {
                ui.showMessage(result.text());
            }
            if (nova.isExitCommand(input)) {
                break;
            }
        }
    }

    /** Handles the bye command, saving the tasks before the app exits. */
    private CommandResult handleBye(Parser parser) {
        if (parser.hasArgument()) {
            return CommandResult.error(extraArgumentsMessage("bye"));
        }
        storage.save(taskList.getAll());
        return withStorageWarning(CommandResult.info(MESSAGE_BYE));
    }

    /** Handles the list command. */
    private CommandResult handleList(Parser parser) {
        if (parser.hasArgument()) {
            return CommandResult.error(extraArgumentsMessage("list"));
        }
        if (taskList.size() == 0) {
            return CommandResult.info(Ui.MESSAGE_LIST_EMPTY);
        }
        return CommandResult.info(formatTaskList());
    }

    /** Handles the help command, which lists every command Nova understands. */
    private CommandResult handleHelp(Parser parser) {
        if (parser.hasArgument()) {
            return CommandResult.error(extraArgumentsMessage("help"));
        }
        return CommandResult.info(Ui.MESSAGE_HELP);
    }

    /** Handles the todo command, which needs a non-empty description. */
    private CommandResult handleTodo(Parser parser) {
        if (!parser.hasArgument()) {
            return CommandResult.error(Ui.MESSAGE_TODO_EMPTY);
        }
        Task task = new Todo(parser.getArguments());
        taskList.add(task);
        return replyAfterSave(formatAddedMessage(task));
    }

    /** Handles the find command, which needs a non-empty keyword. */
    private CommandResult handleFind(Parser parser) {
        if (!parser.hasArgument()) {
            return CommandResult.error(Ui.MESSAGE_FIND_EMPTY);
        }
        String keyword = parser.getArguments().toLowerCase();
        ArrayList<Task> matches = taskList.getAll().stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return CommandResult.info(formatFindResults(matches));
    }

    /**
     * Handles the archive command, moving completed tasks to the archive file.
     *
     * @param parser the parsed user input
     * @return the outcome of the command
     */
    private CommandResult handleArchive(Parser parser) {
        if (parser.hasArgument()) {
            return CommandResult.error(extraArgumentsMessage("archive"));
        }
        ArrayList<Task> archived = taskList.removeCompletedTasks();
        if (archived.isEmpty()) {
            return CommandResult.info(Ui.MESSAGE_ARCHIVE_EMPTY);
        }
        storage.appendToArchive(archived);
        String archiveWarning = storage.consumeErrorMessage();
        storage.save(taskList.getAll());
        String warning = archiveWarning.isEmpty() ? storage.consumeErrorMessage() : archiveWarning;
        String body = IntStream.range(0, archived.size())
                .mapToObj(i -> (i + 1) + ". " + archived.get(i))
                .collect(Collectors.joining("\n"));
        CommandResult result = CommandResult.success(Ui.MESSAGE_ARCHIVE_HEADER + "\n" + body + "\n"
                + taskCountMessage(taskList.size()));
        return withStorageWarning(result, warning);
    }

    /**
     * Handles the deadline command. The description and the /by date are both
     * required, and /by may appear only once.
     *
     * @param parser the parsed user input
     * @return the outcome of the command
     */
    private CommandResult handleDeadline(Parser parser) {
        String arguments = parser.getArguments();
        if (Parser.countOf(arguments, PARAMETER_BY) > 1) {
            return CommandResult.error(Ui.MESSAGE_DUPLICATE_DEADLINE_DATE);
        }
        String[] halves = Parser.splitAround(arguments, PARAMETER_BY);
        boolean isWellFormed = halves.length == 2 && !halves[0].isEmpty() && !halves[1].isEmpty();
        if (!isWellFormed) {
            return CommandResult.error(Ui.MESSAGE_INVALID_DEADLINE);
        }
        LocalDate by = parseDate(halves[1]);
        if (by == null) {
            return CommandResult.error(Ui.MESSAGE_INVALID_DATE);
        }
        Task task = new Deadline(halves[0], by);
        taskList.add(task);
        return replyAfterSave(formatAddedMessage(task));
    }

    /**
     * Handles the event command. The description and both dates are required,
     * /from and /to may appear only once each, and the event must end after it
     * starts.
     *
     * @param parser the parsed user input
     * @return the outcome of the command
     */
    private CommandResult handleEvent(Parser parser) {
        String arguments = parser.getArguments();
        if (Parser.countOf(arguments, PARAMETER_FROM) > 1 || Parser.countOf(arguments, PARAMETER_TO) > 1) {
            return CommandResult.error(Ui.MESSAGE_DUPLICATE_EVENT_DATE);
        }
        String[] fromHalves = Parser.splitAround(arguments, PARAMETER_FROM);
        String[] toHalves = fromHalves.length == 2
                ? Parser.splitAround(fromHalves[1], PARAMETER_TO) : new String[0];
        boolean isWellFormed = toHalves.length == 2
                && !fromHalves[0].isEmpty() && !toHalves[0].isEmpty() && !toHalves[1].isEmpty();
        if (!isWellFormed) {
            return CommandResult.error(Ui.MESSAGE_INVALID_EVENT);
        }
        LocalDate from = parseDate(toHalves[0]);
        LocalDate to = parseDate(toHalves[1]);
        if (from == null || to == null) {
            return CommandResult.error(Ui.MESSAGE_INVALID_DATE);
        }
        if (!from.isBefore(to)) {
            return CommandResult.error(Ui.MESSAGE_INVALID_EVENT_RANGE);
        }
        Task task = new Event(fromHalves[0], from, to);
        taskList.add(task);
        return replyAfterSave(formatAddedMessage(task));
    }

    /**
     * Handles the mark, unmark and delete commands, which all act on a task number.
     *
     * @param command one of mark, unmark or delete
     * @param parser the parsed user input
     * @return the outcome of the command
     */
    private CommandResult handleIndexedCommand(String command, Parser parser) {
        if (!parser.hasArgument()) {
            return CommandResult.error(Ui.MESSAGE_NUMBER_REQUIRED);
        }
        if (!parser.hasSingleWordArgument()) {
            return CommandResult.error(Ui.MESSAGE_ONE_TASK_NUMBER);
        }
        int index = parseTaskIndex(parser.getArguments(), taskList.size());
        if (index < 0) {
            return CommandResult.error(taskNumberError(parser.getArguments(), taskList.size()));
        }
        if (command.equals("delete")) {
            Task removed = taskList.remove(index);
            return replyAfterSave(MESSAGE_REMOVED + "\n" + removed + "\n"
                    + taskCountMessage(taskList.size()));
        }
        Task task = taskList.get(index);
        if (command.equals("mark")) {
            task.markDone();
        } else {
            task.markUndone();
        }
        return replyAfterSave((command.equals("mark") ? MESSAGE_MARKED : MESSAGE_UNMARKED)
                + "\n" + task);
    }

    /** Returns the message for a command that was given arguments it does not take. */
    private static String extraArgumentsMessage(String command) {
        return String.format(Ui.MESSAGE_EXTRA_ARGUMENTS, command);
    }

    /** Formats the added-task reply, which includes the new number of tasks. */
    private String formatAddedMessage(Task task) {
        return MESSAGE_ADDED + "\n" + task + "\n" + taskCountMessage(taskList.size());
    }

    /** Formats the number-of-tasks sentence that closes most replies. */
    private String taskCountMessage(int count) {
        if (count == 0) {
            return "Your list is empty.";
        }
        String noun = count == 1 ? " task" : " tasks";
        return MESSAGE_TASK_COUNT_PREFIX + count + noun + " in the list.";
    }

    /**
     * Saves the current tasks and builds the reply, reporting any storage problem
     * as a failed result so that the user notices it.
     *
     * @param text the reply text for the command
     * @return the outcome of the command, including any storage warning
     */
    private CommandResult replyAfterSave(String text) {
        storage.save(taskList.getAll());
        return withStorageWarning(CommandResult.success(text));
    }

    /**
     * Attaches any storage warning to a reply, and marks the reply as an error
     * when there is one, because losing changes matters more than the styling.
     *
     * @param result the reply built by the command handler
     * @return the reply, with any storage warning attached
     */
    private CommandResult withStorageWarning(CommandResult result) {
        String warning = storage.consumeErrorMessage();
        return withStorageWarning(result, warning);
    }

    /**
     * Attaches a known storage warning to a reply.
     *
     * @param result the reply built by the command handler
     * @param warning the warning to attach, or an empty string when there is none
     * @return the reply, with the warning attached
     */
    private CommandResult withStorageWarning(CommandResult result, String warning) {
        if (warning.isEmpty()) {
            return result;
        }
        return CommandResult.error(result.text() + "\n" + warning);
    }

    /** Formats all tasks using their one-based task numbers. */
    private String formatTaskList() {
        return IntStream.range(0, taskList.size())
                .mapToObj(i -> (i + 1) + ". " + taskList.get(i))
                .collect(Collectors.joining("\n"));
    }

    /** Formats the matching tasks of a find command, numbered from 1. */
    private String formatFindResults(ArrayList<Task> matches) {
        String body = IntStream.range(0, matches.size())
                .mapToObj(i -> (i + 1) + ". " + matches.get(i))
                .collect(Collectors.joining("\n"));
        return body.isEmpty() ? MESSAGE_FIND_HEADER : MESSAGE_FIND_HEADER + "\n" + body;
    }

    /**
     * Converts a one-based task number from the user to a zero-based index.
     *
     * @param argument the task number given by the user
     * @param taskCount the number of tasks in the list
     * @return the zero-based index, or -1 if the argument is not a valid number
     *         or is out of range
     */
    private static int parseTaskIndex(String argument, int taskCount) {
        assert taskCount >= 0 : "Task count must not be negative";
        try {
            int number = Integer.parseInt(argument);
            if (number < 1 || number > taskCount) {
                return -1;
            }
            return number - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the error message for a task number argument that could not be used.
     *
     * @param argument the task number given by the user
     * @param taskCount the number of tasks in the list
     * @return the message for an invalid or out-of-range task number
     */
    private String taskNumberError(String argument, int taskCount) {
        try {
            int number = Integer.parseInt(argument);
            return "There's no task at number " + number + ". " + taskCountMessage(taskCount);
        } catch (NumberFormatException e) {
            return Ui.MESSAGE_INVALID_NUMBER;
        }
    }

    /**
     * Parses a date string in the ISO format yyyy-MM-dd.
     *
     * @param text the date string to parse
     * @return the parsed date, or null if the text is not a valid date
     */
    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
