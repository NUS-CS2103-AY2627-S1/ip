package tt;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tt.storage.StorageException;
import tt.storage.TaskStorage;
import tt.task.Deadline;
import tt.task.Event;
import tt.task.Task;
import tt.task.Todo;

/**
 * Processes task-management commands and generates user-facing responses.
 */
public class TT {
    private static final String MESSAGE_ERROR_PREFIX = "I couldn't process that: ";
    private static final Pattern SEPARATOR_DEADLINE = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern SEPARATOR_EVENT_FROM = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern SEPARATOR_EVENT_TO = Pattern.compile("(?<!\\S)/to(?!\\S)");
    private final ArrayList<Task> tasks;
    private final TaskStorage storage;
    private String storageError;

    /**
     * Creates a task manager backed by the default save file.
     */
    public TT() {
        this(new TaskStorage());
    }

    /**
     * Creates a task manager backed by the supplied storage.
     *
     * @param storage storage used to load and save tasks.
     */
    public TT(TaskStorage storage) {
        assert storage != null : "Storage must not be null";
        this.storage = storage;
        ArrayList<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (StorageException e) {
            loadedTasks = new ArrayList<>();
            storageError = e.getMessage();
        }
        tasks = loadedTasks;
        assert tasks != null : "Storage must return a task list";
    }

    /**
     * Processes one user command and returns the chatbot's response.
     *
     * @param input command entered by the user.
     * @return user-facing response to the command.
     */
    public String getResponse(String input) {
        return getCommandResult(input).message();
    }

    /**
     * Processes one command and includes whether its response represents an error.
     */
    CommandResult getCommandResult(String input) {
        if (input == null) {
            return CommandResult.error(MESSAGE_ERROR_PREFIX + "please enter a command.");
        }

        String trimmedInput = input.strip();
        if (trimmedInput.isEmpty()) {
            return CommandResult.error(MESSAGE_ERROR_PREFIX + "please enter a command.");
        }

        String[] commandParts = trimmedInput.split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length == 2
                ? commandParts[1].replaceAll("\\s+", " ").trim()
                : "";
        Command command = Command.fromString(commandWord);

        if (storageError != null && command != Command.BYE) {
            return CommandResult.error(getStorageErrorMessage());
        }

        try {
            return CommandResult.success(execute(command, arguments));
        } catch (TTException e) {
            return CommandResult.error(MESSAGE_ERROR_PREFIX + e.getMessage());
        } catch (StorageException e) {
            storageError = e.getMessage();
            return CommandResult.error(getStorageErrorMessage());
        }
    }

    /**
     * Returns a startup storage warning, or {@code null} when storage loaded successfully.
     */
    String getStartupError() {
        return storageError == null ? null : getStorageErrorMessage();
    }

    private String execute(Command command, String rest) throws TTException {
        assert command != null : "Command must not be null";
        assert rest != null : "Command arguments must not be null";

        return switch (command) {
            case BYE -> exit(rest);
            case LIST -> listTasks(rest);
            case FIND -> findTasks(rest);
            case SORT -> sortTasks(rest);
            case MARK -> setDone(rest, true);
            case UNMARK -> setDone(rest, false);
            case DELETE -> deleteTask(rest);
            case CLEAR -> clearTasks(rest);
            case TODO -> addTodo(rest);
            case DEADLINE -> addDeadline(rest);
            case EVENT -> addEvent(rest);
            case UNKNOWN -> throw new TTException("I don't recognize that command. Try list, todo, deadline, "
                    + "event, find, sort, mark, unmark, delete, clear, or bye.");
        };
    }

    private String exit(String arguments) throws TTException {
        ensureNoArguments("bye", arguments);
        return "Bye. Hope to see you again soon!";
    }

    private String listTasks(String arguments) throws TTException {
        ensureNoArguments("list", arguments);
        return formatTasks(tasks, "Here are the tasks in your list:");
    }

    private String findTasks(String keywordText) throws TTException {
        if (keywordText.isEmpty()) {
            throw new TTException("tell me what to find, for example: find book.");
        }

        String keyword = keywordText.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getTask().toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        return formatTasks(matchingTasks, "Here are the matching tasks:");
    }

    private String sortTasks(String arguments) throws TTException {
        ensureNoArguments("sort", arguments);

        ArrayList<Task> originalTasks = new ArrayList<>(tasks);
        tasks.sort(Comparator.comparing(Task::getTask, String.CASE_INSENSITIVE_ORDER));
        saveTasksOrRestore(originalTasks);
        return formatTasks(tasks, "Tasks sorted alphabetically:");
    }

    private String setDone(String numberText, boolean isDone) throws TTException {
        Task task = tasks.get(parseIndex(numberText, tasks.size()));
        boolean wasDone = task.isDone();
        if (isDone) {
            task.mark();
        } else {
            task.unmark();
        }
        try {
            storage.save(tasks);
        } catch (StorageException e) {
            if (wasDone) {
                task.mark();
            } else {
                task.unmark();
            }
            throw e;
        }

        String message = isDone
                ? "Nice! I've marked this task as done:\n"
                : "Back in motion—I've marked this task as not done yet:\n";
        return message + task;
    }

