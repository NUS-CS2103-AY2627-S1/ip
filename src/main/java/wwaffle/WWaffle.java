package wwaffle;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import wwaffle.command.CommandType;
import wwaffle.command.Parser;
import wwaffle.exception.WWaffleException;
import wwaffle.storage.Storage;
import wwaffle.task.Deadline;
import wwaffle.task.Event;
import wwaffle.task.Task;
import wwaffle.task.TaskList;
import wwaffle.task.Todo;
import wwaffle.ui.Ui;

/**
 * Coordinates user input, task management, and persistent storage.
 */
public class WWaffle {
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";

    private boolean loadingFailed;

    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates WWaffle using the specified save file.
     *
     * @param filePath Path of the task data file.
     */
    public WWaffle(String filePath) {
        this.ui = new Ui();
        this.parser = new Parser();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(loadTasks());
    }

    /**
     * Starts WWaffle using the default task data file.
     *
     * @param args Command-line arguments; unused by WWaffle.
     */
    public static void main(String[] args) {
        new WWaffle("./data/wwaffle.txt").run();
    }

    /**
     * Runs the command loop until the user exits.
     */
    public void run() {
        ui.showWelcome(tasks.getSize());
        String command = ui.readCommand();

        while (parser.parseCommandType(command) != CommandType.BYE) {
            System.out.println(getResponse(command));
            command = ui.readCommand();
        }
        ui.showExit();
    }

    /**
     * Returns the welcome message used by graphical interfaces.
     *
     * @return Welcome message containing the number of loaded tasks.
     */
    public String getWelcomeMessage() {
        if (loadingFailed) {
            return ui.getErrorMessage("I couldn't load your saved tasks. "
                    + "Please check the task file before continuing. ⚠️");
        }
        return ui.getWelcomeMessage(tasks.getSize());
    }

    /**
     * Returns the current number of tasks.
     *
     * @return Number of tasks stored by WWaffle.
     */
    public int getTaskCount() {
        return tasks.getSize();
    }

    /**
     * Executes a command and returns a plain-text response for a graphical interface.
     *
     * @param command Command entered by the user.
     * @return User-facing response to the command.
     */
    public String getResponse(String command) {
        command = command.strip().replaceAll("\\s+", " ");
        ArrayList<Task> before = new ArrayList<>(tasks.getTasks());
        List<Boolean> statuses = before.stream().map(Task::isDone).toList();
        try {
            CommandType type = parser.parseCommandType(command);
            boolean changesTasks = switch (type) {
                case TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, SORT, CLEAR -> true;
                default -> false;
            };
            if (changesTasks && loadingFailed) {
                throw new WWaffleException("Saved tasks could not be loaded. "
                        + "Repair the file and restart before editing.");
            }
            if (changesTasks && (command.contains("|") || command.contains("\u0000"))) {
                throw new WWaffleException("Task details cannot contain | or null characters.");
            }
            return switch (parser.parseCommandType(command)) {
                case LIST -> ui.getTaskListMessage(tasks.getTasks());
                case SORT -> ui.getSortedTaskListMessage(sortTasks(command),
                        parser.parseArgument(command, "sort"));
                case CLEAR -> clearTasks(command);
                case MARK -> ui.getTaskMarkedMessage(markTask(command, true), true);
                case UNMARK -> ui.getTaskMarkedMessage(markTask(command, false), false);
                case DELETE -> ui.getTaskDeletedMessage(deleteTask(command), tasks.getSize());
                case TODO -> ui.getTaskAddedMessage(addTodo(command), tasks.getSize());
                case DEADLINE -> ui.getTaskAddedMessage(addDeadline(command), tasks.getSize());
                case EVENT -> ui.getTaskAddedMessage(addEvent(command), tasks.getSize());
                case FIND -> ui.getMatchingTasksMessage(findTasks(command));
                case GREETING -> ui.getGreetingMessage();
                case THANKS -> ui.getThanksMessage();
                case BYE -> ui.getExitMessage();
                default -> throw new WWaffleException("No idea 🧇"
                    + "\nTry list, todo, deadline, event, mark, unmark, delete, find, sort, clear, or bye.");
            };
        } catch (IOException e) {
            // Restore both order and mutable status after an unsuccessful save.
            for (int i = 0; i < before.size(); i++) {
                if (statuses.get(i)) {
                    before.get(i).markAsDone();
                } else {
                    before.get(i).markAsNotDone();
                }
            }
            tasks = new TaskList(before);
            return ui.getErrorMessage("I couldn't save your tasks. Please try again. ⚠️");
        } catch (WWaffleException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Identifies farewell commands consistently for graphical and terminal interfaces.
     *
     * @param command User input.
     * @return Whether the application should close after its farewell.
     */
    public boolean isExitCommand(String command) {
        return parser.parseCommandType(command) == CommandType.BYE;
    }

    /**
     * Returns tasks in command-number order for read-only GUI rendering.
     *
     * @return Unmodifiable snapshot of the current task order.
     */
    public List<Task> getTasksForDisplay() {
        return List.copyOf(tasks.getTasks());
    }

    private ArrayList<Task> loadTasks() {
        try {
            return storage.load();
        } catch (IOException e) {
            loadingFailed = true;
            ui.showLoadingError();
            return new ArrayList<>();
        }
    }

    private Task markTask(String command, boolean isDone) throws WWaffleException, IOException {
        String commandName = isDone ? "mark" : "unmark";
        int taskIndex = parser.parseTaskIndex(command, commandName, tasks.getSize());
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks.getTasks());
        return task;
    }

    private Task deleteTask(String command) throws WWaffleException, IOException {
        int taskIndex = parser.parseTaskIndex(command, "delete", tasks.getSize());
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks.getTasks());
        return removedTask;
    }

