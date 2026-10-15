package kdb;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Coordinates the chatbot's user interaction, commands, tasks, and storage. */
public class Kdb {
    /** Contains a GUI response and whether it should use error styling. */
    public record CommandResponse(String text, boolean isError) {
    }

    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private Task taskAwaitingPriority;

    /**
     * Creates a Kdb instance and loads saved tasks.
     */
    public Kdb() {
        this(new Storage("data/tasks.txt"));
    }

    /**
     * Creates a Kdb instance using the given storage.
     *
     * @param storage storage used to load and save tasks
     */
    public Kdb(Storage storage) {
        this.storage = storage;
        parser = new Parser();

        try {
            tasks = storage.load();
        } catch (IOException e) {
            tasks = new TaskList();
        }
    }

    /** Starts Kdb and runs its command loop. */
    public static void main(String[] args) {
        Storage storage = new Storage("data/tasks.txt");
        Parser parser = new Parser();
        Ui ui = new Ui();
        TaskList tasks = loadTasks(storage, ui);

        ui.showWelcome();

        try (ui) {
            runCommandLoop(storage, parser, ui, tasks);
        }
    }

    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            return storage.load();
        } catch (IOException e) {
            ui.showError("An error occurred while loading tasks: " + e.getMessage());
            return new TaskList();
        }
    }

    private static void runCommandLoop(Storage storage, Parser parser, Ui ui, TaskList tasks) {
        boolean isExit = false;
        while (!isExit) {
            Parser.ParsedCommand parsedCommand = parser.parse(ui.readCommand());
            ui.showDivider();
            try {
                isExit = executeCliCommand(storage, ui, tasks, parsedCommand);
            } catch (KdbException e) {
                ui.showError(e.getMessage());
            }
            ui.showDivider();
        }
    }

    private static boolean executeCliCommand(Storage storage, Ui ui, TaskList tasks,
            Parser.ParsedCommand parsedCommand) throws KdbException {
        String arguments = parsedCommand.getArguments();
        switch (parsedCommand.getCommand()) {
            case BYE:
                ui.showBye();
                return true;
            case LIST:
                ui.showTaskList(tasks);
                return false;
            case FIND:
                if (arguments.isEmpty()) {
                    throw new KdbException("Please provide a keyword to find.");
                }
                ui.showMatchingTasks(tasks.find(arguments));
                return false;
            case MARK:
                // Fallthrough
            case UNMARK:
                updateTaskStatus(storage, ui, tasks, arguments, parsedCommand.getCommand());
                return false;
            case DELETE:
                deleteTask(storage, ui, tasks, arguments);
                return false;
            case TODO:
                addTodo(storage, ui, tasks, arguments);
                return false;
            case DEADLINE:
                addCliDeadline(storage, ui, tasks, arguments);
                return false;
            case EVENT:
                addCliEvent(storage, ui, tasks, arguments);
                return false;
            case UNKNOWN:
                // Fallthrough
            default:
                ui.showUnknownCommandHelp();
                return false;
        }
    }

    private static void updateTaskStatus(Storage storage, Ui ui, TaskList tasks,
            String arguments, CommandType command) throws KdbException {
        String action = command == CommandType.MARK ? "mark" : "unmark";
        int index = parseTaskIndex(arguments, action, tasks.size());
        if (command == CommandType.MARK) {
            tasks.get(index).markAsDone();
            saveTasksSafely(storage, tasks);
            ui.showMarked(tasks.get(index));
        } else {
            tasks.get(index).markAsNotDone();
            saveTasksSafely(storage, tasks);
            ui.showUnmarked(tasks.get(index));
        }
    }

    private static void deleteTask(Storage storage, Ui ui, TaskList tasks, String arguments)
            throws KdbException {
        int index = parseTaskIndex(arguments, "delete", tasks.size());
        Task removed = tasks.remove(index);
        saveTasksSafely(storage, tasks);
        ui.showDeleted(removed, tasks.size());
    }

    private static void addTodo(Storage storage, Ui ui, TaskList tasks, String arguments)
            throws KdbException {
        if (arguments.isEmpty()) {
            throw new KdbException("The description of a todo cannot be empty.");
        }
        tasks.add(new Todo(arguments));
        saveTasksSafely(storage, tasks);
        ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    private static void addCliDeadline(Storage storage, Ui ui, TaskList tasks, String arguments)
            throws KdbException {
        if (arguments.isEmpty()) {
            throw new KdbException("The description of a deadline cannot be empty.");
        }
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new KdbException("A deadline needs a description and date/time, "
                            + "e.g. deadline return book /by 2/12/2019 1800.");
        }
        LocalDateTime deadline = parseDate(parts[1].trim());
        if (deadline == null) {
            throw new KdbException("Invalid date/time. Please use d/M/yyyy HHmm, "
                            + "e.g. 2/12/2019 1800.");
        }
        tasks.add(new Deadline(parts[0].trim(), deadline));
        saveTasksSafely(storage, tasks);
        ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    private static void addCliEvent(Storage storage, Ui ui, TaskList tasks, String arguments)
            throws KdbException {
        if (arguments.isEmpty()) {
            throw new KdbException("The description of an event cannot be empty.");
        }
        String[] fromParts = arguments.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[0].trim().isEmpty()) {
            throw new KdbException("An event needs a /from time, "
                            + "e.g. event meeting /from Mon 2pm /to 4pm.");
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new KdbException("An event needs a /to time, "
                            + "e.g. event meeting /from Mon 2pm /to 4pm.");
        }
        tasks.add(new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim()));
        saveTasksSafely(storage, tasks);
        ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /** Returns the GUI response text for compatibility with text-only callers. */
    public String executeCommand(String input) {
        return executeCommandResult(input).text();
    }

    /**
     * Processes one command from the graphical user interface.
     *
     * @param input command entered by the user
     * @return response text and error status for display in the GUI
     */
    public CommandResponse executeCommandResult(String input) {
        if (taskAwaitingPriority != null) {
            try {
                taskAwaitingPriority.setPriority(Priority.fromInput(input));
                saveTasksSafely(storage, tasks);
                String response = "Priority set to " + taskAwaitingPriority.getPriority()
                                + ":\n  " + taskAwaitingPriority;
                taskAwaitingPriority = null;
                return new CommandResponse(response, false);
            } catch (KdbException e) {
                return new CommandResponse(e.getMessage(), true);
            } catch (IllegalArgumentException e) {
                return new CommandResponse(e.getMessage() + " Please try again.", true);
            }
        }

        Parser.ParsedCommand parsed = parser.parse(input);
        String arguments = parsed.getArguments();

        try {
            switch (parsed.getCommand()) {
                case BYE:
                    return new CommandResponse("Full time. See you next match!", false);
                case LIST:
                    return new CommandResponse(
                            formatTasks(tasks, "Here's the current match sheet:"), false);
                case FIND:
                    if (arguments.isEmpty()) {
                        throw new KdbException("Please provide a keyword to find.");
                    }
                    return new CommandResponse(
                            formatTasks(tasks.find(arguments),
                                    "Here are the matching plays:"), false);
                case TODO:
                    if (arguments.isEmpty()) {
                        throw new KdbException("The description of a todo cannot be empty.");
                    }
                    tasks.add(new Todo(arguments));
                    taskAwaitingPriority = tasks.get(tasks.size() - 1);
                    return new CommandResponse(
                            "Help me make the perfect assist. What priority should this task have? "
                                    + "(high/medium/low)", false);
                case MARK:
                    // Fallthrough
                case UNMARK: {
                    String action = parsed.getCommand() == CommandType.MARK ? "mark" : "unmark";
                    int index = parseTaskIndex(arguments, action, tasks.size());
                    if (parsed.getCommand() == CommandType.MARK) {
                        tasks.get(index).markAsDone();
                    } else {
                        tasks.get(index).markAsNotDone();
                    }
                    saveTasksSafely(storage, tasks);
                    return new CommandResponse("Great finish! Updated task:\n  "
                            + tasks.get(index), false);
                }
                case DELETE: {
                    int index = parseTaskIndex(arguments, "delete", tasks.size());
                    Task removed = tasks.remove(index);
                    saveTasksSafely(storage, tasks);
                    return new CommandResponse("Cleared from the pitch! Removed this task:\n  "
                            + removed, false);
                }
                case DEADLINE:
                    return new CommandResponse(addDeadline(arguments), false);
                case EVENT:
                    return new CommandResponse(addEvent(arguments), false);
                case UNKNOWN:
                    // Fallthrough
                default:
                    return new CommandResponse(Ui.unknownCommandHelp(), true);
            }
        } catch (KdbException e) {
            return new CommandResponse(e.getMessage(), true);
        }
    }

    /** Formats a task list for display in the GUI. */
    private String formatTasks(TaskList list, String header) {
        StringBuilder result = new StringBuilder(header);
        for (int i = 0; i < list.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(list.get(i));
        }
        return result.toString();
    }

    /** Adds a deadline from GUI command arguments. */
    private String addDeadline(String arguments) throws KdbException {
        if (arguments.isEmpty()) {
            throw new KdbException(
                    "Your deadline format is incorrect. Please use:\n"
                            + "deadline your_task /by d/M/yyyy HHmm\n"
                            + "Example: deadline submit report /by 5/9/2026 1800");
        }
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new KdbException(
                    "Your deadline format is incorrect. Please use:\n"
                            + "deadline your_task /by d/M/yyyy HHmm\n"
                            + "Example: deadline submit report /by 5/9/2026 1800");
        }
        LocalDateTime date = parseDate(parts[1].trim());
        if (date == null) {
            throw new KdbException(
                    "The deadline date/time format is incorrect. Please use:\n"
                            + "deadline your_task /by d/M/yyyy HHmm\n"
                            + "Example: deadline submit report /by 5/9/2026 1800");
        }
        tasks.add(new Deadline(parts[0].trim(), date));
        taskAwaitingPriority = tasks.get(tasks.size() - 1);
        return "What priority should this task have? (high/medium/low)";
    }

    /** Adds an event from GUI command arguments. */
    private String addEvent(String arguments) throws KdbException {
        if (arguments.isEmpty()) {
            throw new KdbException(
                    "Your event format is incorrect. Please use:\n"
                            + "event your_event /from start_time /to end_time\n"
                            + "Example: event team meeting /from Monday 2pm /to 3pm");
        }
        String[] fromParts = arguments.split(" /from ", 2);
        if (fromParts.length < 2) {
            throw new KdbException(
                    "Your event format is incorrect. Please use:\n"
                            + "event your_event /from start_time /to end_time\n"
                            + "Example: event team meeting /from Monday 2pm /to 3pm");
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new KdbException(
                    "Your event format is incorrect. Please use:\n"
                            + "event your_event /from start_time /to end_time\n"
                            + "Example: event team meeting /from Monday 2pm /to 3pm");
        }
        tasks.add(new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim()));
        taskAwaitingPriority = tasks.get(tasks.size() - 1);
        return "What priority should this task have? (high/medium/low)";
    }

    /**
     * Parses and validates the task index for mark/unmark/delete commands.
     *
     * @param arguments the text after the command word (e.g. "3")
     * @param commandWord "mark", "unmark", or "delete", used in error messages
     * @param taskCount current number of tasks, used for bounds checking
     * @return zero-based task index
     * @throws KdbException if the index is missing, not a number, or out of range
     */
    private static int parseTaskIndex(String arguments, String commandWord, int taskCount) throws KdbException {
        if (arguments.isEmpty()) {
            throw new KdbException(
                    "Please tell me which task number to " + commandWord
                            + ", e.g. " + commandWord + " 2.");
        }

        int index;
        try {
            index = Integer.parseInt(arguments.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new KdbException(
                    "Task number needs to be a whole number, e.g. " + commandWord + " 2.");
        }

        if (index < 0 || index >= taskCount) {
            throw new KdbException(
                    "That task number doesn't exist. You currently have "
                            + taskCount + " task(s).");
        }

        return index;
    }

    private static void saveTasksSafely(Storage storage, TaskList tasks) throws KdbException {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            throw new KdbException("I couldn't save the task list. Please check file access.", e);
        }
    }

    private static LocalDateTime parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("d/M/uuuu HHmm");

            return LocalDateTime.parse(dateStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
