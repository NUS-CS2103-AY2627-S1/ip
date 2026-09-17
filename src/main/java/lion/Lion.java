package lion;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Starts and coordinates the Lion task-management application.
 */
public class Lion {

    /** Prefix every error response begins with; see {@link #isErrorResponse(String)}. */
    private static final String ERROR_PREFIX = "ROAR!!! ";

    /** Farewell response for the {@code bye} command. */
    private static final String BYE_RESPONSE = "Roar! Until next time.";

    /** Header the {@code list} command's response begins with. */
    private static final String LIST_HEADER = "Here's what's in your pride:";

    /** Header the {@code find} command's response begins with. */
    private static final String FIND_HEADER = "Lion tracked down these matching tasks:";

    private TaskList tasks;
    private Parser parser;

    /**
     * Creates the application entry-point object.
     */
    public Lion() {
        parser = new Parser();

        try {
            tasks = Storage.loadTaskList();
        } catch (IOException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Formats a task list as a one-based numbered list, one task per line.
     *
     * <p>Used by both the {@code list} and {@code find} commands, which only
     * differ in their header text and which task list they number.
     *
     * @param list tasks to format.
     * @return each task on its own line prefixed with "\n" and its 1-based
     *     number, or an empty string if the list has no tasks.
     */
    private static String formatNumberedList(TaskList list) {
        return IntStream.range(0, list.size())
                .mapToObj(i -> "\n" + (i + 1) + ". " + list.get(i))
                .collect(Collectors.joining());
    }

    /**
     * Generates a response to the user's command for the GUI.
     *
     * @param input user's input
     * @return Lion's response
     */
    public String getResponse(String input) {
        try {
            // '|' is the field separator in the save file (see Storage.encode()); letting
            // it through would silently corrupt or truncate whatever task it ends up in
            // the next time the file is saved and reloaded.
            if (input.contains("|")) {
                throw new LionException(
                        "Lion can't save tasks containing '|' — it's used internally to store your tasks. "
                        + "Please remove it and try again.");
            }

            CommandType commandType = parser.getCommandType(input);

            // A switch expression, not a switch statement: the compiler checks this
            // covers every CommandType constant, so there is no default case, and
            // adding a new constant without a matching case here fails to compile
            // rather than throwing at runtime.
            return switch (commandType) {
                case LIST -> LIST_HEADER + formatNumberedList(tasks);
                case TODO -> handleTodo(input);
                case DEADLINE -> handleDeadline(input);
                case EVENT -> handleEvent(input);
                case MARK -> handleMark(input);
                case UNMARK -> handleUnmark(input);
                case DELETE -> handleDelete(input);
                case FIND -> handleFind(input);
                case HELP -> handleHelp();
                case BYE -> BYE_RESPONSE;
                case UNKNOWN -> throw new LionException(
                        "Lion didn't quite catch that.\n"
                        + "Type 'help' to see what I can do.");
            };

        } catch (LionException e) {
            return ERROR_PREFIX + e.getMessage();
        }
    }

    /**
     * Handles the {@code todo} command.
     *
     * @param input full user input.
     * @return response confirming the new task was added.
     * @throws LionException if the todo description is empty.
     */
    private String handleTodo(String input) throws LionException {
        String details = parser.getTodoDescription(input);

        if (details.isEmpty()) {
            throw new LionException("Even Lion needs something to chase — the todo description can't be empty.");
        }

        Task newTask = new Todo(details);
        boolean isDuplicate = tasks.hasDuplicate(newTask);
        tasks.add(newTask);

        return formatTaskAddedMessage(newTask, isDuplicate);
    }

    /**
     * Handles the {@code deadline} command.
     *
     * @param input full user input.
     * @return response confirming the new task was added.
     * @throws LionException if the date is missing, malformed, or names a date that
     *     does not exist (e.g. 30/2/2019).
     */
    private String handleDeadline(String input) throws LionException {
        String[] parts = parser.getDeadlineParts(input);
        Deadline.validateByText(parts[1]);

        Task newTask = new Deadline(parts[0], parts[1]);
        boolean isDuplicate = tasks.hasDuplicate(newTask);
        tasks.add(newTask);

        return formatTaskAddedMessage(newTask, isDuplicate);
    }

    /**
     * Handles the {@code event} command.
     *
     * @param input full user input.
     * @return response confirming the new task was added.
     * @throws LionException if {@code /from}/{@code /to} are missing or empty, or both
     *     parse as full date-times with the start not strictly before the end.
     */
    private String handleEvent(String input) throws LionException {
        String[] parts = parser.getEventParts(input);
        Event.validateRange(parts[1], parts[2]);

        Task newTask = new Event(parts[0], parts[1], parts[2]);
        boolean isDuplicate = tasks.hasDuplicate(newTask);
        tasks.add(newTask);

        return formatTaskAddedMessage(newTask, isDuplicate);
    }

    /**
     * Handles the {@code mark} command.
     *
     * @param input full user input.
     * @return response confirming the task was marked as done.
     * @throws LionException if no valid task number is given, or it is out of range.
     */
    private String handleMark(String input) throws LionException {
        int taskNumber = parser.getTaskIndex(input, Parser.MARK_PREFIX_LENGTH);
        requireValidIndex(taskNumber);
        tasks.mark(taskNumber);

        return "Lion's proud of you — task conquered:\n"
                + "[X] "
                + tasks.get(taskNumber).getDescription()
                + saveTasks();
    }

    /**
     * Handles the {@code unmark} command.
     *
     * @param input full user input.
     * @return response confirming the task was marked as not done.
     * @throws LionException if no valid task number is given, or it is out of range.
     */
    private String handleUnmark(String input) throws LionException {
        int taskNumber = parser.getTaskIndex(input, Parser.UNMARK_OR_DELETE_PREFIX_LENGTH);
        requireValidIndex(taskNumber);
        tasks.unmark(taskNumber);

        return "Back to the hunt — task reopened:\n"
                + "[ ] "
                + tasks.get(taskNumber).getDescription()
                + saveTasks();
    }

    /**
     * Handles the {@code delete} command.
     *
     * @param input full user input.
     * @return response confirming the task was removed.
     * @throws LionException if no valid task number is given, or it is out of range.
     */
    private String handleDelete(String input) throws LionException {
        int taskNumber = parser.getTaskIndex(input, Parser.UNMARK_OR_DELETE_PREFIX_LENGTH);
        requireValidIndex(taskNumber);
        Task deletedTask = tasks.delete(taskNumber);

        return "Lion's let this one go:\n"
                + deletedTask
                + "\nYour pride now has "
                + tasks.size()
                + " tasks"
                + saveTasks();
    }

    /**
     * Validates a zero-based task index against the current task list, so callers never
     * pass an out-of-range index on to {@link TaskList}.
     *
     * @param index zero-based task index to validate.
     * @throws LionException if the index is negative or beyond the last task.
     */
    private void requireValidIndex(int index) throws LionException {
        if (index < 0 || index >= tasks.size()) {
            throw new LionException("Task " + (index + 1) + " doesn't exist — your pride only has "
                    + tasks.size() + " task(s).");
        }
    }

    /**
     * Handles the {@code find} command.
     *
     * @param input full user input.
     * @return response listing the matching tasks.
     * @throws LionException if the find keyword is empty.
     */
    private String handleFind(String input) throws LionException {
        String keyword = parser.getFindKeyword(input);

        if (keyword.isEmpty()) {
            throw new LionException("Give Lion a scent to follow — the search keyword can't be empty.");
        }

        TaskList matches = tasks.find(keyword);

        return FIND_HEADER + formatNumberedList(matches);
    }

    /**
     * Handles the {@code help} command.
     *
     * @return response listing every available command and its description.
     */
    private static String handleHelp() {
        return "Here are the available commands:" + Arrays.stream(CommandType.values())
                .filter(type -> type != CommandType.UNKNOWN)
                .map(type -> "\n" + type.name().toLowerCase() + " - " + type.getDescription())
                .collect(Collectors.joining());
    }

    /**
     * Formats the confirmation message shown after a task is added.
     *
     * @param newTask task that was just added.
     * @param isDuplicate true if an existing task already has the same details, so the
     *     user can be warned without blocking the (still valid) add.
     * @return confirmation message, including a duplicate notice and/or a save-failure
     *     notice if either applies.
     */
    private String formatTaskAddedMessage(Task newTask, boolean isDuplicate) {
        String duplicateNotice = isDuplicate
                ? "Heads up: this looks identical to a task already in your pride.\n"
                : "";

        return duplicateNotice
                + "Roar! Added to your pride:\n"
                + newTask
                + "\nYour pride now has "
                + tasks.size()
                + " tasks"
                + saveTasks();
    }

    /**
     * Returns whether a response produced by {@link #getResponse(String)} represents an error.
     *
     * @param response response text to check.
     * @return true if the response is a standalone error message, i.e. it begins with the
     *     standard error prefix rather than merely mentioning a failure partway through
     *     an otherwise successful response (e.g. a save failure appended after a task
     *     was still added).
     */
    public static boolean isErrorResponse(String response) {
        return response.startsWith(ERROR_PREFIX);
    }

    /**
     * Returns whether a response produced by {@link #getResponse(String)} should close the GUI.
     *
     * @param response response text to check.
     * @return true if the response is the standard farewell response.
     */
    public static boolean isByeResponse(String response) {
        return response.equals(BYE_RESPONSE);
    }

    /**
     * Returns whether a response produced by {@link #getResponse(String)} is a numbered
     * task list (from {@code list} or {@code find}), so the GUI can give it a visually
     * distinct, scan-friendly style from ordinary conversational replies.
     *
     * @param response response text to check.
     * @return true if the response begins with the {@code list} or {@code find} header.
     */
    public static boolean isTaskListResponse(String response) {
        return response.startsWith(LIST_HEADER) || response.startsWith(FIND_HEADER);
    }

    /**
     * Saves the current task list.
     *
     * @return a save-failure notice to append to the response, or an empty
     *     string if the tasks were saved successfully.
     */
    private String saveTasks() {
        try {
            tasks.save();
            return "";
        } catch (IOException e) {
            return "\n" + ERROR_PREFIX + "Lion couldn't stash your tasks safely: " + e.getMessage();
        }
    }
}
