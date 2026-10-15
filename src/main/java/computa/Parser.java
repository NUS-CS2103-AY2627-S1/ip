package computa;

import java.time.DayOfWeek;
import java.time.format.DateTimeParseException;

/** Processes and executes user commands for the Computa application. */
public class Parser {
    private static final String COMMAND_EXIT = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";

    /** Processes one command and returns whether the application should exit. */
    public static boolean parse(String command, TaskList taskList, Ui ui, Storage storage)
            throws Computa.ComputaException {
        if (command == null || taskList == null || ui == null || storage == null) {
            throw new IllegalArgumentException("Command, task list, UI, and storage are required");
        }
        String trimmedCommand = command.trim();
        if (trimmedCommand.equals(COMMAND_EXIT)) {
            return true;
        }
        if (trimmedCommand.equals(COMMAND_LIST)) {
            listTasks(taskList, ui);
            return false;
        }
        String[] commandParts = trimmedCommand.split("\\s+");
        String commandName = commandParts[0];
        if (commandName.equals(COMMAND_MARK) || commandName.equals(COMMAND_UNMARK)) {
            updateTaskStatus(commandParts, commandName, taskList, ui, storage);
            return false;
        }
        if (commandName.equals(COMMAND_DELETE)) {
            deleteTask(commandParts, taskList, ui, storage);
            return false;
        }
        if (commandName.equals(COMMAND_FIND)) {
            findTasks(trimmedCommand, taskList, ui);
            return false;
        }
        createTask(trimmedCommand, commandName, commandParts, taskList, ui, storage);
        return false;
    }

    /**
     * Displays all visible tasks with one-based indices.
     *
     * @param taskList the task list whose visible tasks should be displayed
     * @param ui the user interface used to display each task
     */
    private static void listTasks(TaskList taskList, Ui ui) {
        int displayIndex = 1;
        for (Todo task : taskList.getVisibleTasks()) {
            ui.showMessage(displayIndex++ + ". " + task.getTaskDescription());
        }
    }

    /**
     * Toggles the status of the selected task and saves the updated task list.
     *
     * @param parts the command tokens containing the user-visible task number
     * @param commandName the command indicating whether the task is marked or unmarked
     * @param taskList the task list containing the selected task
     * @param ui the user interface used to report the status change
     * @param storage the storage used to persist the updated task list
     */
    private static void updateTaskStatus(String[] parts, String commandName, TaskList taskList,
                                         Ui ui, Storage storage) {
        int taskNumber = parseTaskNumber(parts);
        Todo task = taskList.getVisibleTask(taskNumber - 1);
        task.changeStatusIcon();
        String status = commandName.equals(COMMAND_MARK) ? "done" : "undone";
        ui.showMessage("Task marked " + status + "\n" + task.getTaskDescription());
        storage.saveTasks(taskList);
    }

    /**
     * Deletes the selected visible task and saves the updated task list.
     *
     * @param parts the command tokens containing the user-visible task number
     * @param taskList the task list containing the selected task
     * @param ui the user interface used to report the deletion
     * @param storage the storage used to persist the updated task list
     */
    private static void deleteTask(String[] parts, TaskList taskList, Ui ui, Storage storage) {
        int taskNumber = parseTaskNumber(parts);
        Todo removedTask = taskList.getVisibleTask(taskNumber - 1);
        taskList.deleteTask(findTaskIndex(taskList, removedTask));
        ui.showMessage("Task removed\n" + removedTask.getTaskDescription());
        storage.saveTasks(taskList);
    }

    /**
     * Displays visible tasks whose descriptions contain the search text.
     *
     * @param command the command containing the search text
     * @param taskList the task list to search
     * @param ui the user interface used to display matching tasks
     */
    private static void findTasks(String command, TaskList taskList, Ui ui) {
        String searchWord = getArgument(command);
        ui.showMessage("Matching tasks:");
        int displayIndex = 1;
        for (Todo task : taskList.getVisibleTasks()) {
            if (task.getTaskDescription().contains(searchWord)) {
                ui.showMessage(displayIndex + ". " + task.getTaskDescription());
            }
            displayIndex++;
        }
    }

