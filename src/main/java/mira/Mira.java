package mira;

import java.nio.file.Path;
import java.util.List;

import mira.command.Command;
import mira.command.CommandType;
import mira.exception.MiraException;
import mira.parser.Parser;
import mira.storage.Storage;
import mira.task.Task;
import mira.task.TaskList;
import mira.ui.Ui;

/**
 * Entry point and coordinator for the Mira task-tracking chatbot.
 */
public class Mira {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "mira.txt");
    private static final String WELCOME_MESSAGE = "Hello! I'm Mira\nWhat can I do for you?";
    private static final String GOODBYE_MESSAGE = "Bye. Hope to see you again soon!";

    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Mira session connected to standard input and local storage.
     *
     * @throws MiraException if saved tasks cannot be loaded.
     */
    public Mira() throws MiraException {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates a Mira session backed by the specified data file.
     *
     * @param dataFile File used to load and save tasks.
     * @throws MiraException If saved tasks cannot be loaded.
     */
    public Mira(Path dataFile) throws MiraException {
        this.storage = new Storage(dataFile);
        this.tasks = storage.load();
        this.parser = new Parser();
        this.ui = new Ui();
    }

    /**
     * Processes commands until the user exits or the input stream ends.
     */
    public void run() {
        ui.showResponse(WELCOME_MESSAGE);

        while (ui.hasNextCommand()) {
            try {
                Command command = parser.parse(ui.readCommand());
                ui.showResponse(execute(command));
                if (command.getType() == CommandType.BYE) {
                    return;
                }
            } catch (MiraException exception) {
                ui.showResponse(getErrorMessage(exception));
            }
        }
    }

    /**
     * Processes one command and returns the response for a graphical UI.
     *
     * @param input Complete command entered by the user.
     * @return User-facing response, including validation errors.
     */
    public String getResponse(String input) {
        try {
            return execute(parser.parse(input));
        } catch (MiraException exception) {
            return getErrorMessage(exception);
        }
    }

    /**
     * Returns the greeting shown when a UI starts.
     *
     * @return Mira's welcome message.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Executes a validated command and builds its user-facing response.
     *
     * @param command Command to execute.
     * @return Response describing the command result.
     * @throws MiraException If the command cannot be completed.
     */
    private String execute(Command command) throws MiraException {
        switch (command.getType()) {
            case BYE:
                return GOODBYE_MESSAGE;
            case LIST:
                return listTasks();
            case FIND:
                return findTasks(command.getKeyword());
            case TODO, DEADLINE, EVENT:
                return addTask(command.getTask());
            case MARK:
                return setTaskDone(command.getTaskNumber(), true);
            case UNMARK:
                return setTaskDone(command.getTaskNumber(), false);
            case DELETE:
                return deleteTask(command.getTaskNumber());
            case UNKNOWN:
                throw new MiraException("That command is not supported.");
            default:
                throw new MiraException("That command is not supported.");
        }
    }

    /**
     * Builds the response for listing every stored task.
     *
     * @return The complete task list or its empty-state message.
     */
    private String listTasks() {
        return getTaskListMessage(
                "Here are the tasks in your list:",
                "Your task list is empty.",
                tasks.asList());
    }

    /**
     * Finds tasks matching a keyword and builds the response.
     *
     * @param keyword Text to find in task descriptions.
     * @return Matching tasks or the no-match message.
     */
    private String findTasks(String keyword) {
        return getTaskListMessage(
                "Here are the matching tasks in your list:",
                "No matching tasks found.",
                tasks.find(keyword));
    }

    /**
     * Adds and persists a task before building its confirmation.
     *
     * @param task Task to add.
     * @return Confirmation describing the added task.
     * @throws MiraException If the updated task list cannot be saved.
     */
    private String addTask(Task task) throws MiraException {
        TaskList updatedTasks = new TaskList(tasks.asList());
        updatedTasks.add(task);
        storage.save(updatedTasks);
        tasks = updatedTasks;
        return "Got it. I've added this task:\n  " + task
                + "\n" + getTaskCountMessage(tasks.size());
    }

    /**
     * Changes and persists a task's completion status before building confirmation.
     *
     * @param taskNumber One-based number of the task to update.
     * @param isDone New completion status.
     * @return Confirmation describing the updated task.
     * @throws MiraException If the task number is invalid or the list cannot be saved.
     */
    private String setTaskDone(int taskNumber, boolean isDone) throws MiraException {
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.setDone(isDone);
        try {
            storage.save(tasks);
        } catch (MiraException exception) {
            task.setDone(wasDone);
            throw exception;
        }
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return message + "\n  " + task;
    }

    /**
     * Deletes and persists a task before building confirmation.
     *
     * @param taskNumber One-based number of the task to delete.
     * @return Confirmation describing the deleted task.
     * @throws MiraException If the task number is invalid or the list cannot be saved.
     */
    private String deleteTask(int taskNumber) throws MiraException {
        TaskList updatedTasks = new TaskList(tasks.asList());
        Task removedTask = updatedTasks.delete(taskNumber);
        storage.save(updatedTasks);
        tasks = updatedTasks;
        return "Noted. I've removed this task:\n  " + removedTask
                + "\n" + getTaskCountMessage(tasks.size());
    }

    /**
     * Builds a heading followed by a one-based numbered task list.
     *
     * @param header Heading displayed above matching tasks.
     * @param emptyMessage Message used when no tasks match.
     * @param matchingTasks Tasks to display in order.
     * @return The formatted list or the empty-state message.
     */
    private String getTaskListMessage(
            String header, String emptyMessage, List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder message = new StringBuilder(header);
        List<Task> allTasks = tasks.asList();
        for (Task task : matchingTasks) {
            message.append(System.lineSeparator())
                    .append(allTasks.indexOf(task) + 1)
                    .append(". ")
                    .append(task);
        }
        return message.toString();
    }

    /**
     * Builds a grammatically correct task-count sentence.
     *
     * @param taskCount Number of tasks currently stored.
     * @return Sentence describing the task count.
     */
    private String getTaskCountMessage(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + noun + " in the list.";
    }

    /**
     * Formats a recoverable parsing or storage error.
     *
     * @param exception Error raised while processing a command.
     * @return User-facing error response.
     */
    private String getErrorMessage(MiraException exception) {
        return "OOPS!!! " + exception.getMessage();
    }

    /**
     * Launches Mira.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        try {
            new Mira().run();
        } catch (MiraException exception) {
            new Ui().showResponse("OOPS!!! " + exception.getMessage());
        }
    }
}
