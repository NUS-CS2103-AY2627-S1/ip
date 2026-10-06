package moon;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Provides Moon's task-management commands independently of a user interface. */
public class MoonEngine {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String UNDO_COMMAND = "undo";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Hmm, I don't know that command yet."
            + " Try todo, deadline, event, find, list, mark, unmark, delete, undo, or bye.";

    private final Storage storage;
    private final List<Task> tasks;
    private final boolean loadingError;
    private List<String> previousTaskState;

    /** Creates an engine and loads Moon's saved tasks. */
    public MoonEngine() {
        this(new Storage());
    }

    /** Creates an engine backed by the given storage. */
    MoonEngine(Storage storage) {
        this.storage = storage;
        List<Task> loadedTasks;
        boolean failedToLoad;
        try {
            loadedTasks = storage.load();
            failedToLoad = false;
        } catch (IOException | SecurityException exception) {
            loadedTasks = new ArrayList<>();
            failedToLoad = true;
        }
        tasks = loadedTasks;
        loadingError = failedToLoad;
        previousTaskState = null;
    }

    /**
     * Returns whether Moon had to start with an empty list because saved data could not be read.
     *
     * @return whether loading saved tasks failed
     */
    public boolean hasLoadingError() {
        return loadingError;
    }

    /**
     * Processes one command and returns the response for a user interface to display.
     *
     * @param command the command entered by the user
     * @return Moon's response text
     */
    public String getResponse(String command) {
        String normalizedCommand = normalizeCommand(command);
        if (normalizedCommand.isEmpty()) {
            return "Oof! Please enter a command — don't leave me hanging." + System.lineSeparator();
        }
        if (normalizedCommand.equals(BYE_COMMAND)) {
            return "Aight, catch you later ✌️";
        }
        try {
            return processCommand(normalizedCommand);
        } catch (MoonException exception) {
            return "Oof! " + exception.getMessage() + System.lineSeparator();
        } catch (IOException | SecurityException exception) {
            return "Oof! I couldn't save your task list — the save got a little scuffed." + System.lineSeparator();
        }
    }

    private String normalizeCommand(String command) {
        return command == null ? "" : command.strip().replaceAll("\\s+", " ");
    }

    private String processCommand(String command) throws MoonException, IOException {
        if (command.equals(LIST_COMMAND)) {
            return listTasks();
        }
        if (isCommand(command, FIND_COMMAND)) {
            return findTasks(command);
        }
        if (isCommand(command, TODO_COMMAND)) {
            return addTodo(command);
        }
        if (isCommand(command, DEADLINE_COMMAND)) {
            return addTask(parseDeadline(command));
        }
        if (isCommand(command, EVENT_COMMAND)) {
            return addTask(parseEvent(command));
        }
        if (isCommand(command, MARK_COMMAND)) {
            return markTask(command);
        }
        if (isCommand(command, UNMARK_COMMAND)) {
            return unmarkTask(command);
        }
        if (isCommand(command, DELETE_COMMAND)) {
            return deleteTask(command);
        }
        if (command.equals(UNDO_COMMAND)) {
            return undoLastCommand();
        }
        throw new MoonException(UNKNOWN_COMMAND_MESSAGE);
    }

    private Deadline parseDeadline(String command) throws MoonException {
        String details = command.substring(DEADLINE_COMMAND.length()).trim();
        if (hasDuplicateParameter(details, BY_SEPARATOR)) {
            throw new MoonException("a deadline can only have one /by parameter.");
        }
        int byIndex = details.indexOf(BY_SEPARATOR);
        if (byIndex < 0) {
            throw new MoonException("a deadline needs /by followed by its due date or time, fr.");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new MoonException("your deadline needs a description before /by — give me something to work with.");
        }
        validateDescription(description, "deadline");
        if (by.isEmpty()) {
            throw new MoonException("your deadline needs a date or time after /by.");
        }
        return new Deadline(description, parseDate(by));
    }

