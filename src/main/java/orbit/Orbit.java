package orbit;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Orbit is a command-line chatbot that keeps track of a user's tasks.
 */
public class Orbit {
    private static final String DATA_FILE_PATH = "data/orbit.txt";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";

    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private String storageWarning = "";
    private boolean storageAvailable = true;

    /**
     * Creates an Orbit chatbot that reads commands from the supplied scanner.
     *
     * @param scanner Source of user commands.
     */
    public Orbit(Scanner scanner) {
        this(scanner, DATA_FILE_PATH);
    }

    /**
     * Creates a chatbot using a chosen data location.
     *
     * @param scanner Source of commands.
     * @param filePath Task data location.
     */
    public Orbit(Scanner scanner, String filePath) {
        this.ui = new Ui(scanner);
        this.parser = new Parser();
        this.storage = new Storage(filePath);
        List<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (IOException | OrbitException e) {
            loadedTasks = List.of();
            storageAvailable = false;
            storageWarning = "OOPS!!! Could not load saved tasks: " + e.getMessage()
                    + "\nSaving is disabled to protect your file. Repair it and restart Orbit.";
        }
        this.tasks = new TaskList(loadedTasks);
    }

    /** Creates an Orbit chatbot for use by the graphical interface. */
    public Orbit() {
        this(new Scanner(System.in));
    }

    /**
     * Starts the chatbot and processes commands until the user enters {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        if (!storageWarning.isEmpty()) {
            ui.showMessage(storageWarning);
        }
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showMessage(getResponse(input));
            if (input.strip().equalsIgnoreCase("bye")) {
                return;
            }
        }
    }

    /**
     * Processes one user command and returns the response for either user interface.
     *
     * @param input Complete user command.
     * @return Orbit's response to the command.
     */
    public String getResponse(String input) {
        try {
            input = input == null ? "" : input.strip();
            if (input.contains("|") || input.contains("\n") || input.contains("\r")) {
                throw new OrbitException("Commands cannot contain pipes or line breaks.");
            }
            input = input.replaceAll("\\s+(/by|/from|/to)(?=\\s|$)\\s*", " $1 ");
            CommandType command = parser.parse(input);
            if ((command == CommandType.LIST || command == CommandType.BYE)
                    && input.split("\\s+", 2).length > 1) {
                throw new OrbitException("This command does not take arguments.");
            }
            if (command == CommandType.BYE) {
                return "Bye. Hope to see you again soon!";
            }
            String response = execute(command, input);
            return storageWarning.isEmpty() ? response : response + "\n" + storageWarning;
        } catch (OrbitException e) {
            return "OOPS!!! " + e.getMessage();
        }
    }

    private String execute(CommandType command, String input) throws OrbitException {
        switch (command) {
            case LIST:
                return getTaskListMessage();
            case MARK:
                return updateStatus(input, true);
            case UNMARK:
                return updateStatus(input, false);
            case TODO:
                return addTodo(input);
            case DEADLINE:
                return addDeadline(input);
            case EVENT:
                return addEvent(input);
            case DELETE:
                return deleteTask(input);
            case FIND:
                return findTasks(input);
            default:
                throw new OrbitException("I'm sorry, but I don't know what that means :-(");
        }
    }

    private String getTaskListMessage() {
        return Ui.formatTaskList("Here are the tasks in your list:", tasks.asList());
    }

    private String updateStatus(String input, boolean isDone) throws OrbitException {
        String command = isDone ? "mark" : "unmark";
        int index = parseTaskIndex(input, command);
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        saveTasks();
        String message = isDone ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return message + "\n   " + task;
    }

    private String addTodo(String input) throws OrbitException {
        String description = extractDescription(input, "todo");
        return addTask(new Todo(description));
    }

