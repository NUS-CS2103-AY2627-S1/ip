package anniechat.logic;

import java.io.IOException;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

import anniechat.parser.Parser;
import anniechat.storage.Storage;
import anniechat.task.Task;

/** Executes chatbot commands independently of the user interface. */
public class CommandHandler {
    /** Shared welcome message used by both the command-line and graphical interfaces. */
    public static final String WELCOME_MESSAGE =
            "Meow meow! I'm WhiskerList.\nWhat can I help you with?";

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_FIND = "find";

    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Meow... I don't recognise that command yet, sowwy!\n"
                    + "Try list, todo, deadline, event, mark, unmark, delete, find, or bye.";
    private static final String EMPTY_DESCRIPTION_MESSAGE =
            "Meow! A task description cannot be empty, sowwy.";
    private static final String INVALID_COMMAND_MESSAGE =
            "Meow... I couldn't understand that command. Please check the format, sowwy.";
    private static final String LOAD_ERROR_MESSAGE =
            "Meow... I couldn't load your saved tasks, so I started with an empty list.";
    private static final String SAVE_ERROR_MESSAGE =
            "Meow... I couldn't save your latest changes, sowwy.";

    private final Storage storage;
    private final List<Task> tasks = new ArrayList<>();
    private String startupMessage = "";

    /**
     * Creates a command handler and loads the saved tasks.
     *
     * @param storage storage manager used to load and save tasks.
     */
    public CommandHandler(Storage storage) {
        this.storage = storage;
        loadTasks();
    }

    /**
     * Returns a startup error, if loading the saved tasks failed.
     *
     * @return startup message, or an empty string when loading succeeded.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Interprets and executes one user command.
     *
     * @param input command entered by the user.
     * @return response and exit status for the user interface to display.
     */
    public CommandResult handle(String input) {
        Parser parser = new Parser(input);

        try {
            return switch (parser.getCommandWord()) {
            case COMMAND_BYE -> new CommandResult("Meow for now! See you later!", true);
            case COMMAND_LIST -> listTasks(tasks);
            case COMMAND_MARK -> markTask(parser.getTaskNumber(), true);
            case COMMAND_UNMARK -> markTask(parser.getTaskNumber(), false);
            case COMMAND_TODO, COMMAND_DEADLINE, COMMAND_EVENT -> addTask(parser);
            case COMMAND_DELETE -> deleteTask(parser.getTaskNumber());
            case COMMAND_FIND -> listTasks(parser.findMatchingTasks(tasks));
            default -> new CommandResult(UNKNOWN_COMMAND_MESSAGE, false);
            };
        } catch (IllegalArgumentException | IndexOutOfBoundsException
                 | DateTimeException exception) {
            return new CommandResult(INVALID_COMMAND_MESSAGE, false);
        }
    }

    /** Loads the saved tasks and records a user-facing error if loading fails. */
    private void loadTasks() {
        try {
            tasks.addAll(storage.load());
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException
                 | DateTimeException exception) {
            startupMessage = LOAD_ERROR_MESSAGE;
        }
    }

    /** Adds a task represented by the parsed command. */
    private CommandResult addTask(Parser parser) {
        if (parser.getTaskDescription().isEmpty()) {
            return new CommandResult(EMPTY_DESCRIPTION_MESSAGE, false);
        }

        Task task = parser.createTask();
        tasks.add(task);
        if (!saveTasks()) {
            return new CommandResult(SAVE_ERROR_MESSAGE, false);
        }

        String message = "Purrfect! I've added this task:\n"
                + formatTask(task)
                + "\nNow you have " + tasks.size() + " tasks in the list.";
        return new CommandResult(message, false);
    }

    /** Marks or unmarks a task at the supplied zero-based index. */
    private CommandResult markTask(int taskIndex, boolean done) {
        Task task = getTask(taskIndex);
        if (done) {
            task.markDone();
        } else {
            task.markUndone();
        }

        if (!saveTasks()) {
            return new CommandResult(SAVE_ERROR_MESSAGE, false);
        }

        String action = done
                ? "Purrfect! I've marked this task as done:\n"
                : "Meow, I've marked this task as not done yet:\n";
        return new CommandResult(action + formatTask(task), false);
    }

    /** Deletes a task at the supplied zero-based index. */
    private CommandResult deleteTask(int taskIndex) {
        Task task = getTask(taskIndex);
        tasks.remove(taskIndex);
        Task.removeTask();

        if (!saveTasks()) {
            return new CommandResult(SAVE_ERROR_MESSAGE, false);
        }

        String message = "Okii! I've removed this task:\n"
                + formatTask(task)
                + "\nNow you have " + tasks.size() + " tasks in the list.";
        return new CommandResult(message, false);
    }

    /** Returns the task at the supplied zero-based index. */
    private Task getTask(int taskIndex) {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        return tasks.get(taskIndex);
    }

    /** Creates a response containing the supplied tasks. */
    private CommandResult listTasks(List<Task> tasksToShow) {
        StringBuilder message = new StringBuilder("a glimpse of ur tasks :)\n");
        for (int i = 0; i < tasksToShow.size(); i++) {
            message.append(i + 1).append(".")
                    .append(formatTask(tasksToShow.get(i))).append("\n");
        }
        return new CommandResult(message.toString().trim(), false);
    }

    /** Saves the current task list. */
    private boolean saveTasks() {
        try {
            storage.save(tasks);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    /** Formats a task consistently for both the CLI and GUI. */
    private String formatTask(Task task) {
        return "[" + task.getTaskIcon() + "]" + task.statusIcon() + " " + task.getTaskDesc();
    }
}
