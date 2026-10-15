package bot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import bot.exception.BotException;
import bot.parser.ParsedCommand;
import bot.parser.Parser;
import bot.storage.Storage;
import bot.task.Task;
import bot.task.TaskList;
import bot.ui.ResponseFormatter;
import bot.ui.Ui;

/**
 * A chatbot that stores tasks and executes commands from console or JavaFX interfaces.
 */
public class Bot {
    private static final String DEFAULT_DATA_FILE_PATH = "./data/bot.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final String startupMessage;
    private boolean shouldExit;

    /**
     * Creates a chatbot backed by the default data file.
     */
    public Bot() {
        this(new Storage(new File(DEFAULT_DATA_FILE_PATH)));
    }

    /**
     * Creates a chatbot backed by the specified storage.
     *
     * @param storage storage used to load and save tasks.
     */
    Bot(Storage storage) {
        this.storage = storage;

        TaskList loadedTasks;
        String loadingMessage = "";
        try {
            loadedTasks = storage.loadTasks();
        } catch (IOException error) {
            loadedTasks = new TaskList();
            loadingMessage = ResponseFormatter.formatLoadingError(error.getMessage());
        }
        this.tasks = loadedTasks;
        this.startupMessage = loadingMessage;
    }

    /**
     * Parses and executes one command, returning a response suitable for display.
     *
     * @param input command entered by the user.
     * @return response describing the result of the command
     */
    public String getResponse(String input) {
        try {
            return executeCommand(Parser.parse(input));
        } catch (BotException error) {
            return ResponseFormatter.formatError(error.getMessage());
        } catch (IOException error) {
            return ResponseFormatter.formatSavingError(error.getMessage());
        }
    }

    /**
     * Returns any error encountered while loading tasks during startup.
     *
     * @return loading error response, or an empty string when loading succeeded
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Checks whether the user has entered the bye command.
     *
     * @return {@code true} when the application should exit
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Starts the console interface and handles commands entered by the user.
     *
     * @param args command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Bot bot = new Bot();
        if (!bot.getStartupMessage().isEmpty()) {
            ui.showResponse(bot.getStartupMessage());
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }

            ui.showCommandSeparator();
            ui.showResponse(bot.getResponse(input));
            ui.showCommandSeparator();
        }
        ui.showGoodbye();
    }

    /**
     * Executes a parsed command and applies any resulting task-list changes.
     *
     * @param command parsed command to execute.
     * @return response describing the command result
     * @throws BotException if the command or its arguments are invalid
     * @throws IOException if a task-list change cannot be saved
     */
    private String executeCommand(ParsedCommand command) throws BotException, IOException {
        String arguments = command.getArguments();

        switch (command.getType()) {
            case LIST:
                return listTasks(arguments);
            case MARK:
                return markTask(arguments);
            case UNMARK:
                return unmarkTask(arguments);
            case DELETE:
                return deleteTask(arguments);
            case TODO:
                return addTask(Parser.parseTodo(arguments));
            case DEADLINE:
                return addTask(Parser.parseDeadline(arguments));
            case EVENT:
                return addTask(Parser.parseEvent(arguments));
            case FIND:
                return findTasks(arguments);
            case ON:
                return findTasksOnDate(arguments);
            case SORT:
                return sortTasks(arguments);
            case BYE:
                return exit(arguments);
            case UNKNOWN:
                throw createUnknownCommandException(command.getCommandWord());
            default:
                throw new BotException("I don't know what that command means.");
        }
    }

    /**
     * Validates the list command and formats all stored tasks.
     *
     * @param arguments text following the command word.
     * @return formatted task list
     * @throws BotException if unexpected arguments were supplied
     */
    private String listTasks(String arguments) throws BotException {
        Parser.ensureNoArguments(arguments, "list");
        return ResponseFormatter.formatTaskList(tasks);
    }

    /**
     * Sorts dated tasks chronologically, places todos afterward, and saves the new order.
     *
     * @param arguments unexpected text after the sort command.
     * @return message containing the sorted task list
     * @throws BotException if extra arguments are supplied
     * @throws IOException if the updated task order cannot be saved
     */
    private String sortTasks(String arguments) throws BotException, IOException {
        Parser.ensureNoArguments(arguments, "sort");
        tasks.sortChronologically();
        storage.saveTasks(tasks);
        return ResponseFormatter.formatSortedTaskList(tasks);
    }

    /**
     * Marks a task as done and saves the updated task list.
     *
     * @param arguments text containing the task number.
     * @return confirmation that the task was marked
     * @throws BotException if the task number is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String markTask(String arguments) throws BotException, IOException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "mark");
        Task task = tasks.mark(taskIndex);
        storage.saveTasks(tasks);
        return ResponseFormatter.formatTaskMarked(task);
    }

    /**
     * Marks a task as not done and saves the updated task list.
     *
     * @param arguments text containing the task number.
     * @return confirmation that the task was unmarked
     * @throws BotException if the task number is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String unmarkTask(String arguments) throws BotException, IOException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "unmark");
        Task task = tasks.unmark(taskIndex);
        storage.saveTasks(tasks);
        return ResponseFormatter.formatTaskUnmarked(task);
    }

    /**
     * Deletes a task and saves the updated task list.
     *
     * @param arguments text containing the task number.
     * @return confirmation that the task was deleted
     * @throws BotException if the task number is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String deleteTask(String arguments) throws BotException, IOException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "delete");
        Task removedTask = tasks.delete(taskIndex);
        storage.saveTasks(tasks);
        return ResponseFormatter.formatTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Adds a task and saves the updated task list.
     *
     * @param task task to add.
     * @return confirmation that the task was added
     * @throws BotException if an equivalent task already exists
     * @throws IOException if the updated task list cannot be saved
     */
    private String addTask(Task task) throws BotException, IOException {
        if (tasks.containsSameTask(task)) {
            throw new BotException("That task is already in your list.");
        }
        tasks.add(task);
        storage.saveTasks(tasks);
        return ResponseFormatter.formatTaskAdded(task, tasks.size());
    }

    /**
     * Finds tasks containing the requested description keyword.
     *
     * @param arguments text containing the search keyword.
     * @return formatted matching tasks
     * @throws BotException if the keyword is empty
     */
    private String findTasks(String arguments) throws BotException {
        String keyword = Parser.parseFindKeyword(arguments);
        return ResponseFormatter.formatMatchingTasks(tasks.find(keyword));
    }

    /**
     * Finds deadlines and events occurring on the requested date.
     *
     * @param arguments text containing the requested date.
     * @return formatted tasks occurring on the date
     * @throws BotException if the date is missing or invalid
     */
    private String findTasksOnDate(String arguments) throws BotException {
        LocalDate date = Parser.parseDateQuery(arguments);
        return ResponseFormatter.formatTasksOnDate(tasks, date);
    }

    /**
     * Validates the bye command and records that the application should exit.
     *
     * @param arguments text following the command word.
     * @return farewell response
     * @throws BotException if unexpected arguments were supplied
     */
    private String exit(String arguments) throws BotException {
        if (!arguments.isEmpty()) {
            throw new BotException("Use bye without any extra words.");
        }
        shouldExit = true;
        return ResponseFormatter.formatGoodbye();
    }

    /**
     * Creates the error reported for an empty or unrecognized command word.
     *
     * @param commandWord command word entered by the user.
     * @return exception describing why the command is unknown
     */
    private BotException createUnknownCommandException(String commandWord) {
        if (commandWord.isEmpty()) {
            return new BotException("Please enter a command.");
        }
        return new BotException("I don't know what \"" + commandWord + "\" means.");
    }
}