    private String addDeadline(String input) throws OrbitException {
        String details = extractDescription(input, "deadline");
        int separator = details.indexOf(DEADLINE_SEPARATOR);
        if (separator < 0 || separator != details.lastIndexOf(DEADLINE_SEPARATOR)) {
            throw new OrbitException("A deadline needs a description and '/by' date or time.");
        }
        String description = details.substring(0, separator).trim();
        String by = details.substring(separator + DEADLINE_SEPARATOR.length()).trim();
        requireNonEmpty(description, "The description of a deadline cannot be empty.");
        requireNonEmpty(by, "The '/by' date or time of a deadline cannot be empty.");
        try {
            return addTask(new Deadline(description, by));
        } catch (DateTimeParseException e) {
            throw new OrbitException("Use yyyy-mm-dd for deadline dates, for example 2026-08-28.");
        }
    }

    private String addEvent(String input) throws OrbitException {
        String details = extractDescription(input, "event");
        int fromSeparator = details.indexOf(EVENT_START_SEPARATOR);
        int toSeparator = details.indexOf(EVENT_END_SEPARATOR);
        if (fromSeparator < 0 || toSeparator < 0 || toSeparator < fromSeparator
                || fromSeparator != details.lastIndexOf(EVENT_START_SEPARATOR)
                || toSeparator != details.lastIndexOf(EVENT_END_SEPARATOR)) {
            throw new OrbitException("An event needs a description, '/from' time, and '/to' time.");
        }
        String description = details.substring(0, fromSeparator).trim();
        String from = details.substring(fromSeparator + EVENT_START_SEPARATOR.length(), toSeparator).trim();
        String to = details.substring(toSeparator + EVENT_END_SEPARATOR.length()).trim();
        requireNonEmpty(description, "The description of an event cannot be empty.");
        requireNonEmpty(from, "The '/from' time of an event cannot be empty.");
        requireNonEmpty(to, "The '/to' time of an event cannot be empty.");
        return addTask(new Event(description, from, to));
    }

    private String addTask(Task task) throws OrbitException {
        if (tasks.containsDuplicate(task)) {
            throw new OrbitException("This task already exists. Use list to find it, or unmark it to reopen it.");
        }
        tasks.add(task);
        saveTasks();
        return "Got it. I've added this task:\n   " + task
                + "\n Now you have " + tasks.size() + " tasks in the list.";
    }

    private String deleteTask(String input) throws OrbitException {
        int index = parseTaskIndex(input, "delete");
        Task removed = tasks.remove(index);
        saveTasks();
        return "Noted. I've removed this task:\n   " + removed
                + "\n Now you have " + tasks.size() + " tasks in the list.";
    }

    private String findTasks(String input) throws OrbitException {
        String keyword = extractDescription(input, "find");
        List<Task> matches = tasks.find(keyword);
        return Ui.formatTaskList("Here are the matching tasks in your list:", matches);
    }

    /** Keeps unsaved changes in memory and reports persistence failures to both interfaces. */
    private void saveTasks() {
        if (!storageAvailable) {
            return;
        }
        storageWarning = "";
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            storageWarning = "OOPS!!! Could not save tasks: " + e.getMessage()
                    + "\nChanges are in memory only. Keep Orbit open and restore access to the data folder.";
        }
    }

    private int parseTaskIndex(String input, String command) throws OrbitException {
        String argument = input.substring(command.length()).trim();
        if (argument.isEmpty()) {
            throw new OrbitException("Please specify a task number to " + command + ".");
        }
        try {
            int index = Integer.parseInt(argument) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new OrbitException("That task number does not exist.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new OrbitException("The task number must be a whole number.");
        }
    }

    private String extractDescription(String input, String command) throws OrbitException {
        String description = input.substring(command.length()).trim();
        requireNonEmpty(description, "The description of a " + command + " cannot be empty.");
        return description;
    }

    private void requireNonEmpty(String text, String errorMessage) throws OrbitException {
        if (text.isEmpty()) {
            throw new OrbitException(errorMessage);
        }
    }

    /** @return Storage warning, or an empty string when storage is healthy. */
    public String getStorageWarning() {
        return storageWarning;
    }

    /**
     * Runs Orbit using standard input.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Orbit(new Scanner(System.in)).run();
    }
}
