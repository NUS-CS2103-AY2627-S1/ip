package computa;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import computa.command.Command;
import computa.command.Parser;
import computa.exception.ComputaException;
import computa.storage.Storage;
import computa.task.Deadline;
import computa.task.Event;
import computa.task.Task;
import computa.task.Todo;
import computa.ui.Ui;
import computa.util.DateTimeParser;

/**
 * A minimal chatbot that stores tasks and displays them when requested.
 */
public class Computa {
    /** Relative path of the file used to store tasks. */
    private static final String FILE_PATH = "." + File.separator + "data" + File.separator + "computa.txt";
    /** Error shown when a task number is missing or outside the current task list. */
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "TOMARE!!!! Don't think you can mark tasks without doing them.";
    /** Error shown when the user attempts to add an existing task again. */
    private static final String DUPLICATE_TASK_MESSAGE =
            "Hmph! You already have that task on your list. No need to overwork yourself!";

    /** Reads and writes the task data file. */
    private final Storage storage;
    /** In-memory tasks used by the current Computa session. */
    private final ArrayList<Task> tasks;
    /** Handles user-facing console output. */
    private final Ui ui;

    /** Creates an empty task list and prepares its data file. */
    public Computa() {
        this(new Storage(FILE_PATH), new Ui());
    }

    /**
     * Creates a chatbot that sends responses to the supplied UI.
     *
     * @param ui receiver for chatbot responses.
     */
    public Computa(Ui ui) {
        this(new Storage(FILE_PATH), ui);
    }

    /**
     * Creates a chatbot using the supplied storage and UI collaborators.
     *
     * @param storage source and destination for task data.
     * @param ui receiver for chatbot responses.
     */
    Computa(Storage storage, Ui ui) {
        assert storage != null : "Storage collaborator must be provided";
        assert ui != null : "UI collaborator must be provided";
        this.storage = storage;
        storage.initialiseDataFile();
        tasks = storage.loadTasks();
        assert tasks != null : "Storage must return a task list";
        this.ui = ui;
    }

    /**
     * Starts the interactive command loop and processes input until the user exits.
     * Invalid commands are converted into friendly error messages while the loop
     * continues so that one bad input does not terminate the session.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        startSession();

        while (scanner.hasNextLine()) {
            if (!processCommand(scanner.nextLine())) {
                break;
            }
        }
    }

    /** Displays the greeting for a new console or GUI session. */
    public void startSession() {
        ui.showGreeting();
    }

    /**
     * Processes one complete command and returns whether the session should continue.
     *
     * @param command complete line entered by the user.
     * @return false only after the exit command is processed.
     */
    public boolean processCommand(String command) {
        assert command != null : "A command line must be provided";
        ui.showSeparator();
        String normalizedCommand = command.trim();
        if (normalizedCommand.isEmpty()) {
            ui.showError("Hmph! Enter a command so I know how to help you.");
            ui.showSeparator();
            return true;
        }

        Command parsedCommand = Parser.parse(normalizedCommand);
        if (parsedCommand != null) {
            try {
                parsedCommand.execute(tasks, ui, storage);
            } catch (ComputaException exception) {
                ui.showError(exception.getMessage());
            }
            if (parsedCommand.isExit()) {
                ui.showSeparator();
                return false;
            }
            ui.showSeparator();
            return true;
        }

        try {
            if (normalizedCommand.equals("list")) {
                ui.showTasks(tasks);
            } else if (normalizedCommand.equals("sort")) {
                sortTasks();
                storage.saveTasks(tasks);
                ui.showSortedTasks(tasks);
            } else if (isCommand(normalizedCommand, "find")) {
                findTasks(normalizedCommand);
            } else if (isCommand(normalizedCommand, "on")) {
                showTasksOnDate(normalizedCommand);
            } else if (isCommand(normalizedCommand, "mark")) {
                Task updatedTask = updateTaskStatus(normalizedCommand, tasks, true);
                storage.saveTasks(tasks);
                ui.showStatusUpdate(updatedTask, true);
            } else if (isCommand(normalizedCommand, "unmark")) {
                Task updatedTask = updateTaskStatus(normalizedCommand, tasks, false);
                storage.saveTasks(tasks);
                ui.showStatusUpdate(updatedTask, false);
            } else if (isCommand(normalizedCommand, "delete")) {
                Task deletedTask = deleteTask(normalizedCommand, tasks);
                storage.saveTasks(tasks);
                ui.showDeletedTask(deletedTask, tasks.size());
            } else {
                Task newTask = createTask(normalizedCommand);
                if (hasDuplicateTask(newTask)) {
                    throw new ComputaException(DUPLICATE_TASK_MESSAGE);
                }
                tasks.add(newTask);
                storage.saveTasks(tasks);
                ui.showAddedTask(newTask, tasks.size());
            }
        } catch (ComputaException exception) {
            ui.showError(exception.getMessage());
        }

        ui.showSeparator();
        return true;
    }

    /**
     * Prints Computa's greeting, then processes commands until {@code bye} is entered.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Computa().run();
    }

    /**
     * Creates a task object from a todo, deadline, or event command.
     *
     * @param command complete command entered by the user.
     * @return the corresponding task.
     * @throws ComputaException if the command is malformed or unknown.
     */
    private static Task createTask(String command) throws ComputaException {
        if (isCommand(command, "todo")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new ComputaException("Hmph! This is just an excuse to hang out with me, right?\n"
                        + "Who says you get to spend empty time with me? ୧( ˵ ° ~ ° ˵ )୨");
            }
            return new Todo(description);
        }

