package bob;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import bob.command.Command;
import bob.command.ParsedCommand;
import bob.parser.Parser;
import bob.storage.Storage;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.TaskList;
import bob.task.Todo;
import bob.ui.Ui;

/**
 * Coordinates the components of the Bob chatbot.
 */
public class Bob {
    private static final String MARK_USAGE = "mark <task_number>";
    private static final String UNMARK_USAGE = "unmark <task_number>";
    private static final String DELETE_USAGE = "delete <task_number>";
    private static final String FIND_USAGE = "find <keyword>";
    private static final String TODO_USAGE = "todo <description>";
    private static final String DEADLINE_USAGE =
            "deadline <description> /by <dd/MM/yyyy or dd/MM/yyyy HHmm>";
    private static final String EVENT_USAGE = "event <description> "
            + "/from <dd/MM/yyyy or dd/MM/yyyy HHmm> "
            + "/to <dd/MM/yyyy or dd/MM/yyyy HHmm>";

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Bob with its UI, parser, task list, and default storage.
     */
    public Bob() {
        this(new Ui(), Storage.createDefaultStorage());
    }

    /**
     * Creates Bob with the supplied UI and storage components.
     */
    public Bob(Ui ui, Storage storage) {
        this.ui = ui;
        this.parser = new Parser();
        this.storage = storage;
        this.tasks = loadTasks();
    }

    /**
     * Processes one command and displays its response through the configured UI.
     *
     * @param input Command text entered by the user.
     * @return True if Bob should exit.
     */
    public boolean respond(String input) {
        ParsedCommand parsedCommand = parser.parse(input);
        return executeCommand(parsedCommand);
    }

    /**
     * Starts Bob and responds to commands until the user exits or input ends.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Bob().run();
    }

    /**
     * Displays the welcome message and runs the command loop.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            ui.showCommandStart();
            ParsedCommand parsedCommand = parser.parse(ui.readCommand());
            boolean shouldExit = executeCommand(parsedCommand);
            ui.showCommandEnd();
            if (shouldExit) {
                return;
            }
        }
    }

    /**
     * Executes one parsed command.
     *
     * @param parsedCommand Command type and arguments supplied by the user.
     * @return True if Bob should exit after executing the command.
     */
    private boolean executeCommand(ParsedCommand parsedCommand) {
        switch (parsedCommand.getCommand()) {
            case BYE:
                return exit(parsedCommand.getArguments());
            case LIST:
                list(parsedCommand.getArguments());
                break;
            case FIND:
                find(parsedCommand.getArguments());
                break;
            case MARK:
                mark(parsedCommand.getArguments());
                break;
            case UNMARK:
                unmark(parsedCommand.getArguments());
                break;
            case DELETE:
                delete(parsedCommand.getArguments());
                break;
            case TODO:
                createTodo(parsedCommand.getArguments());
                break;
            case DEADLINE:
                createDeadline(parsedCommand.getArguments());
                break;
            case EVENT:
                createEvent(parsedCommand.getArguments());
                break;
            default:
                // Every recognized enum value must have its own handler above.
                assert parsedCommand.getCommand() == Command.UNKNOWN : "Recognized command has no handler";
                ui.showUnknownCommand();
                break;
        }
        return false;
    }

    /**
     * Exits when the bye command has no arguments.
     *
     * @param arguments Text following the bye command.
     * @return True when Bob should exit.
     */
    private boolean exit(String arguments) {
        if (!arguments.isEmpty()) {
            ui.showUnknownCommand();
            return false;
        }
        ui.showGoodbye();
        return true;
    }

    /**
     * Displays all tasks when the list command has no arguments.
     *
     * @param arguments Text following the list command.
     */
    private void list(String arguments) {
        if (!arguments.isEmpty()) {
            ui.showUnknownCommand();
            return;
        }
        ui.showTaskList(tasks);
    }

