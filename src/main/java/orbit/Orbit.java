package orbit;

import java.nio.file.Paths;
import java.util.List;

import orbit.exception.OrbitException;
import orbit.parser.ParsedCommand;
import orbit.parser.Parser;
import orbit.storage.Storage;
import orbit.task.Task;
import orbit.task.TaskList;
import orbit.ui.Ui;

/**
 * Coordinates Orbit's parser, task list, storage, and response output.
 */
public class Orbit {
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private final TaskList tasks;

    /**
     * Creates an Orbit application from its collaborating components.
     *
     * @param ui console user interface
     * @param storage persistent task storage
     * @param parser command parser
     * @param tasks current task list
     */
    public Orbit(Ui ui, Storage storage, Parser parser, TaskList tasks) {
        this.ui = ui;
        this.storage = storage;
        this.parser = parser;
        this.tasks = tasks;
    }

    /**
     * Processes commands until the user exits or closes the input stream.
     */
    public void run() {
        boolean shouldContinue = true;
        while (shouldContinue && ui.hasNextCommand()) {
            shouldContinue = processCommand(ui.readCommand());
            ui.showSeparator();
        }
    }

    /**
     * Processes one command for either the console or graphical interface.
     *
     * @param input complete user command
     * @return false only when a valid exit command was processed
     */
    public boolean processCommand(String input) {
        try {
            return execute(parser.parse(input));
        } catch (OrbitException exception) {
            ui.showError(exception.getMessage());
            return true;
        }
    }

    private boolean execute(ParsedCommand command) throws OrbitException {
        switch (command.getType()) {
            case BYE:
                ui.showGoodbye();
                return false;
            case LIST:
                ui.showTaskList(tasks.asList());
                return true;
            case SORT:
                sortTasks();
                return true;
            case FIND:
                ui.showMatchingTasks(tasks.find(command.getKeyword()));
                return true;
            case MARK:
                markTask(command.getTaskNumber());
                return true;
            case UNMARK:
                unmarkTask(command.getTaskNumber());
                return true;
            case DELETE:
                deleteTask(command.getTaskNumber());
                return true;
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                addTask(command.getTask());
                return true;
            default:
                throw new OrbitException("I don't know that command.");
        }
    }

    private void addTask(Task task) throws OrbitException {
        tasks.add(task);
        try {
            saveTasks();
        } catch (OrbitException exception) {
            tasks.delete(tasks.size());
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }

    private void markTask(int taskNumber) throws OrbitException {
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks();
        } catch (OrbitException exception) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw exception;
        }
        ui.showMarkedTask(task);
    }

    private void unmarkTask(int taskNumber) throws OrbitException {
        Task task = tasks.get(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            saveTasks();
        } catch (OrbitException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        ui.showUnmarkedTask(task);
    }

    private void deleteTask(int taskNumber) throws OrbitException {
        Task removedTask = tasks.delete(taskNumber);
        try {
            saveTasks();
        } catch (OrbitException exception) {
            tasks.restore(taskNumber, removedTask);
            throw exception;
        }
        ui.showDeletedTask(removedTask, tasks.size());
    }

    private void sortTasks() throws OrbitException {
        List<Task> previousOrder = List.copyOf(tasks.asList());
        tasks.sortChronologically();
        try {
            saveTasks();
        } catch (OrbitException exception) {
            tasks.restoreOrder(previousOrder);
            throw exception;
        }
        ui.showSortedTasks(tasks.asList());
    }

    private void saveTasks() throws OrbitException {
        storage.save(tasks.asList());
    }

    /**
     * Starts Orbit with its default relative data file.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Paths.get("data", "orbit.txt"));
        TaskList tasks;

        ui.showWelcome();
        try {
            tasks = new TaskList(storage.load());
        } catch (OrbitException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }

        new Orbit(ui, storage, new Parser(), tasks).run();
    }
}
