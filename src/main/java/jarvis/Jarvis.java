package jarvis;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Runs the command-line version of the Jarvis chatbot. */
public class Jarvis {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String DELETE_COMMAND = "delete";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String SNOOZE_COMMAND = "snooze";
    private static final String BYE_COMMAND = "bye";
    private static final String BY_DELIMITER = " /by ";
    private static final String FROM_DELIMITER = " /from ";
    private static final String TO_DELIMITER = " /to ";

    /**
     * Prints Jarvis's introductory greeting, then processes commands until the
     * user enters {@code bye}. Tasks are created with {@code todo},
     * {@code deadline}, or {@code event} commands and can be removed with
     * {@code delete}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        TaskStorage storage = new TaskStorage();
        List<Task> tasks = storage.load();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand().trim();
            ui.showSeparator();

            if (command.equals(BYE_COMMAND)) {
                executeExitCommand(tasks, ui, storage);
                break;
            }

            processCommand(command, tasks, ui, storage);
            ui.showSeparator();
        }
    }

    /** Executes the exit command and reports any command-level error. */
    private static void executeExitCommand(List<Task> tasks, Ui ui, TaskStorage storage) {
        Command exitCommand = new ExitCommand();
        try {
            exitCommand.execute(tasks, ui, storage);
        } catch (JarvisException exception) {
            ui.showError(exception);
        }
    }

    /** Dispatches one non-exit command to the appropriate operation. */
    private static void processCommand(String command, List<Task> tasks, Ui ui, TaskStorage storage) {
        try {
            if (command.equals(LIST_COMMAND)) {
                ui.showTasks(tasks.toArray(Task[]::new));
                return;
            }
            if (isCommand(command, FIND_COMMAND)) {
                handleFind(command, tasks, ui);
                return;
            }
            if (isCommand(command, DELETE_COMMAND)) {
                handleDelete(command, tasks, ui, storage);
                return;
            }
            if (isCommand(command, MARK_COMMAND)) {
                handleStatusChange(command, tasks, ui, storage, true);
                return;
            }
            if (isCommand(command, UNMARK_COMMAND)) {
                handleStatusChange(command, tasks, ui, storage, false);
                return;
            }
            if (isCommand(command, SNOOZE_COMMAND)) {
                handleSnooze(command, tasks, ui, storage);
                return;
            }
            handleCreate(command, tasks, ui, storage);
        } catch (JarvisException exception) {
            ui.showError(exception);
        }
    }

    /** Returns whether a command is either an action alone or an action with arguments. */
    private static boolean isCommand(String command, String commandName) {
        return command.equals(commandName) || command.startsWith(commandName + " ");
    }

