package johnny;

import java.io.IOException;

import johnny.command.Command;
import johnny.command.Parser;
import johnny.storage.Storage;
import johnny.task.Task;
import johnny.task.TaskList;
import johnny.ui.Ui;

/**
 * Coordinates the UI, storage, task list, and parser for the Johnny chatbot.
 */
public class Johnny {

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    /** Restores the state before the latest successful task-changing command. */
    private Runnable undoAction;

    /**
     * Creates a Johnny instance, loading saved tasks from the given file path.
     * If the file cannot be read, starts with an empty task list.
     *
     * @param filePath path to the task data file (e.g., "./data/johnny.txt").
     */
    public Johnny(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates a Johnny instance that sends messages through the supplied UI.
     *
     * @param filePath path to the task data file.
     * @param ui user interface used for input-independent output.
     */
    public Johnny(String filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
        assert tasks != null : "Task list must be initialized";
    }

    private void saveTasks() {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSaveError();
        }
    }

    private Task getTask(String arguments) throws JohnnyException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
        return tasks.get(taskIndex);
    }

    private void markTask(String arguments) throws JohnnyException {
        Task task = getTask(arguments);
        boolean wasDone = task.isDone();
        task.markAsDone();
        undoAction = wasDone ? task::markAsDone : task::markAsNotDone;
        ui.showTaskMarked(task);
        saveTasks();
    }

    private void unmarkTask(String arguments) throws JohnnyException {
        Task task = getTask(arguments);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        undoAction = wasDone ? task::markAsDone : task::markAsNotDone;
        ui.showTaskUnmarked(task);
        saveTasks();
    }

    private void deleteTask(String arguments) throws JohnnyException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        undoAction = () -> tasks.add(taskIndex, deletedTask);
        ui.showTaskDeleted(deletedTask, tasks.size());
        saveTasks();
    }

    private void addTask(Task task) {
        int taskIndex = tasks.size();
        tasks.add(task);
        undoAction = () -> tasks.delete(taskIndex);
        ui.showTaskAdded(task, tasks.size());
        saveTasks();
    }

    private void undoLastCommand() throws JohnnyException {
        if (undoAction == null) {
            throw new JohnnyException("There is no command to undo.");
        }
        undoAction.run();
        undoAction = null;
        ui.showCommandUndone();
        saveTasks();
    }

    private void findTasks(String arguments) throws JohnnyException {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new JohnnyException("Please provide a keyword to search for.");
        }
        ui.showFindResults(tasks.find(keyword));
    }

    /** Rejects arguments supplied to a command that has no parameters. */
    private void rejectUnexpectedArguments(String arguments, String commandName)
            throws JohnnyException {
        if (!arguments.isEmpty()) {
            throw new JohnnyException("The " + commandName + " command does not accept parameters.");
        }
    }

    /**
     * Processes one command and displays the resulting response.
     *
     * @param input command entered by the user.
     * @return true if the command requests that the application exit.
     */
    public boolean processCommand(String input) {
        try {
            Command command = Parser.parseCommand(input);
            String arguments = Parser.parseArguments(input);
            assert command != null && arguments != null : "Parser results must not be null";

            switch (command) {
                case BYE:
                    rejectUnexpectedArguments(arguments, "bye");
                    ui.showFarewell();
                    return true;
                case LIST:
                    rejectUnexpectedArguments(arguments, "list");
                    ui.showTaskList(tasks);
                    break;
                case MARK:
                    markTask(arguments);
                    break;
                case UNMARK:
                    unmarkTask(arguments);
                    break;
                case DELETE:
                    deleteTask(arguments);
                    break;
                case TODO:
                    addTask(Parser.parseTodo(arguments));
                    break;
                case DEADLINE:
                    addTask(Parser.parseDeadline(arguments));
                    break;
                case EVENT:
                    addTask(Parser.parseEvent(arguments));
                    break;
                case FIND:
                    findTasks(arguments);
                    break;
                case UNDO:
                    rejectUnexpectedArguments(arguments, "undo");
                    undoLastCommand();
                    break;
                default:
                    throw new JohnnyException("I couldn't identify that command.");
            }
        } catch (JohnnyException e) {
            ui.showError(e.getMessage());
        }
        return false;
    }

    /**
     * Runs the console command loop until the user says bye or input ends.
     */
    public void run() {
        ui.showGreeting();

        while (ui.hasNextLine()) {
            String input = ui.readCommand();
            ui.showLine();
            boolean shouldExit = processCommand(input);
            ui.showLine();
            if (shouldExit) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Starts the console version of Johnny.
     *
     * @param args command-line arguments, which are unused.
     */
    public static void main(String[] args) {
        new Johnny("./data/johnny.txt").run();
    }
}
