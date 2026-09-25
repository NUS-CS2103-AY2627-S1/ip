package goat;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Coordinates user interaction, command parsing, task management, and storage.
 */
public class Goat {
    private static final Path DATA_FILE_PATH = Path.of("data", "goat.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExitRequested;

    /**
     * Creates an application using the default task data file.
     */
    public Goat() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates an application that stores tasks at the given path.
     *
     * @param filePath path of the task data file
     */
    public Goat(Path filePath) {
        storage = new Storage(filePath);
        ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException | GoatException exception) {
            loadedTasks = new TaskList();
            ui.showLoadWarning(exception.getMessage());
        }
        tasks = loadedTasks;
    }

    /**
     * Runs the application until the input ends or the user exits.
     */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            ui.showResponse(getResponse(input));
            ui.showLine();
            if (isExitRequested) {
                return;
            }
        }
    }

    /**
     * Returns the greeting and any warning raised while loading saved tasks.
     *
     * @return startup message
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Processes one command and returns the response for either user interface.
     *
     * @param input raw command entered by the user
     * @return response to display
     */
    public String getResponse(String input) {
        try {
            return execute(Parser.parse(input));
        } catch (GoatException exception) {
            return ui.formatError(exception.getMessage());
        } catch (IOException exception) {
            return ui.formatError("I couldn't save your tasks: " + exception.getMessage()
                    + " Your task list was not changed.");
        }
    }

    /**
     * Returns whether the most recent command requested that the application close.
     *
     * @return whether exit was requested
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Executes one parsed command and returns its response.
     */
    private String execute(ParsedCommand command) throws GoatException, IOException {
        switch (command.type()) {
            case BYE:
                Parser.requireNoArgument(command);
                isExitRequested = true;
                return ui.getGoodbyeMessage();
            case LIST:
                Parser.requireNoArgument(command);
                return ui.formatTaskList(tasks);
            case MARK:
                return updateCompletion(command.argument(), command.commandWord(), true);
            case SORT:
                Parser.requireNoArgument(command);
                return sortTasks();
            case UNMARK:
                return updateCompletion(command.argument(), command.commandWord(), false);
            case DELETE:
                return deleteTask(command.argument(), command.commandWord());
            case TODO:
                return addTask(Parser.parseTodo(command.argument()));
            case DEADLINE:
                return addTask(Parser.parseDeadline(command.argument()));
            case EVENT:
                return addTask(Parser.parseEvent(command.argument()));
            case FIND:
                return ui.formatMatchingTasks(tasks.find(Parser.parseKeyword(command.argument())));
            case UNKNOWN:
                throw Parser.unknownCommandException();
            default:
                throw Parser.unknownCommandException();
        }
    }

    /**
     * Adds and saves a task, rolling back if saving fails.
     */
    private String addTask(Task task) throws IOException {
        tasks.add(task);
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            tasks.delete(tasks.size() - 1);
            throw exception;
        }
        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Changes and saves a task's completion state, rolling back if saving fails.
     */
    private String updateCompletion(String argument, String commandWord, boolean isDone)
            throws GoatException, IOException {
        int index = tasks.parseIndex(argument, commandWord);
        Task task = tasks.get(index);
        boolean wasDone = task.isDone;
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        return ui.formatCompletionChanged(task, isDone);
    }

    /**
     * Deletes and saves a task, restoring it if saving fails.
     */
    private String deleteTask(String argument, String commandWord)
            throws GoatException, IOException {
        int index = tasks.parseIndex(argument, commandWord);
        Task deletedTask = tasks.delete(index);
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            tasks.add(index, deletedTask);
            throw exception;
        }
        return ui.formatTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Saves tasks in chronological order, then adopts that order in memory.
     */
    private String sortTasks() throws IOException {
        TaskList sortedTasks = tasks.sortedChronologically();
        storage.save(sortedTasks.asList());
        tasks.replaceWith(sortedTasks);
        return ui.formatSortedTasks(tasks);
    }

    /**
     * Starts Goat using its default relative data-file path.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Goat().run();
    }
}