    /**
     * Displays tasks with descriptions containing the supplied keyword.
     *
     * @param arguments Keyword supplied after the find command.
     */
    private void find(String arguments) {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            ui.showUsage(FIND_USAGE);
            return;
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /**
     * Marks the task identified by the command argument as complete.
     *
     * @param arguments Task number supplied after the mark command.
     */
    private void mark(String arguments) {
        updateTaskStatus(arguments, true);
    }

    /**
     * Marks the task identified by the command argument as incomplete.
     *
     * @param arguments Task number supplied after the unmark command.
     */
    private void unmark(String arguments) {
        updateTaskStatus(arguments, false);
    }

    /**
     * Validates a task number, updates its status, persists it, and displays feedback.
     *
     * @param arguments Task-number text supplied after mark or unmark.
     * @param isDone Requested completion status.
     */
    private void updateTaskStatus(String arguments, boolean isDone) {
        String usage = isDone ? MARK_USAGE : UNMARK_USAGE;
        Integer taskNumber = parseTaskNumber(arguments, usage);
        if (taskNumber == null) {
            return;
        }
        Task task = tasks.get(taskNumber);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        saveTasks();
        ui.showMarkedTask(task, isDone);
    }

    /**
     * Removes the task identified by the command argument.
     *
     * @param arguments Task number supplied after the delete command.
     */
    private void delete(String arguments) {
        Integer taskNumber = parseTaskNumber(arguments, DELETE_USAGE);
        if (taskNumber == null) {
            return;
        }
        Task removedTask = tasks.remove(taskNumber);
        saveTasks();
        ui.showDeletedTask(removedTask, tasks.size());
    }

    /**
     * Creates a to-do task from the supplied description.
     *
     * @param arguments Description supplied after the todo command.
     */
    private void createTodo(String arguments) {
        String description = arguments.trim();
        if (description.isEmpty()) {
            ui.showUsage(TODO_USAGE);
            return;
        }
        addTask(new Todo(description));
    }

    /**
     * Creates a deadline task from its description and deadline text.
     *
     * @param arguments Text supplied after the deadline command.
     */
    private void createDeadline(String arguments) {
        Deadline deadline;
        try {
            deadline = parser.parseDeadline(arguments);
        } catch (DateTimeParseException exception) {
            ui.showInvalidDateTime();
            return;
        } catch (IllegalArgumentException exception) {
            ui.showUsage(DEADLINE_USAGE);
            return;
        }
        addTask(deadline);
    }

    /**
     * Creates an event task from its description, start time, and end time.
     *
     * @param arguments Text supplied after the event command.
     */
    private void createEvent(String arguments) {
        int fromIndex = arguments.indexOf(" /from ");
        int toIndex = arguments.indexOf(" /to ");
        if (fromIndex <= 0 || toIndex < fromIndex) {
            ui.showUsage(EVENT_USAGE);
            return;
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + " /from ".length(), toIndex).trim();
        String to = arguments.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            ui.showUsage(EVENT_USAGE);
            return;
        }
        try {
            addTask(new Event(description, from, to));
        } catch (DateTimeParseException exception) {
            ui.showInvalidDateTime();
        }
    }

    /**
     * Adds a task, saves the updated list, and displays the result.
     *
     * @param task Task to add.
     */
    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Parses and validates a one-based task number.
     *
     * @param arguments Task-number text to parse.
     * @param usage Command format displayed when an argument is missing or malformed.
     * @return The valid one-based task number, or null when validation fails.
     */
    private Integer parseTaskNumber(String arguments, String usage) {
        if (arguments.isEmpty() || arguments.contains(" ")) {
            ui.showUsage(usage);
            return null;
        }
        try {
            int taskNumber = Integer.parseInt(arguments);
            if (!tasks.hasTaskNumber(taskNumber)) {
                ui.showInvalidTaskNumber();
                return null;
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumberFormat();
            return null;
        }
    }

    /**
     * Loads saved tasks, falling back to an empty task list when reading fails.
     *
     * @return The loaded or empty task list.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (IOException exception) {
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /**
     * Saves the current task list and reports errors without terminating Bob.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            ui.showSavingError();
        }
    }
}
