package happy.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import happy.exception.HappyException;
import happy.storage.Storage;
import happy.task.Deadline;
import happy.task.Event;
import happy.task.Priority;
import happy.task.Task;
import happy.task.TaskList;
import happy.task.Todo;
import happy.ui.Ui;

/**
 * Handles parsing user command strings and executing corresponding actions.
 */
public class Parser {

    /**
     * Parses the user command input and executes the appropriate operation on TaskList, Ui, and Storage.
     *
     * @param fullCommand Full user command string input.
     * @param tasks TaskList object containing current tasks.
     * @param ui Ui object for displaying messages.
     * @param storage Storage object for persisting tasks.
     * @return true if command is "bye" (exit signal), false otherwise.
     * @throws HappyException If command syntax or arguments are invalid.
     */
    public static boolean parseAndExecute(String fullCommand, TaskList tasks, Ui ui, Storage storage)
            throws HappyException {
        if (fullCommand.equalsIgnoreCase("bye")) {
            ui.showGoodbye();
            return true;
        } else if (fullCommand.equalsIgnoreCase("list")) {
            ui.showTaskList(tasks);
            return false;
        } else if (fullCommand.startsWith("date") || fullCommand.startsWith("on")) {
            handleDate(fullCommand, tasks, ui);
            return false;
        } else if (fullCommand.startsWith("delete") || fullCommand.equalsIgnoreCase("delete")) {
            handleDelete(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("mark") || fullCommand.equalsIgnoreCase("mark")) {
            handleMark(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("unmark") || fullCommand.equalsIgnoreCase("unmark")) {
            handleUnmark(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("todo") || fullCommand.equalsIgnoreCase("todo")) {
            handleTodo(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("deadline") || fullCommand.equalsIgnoreCase("deadline")) {
            handleDeadline(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("event") || fullCommand.equalsIgnoreCase("event")) {
            handleEvent(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("find") || fullCommand.equalsIgnoreCase("find")) {
            handleFind(fullCommand, tasks, ui);
            return false;
        } else {
            throw new HappyException("Happy is way too happy right now to understand that command! "
                    + "Perhaps you could phrase it differently?");
        }
    }

    /**
     * Parses user command for GUI execution and returns the response message.
     *
     * @param fullCommand Full user command string.
     * @param tasks TaskList instance.
     * @param storage Storage instance.
     * @return Response text message to display in GUI dialog box.
     * @throws HappyException If command syntax or arguments are invalid.
     */
    public static String parseAndExecuteForGui(String fullCommand, TaskList tasks, Storage storage)
            throws HappyException {
        assert fullCommand != null : "User command string should not be null";
        assert tasks != null : "TaskList instance passed to Parser should not be null";
        String commandWord = fullCommand.trim().split("\\s+")[0].toLowerCase();

        switch (commandWord) {
        case "bye":
            return "Bye. Be happy everyday!";
        case "list":
            return executeListForGui(tasks);
        case "todo":
            return executeTodoForGui(fullCommand, tasks, storage);
        case "deadline":
            return executeDeadlineForGui(fullCommand, tasks, storage);
        case "event":
            return executeEventForGui(fullCommand, tasks, storage);
        case "delete":
            return executeDeleteForGui(fullCommand, tasks, storage);
        case "mark":
            return executeMarkForGui(fullCommand, tasks, storage);
        case "unmark":
            return executeUnmarkForGui(fullCommand, tasks, storage);
        case "find":
            return executeFindForGui(fullCommand, tasks);
        case "priority":
            return executePriorityForGui(fullCommand, tasks, storage);
        case "date":
        case "on":
            return executeDateForGui(fullCommand, tasks);
        default:
            throw new HappyException("Happy is way too happy right now to understand that command! "
                    + "Perhaps you could phrase it differently?");
        }
    }

    private static String executeListForGui(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your task list is empty. Time to add some tasks and be happy!";
        }
        StringBuilder sb = new StringBuilder("Here are the HAPPY TASKS in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1)).append(".").append(tasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private static String executeTodoForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String description = command.length() > 4 ? command.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of a todo cannot be empty.");
        }
        validateNoPipeCharacter(description);
        Task task = new Todo(description);
        validateNotDuplicate(task, tasks);
        tasks.add(task);
        storage.save(tasks);
        return formatTaskAddedResponse(task, tasks.size());
    }

    private static String executeDeadlineForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String body = command.length() > 8 ? command.substring(8).trim() : "";
        if (body.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of a deadline cannot be empty.");
        }
        int byIndex = body.indexOf("/by");
        if (byIndex == -1) {
            throw new HappyException("HAPPY OOPS!!! A deadline task must include a '/by' specified date/time.");
        }
        String description = body.substring(0, byIndex).trim();
        String by = body.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The deadline date/time ('/by') cannot be empty.");
        }
        validateNoPipeCharacter(description);
        validateNoPipeCharacter(by);
        Task task = new Deadline(description, by);
        validateNotDuplicate(task, tasks);
        tasks.add(task);
        storage.save(tasks);
        return formatTaskAddedResponse(task, tasks.size());
    }

    private static String executeEventForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String body = command.length() > 5 ? command.substring(5).trim() : "";
        if (body.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of an event cannot be empty.");
        }
        int fromIndex = body.indexOf("/from");
        int toIndex = body.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            throw new HappyException("HAPPY OOPS!!! An event task must specify both '/from' and '/to' time frames.");
        }
        String description = body.substring(0, fromIndex).trim();
        String from = body.substring(fromIndex + 5, toIndex).trim();
        String to = body.substring(toIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The event start ('/from') and end ('/to') times cannot be empty.");
        }
        validateNoPipeCharacter(description);
        validateNoPipeCharacter(from);
        validateNoPipeCharacter(to);
        validateEventTimes(from, to);
        Task task = new Event(description, from, to);
        validateNotDuplicate(task, tasks);
        tasks.add(task);
        storage.save(tasks);
        return formatTaskAddedResponse(task, tasks.size());
    }