    private Event parseEvent(String command) throws MoonException {
        String details = command.substring(EVENT_COMMAND.length()).trim();
        if (hasDuplicateParameter(details, FROM_SEPARATOR)) {
            throw new MoonException("an event can only have one /from parameter.");
        }
        if (hasDuplicateParameter(details, TO_SEPARATOR)) {
            throw new MoonException("an event can only have one /to parameter.");
        }
        int fromIndex = details.indexOf(FROM_SEPARATOR);
        int toIndex = details.indexOf(TO_SEPARATOR);
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new MoonException("an event needs both /from and /to times.");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new MoonException("your event needs a description before /from.");
        }
        validateDescription(description, "event");
        if (from.isEmpty() || to.isEmpty()) {
            throw new MoonException("your event needs both a start time and an end time.");
        }
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        if (!fromDate.isBefore(toDate)) {
            throw new MoonException("an event's /from date must be before its /to date.");
        }
        return new Event(description, fromDate, toDate);
    }

    private LocalDate parseDate(String dateText) throws MoonException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new MoonException("use a date in yyyy-MM-dd format, for example: 2019-12-02 — easy peasy.");
        }
    }

    private int findTaskIndex(String command, String action) throws MoonException {
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new MoonException("tell me which task number to " + action + ".");
        }
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new MoonException("task " + taskNumber + " isn't in your list yet.");
            }
            assert taskNumber - 1 >= 0 && taskNumber - 1 < tasks.size()
                    : "A validated task number must produce a valid list index.";
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new MoonException("use a task number after " + action + ", for example: "
                    + action + " 1.");
        }
    }

    private boolean isCommand(String command, String commandName) {
        return command.equals(commandName) || command.startsWith(commandName + " ");
    }

    private boolean hasDuplicateParameter(String details, String parameter) {
        return details.indexOf(parameter) != details.lastIndexOf(parameter);
    }

    private void validateDescription(String description, String taskType) throws MoonException {
        if (description.contains("|")) {
            throw new MoonException("your " + taskType + " description cannot contain the | character.");
        }
    }

    private String listTasks() {
        StringBuilder response = new StringBuilder();
        printTaskList(response);
        return response.toString();
    }

    private String findTasks(String command) throws MoonException {
        StringBuilder response = new StringBuilder();
        printMatchingTasks(command, response);
        return response.toString();
    }

    private String addTodo(String command) throws MoonException, IOException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new MoonException("your todo needs a description — drop the details here.");
        }
        validateDescription(description, "todo");
        return addTask(new ToDo(description));
    }

    private String markTask(String command) throws MoonException, IOException {
        int taskIndex = findTaskIndex(command, MARK_COMMAND);
        List<String> taskStateBeforeMutation = snapshotTaskState();
        List<String> undoStateBeforeMutation = previousTaskState;
        rememberCurrentState();
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        saveWithRollback(taskStateBeforeMutation, undoStateBeforeMutation);

        StringBuilder response = new StringBuilder();
        appendLine(response, " Bet. Marked this task as done:");
        appendLine(response, "   " + task);
        return response.toString();
    }

    private String unmarkTask(String command) throws MoonException, IOException {
        int taskIndex = findTaskIndex(command, UNMARK_COMMAND);
        List<String> taskStateBeforeMutation = snapshotTaskState();
        List<String> undoStateBeforeMutation = previousTaskState;
        rememberCurrentState();
        Task task = tasks.get(taskIndex);
        task.unmarkAsDone();
        saveWithRollback(taskStateBeforeMutation, undoStateBeforeMutation);

        StringBuilder response = new StringBuilder();
        appendLine(response, "Got you. This task is back to not done:");
        appendLine(response, "   " + task);
        return response.toString();
    }

    private String deleteTask(String command) throws MoonException, IOException {
        int taskIndex = findTaskIndex(command, DELETE_COMMAND);
        List<String> taskStateBeforeMutation = snapshotTaskState();
        List<String> undoStateBeforeMutation = previousTaskState;
        rememberCurrentState();
        Task removedTask = tasks.remove(taskIndex);
        saveWithRollback(taskStateBeforeMutation, undoStateBeforeMutation);

        StringBuilder response = new StringBuilder();
        appendLine(response, " Say less. Deleted this task:");
        appendLine(response, "   " + removedTask);
        appendLine(response, " You have " + tasks.size() + " tasks left in the list.");
        return response.toString();
    }

    private void printTaskList(StringBuilder response) {
        appendLine(response, " Here's the current tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            appendLine(response, " " + (i + 1) + "." + tasks.get(i));
        }
    }

    private void printMatchingTasks(String command, StringBuilder response) throws MoonException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new MoonException("your find needs a keyword.");
        }

        appendLine(response, " Found these matching tasks:");
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            Task task = tasks.get(taskIndex);
            if (task.matchesKeyword(keyword)) {
                appendLine(response, " " + (taskIndex + 1) + "." + task);
            }
        }
    }

    private String addTask(Task task) throws MoonException, IOException {
        boolean duplicateTask = tasks.stream()
                .anyMatch(existingTask -> existingTask.hasSameDetailsAs(task));
        if (duplicateTask) {
            throw new MoonException("that exact task is already on your list.");
        }
        List<String> taskStateBeforeMutation = snapshotTaskState();
        List<String> undoStateBeforeMutation = previousTaskState;
        rememberCurrentState();
        tasks.add(task);
        saveWithRollback(taskStateBeforeMutation, undoStateBeforeMutation);

        StringBuilder response = new StringBuilder();
        appendLine(response, " Bet. Added this task:");
        appendLine(response, "   " + task);
        appendLine(response, " You have " + tasks.size() + " tasks in the list now.");
        return response.toString();
    }

    private String undoLastCommand() throws IOException {
        if (previousTaskState == null) {
            return " No previous move to undo." + System.lineSeparator();
        }

        List<String> taskStateBeforeMutation = snapshotTaskState();
        List<String> undoStateBeforeMutation = previousTaskState;
        List<Task> restoredTasks = storage.loadFromSaveFormats(previousTaskState);
        tasks.clear();
        tasks.addAll(restoredTasks);
        saveWithRollback(taskStateBeforeMutation, undoStateBeforeMutation);
        previousTaskState = null;
        return " Rewound the previous command — we're so back." + System.lineSeparator()
                + " You have " + tasks.size() + " tasks in the list now." + System.lineSeparator();
    }

    private void rememberCurrentState() {
        previousTaskState = snapshotTaskState();
    }

    private List<String> snapshotTaskState() {
        return tasks.stream()
                .map(Task::toSaveFormat)
                .toList();
    }

    private void saveWithRollback(List<String> taskStateBeforeMutation,
                                  List<String> undoStateBeforeMutation) throws IOException {
        try {
            storage.save(tasks);
        } catch (IOException | SecurityException exception) {
            restoreTaskState(taskStateBeforeMutation);
            previousTaskState = undoStateBeforeMutation;
            throw exception;
        }
    }

    private void restoreTaskState(List<String> savedState) throws IOException {
        List<Task> restoredTasks = storage.loadFromSaveFormats(savedState);
        tasks.clear();
        tasks.addAll(restoredTasks);
    }

    private void appendLine(StringBuilder response, String line) {
        response.append(line).append(System.lineSeparator());
    }
}
