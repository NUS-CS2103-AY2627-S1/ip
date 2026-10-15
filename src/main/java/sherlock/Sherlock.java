package sherlock;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import sherlock.command.Command;
import sherlock.exception.SherlockException;
import sherlock.parser.Parser;
import sherlock.storage.Storage;
import sherlock.task.Task;
import sherlock.task.TaskList;
import sherlock.ui.Ui;

/**
 * The entry point for Sherlock, a detective-themed personal assistant chatbot.
 */
public class Sherlock {
    private static final Path SAVE_FILE = Path.of("data", "sherlock.txt");
    private static final String ERROR_PREFIX = "⚠ The trail has gone cold: ";
    private static final String HELP_MESSAGE = String.join(System.lineSeparator(),
            "Here are the commands in my casebook:",
            "  list - show every task",
            "  todo DESCRIPTION - add a todo",
            "  deadline DESCRIPTION /by yyyy-MM-dd - add a deadline",
            "  event DESCRIPTION /from START /to END - add an event",
            "  mark NUMBER - mark a task as done",
            "  unmark NUMBER - mark a task as not done",
            "  delete NUMBER - remove a task",
            "  find KEYWORD - find matching tasks",
            "  help - show this command guide",
            "  bye - close Sherlock");
    private final Storage storage;
    private TaskList tasks;
    private final Parser parser;
    /** Loading failure retained so both user interfaces can report it after construction. */
    private String loadingError;

    /**
     * Creates Sherlock using the default task data file.
     */
    public Sherlock() {
        this(SAVE_FILE);
    }

    /**
     * Creates Sherlock using a specified task data file.
     *
     * @param filePath location of the task data file
     */
    Sherlock(Path filePath) {
        this.storage = new Storage(filePath);
        this.parser = new Parser();
        this.tasks = loadTasks();
    }

    /**
     * Starts Sherlock, stores entered tasks, lists them on request, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used at this level
     */
    public static void main(String[] args) {
        try (Ui ui = new Ui(new Scanner(System.in))) {
            new Sherlock().run(ui);
        }
    }

    /**
     * Runs Sherlock's command loop until the user exits.
     *
     * @param ui console user interface
     */
    void run(Ui ui) {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showLoadingError(loadingError);
        }
        while (true) {
            String input = ui.readCommand();
            ui.showResponse(getResponse(input));
            if (input.equals("bye")) {
                return;
            }
        }
    }

    /**
     * Processes one command and returns the text to display in either user interface.
     *
     * @param input full command entered by the user
     * @return Sherlock's response to the command
     */
    public String getResponse(String input) {
        try {
            return execute(parser.parse(input, tasks.size()));
        } catch (DateTimeParseException exception) {
            return errorMessage("Enter deadline dates in yyyy-MM-dd format, for example 2019-10-15.");
        } catch (SherlockException | IOException exception) {
            return errorMessage(exception.getMessage());
        }
    }

    /**
     * Returns the greeting displayed when the graphical interface opens.
     *
     * @return greeting, including a storage warning when saved tasks could not be loaded
     */
    public String getWelcomeMessage() {
        String greeting = "Sherlock at your service.\nWhat mystery shall we solve today?";
        if (loadingError == null) {
            return greeting;
        }
        return greeting + "\n\n" + errorMessage("I could not load saved tasks: " + loadingError);
    }

    /**
     * Returns whether a response represents an error that should be highlighted by the GUI.
     *
     * @param response response returned by {@link #getResponse(String)}
     * @return whether the response begins with Sherlock's error phrase
     */
    public boolean isErrorResponse(String response) {
        return response.startsWith(ERROR_PREFIX);
    }

    /**
     * Applies a parsed command to Sherlock's task list.
     *
     * @param command command to execute
     * @return response describing the result
     * @throws IOException if an updated task list cannot be saved
     */
    private String execute(Command command) throws IOException {
        switch (command.getType()) {
            case BYE:
                return "The casebook is closed. Until our next investigation!";
            case HELP:
                return HELP_MESSAGE;
            case LIST:
                return formatTaskList("Here are the cases in your casebook:", tasks);
            case FIND:
                return formatMatchingTasks(tasks.find(command.getKeyword()));
            case MARK:
                Task completedTask = tasks.get(command.getTaskNumber() - 1);
                completedTask.markAsDone();
                storage.save(tasks);
                return "Elementary! This case is solved:\n  " + completedTask;
            case UNMARK:
                Task incompleteTask = tasks.get(command.getTaskNumber() - 1);
                incompleteTask.markAsNotDone();
                storage.save(tasks);
                return "The investigation continues for this case:\n  " + incompleteTask;
            case DELETE:
                Task deletedTask = tasks.delete(command.getTaskNumber() - 1);
                storage.save(tasks);
                return "That case has been removed from the casebook:\n  " + deletedTask
                        + "\n" + tasks.size() + " cases remain.";
            case ADD:
                tasks.add(command.getTask());
                storage.save(tasks);
                return "A new case has been entered into the casebook:\n  " + command.getTask();
            default:
                throw new AssertionError("Unhandled command type: " + command.getType());
        }
    }

    /**
     * Formats search results with one-based numbering for display.
     *
     * @param matchingTasks tasks whose descriptions matched the search keyword
     * @return formatted search result message
     */
    private String formatMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "No matching clues were found in the casebook.";
        }
        StringBuilder response = new StringBuilder("These cases match your clue:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            response.append(System.lineSeparator())
                    .append(index + 1)
                    .append(". ")
                    .append(matchingTasks.get(index));
        }
        return response.toString();
    }

    /**
     * Formats all tasks with one-based numbering for display.
     *
     * @param heading text placed before the numbered tasks
     * @param taskList tasks to include
     * @return formatted task-list message
     */
    private String formatTaskList(String heading, TaskList taskList) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < taskList.size(); index++) {
            response.append(System.lineSeparator())
                    .append(index + 1)
                    .append(". ")
                    .append(taskList.get(index));
        }
        return response.toString();
    }

    /**
     * Adds the common visual prefix to a user-facing error.
     *
     * @param message explanation of the error
     * @return consistently formatted error message
     */
    private String errorMessage(String message) {
        return ERROR_PREFIX + message;
    }

    /**
     * Loads tasks, showing an error and continuing with an empty list if storage cannot be read.
     *
     * @return saved tasks, or an empty list after a loading error
     */
    private TaskList loadTasks() {
        try {
            return storage.load();
        } catch (IOException | SherlockException | DateTimeParseException exception) {
            loadingError = exception.getMessage();
            return new TaskList(100);
        }
    }

}
