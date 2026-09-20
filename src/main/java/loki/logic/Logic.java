package loki.logic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import loki.exception.LokiExceptions;
import loki.model.Task;
import loki.model.TaskList;
import loki.parser.Parser;
import loki.parser.TaskUpdate;
import loki.storage.Storage;

/** Provides command processing for non-console user interfaces. */
public class Logic {
    private static final String ADD_TASK_INTRO = "A cunning addition to your saga:";
    private static final String STATUS_INTRO = "By Loki's decree:";
    private static final String DELETE_OUTRO = "A little mischief, and it has vanished.";
    private static final String FAREWELL = "Farewell, mortal. May your next quest be worthy of legend.";
    private static final String TODO_USAGE = "Usage: todo <task description>\n"
            + "Example: todo Buy groceries";
    private static final String DEADLINE_USAGE = "Usage: deadline <task description> /by <date/time>\n"
            + "Example: deadline Return book /by 2019-06-06\n"
            + "Use yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm for date/time values.";
    private static final String EVENT_USAGE = "Usage: event <title> /from <date/time> /to <date/time>\n"
            + "Example: event Project meeting /from 2019-08-06 1400 /to 2019-08-06 1600\n"
            + "Use yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm for date/time values.";
    private static final String HELP_RESPONSE = String.join(System.lineSeparator(),
            "Commands:",
            "  todo <task description>",
            "    Example: todo Buy groceries",
            "  deadline <task description> /by <date/time>",
            "    Example: deadline Return book /by 2019-06-06",
            "  event <title> /from <date/time> /to <date/time>",
            "    Example: event Project meeting /from 2019-08-06 1400 /to 2019-08-06 1600",
            "  list",
            "    Example: list",
            "  mark <task number>",
            "    Example: mark 1",
            "  unmark <task number>",
            "    Example: unmark 1",
            "  delete <task number>",
            "    Example: delete 1",
            "  update <task number> <field> [<field>...]",
            "    Updates one or more fields while keeping the task type unchanged.",
            "    To-do fields: /title <new title>",
            "    Deadline fields: /title <new title>, /by <date/time>",
            "    Event fields: /title <new title>, /from <date/time>, /to <date/time>",
            "    Date formats: yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm",
            "    Examples:",
            "      update 1 /title Buy groceries and toiletries",
            "      update 2 /by 20/9/2026 1800",
            "      update 3 /title Final consultation /from 20/9/2026 1400 /to 20/9/2026 1600",
            "  help",
            "    Example: help",
            "  exit (alias: faretheewell)",
            "    Example: exit");

    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;

    /** Creates logic backed by Loki's default task file. */
    public Logic() {
        this(new Storage());
    }

    /**
     * Creates logic backed by a specified task file.
     *
     * @param filePath the path of the task file.
     */
    public Logic(String filePath) {
        this(new Storage(filePath));
    }

    /**
     * Loads persisted tasks and initializes the command-processing collaborators.
     *
     * @param storage the storage implementation to use.
     */
    private Logic(Storage storage) {
        this.storage = storage;
        parser = new Parser();
        tasks = new TaskList();
        try {
            tasks.addAll(storage.load());
        } catch (LokiExceptions exception) {
            throw new IllegalStateException("Unable to load Loki tasks", exception);
        }
    }

    /**
     * Executes one command and returns the response for display in a GUI.
     *
     * @param input the command entered by the user.
     * @return the response to display.
     */
    public String processCommand(String input) {
        String keyword = null;
        try {
            keyword = parser.parseKeyword(input);
            return switch (keyword) {
                case "faretheewell", "exit" -> {
                    save();
                    yield FAREWELL;
                }
                case "list" -> formatTaskList();
                case "help" -> {
                    if (!input.trim().equalsIgnoreCase("help")) {
                        throw LokiExceptions.unknownCommand();
                    }
                    yield HELP_RESPONSE;
                }
                case "mark" -> STATUS_INTRO + "\n" + tasks.mark(parser.parseTaskNumber(input));
                case "unmark" -> STATUS_INTRO + "\n" + tasks.unmark(parser.parseTaskNumber(input));
                case "todo", "deadline", "event" -> addTask(input);
                case "delete" -> deleteTask(input);
                case "update" -> updateTask(input);
                default -> throw LokiExceptions.unknownCommand();
            };
        } catch (LokiExceptions | IllegalArgumentException exception) {
            return formatErrorResponse(keyword, exception.getMessage());
        }
    }

    /** Saves the current task list to storage. */
    public void save() throws LokiExceptions {
        storage.save(tasks);
    }

    /**
     * Adds a parsed task and returns the resulting response.
     *
     * @param input the task creation command.
     * @return the added task and remaining task count.
     * @throws LokiExceptions if the task command is malformed.
     */
    private String addTask(String input) throws LokiExceptions {
        Task task = parser.parseTask(input);
        tasks.add(task);
        return ADD_TASK_INTRO + "\n" + task + "\nYou have " + tasks.size() + " tasks left to conquer.";
    }

    /**
     * Deletes a task and returns the resulting response.
     *
     * @param input the deletion command.
     * @return the deleted task and remaining task count.
     * @throws LokiExceptions if the task number is invalid.
     */
    private String deleteTask(String input) throws LokiExceptions {
        Task deletedTask = tasks.delete(parser.parseTaskNumber(input));
        return "Deleted: " + deletedTask + "\n" + DELETE_OUTRO + "\nYou have " + tasks.size()
                + " tasks left to conquer.";
    }

    /**
     * Applies an update and returns the updated task.
     *
     * @param input the update command.
     * @return the updated task response.
     * @throws LokiExceptions if the command or update is invalid.
     */
    private String updateTask(String input) throws LokiExceptions {
        TaskUpdate update = parser.parseUpdate(input);
        try {
            return "Updated: " + tasks.update(update);
        } catch (LokiExceptions | IllegalArgumentException exception) {
            throw LokiExceptions.invalidUpdate();
        }
    }

    /**
     * Formats all tasks in their one-based list order.
     *
     * @return the formatted task list, or an empty-list response.
     */
    private String formatTaskList() {
        if (tasks.isEmpty()) {
            return "You lack any tasks";
        }

        List<Task> taskView = tasks.asList();
        return IntStream.range(0, taskView.size())
                .mapToObj(index -> (index + 1) + ". " + taskView.get(index))
                .collect(Collectors.joining(System.lineSeparator()))
                .trim();
    }

    /**
     * Adds a command-specific usage tip to malformed event responses.
     *
     * @param keyword the parsed command keyword, if available.
     * @param errorMessage the base error message.
     * @return the error message with a usage tip when appropriate.
     */
    private String formatErrorResponse(String keyword, String errorMessage) {
        String usageTip = null;
        if (keyword != null) {
            usageTip = switch (keyword) {
                case "todo" -> TODO_USAGE;
                case "deadline" -> DEADLINE_USAGE;
                case "event" -> EVENT_USAGE;
                default -> null;
            };
        }
        return usageTip == null
                ? errorMessage
                : errorMessage + System.lineSeparator() + usageTip;
    }
}