    /** Displays all tasks whose descriptions contain the requested keyword. */
    private static void handleFind(String command, List<Task> tasks, Ui ui) throws JarvisException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        ui.showMatchingTasks(findMatchingTasks(tasks, keyword));
    }

    /** Deletes the selected task and persists the updated task list. */
    private static void handleDelete(String command, List<Task> tasks, Ui ui, TaskStorage storage)
            throws JarvisException {
        int taskIndex = requireTaskIndex(tasks, command, DELETE_COMMAND);
        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks);
        ui.showTaskRemoved(removedTask, tasks.size());
    }

    /** Changes the completion status of the selected task and persists it. */
    private static void handleStatusChange(String command, List<Task> tasks, Ui ui, TaskStorage storage,
            boolean isDone) throws JarvisException {
        String action = isDone ? MARK_COMMAND : UNMARK_COMMAND;
        int taskIndex = requireTaskIndex(tasks, command, action);
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks);
        if (isDone) {
            ui.showTaskMarked(task);
        } else {
            ui.showTaskUnmarked(task);
        }
    }

    /** Postpones a deadline task and persists the new date. */
    private static void handleSnooze(String command, List<Task> tasks, Ui ui, TaskStorage storage)
            throws JarvisException {
        int taskIndex = snoozeTask(command, tasks);
        Deadline deadline = (Deadline) tasks.get(taskIndex);
        storage.save(tasks);
        ui.showTaskSnoozed(taskIndex + 1, deadline);
    }

    /**
     * Updates a deadline task with a validated snooze date.
     *
     * @return the zero-based index of the snoozed task.
     */
    static int snoozeTask(String command, List<Task> tasks) throws JarvisException {
        String remainder = command.substring(SNOOZE_COMMAND.length()).trim();
        int byIndex = remainder.indexOf(BY_DELIMITER);
        if (byIndex < 0) {
            throw new JarvisException("Use snooze <task number> /by <yyyy-mm-dd>.");
        }

        String taskNumberCommand = SNOOZE_COMMAND + " " + remainder.substring(0, byIndex);
        int taskIndex = requireTaskIndex(tasks, taskNumberCommand, SNOOZE_COMMAND);
        String dateText = requirePart(remainder.substring(byIndex + BY_DELIMITER.length()), "snooze date");
        LocalDate newDate = parseDate(dateText,
                "A snooze date must use yyyy-mm-dd, for example: 2019-10-15");

        Task task = tasks.get(taskIndex);
        if (!(task instanceof Deadline deadline)) {
            throw new JarvisException("Only deadline tasks can be snoozed.");
        }
        deadline.snoozeUntil(newDate);
        return taskIndex;
    }

    /** Creates a task from the command and persists the updated task list. */
    private static void handleCreate(String command, List<Task> tasks, Ui ui, TaskStorage storage)
            throws JarvisException {
        Task task = parseTask(command);
        if (tasks.stream().anyMatch(existing -> isDuplicate(existing, task))) {
            throw new JarvisException("That task is already in your list.");
        }
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    /** Returns whether two tasks have the same type and description. */
    static boolean isDuplicate(Task first, Task second) {
        return first.getClass().equals(second.getClass())
                && first.getDescription().equalsIgnoreCase(second.getDescription());
    }

    /** Returns tasks whose descriptions contain the requested keyword. */
    static List<Task> findMatchingTasks(List<Task> tasks, String keyword) throws JarvisException {
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            throw new JarvisException("Please provide a keyword after find.");
        }
        String lowerCaseKeyword = trimmedKeyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .toList();
    }

    /** Returns the zero-based index for a validated task-list action. */
    private static int requireTaskIndex(List<Task> tasks, String command, String action)
            throws JarvisException {
        int taskNumber = parseTaskNumber(command, action);
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new JarvisException("There is no task with that number.");
        }
        return taskNumber - 1;
    }

    /** Converts a task-creation command into its corresponding task subtype. */
    static Task parseTask(String command) throws JarvisException {
        if (isCommand(command, TODO_COMMAND)) {
            return parseTodo(command);
        }
        if (command.startsWith(DEADLINE_COMMAND + " ")) {
            return parseDeadline(command);
        }
        if (command.startsWith(EVENT_COMMAND + " ")) {
            return parseEvent(command);
        }

        throw new JarvisException(
                "I don't recognize that command. Try todo, deadline, event, list, find, mark, "
                        + "unmark, delete, snooze, or bye.");
    }

    /** Creates a todo task after validating its description. */
    private static Task parseTodo(String command) throws JarvisException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        return new Todo(requireDescription(description, TODO_COMMAND));
    }

    /** Creates a deadline task after validating its description and date. */
    private static Task parseDeadline(String command) throws JarvisException {
        String remainder = command.substring(DEADLINE_COMMAND.length() + 1);
        int byIndex = remainder.indexOf(BY_DELIMITER);
        if (byIndex < 0) {
            throw new JarvisException("A deadline needs a description and a date, "
                    + "for example: deadline report /by Friday");
        }

        String description = requireDescription(remainder.substring(0, byIndex), DEADLINE_COMMAND);
        String dateText = requirePart(remainder.substring(byIndex + BY_DELIMITER.length()), "deadline date");
        LocalDate date = parseDate(dateText,
                "A deadline date must use yyyy-mm-dd, for example: 2019-10-15");
        return new Deadline(description, date);
    }

    /** Creates an event task after validating its description and time range. */
    private static Task parseEvent(String command) throws JarvisException {
        String remainder = command.substring(EVENT_COMMAND.length() + 1);
        int fromIndex = remainder.indexOf(FROM_DELIMITER);
        int toIndex = remainder.indexOf(TO_DELIMITER, fromIndex + 1);
        if (fromIndex < 0 || toIndex < 0) {
            throw new JarvisException("An event needs a description, start time, and end time, "
                    + "for example: event meeting /from 2pm /to 3pm");
        }

        String description = requireDescription(remainder.substring(0, fromIndex), EVENT_COMMAND);
        String from = requirePart(remainder.substring(fromIndex + FROM_DELIMITER.length(), toIndex),
                "event start time");
        String to = requirePart(remainder.substring(toIndex + TO_DELIMITER.length()), "event end time");
        return new Event(description, from, to);
    }

    /** Parses a date and reports a user-friendly error when its format is invalid. */
    private static LocalDate parseDate(String dateText, String errorMessage) throws JarvisException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new JarvisException(errorMessage);
        }
    }

    /** Parses the numeric argument of a task-list action. */
    static int parseTaskNumber(String command, String action) throws JarvisException {
        String taskNumberText = command.substring(action.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new JarvisException("Please provide a task number after " + action + ".");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1) {
                throw new JarvisException("Task number must be positive.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new JarvisException("Please provide a valid task number after " + action + ".");
        }
    }

    /** Validates the description portion of a task-creation command. */
    private static String requireDescription(String description, String command) throws JarvisException {
        return requirePart(description, command + " description");
    }

    /** Validates and trims a required command component. */
    private static String requirePart(String value, String partName) throws JarvisException {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new JarvisException("A " + partName + " cannot be empty.");
        }
        return trimmedValue;
    }
}
