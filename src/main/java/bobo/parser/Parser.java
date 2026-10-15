package bobo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import bobo.exception.BoboException;
import bobo.storage.Storage;
import bobo.task.Deadline;
import bobo.task.Event;
import bobo.task.Task;
import bobo.task.TaskList;
import bobo.task.Todo;
import bobo.ui.Ui;
import bobo.util.DateUtil;

/**
 * Parses user input commands and executes the corresponding actions on
 * TaskList, Ui, and Storage.
 */
public class Parser {

    /**
     * Executes the command given in the text input.
     *
     * @param text    Raw user input string.
     * @param tasks   TaskList instance to manipulate.
     * @param ui      Ui instance for output.
     * @param storage Storage instance for saving changes.
     * @return true if command is 'bye' (signals exit), false otherwise.
     * @throws BoboException If input command is invalid or parameters are missing.
     */
    public static boolean executeCommand(String text, TaskList tasks, Ui ui, Storage storage) throws BoboException {
        assert tasks != null : "TaskList instance passed to Parser cannot be null";
        assert ui != null : "Ui instance passed to Parser cannot be null";
        assert storage != null : "Storage instance passed to Parser cannot be null";

        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        String trimmedText = text.trim();

        if (trimmedText.equals("bye")) {
            ui.showBye();
            return true;
        } else if (trimmedText.equals("help")) {
            ui.showHelp();
        } else if (trimmedText.equals("list")) {
            ui.showTaskList(tasks);
        } else if (isCommand(trimmedText, "unmark")) {
            executeToggleMark(getArg(trimmedText, 6), "unmark", false, tasks, ui, storage);
        } else if (isCommand(trimmedText, "mark")) {
            executeToggleMark(getArg(trimmedText, 4), "mark", true, tasks, ui, storage);
        } else if (isCommand(trimmedText, "todo")) {
            executeTodo(trimmedText, tasks, ui, storage);
        } else if (isCommand(trimmedText, "deadline")) {
            executeDeadline(trimmedText, tasks, ui, storage);
        } else if (isCommand(trimmedText, "event")) {
            executeEvent(trimmedText, tasks, ui, storage);
        } else if (isCommand(trimmedText, "delete")) {
            executeDelete(trimmedText, tasks, ui, storage);
        } else if (isCommand(trimmedText, "on")) {
            executeOnDate(trimmedText, tasks, ui);
        } else if (isCommand(trimmedText, "find")) {
            executeFind(trimmedText, tasks, ui);
        } else {
            throw new BoboException("OOPS!!! I'm sorry, but I don't know what that means :-(\n"
                    + "Type 'help' to see available commands.");
        }

        return false;
    }

    private static boolean isCommand(String text, String command) {
        return text.equals(command) || text.startsWith(command + " ");
    }

    private static String getArg(String text, int prefixLength) {
        return text.length() > prefixLength ? text.substring(prefixLength).trim() : "";
    }

    private static void executeToggleMark(String arg, String commandName, boolean isDone,
            TaskList tasks, Ui ui, Storage storage) throws BoboException {
        int number = parseTaskIndex(arg, commandName);
        Task task = isDone ? tasks.mark(number) : tasks.unmark(number);
        assert task != null : "Toggled task must not be null";
        storage.save(tasks);
        ui.showTaskMarked(task, isDone);
    }

    private static void executeTodo(String trimmedText, TaskList tasks, Ui ui, Storage storage)
            throws BoboException {
        String description = getArg(trimmedText, 4);
        if (description.isEmpty()) {
            throw new BoboException("OOPS!!! The description of a todo cannot be empty.");
        }
        Task task = new Todo(description);
        addAndSaveTask(task, tasks, ui, storage);
    }