    private String deleteTask(String numberText) throws TTException {
        ArrayList<Task> originalTasks = new ArrayList<>(tasks);
        Task removedTask = tasks.remove(parseIndex(numberText, tasks.size()));
        saveTasksOrRestore(originalTasks);
        return "Noted. I've removed this task:\n" + removedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String clearTasks(String taskTypeText) throws TTException {
        String taskType = taskTypeText.toLowerCase(Locale.ROOT);
        ArrayList<Task> originalTasks = new ArrayList<>(tasks);
        int originalTaskCount = tasks.size();

        switch (taskType) {
            case "todo", "todos" -> tasks.removeIf(task -> task instanceof Todo);
            case "deadline", "deadlines" -> tasks.removeIf(task -> task instanceof Deadline);
            case "event", "events" -> tasks.removeIf(task -> task instanceof Event);
            case "all", "task", "tasks" -> tasks.clear();
            case "" -> throw new TTException("tell me what to clear: todo, deadline, event, or all.");
            default -> throw new TTException("I can clear only todo, deadline, event, or all tasks.");
        }

        int clearedTaskCount = originalTaskCount - tasks.size();
        saveTasksOrRestore(originalTasks);
        return "Cleared " + clearedTaskCount + " " + getClearDescription(taskType, clearedTaskCount) + ".";
    }

    private String getClearDescription(String taskType, int clearedTaskCount) {
        String description = switch (taskType) {
            case "todo", "todos" -> "todo task";
            case "deadline", "deadlines" -> "deadline task";
            case "event", "events" -> "event task";
            default -> "task";
        };
        return clearedTaskCount == 1 ? description : description + "s";
    }

    private String addTodo(String description) throws TTException {
        validateDescription(description, "todo");
        return addTask(new Todo(description));
    }

    private String addDeadline(String details) throws TTException {
        int separator = findSingleSeparator(details, SEPARATOR_DEADLINE);
        if (separator < 0) {
            throw new TTException("a deadline needs exactly one '/by' date, for example: "
                    + "deadline return book /by 2026-09-15.");
        }

        String description = details.substring(0, separator).trim();
        String dateText = details.substring(separator + 3).trim();
        validateDescription(description, "deadline");
        if (dateText.isEmpty()) {
            throw new TTException("the /by date of a deadline cannot be empty.");
        }

        try {
            return addTask(new Deadline(description, LocalDate.parse(dateText)));
        } catch (DateTimeParseException e) {
            throw new TTException("use a real deadline date in yyyy-MM-dd format, for example: 2026-09-15.");
        }
    }

    private String addEvent(String details) throws TTException {
        int fromSeparator = findSingleSeparator(details, SEPARATOR_EVENT_FROM);
        int toSeparator = findSingleSeparator(details, SEPARATOR_EVENT_TO);
        if (fromSeparator < 0 || toSeparator < 0 || fromSeparator > toSeparator) {
            throw new TTException("an event needs one '/from' followed by one '/to', for example: "
                    + "event meeting /from Mon 2pm /to 4pm.");
        }

        String description = details.substring(0, fromSeparator).trim();
        String from = details.substring(fromSeparator + 5, toSeparator).trim();
        String to = details.substring(toSeparator + 3).trim();
        validateDescription(description, "event");
        if (from.isEmpty() || to.isEmpty()) {
            throw new TTException("the /from and /to times of an event cannot be empty.");
        }
        return addTask(new Event(description, from, to));
    }

    private String addTask(Task task) {
        assert task != null : "Task to add must not be null";

        ArrayList<Task> originalTasks = new ArrayList<>(tasks);
        tasks.add(task);
        saveTasksOrRestore(originalTasks);
        return "Got it. I've added this task:\n" + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String formatTasks(List<Task> tasksToFormat, String heading) {
        assert tasksToFormat != null : "Task list to format must not be null";
        assert heading != null && !heading.isBlank() : "Task list heading must not be blank";

        if (tasksToFormat.isEmpty()) {
            return heading + "\nThere are no tasks to show.";
        }

        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasksToFormat.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(tasksToFormat.get(i));
        }
        return response.toString();
    }

    private int parseIndex(String numberText, int taskCount) throws TTException {
        int index;
        try {
            index = Integer.parseInt(numberText.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new TTException("enter one valid task number, for example: mark 2.");
        }

        if (index < 0 || index >= taskCount) {
            throw new TTException("task " + (index + 1) + " isn't in your list.");
        }
        assert index >= 0 && index < taskCount : "Parsed task index must be within bounds";
        return index;
    }

    private void ensureNoArguments(String commandWord, String arguments) throws TTException {
        if (!arguments.isEmpty()) {
            throw new TTException("the " + commandWord + " command does not take extra details.");
        }
    }

    private void validateDescription(String description, String taskType) throws TTException {
        if (description.isEmpty()) {
            throw new TTException("the description of a " + taskType + " cannot be empty.");
        }
        if (description.contains("|")) {
            throw new TTException("task descriptions cannot contain the '|' character.");
        }
    }

    private int findSingleSeparator(String text, Pattern separatorPattern) {
        Matcher matcher = separatorPattern.matcher(text);
        if (!matcher.find()) {
            return -1;
        }
        int separatorIndex = matcher.start();
        return matcher.find() ? -1 : separatorIndex;
    }

    private void saveTasksOrRestore(ArrayList<Task> originalTasks) {
        try {
            storage.save(tasks);
        } catch (StorageException e) {
            tasks.clear();
            tasks.addAll(originalTasks);
            throw e;
        }
    }

    private String getStorageErrorMessage() {
        return "I can't safely use your saved tasks. " + storageError;
    }
}
