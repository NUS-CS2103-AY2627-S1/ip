package jelly;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import jelly.command.CommandType;
import jelly.command.Parser;
import jelly.exception.JellyException;
import jelly.model.Deadline;
import jelly.model.Event;
import jelly.model.Task;
import jelly.model.TaskList;
import jelly.model.Todo;
import jelly.storage.Storage;
import jelly.ui.Ui;
import jelly.util.DateTimeParser;

/** Runs Jelly's command-line task manager. */
public class Jelly {

    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String FIND_COMMAND = "find";
    private static final String TAG_COMMAND = "tag";
    private static final String UNTAG_COMMAND = "untag";

    private final TaskList tasks;
    private final Storage storage;
    private final Parser parser;
    private final Ui ui;
    private boolean lastResponseWasError;

    /**
     * Creates a Jelly instance and loads its saved tasks.
     */
    public Jelly() {
        ui = new Ui();
        storage = new Storage();
        parser = new Parser();

        TaskList loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Starts Jelly, reads commands from standard input, and updates the task list.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Jelly jelly = new Jelly();
        jelly.ui.showWelcome();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (jelly.parser.parse(command) == CommandType.BYE) {
                jelly.ui.showBye();
                break;
            }

            System.out.println(jelly.executeCommand(command));
        }
    }

    /**
     * Processes one command entered by the user.
     *
     * @param command the command to process.
     * @return the response that should be displayed.
     */
    public String executeCommand(String command) {
        lastResponseWasError = false;
        CommandType commandType = parser.parse(command);

        try {
            switch (commandType) {
                case LIST:
                    return ui.formatTaskList(tasks);
                case TODO:
                    return executeTodoCommand(command);
                case FIND:
                    return executeFindCommand(command);
                case MARK:
                    return executeMarkCommand(command, true);
                case UNMARK:
                    return executeMarkCommand(command, false);
                case DELETE:
                    return executeDeleteCommand(command);
                case DEADLINE:
                    return executeDeadlineCommand(command);
                case EVENT:
                    return executeEventCommand(command);
                case TAG:
                    return executeTagCommand(command);
                case UNTAG:
                    return executeUntagCommand(command);
                case HELP:
                    return Ui.HELP_MESSAGE;
                case BYE:
                    return Ui.BYE_MESSAGE;
                default:
                    throw new JellyException("Jelly doesn't recognize your command. Try again~");
            }
        } catch (JellyException e) {
            lastResponseWasError = true;
            return e.getMessage();
        } catch (IllegalArgumentException e) {
            lastResponseWasError = true;
            return "Please enter a valid command: " + e.getMessage();
        }
    }

    /** Returns whether the most recently executed command produced an error. */
    public boolean wasLastResponseAnError() {
        return lastResponseWasError;
    }

    /**
     * Creates and saves a to-do task.
     *
     * @param command the complete to-do command.
     * @return the response to display.
     * @throws JellyException if the description is empty.
     */
    private String executeTodoCommand(String command) throws JellyException {
        String description = argumentAfter(command, TODO_COMMAND);

        if (description.isEmpty()) {
            throw new JellyException("Jelly needs a task to stick to the jar!");
        }

        Todo todo = new Todo(description);
        tasks.addTask(todo);

        StringBuilder response = new StringBuilder();
        response.append("Boop! This task is now stuck to your jar:\n");
        response.append("   ").append(todo);
        response.append("\nNow you have ")
                .append(tasks.size())
                .append(" tasks in your Jelly jar~");

        try {
            storage.save(tasks);
        } catch (IOException e) {
            response.append("\n\nJelly could not save your tasks.");
        }

        return response.toString();
    }

    /** Finds tasks whose descriptions contain the requested keyword. */
    private String executeFindCommand(String command) throws JellyException {
        String keyword = argumentAfter(command, FIND_COMMAND);
        if (keyword.isEmpty()) {
            throw new JellyException("Please enter a keyword to find.");
        }

        StringBuilder response = new StringBuilder("Here are the tasks Jelly found in your jar:\n");
        for (int taskNumber : tasks.findMatchingTaskNumbers(keyword)) {
            response.append(taskNumber)
                    .append(".")
                    .append(tasks.getTask(taskNumber - 1))
                    .append("\n");
        }
        return response.toString().trim();
    }

    /** Marks or unmarks a task. */
    private String executeMarkCommand(String command, boolean mark) throws JellyException {
        String keyword = mark ? MARK_COMMAND : UNMARK_COMMAND;
        if (command.equals(keyword)) {
            throw new JellyException("Please enter a valid task number.");
        }

        int taskNumber = parseTaskNumber(argumentAfter(command, keyword));
        Task task = tasks.getTask(taskNumber - 1);
        if (mark) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        saveTasks();

        String status = mark ? "[X]" : "[ ]";
        String message = mark ? "Nice wobble! Jelly marked this task as done~"
                : "No wobble—Jelly marked this task as not done yet~";
        return message + "\n   " + status + " " + task.getDescription();
    }

    /** Deletes a task. */
    private String executeDeleteCommand(String command) throws JellyException {
        if (command.equals(DELETE_COMMAND)) {
            throw new JellyException("Please enter a task number to delete.");
        }

        int taskNumber = parseTaskNumber(argumentAfter(command, DELETE_COMMAND));
        Task deletedTask = tasks.deleteTask(taskNumber - 1);
        saveTasks();
        return "Splat! Jelly removed that task from the jar :)\n" + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in your Jelly jar~";
    }

    /** Creates and saves a deadline task. */
    private String executeDeadlineCommand(String command) throws JellyException {
        if (!command.startsWith(DEADLINE_COMMAND + " ")) {
            throw new JellyException("A Jelly deadline needs a description and a /by date.");
        }

        String[] parts = argumentAfter(command, DEADLINE_COMMAND).split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new JellyException("Use: deadline <description> /by yyyy-mm-dd HHmm");
        }

        LocalDateTime dateTime = parseDate(parts[1].trim(),
                "Use a deadline date in the format yyyy-MM-dd HHmm, e.g. 2019-12-02 1800.");
        Deadline deadline = new Deadline(parts[0].trim(), dateTime);
        tasks.addTask(deadline);
        saveTasks();
        return "Boop! This deadline is now stuck to your jar:\n   " + deadline
                + "\nNow you have " + tasks.size() + " tasks in your Jelly jar~";
    }

    /** Creates and saves an event task. */
    private String executeEventCommand(String command) throws JellyException {
        if (!command.startsWith(EVENT_COMMAND + " ")) {
            throw new JellyException("A Jelly event needs a description, start time, and end time.");
        }

        String[] parts = argumentAfter(command, EVENT_COMMAND).split(" /from ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty()) {
            throw new JellyException("Use: event <description> /from yyyy-mm-dd HHmm /to yyyy-mm-dd HHmm");
        }
        String[] times = parts[1].split(" /to ", 2);
        if (times.length < 2 || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            throw new JellyException("Use: event <description> /from yyyy-mm-dd HHmm /to yyyy-mm-dd HHmm");
        }

        LocalDateTime from = parseDate(times[0].trim(),
                "Use event dates in the format yyyy-MM-dd HHmm, e.g. 2019-12-02 1800.");
        LocalDateTime to = parseDate(times[1].trim(),
                "Use event dates in the format yyyy-MM-dd HHmm, e.g. 2019-12-02 1800.");
        if (!to.isAfter(from)) {
            throw new JellyException("An event's end time must be after its start time.");
        }

        Event event = new Event(parts[0].trim(), from, to);
        tasks.addTask(event);
        saveTasks();
        return "Boop! This event is now tucked into your jar:\n   " + event
                + "\nNow you have " + tasks.size() + " tasks in your Jelly jar~";
    }

    /** Parses and validates a one-based task number against the current task list. */
    private int parseTaskNumber(String value) throws JellyException {
        try {
            int taskNumber = Integer.parseInt(value);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new JellyException("Please enter a valid task number.");
            }
            assert taskNumber >= 1 && taskNumber <= tasks.size() : "Task number must refer to an existing task";
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new JellyException("Jelly needs a valid task number to wobble with.");
        }
    }

    /** Returns the trimmed argument following a command prefix. */
    private static String argumentAfter(String command, String commandPrefix) {
        return command.substring(commandPrefix.length()).trim();
    }

    /** Parses a date and converts parsing failures into Jelly errors. */
    private LocalDateTime parseDate(String value, String errorMessage) throws JellyException {
        try {
            return DateTimeParser.parse(value);
        } catch (DateTimeParseException e) {
            throw new JellyException(errorMessage);
        }
    }

    /** Saves the current task list. */
    private void saveTasks() throws JellyException {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            throw new JellyException("Jelly could not save your tasks. Please check access to data/jelly.txt.");
        }
    }

    /**
     * Assigns a tag to a task.
     *
     * @param command the complete tag command.
     * @return a confirmation message.
     * @throws JellyException if the task number is invalid.
     */
    private String executeTagCommand(String command) throws JellyException {
        String arguments = argumentAfter(command, TAG_COMMAND);

        if (arguments.isBlank()) {
            throw new JellyException("Use: tag <task number> <tag>");
        }

        String[] parts = arguments.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new JellyException("Use: tag <task number> <tag>");
        }

        int taskNumber = parseTaskNumber(parts[0]);
        String tag = parts[1].trim();

        if (!tag.matches("[A-Za-z0-9_-]+")) {
            throw new JellyException("Tags may contain only letters, numbers, '-' and '_'.");
        }

        Task task = tasks.getTask(taskNumber - 1);
        task.setTag(tag);
        saveTasks();

        return "Jelly has tagged your task as #" + tag + ":\n " + task;
    }

    /**
     * Removes a tag from a task.
     *
     * @param command the complete untag command.
     * @return the response to display.
     * @throws JellyException if the task number is invalid.
     */
    private String executeUntagCommand(String command) throws JellyException {
        String arguments = argumentAfter(command, UNTAG_COMMAND);

        if (arguments.isBlank()) {
            throw new JellyException("Use: untag <task number>");
        }

        int taskNumber = parseTaskNumber(arguments);
        Task task = tasks.getTask(taskNumber - 1);
        task.removeTag();
        saveTasks();

        return "Jelly has removed the tag from this task:\n " + task;
    }

}
