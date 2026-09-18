package meowmeow;

import meowmeow.parser.CommandType;
import meowmeow.parser.Parser;
import meowmeow.storage.Storage;
import meowmeow.task.Task;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

import java.time.LocalDate;

/**
 * Entry point for the MeowMeow chatbot.
 * Reads commands from standard input, updates the task list, and persists
 * changes to disk after every modification.
 */
public class MeowMeow {

    private static final String DEFAULT_FILE_PATH = "data/meowmeow.txt";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private String loadingError = null;
    private boolean isExit = false;

    /**
     * Constructs a chatbot that saves to and loads from the given file path.
     * Starts with an empty task list if the file cannot be read.
     *
     * @param filePath Path to the save file, relative to the project root.
     */
    public MeowMeow(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (MeowMeowException e) {
            loadingError = e.getMessage();
            tasks = new TaskList();
        }
    }

    /**
     * Constructs a chatbot using the default save file path.
     */
    public MeowMeow() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Returns the greeting shown when the chatbot starts, including a
     * warning if the save file could not be read.
     *
     * @return Welcome text.
     */
    public String getWelcome() {
        if (loadingError != null) {
            return ui.formatMessage(ui.formatLoadingError(loadingError), ui.formatWelcome());
        }
        return ui.formatWelcome();
    }

    /**
     * Returns whether the user has asked to exit.
     *
     * @return True once the bye command has been given.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the chatbot's reply to a single line of user input.
     * Errors are returned as text rather than thrown, so that the caller
     * can display them like any other response.
     *
     * @param input Raw command typed by the user.
     * @return Text to show the user.
     */
    public String getResponse(String input) {
        try {
            if (input.isEmpty()) {
                throw new MeowMeowException("You didn't type anything. Give me something to work with!");
            }

            CommandType command = Parser.parseCommand(input);
            assert command != null : "parseCommand returns UNKNOWN, never null";
            String arguments = Parser.parseArguments(input);

            return switch (command) {
                case BYE -> exit();
                case LIST -> ui.formatList(tasks.asList());
                case MARK -> markTask(arguments);
                case UNMARK -> unmarkTask(arguments);
                case DELETE -> deleteTask(arguments);
                case TODO -> addTask(Parser.parseTodo(arguments));
                case DEADLINE -> addTask(Parser.parseDeadline(arguments));
                case EVENT -> addTask(Parser.parseEvent(arguments));
                case ON -> listTasksOn(arguments);
                case FIND -> findTasks(arguments);
                case UNKNOWN -> throw new MeowMeowException(
                        ui.formatUnknownCommand(Parser.parseCommandWord(input)));
            };

        } catch (MeowMeowException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Adds the given task to the list, saves, and returns the confirmation.
     *
     * @param task Task to add.
     * @return Confirmation text naming the task and the new list size.
     * @throws MeowMeowException If the list already holds a duplicate, or the
     *         updated list cannot be saved.
     */
    private String addTask(Task task) throws MeowMeowException {
        if (tasks.hasDuplicateOf(task)) {
            throw new MeowMeowException("Meow :> You already have that task. I won't add it twice.");
        }
        tasks.add(task);
        storage.save(tasks.asList());
        return ui.formatAdded(task, tasks.size());
    }

    /**
     * Runs the command loop until the user exits.
     */
    public void run() {
        ui.printToConsole(getWelcome());
        while (!isExit) {
            ui.printToConsole(getResponse(ui.readCommand()));
        }
        ui.close();
    }

    /**
     * Starts the console version of chatbot.
     *
     * @param args Command line arguments, which are ignored.
     */
    public static void main(String[] args) {
        new MeowMeow(DEFAULT_FILE_PATH).run();
    }

    /**
     * Ends the session and returns the farewell message.
     *
     * @return Farewell text.
     */
    private String exit() {
        isExit = true;
        return ui.formatFarewell();
    }

    /**
     * Marks the task named by the given argument as done and saves the list.
     *
     * @param arguments Text the user typed after the command word.
     * @return Confirmation naming the task.
     * @throws MeowMeowException If the argument is not a valid task number, or
     *         the updated list cannot be saved.
     */
    private String markTask(String arguments) throws MeowMeowException {
        int index = tasks.parseIndex(arguments, "mark");
        tasks.markAsDone(index);
        storage.save(tasks.asList());
        return ui.formatTaskMessage("Nice! I've marked this task as done:", tasks.get(index));
    }

    /**
     * Marks the task named by the given argument as not done and saves the list.
     *
     * @param arguments Text the user typed after the command word.
     * @return Confirmation naming the task.
     * @throws MeowMeowException If the argument is not a valid task number, or
     *         the updated list cannot be saved.
     */
    private String unmarkTask(String arguments) throws MeowMeowException {
        int index = tasks.parseIndex(arguments, "unmark");
        tasks.markAsNotDone(index);
        storage.save(tasks.asList());
        return ui.formatTaskMessage("OK, I've marked this task as not done yet:", tasks.get(index));
    }

    /**
     * Removes the task named by the given argument and saves the list.
     *
     * @param arguments Text the user typed after the command word.
     * @return Confirmation naming the task and the new list size.
     * @throws MeowMeowException If the argument is not a valid task number, or
     *         the updated list cannot be saved.
     */
    private String deleteTask(String arguments) throws MeowMeowException {
        int index = tasks.parseIndex(arguments, "delete");
        Task removed = tasks.delete(index);
        storage.save(tasks.asList());
        return ui.formatRemoved(removed, tasks.size());
    }

    /**
     * Returns the tasks occurring on the date named by the given argument.
     *
     * @param arguments Text the user typed after the command word.
     * @return Numbered list of tasks on that date.
     * @throws MeowMeowException If the date is missing or cannot be read.
     */
    private String listTasksOn(String arguments) throws MeowMeowException {
        LocalDate date = Parser.parseDate(arguments).toLocalDate();
        return ui.formatTasksOn(date, tasks.findOccurringOn(date));
    }

    /**
     * Returns the tasks whose descriptions contain the given search text.
     *
     * @param arguments Text the user typed after the command word.
     * @return Numbered list of matching tasks.
     * @throws MeowMeowException If no search text was given.
     */
    private String findTasks(String arguments) throws MeowMeowException {
        if (arguments.isEmpty()) {
            throw new MeowMeowException("What should I search for? Try: find book");
        }
        return ui.formatMatches(tasks.findByKeyword(arguments));
    }

}