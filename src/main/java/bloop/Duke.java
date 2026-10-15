package bloop;

import java.nio.file.Path;
import java.util.Scanner;

import bloop.exception.DukeException;
import bloop.parser.CommandType;
import bloop.parser.Parser;
import bloop.storage.Storage;
import bloop.task.Task;
import bloop.task.TaskList;
import bloop.ui.Ui;

/** Coordinates command handling, task storage, and user interaction for Bloop. */
public class Duke {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private DukeException loadingError;

    /** Creates Bloop and restores its saved task list when possible. */
    public Duke(String filePath) {
        this(filePath, new Ui());
    }

    /** Creates Bloop with the supplied interface and restores saved tasks. */
    public Duke(String filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(Path.of(filePath));
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (DukeException exception) {
            loadingError = exception;
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Runs Bloop's command loop. */
    public void run() {
        if (!startConversation()) {
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                ui.showDivider();
                if (processCommand(scanner.nextLine())) {
                    ui.showDivider();
                    return;
                }
                ui.showDivider();
            }
        }
    }

    /** Shows the greeting and reports whether commands can be processed. */
    public boolean startConversation() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
            ui.showDivider();
            return false;
        }
        return true;
    }

    /** Processes one command and returns whether the application should exit. */
    public boolean processCommand(String input) {
        try {
            return handleCommand(input);
        } catch (DukeException exception) {
            ui.showError(exception);
            return false;
        }
    }

    /** Returns the tasks for display by another user interface. */
    public TaskList getTasks() {
        return tasks;
    }

    /** Executes one user command. */
    private boolean handleCommand(String input) throws DukeException {
        CommandType commandType = Parser.parseCommandType(input);
        switch (commandType) {
            case BYE:
                ui.showGoodbye();
                return true;
            case LIST:
                ui.showTaskList(tasks);
                return false;
            case MARK:
                updateTaskStatus(input, commandType, true);
                return false;
            case UNMARK:
                updateTaskStatus(input, commandType, false);
                return false;
            case DELETE:
                deleteTask(input);
                return false;
            case TODO:
                addTask(Parser.parseTodo(input));
                return false;
            case DEADLINE:
                addTask(Parser.parseDeadline(input));
                return false;
            case EVENT:
                addTask(Parser.parseEvent(input));
                return false;
            case FIND:
                ui.showFoundTasks(tasks, Parser.parseFindKeyword(input));
                return false;
            case UNKNOWN:
            default:
                throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Changes a task status, saving the result and undoing it if saving fails. */
    private void updateTaskStatus(String input, CommandType commandType, boolean isDone) throws DukeException {
        Task task = tasks.get(Parser.parseTaskIndex(input, commandType, tasks.size()));
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
        try {
            storage.save(tasks);
        } catch (DukeException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.unmarkAsDone();
            }
            throw exception;
        }
        ui.showStatusUpdated(task, isDone);
    }

    /** Deletes a task, saving the result and restoring it if saving fails. */
    private void deleteTask(String input) throws DukeException {
        int taskIndex = Parser.parseTaskIndex(input, CommandType.DELETE, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (DukeException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /** Adds a task, saving the result and undoing the addition if saving fails. */
    private void addTask(Task task) throws DukeException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (DukeException exception) {
            // The task was appended immediately above, so removing the last entry restores the pre-command list.
            assert tasks.get(tasks.size() - 1) == task : "The newly added task must be last";
            tasks.removeLast();
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /** Starts the chatbot using the default data-file location. */
    public static void main(String[] args) {
        new Duke("data/duke.txt").run();
    }
}