        if (isCommand(command, "deadline")) {
            String details = command.substring("deadline".length()).trim();
            int byIndex = details.indexOf("/by");
            if (byIndex < 0 || byIndex != details.lastIndexOf("/by")) {
                throw new ComputaException("Hmph! A deadline needs a description and a /by date or time. \n"
                        + " Do I HAVE to help you with everything?");
            }
            String description = details.substring(0, byIndex).trim();
            String by = details.substring(byIndex + "/by".length()).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new ComputaException("Hmph! A deadline needs a description and a /by date or time. \n"
                        + " Do I HAVE to help you with everything?");
            }
            if (DateTimeParser.looksLikeDate(by) && DateTimeParser.parse(by) == null) {
                throw new ComputaException("Hmph! I can't understand that deadline date. Use yyyy-mm-dd.");
            }
            return new Deadline(description, by);
        }

        if (isCommand(command, "event")) {
            String details = command.substring("event".length()).trim();
            int fromIndex = details.indexOf("/from");
            int toIndex = details.indexOf("/to");
            if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex
                    || fromIndex != details.lastIndexOf("/from") || toIndex != details.lastIndexOf("/to")) {
                throw new ComputaException(
                        "Hmph! An event needs a description, /from date or time, and "
                                + "/to date or time. \n"
                                + " Do I HAVE to help you with everything?");
            }
            String description = details.substring(0, fromIndex).trim();
            String from = details.substring(fromIndex + "/from".length(), toIndex).trim();
            String to = details.substring(toIndex + "/to".length()).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new ComputaException(
                        "Hmph! An event needs a description, /from date or time, and "
                                + "/to date or time. \n"
                                + " Do I HAVE to help you with everything?");
            }
            if ((DateTimeParser.looksLikeDate(from) && DateTimeParser.parse(from) == null)
                    || (DateTimeParser.looksLikeDate(to) && DateTimeParser.parse(to) == null)) {
                throw new ComputaException("Hmph! I can't understand that event date. Use yyyy-mm-dd.");
            }
            LocalDateTime parsedFrom = DateTimeParser.parse(from);
            LocalDateTime parsedTo = DateTimeParser.parse(to);
            if (parsedFrom != null && parsedTo != null && !parsedFrom.isBefore(parsedTo)) {
                throw new ComputaException("Hmph! An event must end after it starts.");
            }
            return new Event(description, from, to);
        }

        throw new ComputaException("Hmph! Making small talk won't get you anywhere.  ʕ ꈍᴥꈍʔ");
    }

    /**
     * Checks whether the current list already contains a task with matching details.
     *
     * @param newTask task that the user is attempting to add.
     * @return true when a task with the same type, description, and schedule exists.
     */
    private boolean hasDuplicateTask(Task newTask) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(newTask));
    }

    /** Parses a date query and delegates its display to the UI. */
    private void showTasksOnDate(String command) throws ComputaException {
        String dateText = command.substring("on".length()).trim();
        LocalDate date = DateTimeParser.parseQueryDate(dateText);
        if (date == null) {
            throw new ComputaException("Hmph! Enter a date in yyyy-mm-dd format.");
        }

        ui.showTasksOnDate(tasks, date);
    }

    /** Parses a find command and delegates the keyword search to the UI. */
    private void findTasks(String command) throws ComputaException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ComputaException("Hmph! Tell me what to find.");
        }

        ui.showTasksContaining(tasks, keyword);
    }

    /** Sorts dated tasks chronologically and places tasks without parsed dates last. */
    private void sortTasks() {
        tasks.sort(Comparator.comparing(Task::getSortDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Marks or unmarks a task selected by its one-based number.
     *
     * @param command the complete mark or unmark command.
     * @param tasks stored tasks.
     * @param isMark true to mark the task, false to unmark it.
     */
    private static Task updateTaskStatus(String command, ArrayList<Task> tasks, boolean isMark)
            throws ComputaException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ComputaException(INVALID_TASK_NUMBER_MESSAGE);
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new ComputaException(INVALID_TASK_NUMBER_MESSAGE);
            }

            int taskIndex = taskNumber - 1;
            Task task = tasks.get(taskIndex);
            if (isMark) {
                task.markAsDone();
            } else {
                task.markAsUndone();
            }
            return task;
        } catch (NumberFormatException exception) {
            throw new ComputaException(INVALID_TASK_NUMBER_MESSAGE);
        }
    }

    /**
     * Deletes a task selected by its one-based number.
     *
     * @param command the complete delete command.
     * @param tasks stored tasks.
     * @throws ComputaException if the command does not contain a valid task number.
     */
    private static Task deleteTask(String command, ArrayList<Task> tasks) throws ComputaException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ComputaException(INVALID_TASK_NUMBER_MESSAGE);
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new ComputaException(INVALID_TASK_NUMBER_MESSAGE);
            }

            return tasks.remove(taskNumber - 1);
        } catch (NumberFormatException exception) {
            throw new ComputaException(INVALID_TASK_NUMBER_MESSAGE);
        }
    }

    /** Returns whether a command is exactly the keyword or starts with its argument separator. */
    private static boolean isCommand(String command, String keyword) {
        return command.equals(keyword) || (command.startsWith(keyword)
                && command.length() > keyword.length() && Character.isWhitespace(command.charAt(keyword.length())));
    }
}