    private static String executeDeleteForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String indexStr = command.length() > 6 ? command.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a task number to delete.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task removedTask = tasks.delete(index);
            storage.save(tasks);
            return "Noted. I've removed this task:\n  " + removedTask
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static String executeMarkForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String indexStr = command.length() > 4 ? command.substring(4).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a task number to mark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task markedTask = tasks.mark(index);
            storage.save(tasks);
            return "Nice! I've marked this task as done:\n  " + markedTask;
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static String executeUnmarkForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String indexStr = command.length() > 6 ? command.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a task number to unmark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task unmarkedTask = tasks.unmark(index);
            storage.save(tasks);
            return "OK, I've marked this task as not done yet:\n  " + unmarkedTask;
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static String executeFindForGui(String command, TaskList tasks) throws HappyException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a keyword to search for.");
        }
        ArrayList<Task> matchingTasks = tasks.find(keyword);
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found in your list.";
        }
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            sb.append((i + 1)).append(".").append(matchingTasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private static String executeDateForGui(String command, TaskList tasks) throws HappyException {
        int spaceIndex = command.trim().indexOf(' ');
        if (spaceIndex == -1 || spaceIndex == command.trim().length() - 1) {
            throw new HappyException("HAPPY OOPS!!! Please specify a date (e.g. date 2026-09-18 or date 18/9/2026).");
        }
        String dateStr = command.trim().substring(spaceIndex + 1).trim();
        LocalDate targetDate = parseInputDate(dateStr);
        if (targetDate == null) {
            throw new HappyException(
                    "HAPPY OOPS!!! Invalid date format. Please use yyyy-MM-dd or d/M/yyyy (e.g., 2026-09-18).");
        }
        ArrayList<Task> matchingTasks = tasks.getTasksOccurringOn(targetDate);
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        if (matchingTasks.isEmpty()) {
            return "No tasks found occurring on " + formattedDate + ".";
        }
        StringBuilder sb = new StringBuilder("Here are the tasks occurring on " + formattedDate + ":\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            sb.append((i + 1)).append(".").append(matchingTasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private static String executePriorityForGui(String command, TaskList tasks, Storage storage)
            throws HappyException {
        String body = command.length() > 8 ? command.substring(8).trim() : "";
        String[] parts = body.split("\\s+");
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify task number and priority level "
                    + "(e.g. priority 1 high).");
        }
        try {
            int index = Integer.parseInt(parts[0]) - 1;
            Priority priority = Priority.parse(parts[1]);
            Task updatedTask = tasks.setPriority(index, priority);
            storage.save(tasks);
            return "Got it. I've set the priority of this task:\n  " + updatedTask;
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static String formatTaskAddedResponse(Task task, int totalTasks) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + totalTasks + " tasks in the list.";
    }

    private static void handleFind(String command, TaskList tasks, Ui ui) throws HappyException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a keyword to search for.");
        }
        ui.showFoundTasks(tasks.find(keyword));
    }

    private static void handleTodo(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String description = command.length() > 4 ? command.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of a todo cannot be empty.");
        }
        validateNoPipeCharacter(description);
        Task task = new Todo(description);
        validateNotDuplicate(task, tasks);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void handleDeadline(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String body = command.length() > 8 ? command.substring(8).trim() : "";
        if (body.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of a deadline cannot be empty.");
        }
        int byIndex = body.indexOf("/by");
        if (byIndex == -1) {
            throw new HappyException("HAPPY OOPS!!! A deadline task must include a '/by' specified date/time.");
        }
        String description = body.substring(0, byIndex).trim();
        String by = body.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The deadline date/time ('/by') cannot be empty.");
        }
        validateNoPipeCharacter(description);
        validateNoPipeCharacter(by);
        Task task = new Deadline(description, by);
        validateNotDuplicate(task, tasks);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void handleEvent(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String body = command.length() > 5 ? command.substring(5).trim() : "";
        if (body.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of an event cannot be empty.");
        }
        int fromIndex = body.indexOf("/from");
        int toIndex = body.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            throw new HappyException("HAPPY OOPS!!! An event task must specify both '/from' and '/to' time frames.");
        }
        String description = body.substring(0, fromIndex).trim();
        String from = body.substring(fromIndex + 5, toIndex).trim();
        String to = body.substring(toIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! The event start ('/from') and end ('/to') times cannot be empty.");
        }
        validateNoPipeCharacter(description);
        validateNoPipeCharacter(from);
        validateNoPipeCharacter(to);
        validateEventTimes(from, to);
        Task task = new Event(description, from, to);
        validateNotDuplicate(task, tasks);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void handleDelete(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String indexStr = command.length() > 6 ? command.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a task number to delete.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task removedTask = tasks.delete(index);
            storage.save(tasks);
            ui.showTaskRemoved(removedTask, tasks.size());
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleMark(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String indexStr = command.length() > 4 ? command.substring(4).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a task number to mark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task markedTask = tasks.mark(index);
            storage.save(tasks);
            ui.showTaskMarked(markedTask);
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleUnmark(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String indexStr = command.length() > 6 ? command.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("HAPPY OOPS!!! Please specify a task number to unmark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task unmarkedTask = tasks.unmark(index);
            storage.save(tasks);
            ui.showTaskUnmarked(unmarkedTask);
        } catch (NumberFormatException e) {
            throw new HappyException("HAPPY OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleDate(String command, TaskList tasks, Ui ui) throws HappyException {
        int spaceIndex = command.indexOf(' ');
        if (spaceIndex == -1 || spaceIndex == command.length() - 1) {
            throw new HappyException("HAPPY OOPS!!! Please specify a date (e.g. date 2019-12-02 or date 2/12/2019).");
        }
        String dateStr = command.substring(spaceIndex + 1).trim();
        LocalDate targetDate = parseInputDate(dateStr);
        if (targetDate == null) {
            throw new HappyException(
                    "HAPPY OOPS!!! Invalid date format. Please use yyyy-MM-dd or d/M/yyyy (e.g., 2019-12-02).");
        }
        ui.showTasksOnDate(targetDate, tasks.getTasksOccurringOn(targetDate));
    }

    private static LocalDate parseInputDate(String dateStr) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("MMM dd yyyy"),
            DateTimeFormatter.ofPattern("MMM d yyyy")
        };
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Continue trying remaining formatters
            }
        }
        return null;
    }

    /**
     * Validates that the input string does not contain the pipe character '|' reserved for file storage.
     *
     * @param input String input to validate.
     * @throws HappyException If the input string contains '|'.
     */
    private static void validateNoPipeCharacter(String input) throws HappyException {
        if (input != null && input.contains("|")) {
            throw new HappyException("HAPPY OOPS!!! Task parameters cannot contain the '|' character.");
        }
    }

    /**
     * Validates that event start date/time is not equal to or after the end date/time.
     *
     * @param from Start date/time string.
     * @param to End date/time string.
     * @throws HappyException If start time equals end time or start date is after end date.
     */
    private static void validateEventTimes(String from, String to) throws HappyException {
        if (from.equalsIgnoreCase(to)) {
            throw new HappyException("HAPPY OOPS!!! Event start time cannot be equal to end time.");
        }
        LocalDate startDate = parseInputDate(from);
        LocalDate endDate = parseInputDate(to);
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new HappyException("HAPPY OOPS!!! Event start date cannot be after end date.");
        }
    }

    /**
     * Validates that the task is not a duplicate of an existing task in the TaskList.
     *
     * @param task Task object to check.
     * @param tasks TaskList instance containing current tasks.
     * @throws HappyException If an identical task already exists in the list.
     */
    private static void validateNotDuplicate(Task task, TaskList tasks) throws HappyException {
        if (tasks.isDuplicate(task)) {
            throw new HappyException("HAPPY OOPS!!! This task already exists in your list.");
        }
    }
}