    private Task addTodo(String command) throws WWaffleException, IOException {
        String description = parser.parseArgument(command, "todo");
        if (description.isBlank()) {
            throw new WWaffleException("No idea 🧇\nTry: todo <description>.");
        }
        return addTask(new Todo(description));
    }

    private Task addDeadline(String command) throws WWaffleException, IOException {
        String details = parser.parseArgument(command, "deadline");
        int byIndex = details.indexOf(DEADLINE_SEPARATOR);
        if (byIndex < 0) {
            throw new WWaffleException("No idea 🧇"
                + "\nTry: deadline submit assignment /by 2026-09-18");
        }

        String description = details.substring(0, byIndex);
        String by = details.substring(byIndex + DEADLINE_SEPARATOR.length());
        if (by.contains("/by")) {
            throw new WWaffleException("Use /by exactly once.");
        }
        if (description.isBlank() || by.isBlank()) {
            throw new WWaffleException("No idea 🧇"
                + "\nTry: deadline submit assignment /by 2026-09-18");
        }

        try {
            return addTask(new Deadline(description, by));
        } catch (DateTimeParseException e) {
            throw new WWaffleException("No idea 🧇\nTry a date in YYYY-MM-DD format, e.g. 2026-09-18.");
        }
    }

    private Task addEvent(String command) throws WWaffleException, IOException {
        String details = parser.parseArgument(command, "event");
        int fromIndex = details.indexOf(EVENT_START_SEPARATOR);
        if (fromIndex < 0) {
            throw new WWaffleException("No idea 🧇\nTry: "
                + "event meeting /from 2pm /to 3pm");
        }

        String description = details.substring(0, fromIndex);
        String times = details.substring(fromIndex + EVENT_START_SEPARATOR.length());
        int toIndex = times.indexOf(EVENT_END_SEPARATOR);
        if (toIndex < 0) {
            throw new WWaffleException("No idea 🧇\nTry: "
                + "event meeting /from 2pm /to 3pm");
        }

        String from = times.substring(0, toIndex);
        String to = times.substring(toIndex + EVENT_END_SEPARATOR.length());
        if (description.isBlank() || from.isBlank() || to.isBlank()) {
            throw new WWaffleException("No idea 🧇"
                + "\nTry: event meeting /from 2pm /to 3pm");
        }
        if (times.contains("/from") || to.contains("/to")) {
            throw new WWaffleException("Use /from and /to exactly once.");
        }
        return addTask(new Event(description, from, to));
    }

    private List<Task> findTasks(String command) throws WWaffleException {
        String keyword = parser.parseArgument(command, "find");
        if (keyword.isBlank()) {
            throw new WWaffleException("No idea 🧇\nTry: find <keyword>.");
        }
        return tasks.find(keyword);
    }

    /**
     * Validates the sort key, reorders tasks once, and persists their new order.
     *
     * @param command Full sort command.
     * @return Tasks in their new display order.
     * @throws WWaffleException If the sort key is unsupported or missing.
     * @throws IOException If the reordered tasks cannot be saved.
     */
    private List<Task> sortTasks(String command) throws WWaffleException, IOException {
        String sortKey = parser.parseArgument(command, "sort");
        switch (sortKey) {
            case "name" -> tasks.sortByName();
            case "status" -> tasks.sortByStatus();
            default -> throw new WWaffleException("No idea 🧇\nTry \"sort name\" or \"sort status\".");
        }
        storage.save(tasks.getTasks());
        return tasks.getTasks();
    }

    /**
     * Removes all tasks and persists the empty list.
     *
     * @param command Full clear command, which must not contain arguments.
     * @return Confirmation shown after a successful save.
     * @throws WWaffleException If arguments follow the command.
     * @throws IOException If the empty list cannot be saved.
     */
    private String clearTasks(String command) throws WWaffleException, IOException {
        if (!parser.parseArgument(command, "clear").isEmpty()) {
            throw new WWaffleException("No idea 🧇\nTry: clear");
        }
        tasks.clear();
        storage.save(tasks.getTasks());
        return "Cleared!";
    }

    private Task addTask(Task task) throws IOException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return task;
    }
}
