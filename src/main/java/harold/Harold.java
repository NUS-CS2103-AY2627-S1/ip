package harold;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import harold.command.CommandType;
import harold.storage.Storage;
import harold.task.Deadline;
import harold.task.Event;
import harold.task.Task;
import harold.task.TaskDate;
import harold.task.TaskList;
import harold.task.Todo;
import harold.ui.Ui;

/**
 * Coordinates Harold's command handling and task storage independently of the user interface.
 */
public class Harold {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "harold.txt");
    private static final int MAX_SIMILAR_MATCHES = 3;
    private static final String ERROR_PREFIX = "i pooped on myself... ";
    private static final String EXIT_MESSAGE =
            "i miss my owner, hope hes back soon, SIKE!";
    private static final String FETCH_MESSAGE =
            "nomnomnom ball! I've fetched this task:";

    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";
    private static final String BY_MARKER = " /by";
    private static final String FROM_MARKER = " /from";
    private static final String TO_MARKER = " /to";

    private final Storage storage;
    private final TaskList tasks;
    private final String loadMessage;

    /**
     * Creates Harold using the default task data file.
     */
    public Harold() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates Harold using the given task data file.
     *
     * @param filePath Path from which tasks are loaded and to which changes are saved.
     */
    public Harold(Path filePath) {
        this(new Storage(filePath));
    }

    /**
     * Creates Harold using the supplied storage implementation.
     *
     * @param storage Storage from which tasks are loaded and to which changes are saved.
     */
    Harold(Storage storage) {
        this.storage = Objects.requireNonNull(storage);

        TaskList loadedTasks;
        String loadingWarning = null;
        try {
            Storage.LoadResult loadResult = storage.load();
            loadedTasks = new TaskList(loadResult.tasks());
            int skippedTaskCount = loadResult.skippedLineCount()
                    + loadedTasks.getDiscardedTaskCount();
            if (skippedTaskCount > 0) {
                loadingWarning = "I skipped " + skippedTaskCount
                        + " invalid task record(s) while loading your data.";
            }
        } catch (IOException e) {
            loadingWarning = "I couldn't load your saved tasks, so I started with an empty list.";
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        loadMessage = loadingWarning;
    }

    /**
     * Runs Harold using the original command-line interface.
     *
     * @param args Command-line arguments, which Harold does not currently use.
     */
    public static void main(String[] args) {
        Harold harold = new Harold();
        Ui ui = new Ui();
        ui.showWelcome(harold.getLoadMessage());

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            CommandResult result = harold.respond(command);
            ui.showMessage(result.message());
            ui.showSeparator();
            if (result.isExit()) {
                return;
            }
        }
    }

    /**
     * Returns a warning encountered while loading tasks, if any.
     *
     * @return Loading warning, or {@code null} when loading succeeded.
     */
    public String getLoadMessage() {
        return loadMessage;
    }

    /**
     * Executes one user command and returns the response for display by any interface.
     *
     * @param command Full command entered by the user.
     * @return Response message and whether the interface should stop accepting commands.
     */
    public CommandResult respond(String command) {
        try {
            return executeCommand(normalizeCommand(command));
        } catch (HaroldException e) {
            return CommandResult.continueWith(ERROR_PREFIX + e.getMessage());
        } catch (IOException e) {
            return CommandResult.continueWith(
                    ERROR_PREFIX + "I couldn't save your tasks. Please try again."
            );
        }
    }

    /**
     * Executes one validated command and persists task-list changes.
     */
    private CommandResult executeCommand(String command) throws HaroldException, IOException {
        if (command.isBlank()) {
            throw new HaroldException("Please enter a command pls.");
        }

        CommandType commandType = getCommandType(command);
        return switch (commandType) {
            case BYE -> CommandResult.exitWith(EXIT_MESSAGE);
            case LIST -> executeList(command);
            case MARK -> executeMark(command);
            case UNMARK -> executeUnmark(command);
            case DELETE -> executeDelete(command);
            case TODO -> executeTodo(command);
            case DEADLINE -> executeDeadline(command);
            case EVENT -> executeEvent(command);
            case FIND -> executeFind(command);
            case UNKNOWN -> throw new HaroldException(
                    "I don't know what '" + command + "' means. "
                            + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
            default -> throw new AssertionError("Unhandled command type: " + commandType);
        };
    }

    /**
     * Validates and executes a list command.
     */
    private CommandResult executeList(String command) throws HaroldException {
        if (!command.equals("list")) {
            throw new HaroldException("The list command does not accept extra text.");
        }
        return CommandResult.continueWith(formatTasks("Here are the tasks in your list:", tasks));
    }

    /**
     * Marks the requested task as complete.
     */
    private CommandResult executeMark(String command) throws HaroldException, IOException {
        int index = parseTaskIndex(command, "mark", tasks.size());
        boolean wasDone = tasks.get(index).isDone();
        Task task = tasks.mark(index);
        try {
            storage.save(tasks);
        } catch (IOException e) {
            restoreCompletion(task, wasDone);
            throw e;
        }
        return CommandResult.continueWith("Nice! I've marked this task as done:\n  " + task);
    }

    /**
     * Marks the requested task as incomplete.
     */
    private CommandResult executeUnmark(String command) throws HaroldException, IOException {
        int index = parseTaskIndex(command, "unmark", tasks.size());
        boolean wasDone = tasks.get(index).isDone();
        Task task = tasks.unmark(index);
        try {
            storage.save(tasks);
        } catch (IOException e) {
            restoreCompletion(task, wasDone);
            throw e;
        }
        return CommandResult.continueWith("OK, I've marked this task as not done yet:\n  " + task);
    }

    /**
     * Deletes the requested task.
     */
    private CommandResult executeDelete(String command) throws HaroldException, IOException {
        int index = parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(index);
        try {
            storage.save(tasks);
        } catch (IOException e) {
            tasks.restoreDeletedTask(index, removedTask);
            throw e;
        }
        return CommandResult.continueWith(
                "DELETED. I've removed this task:\n  " + removedTask + "\n" + formatTaskCount());
    }

    /**
     * Creates and stores a todo task.
     */
    private CommandResult executeTodo(String command) throws HaroldException, IOException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        requireDescription(description, TODO_COMMAND);
        return addTask(new Todo(description));
    }

    /**
     * Creates and stores a deadline task.
     */
    private CommandResult executeDeadline(String command) throws HaroldException, IOException {
        String syntaxError = "A deadline needs '/by <date or time>'. "
                + "Try: deadline <description> /by <date or time>";
        int byIndex = findSingleMarker(
                command,
                BY_MARKER,
                0,
                syntaxError,
                "Use /by only once in a deadline command."
        );
        assert byIndex >= 0
                : "A validated deadline command must contain a /by delimiter";

        String description = command.substring(DEADLINE_COMMAND.length(), byIndex).trim();
        String byText = command.substring(byIndex + BY_MARKER.length()).trim();
        requireDescription(description, DEADLINE_COMMAND);
        if (byText.isEmpty()) {
            throw new HaroldException("Please enter a date after /by.");
        }
        LocalDate by = parseDate(byText, "/by");
        return addTask(new Deadline(description, by));
    }

    /**
     * Creates and stores an event task.
     */
    private CommandResult executeEvent(String command) throws HaroldException, IOException {
        String fromSyntaxError = "An event needs '/from <start>'. "
                + "Try: event <description> /from <start> /to <end>";
        int fromIndex = findSingleMarker(
                command,
                FROM_MARKER,
                0,
                fromSyntaxError,
                "Use /from only once in an event command."
        );
        if (command.indexOf(TO_MARKER) >= 0 && command.indexOf(TO_MARKER) < fromIndex) {
            throw new HaroldException("Place /from before /to in an event command.");
        }
        String toSyntaxError = "An event needs '/to <end>'. "
                + "Try: event <description> /from <start> /to <end>";
        int toIndex = findSingleMarker(
                command,
                TO_MARKER,
                fromIndex + FROM_MARKER.length(),
                toSyntaxError,
                "Use /to only once in an event command."
        );
        assert fromIndex >= 0
                : "A validated event command must contain a /from delimiter";
        assert toIndex > fromIndex
                : "A validated event command must place /to after /from";

        String description = command.substring(EVENT_COMMAND.length(), fromIndex).trim();
        String fromText = command.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String toText = command.substring(toIndex + TO_MARKER.length()).trim();
        requireDescription(description, EVENT_COMMAND);
        if (fromText.isEmpty()) {
            throw new HaroldException("Please enter a start date after /from.");
        } else if (toText.isEmpty()) {
            throw new HaroldException("Please enter an end date after /to.");
        }
        LocalDate from = parseDate(fromText, "/from");
        LocalDate to = parseDate(toText, "/to");
        if (to.isBefore(from)) {
            throw new HaroldException("The event end date cannot be before its start date.");
        }
        return addTask(new Event(description, from, to));
    }

    /**
     * Finds tasks whose descriptions contain the requested keyword.
     */
    private CommandResult executeFind(String command) throws HaroldException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new HaroldException("Please enter a keyword after find. Try: find <keyword>");
        }

        List<Task> exactMatches = tasks.find(keyword);
        if (!exactMatches.isEmpty()) {
            return CommandResult.continueWith(
                    formatTasks("Here are the matching tasks in your list:", exactMatches));
        }

        List<Task> similarMatches = tasks.findSimilar(keyword, MAX_SIMILAR_MATCHES);
        if (!similarMatches.isEmpty()) {
            return CommandResult.continueWith(
                    formatTasks(
                            "I couldn't find an exact match. Here are the most similar tasks:",
                            similarMatches));
        }
        return CommandResult.continueWith(
                "I couldn't find any tasks matching '" + keyword + "'.");
    }

    /**
     * Adds and persists one task, then formats its confirmation message.
     */
    private CommandResult addTask(Task task) throws HaroldException, IOException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (IOException e) {
            tasks.delete(tasks.size() - 1);
            throw e;
        }
        return CommandResult.continueWith(
                FETCH_MESSAGE + "\n  " + task + "\n" + formatTaskCount());
    }

    /**
     * Formats a heading followed by a numbered task list.
     */
    private static String formatTasks(String heading, TaskList tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        return response.toString();
    }

    /**
     * Formats a heading followed by a numbered task collection.
     */
    private static String formatTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }
        return response.toString();
    }

    /**
     * Formats the current task count using the correct singular or plural noun.
     */
    private String formatTaskCount() {
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        return "Now you have " + tasks.size() + " " + taskWord + " in the list.";
    }

    /**
     * Returns the type of command entered by the user.
     */
    private static CommandType getCommandType(String command) {
        return isByeCommand(command) ? CommandType.BYE : CommandType.fromCommand(command);
    }

    /**
     * Parses and validates the one-based task number supplied to a task command.
     */
    private static int parseTaskIndex(String command, String commandWord, int taskCount)
            throws HaroldException {
        String numberText = command.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new HaroldException("Please enter a task number after " + commandWord + ".");
        }
        if (taskCount == 0) {
            throw new HaroldException("Your task list is empty, so there is nothing to "
                    + commandWord + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new HaroldException(
                    "'" + numberText + "' is not a valid task number. "
                            + "Try: " + commandWord + " <task number>");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new HaroldException(
                    "Task " + taskNumber + " does not exist. "
                            + "Choose a number from 1 to " + taskCount + ".");
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A validated task number must map to an existing zero-based index";
        return taskIndex;
    }

    /**
     * Finds a command marker and verifies that it is followed by whitespace or the command end.
     */
    private static int findSingleMarker(
            String command,
            String marker,
            int startIndex,
            String errorMessage,
            String duplicateErrorMessage
    ) throws HaroldException {
        int markerIndex = command.indexOf(marker, startIndex);
        if (markerIndex < 0) {
            throw new HaroldException(errorMessage);
        }

        int markerEndIndex = markerIndex + marker.length();
        boolean isAtCommandEnd = markerEndIndex == command.length();
        boolean isFollowedByWhitespace = !isAtCommandEnd
                && Character.isWhitespace(command.charAt(markerEndIndex));
        if (!isAtCommandEnd && !isFollowedByWhitespace) {
            throw new HaroldException(errorMessage);
        }
        if (command.indexOf(marker, markerEndIndex) >= 0) {
            throw new HaroldException(duplicateErrorMessage);
        }
        return markerIndex;
    }

    /**
     * Converts user-entered whitespace to a consistent command representation.
     */
    private static String normalizeCommand(String command) {
        return command == null ? "" : command.strip().replaceAll("\\s+", " ");
    }

    /**
     * Restores a task's completion state after a failed persistence operation.
     */
    private static void restoreCompletion(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Ensures that a task command contains a description.
     */
    private static void requireDescription(String description, String taskType)
            throws HaroldException {
        if (description.isEmpty()) {
            String article = taskType.equals("event") ? "an" : "a";
            throw new HaroldException(
                    "The description of " + article + " " + taskType + " cannot be empty. "
                            + "Try: " + taskType + " <description>");
        }
    }

    /**
     * Parses a task date and reports the required input format when parsing fails.
     */
    private static LocalDate parseDate(String dateText, String commandMarker)
            throws HaroldException {
        try {
            return TaskDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new HaroldException(
                    "Please enter the date after " + commandMarker
                            + " as yyyy-MM-dd, for example 2019-10-15."
            );
        }
    }

    /**
     * Returns true when the command contains only the letters in "bye",
     * regardless of their order, capitalization, or number of repetitions.
     * Each of the three letters must occur at least once.
     */
    private static boolean isByeCommand(String command) {
        boolean hasB = false;
        boolean hasY = false;
        boolean hasE = false;

        for (int i = 0; i < command.length(); i++) {
            char character = Character.toLowerCase(command.charAt(i));
            switch (character) {
                case 'b':
                    hasB = true;
                    break;
                case 'y':
                    hasY = true;
                    break;
                case 'e':
                    hasE = true;
                    break;
                default:
                    return false;
            }
        }

        return hasB && hasY && hasE;
    }
}