    /**
     * Creates a task from the command and adds it to the task list.
     *
     * @param command the complete command entered by the user
     * @param commandName the task type specified by the command
     * @param parts the command tokens used to validate the command
     * @param taskList the task list to which the new task is added
     * @param ui the user interface used to report the newly added task
     * @param storage the storage used to persist the updated task list
     * @throws Computa.ComputaException if the command is invalid or lacks a description
     */
    private static void createTask(String command, String commandName, String[] parts,
                                   TaskList taskList, Ui ui, Storage storage)
            throws Computa.ComputaException {
        String description;
        switch (commandName) {
        case "todo" -> {
            requireParts(parts, 2, "A todo needs a description");
            description = getArgument(command);
            taskList.addTask(new Todo(description));
        }
        case "deadline" -> description = addDeadline(command, taskList);
        case "event" -> description = addEvent(command, taskList);
        case "recurring" -> description = addRecurring(command, taskList);
        default -> throw new Computa.ComputaException("Unknown command");
        }
        if (description.isEmpty()) {
            throw new Computa.ComputaException("A task description is required");
        }
        ui.showMessage("Added: " + taskList.getTask(taskList.getSize() - 1).getTaskDescription());
        ui.showMessage("Task count: " + taskList.getSize());
        storage.saveTasks(taskList);
    }

    /**
     * Creates and adds a deadline task from the supplied command.
     *
     * @param command the command containing the description and deadline
     * @param taskList the task list to which the deadline is added
     * @return the description of the created deadline task
     * @throws Computa.ComputaException if the command format or deadline is invalid
     */
    private static String addDeadline(String command, TaskList taskList)
            throws Computa.ComputaException {
        String[] parts = command.split("/", 2);
        requireParts(parts, 2, "Use: deadline <description> / yyyy-MM-dd HHmm");
        String description = getArgument(parts[0]);
        try {
            taskList.addTask(new Deadline(description, parts[1].trim()));
        } catch (DateTimeParseException exception) {
            throw new Computa.ComputaException("Invalid deadline format; use yyyy-MM-dd HHmm");
        }
        return description;
    }

    /**
     * Creates and adds an event task from the supplied command.
     *
     * @param command the command containing the description, start, and end times
     * @param taskList the task list to which the event is added
     * @return the description of the created event task
     * @throws Computa.ComputaException if the command format or event times are invalid
     */
    private static String addEvent(String command, TaskList taskList)
            throws Computa.ComputaException {
        String[] parts = command.split("/");
        requireParts(parts, 3, "Use: event <description> / start / end");
        String description = getArgument(parts[0]);
        try {
            taskList.addTask(new Event(description, parts[1].trim(), parts[2].trim()));
        } catch (DateTimeParseException exception) {
            throw new Computa.ComputaException("Invalid event date/time; use yyyy-MM-dd HHmm");
        }
        return description;
    }

    /**
     * Creates and adds a recurring task from the supplied command.
     *
     * @param command the command containing the description and weekday
     * @param taskList the task list to which the recurring task is added
     * @return the description of the created recurring task
     * @throws Computa.ComputaException if the command format or weekday is invalid
     */
    private static String addRecurring(String command, TaskList taskList)
            throws Computa.ComputaException {
        String[] parts = command.split("/", 2);
        requireParts(parts, 2, "Use: recurring <description> / <weekday>");
        String description = getArgument(parts[0]);
        try {
            DayOfWeek day = DayOfWeek.valueOf(parts[1].trim().toUpperCase());
            taskList.addTask(new RecurringTask(description, day));
        } catch (IllegalArgumentException exception) {
            throw new Computa.ComputaException("Weekday must be Monday to Sunday");
        }
        return description;
    }

    /**
     * Verifies that a command contains at least the required number of parts.
     *
     * @param parts the command parts to validate
     * @param minimum the minimum number of required parts
     * @param message the error message to use when validation fails
     * @throws Computa.ComputaException if fewer than the required parts are present
     */
    private static void requireParts(String[] parts, int minimum, String message)
            throws Computa.ComputaException {
        if (parts.length < minimum) {
            throw new Computa.ComputaException(message);
        }
    }

    /**
     * Extracts the user-visible task number from command parts.
     *
     * @param parts the command parts containing the task number
     * @return the parsed task number
     * @throws NumberFormatException if the task number is missing or not an integer
     */
    private static int parseTaskNumber(String[] parts) {
        if (parts.length < 2) {
            throw new NumberFormatException("Missing task number");
        }
        return Integer.parseInt(parts[1]);
    }

    /**
     * Finds the zero-based position of a task in the complete task list.
     *
     * @param taskList the task list to search
     * @param target the task whose position should be found
     * @return the zero-based index of the target task
     * @throws IndexOutOfBoundsException if the target task is not in the list
     */
    private static int findTaskIndex(TaskList taskList, Todo target) {
        for (int i = 0; i < taskList.getSize(); i++) {
            if (taskList.getTask(i) == target) {
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /** Returns the text following the command name. */
    private static String getArgument(String command) {
        int separator = command.indexOf(' ');
        return separator < 0 ? "" : command.substring(separator + 1).trim();
    }
}