    private static void executeDeadline(String trimmedText, TaskList tasks, Ui ui, Storage storage)
            throws BoboException {
        String content = getArg(trimmedText, 8);
        if (content.isEmpty()) {
            throw new BoboException("OOPS!!! The description of a deadline cannot be empty.");
        }

        String[] parts = content.split(" /by ", 2);
        String description = parts[0].trim();
        String by = parts.length > 1 ? parts[1].trim() : "";

        if (parts.length < 2 || by.isEmpty()) {
            if (content.contains("/by")) {
                throw new BoboException("OOPS!!! Please format the deadline with spaces around '/by' "
                        + "(e.g., /by 2026-09-30).");
            }
            throw new BoboException("OOPS!!! The deadline of a deadline cannot be empty.");
        }
        if (description.isEmpty()) {
            throw new BoboException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (DateUtil.parseDateTimeOrDate(by) == null) {
            throw new BoboException("OOPS!!! Please specify a valid date (e.g., yyyy-MM-dd or d/M/yyyy).");
        }

        Task task = new Deadline(description, by);
        addAndSaveTask(task, tasks, ui, storage);
    }

    private static void executeEvent(String trimmedText, TaskList tasks, Ui ui, Storage storage)
            throws BoboException {
        String content = getArg(trimmedText, 5);
        if (content.isEmpty()) {
            throw new BoboException("OOPS!!! The description of an event cannot be empty.");
        }

        String[] parts = content.split(" /from ", 2);
        String description = parts[0].trim();
        String from = "";
        String to = "";
        if (parts.length > 1) {
            String[] timeParts = parts[1].split(" /to ", 2);
            from = timeParts[0].trim();
            to = timeParts.length > 1 ? timeParts[1].trim() : "";
        }

        if (description.isEmpty()) {
            throw new BoboException("OOPS!!! The description of an event cannot be empty.");
        }
        if (parts.length < 2 || from.isEmpty()) {
            if (content.contains("/from")) {
                throw new BoboException("OOPS!!! Please format the event with spaces around '/from' "
                        + "(e.g., /from 2026-09-30 14:00).");
            }
            throw new BoboException("OOPS!!! The from of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            if (content.contains("/to")) {
                throw new BoboException("OOPS!!! Please format the event with spaces around '/to' "
                        + "(e.g., /to 2026-09-30 16:00).");
            }
            throw new BoboException("OOPS!!! The to of an event cannot be empty.");
        }
        if (DateUtil.parseDateTimeOrDate(from) == null) {
            throw new BoboException("OOPS!!! Please specify a valid start date (e.g., yyyy-MM-dd or d/M/yyyy).");
        }
        if (DateUtil.parseDateTimeOrDate(to) == null) {
            throw new BoboException("OOPS!!! Please specify a valid end date (e.g., yyyy-MM-dd or d/M/yyyy).");
        }

        Task task = new Event(description, from, to);
        addAndSaveTask(task, tasks, ui, storage);
    }

    private static void executeDelete(String trimmedText, TaskList tasks, Ui ui, Storage storage)
            throws BoboException {
        String arg = getArg(trimmedText, 6);
        int number = parseTaskIndex(arg, "delete");
        Task task = tasks.delete(number);
        assert task != null : "Deleted task must not be null";
        storage.save(tasks);
        ui.showTaskRemoved(task, tasks.size());
    }

    private static void executeOnDate(String trimmedText, TaskList tasks, Ui ui) throws BoboException {
        String dateStr = getArg(trimmedText, 2);
        Object parsed = DateUtil.parseDateTimeOrDate(dateStr);
        LocalDate targetDate = (parsed instanceof LocalDateTime) ? ((LocalDateTime) parsed).toLocalDate()
                : (parsed instanceof LocalDate) ? (LocalDate) parsed : null;
        if (targetDate == null) {
            throw new BoboException("Please specify a valid date (e.g., yyyy-MM-dd or d/M/yyyy).");
        }
        List<Task> matchingTasks = tasks.getTasksOnDate(targetDate);
        assert matchingTasks != null : "Matching tasks list must not be null";
        ui.showTasksOnDate(targetDate, matchingTasks);
    }

    private static void executeFind(String trimmedText, TaskList tasks, Ui ui) throws BoboException {
        String keyword = getArg(trimmedText, 4);
        if (keyword.isEmpty()) {
            throw new BoboException("OOPS!!! The search keyword for find cannot be empty.");
        }
        List<Task> matchingTasks = tasks.findTasks(keyword);
        assert matchingTasks != null : "Matching tasks list must not be null";
        ui.showMatchingTasks(matchingTasks);
    }

    private static void addAndSaveTask(Task task, TaskList tasks, Ui ui, Storage storage) throws BoboException {
        assert task != null : "Created task object must not be null";
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Parses a string representation of a 1-based task index into an integer.
     *
     * @param input       Raw index argument string.
     * @param commandName Name of the command requesting the index.
     * @return Parsed 1-based integer task index.
     * @throws BoboException If input is empty or not a valid integer.
     */
    private static int parseTaskIndex(String input, String commandName) throws BoboException {
        if (input.isEmpty()) {
            throw new BoboException("OOPS!!! Please specify a task number for " + commandName + ".");
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new BoboException("Error: Invalid task number!");
        }
    }
}
