package tblade;

import java.util.ArrayList;
import java.util.List;

import tblade.exception.TBladeException;
import tblade.parser.Parser;
import tblade.storage.Storage;
import tblade.task.Deadline;
import tblade.task.Event;
import tblade.task.Task;
import tblade.task.TaskList;
import tblade.task.Todo;
import tblade.ui.Ui;

/**
 * Runs the TBlade application, coordinating the Ui, Storage, and TaskList. Supports both the
 * console UI ({@link #run()}) and a GUI ({@link #getResponse(String)}) built on the same command
 * logic, so both surfaces produce identical responses.
 */
public class TBlade {
    /**
     * The data file path used when the application isn't given another one, shared by the
     * console entry point ({@link #main}) and the GUI entry point ({@code tblade.gui.Main}) so
     * both surfaces persist to the same file.
     */
    public static final String DEFAULT_DATA_FILE_PATH = "data/duke.txt";

    /**
     * The text response to a command, and whether the application should keep running afterward.
     */
    private record CommandResult(String message, boolean isRunning) {
    }

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private boolean isRunning = true;

    /**
     * Creates a TBlade that persists its tasks to the given file path, loading any tasks
     * already saved there.
     *
     * @param filePath path of the file used to store tasks
     */
    public TBlade(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        List<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (TBladeException exception) {
            ui.showMessage(ui.formatError(exception.getMessage()));
            loadedTasks = new ArrayList<>();
        }
        tasks = new TaskList(loadedTasks);
    }

    /**
     * Greets the user, then repeatedly reads and executes commands until {@code bye} is entered.
     */
    public void run() {
        ui.showWelcome();
        while (isRunning) {
            String command = ui.readCommand();
            ui.showLine();
            ui.showMessage(processCommand(command));
            ui.showLine();
        }
    }

    /**
     * Processes one line of user input and returns TBlade's reply, for use by a GUI.
     *
     * @param input full command entered by the user
     * @return the text TBlade responds with
     */
    public String getResponse(String input) {
        return processCommand(input);
    }

    /**
     * Returns whether the application should keep accepting commands.
     *
     * @return {@code false} once {@code bye} has been processed, {@code true} otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Interprets and carries out a single command, updating {@link #isRunning}. Leading/trailing
     * whitespace is stripped first so an accidental stray space before the command word (e.g.
     * " todo read book") is still recognized, rather than falling through to "unknown command".
     *
     * @param command full command entered by the user
     * @return the text response to the command, including any error message
     */
    private String processCommand(String command) {
        try {
            CommandResult result = executeCommand(command.trim());
            isRunning = result.isRunning();
            return result.message();
        } catch (TBladeException exception) {
            return ui.formatError(exception.getMessage());
        }
    }

    /**
     * Interprets and carries out a single command.
     *
     * @param command full command entered by the user
     * @return the response text and whether the application should keep running
     * @throws TBladeException if the command is invalid
     */
    private CommandResult executeCommand(String command) throws TBladeException {
        if (command.isBlank()) {
            throw new TBladeException("Please enter a command. "
                    + "Use: todo, deadline, event, list, find, mark, unmark, delete, or bye.");
        } else if (command.equals("bye")) {
            return new CommandResult(ui.formatGoodbye(), false);
        } else if (command.startsWith("bye ")) {
            throw new TBladeException("The bye command does not take extra text. Use: bye");
        } else if (command.equals("list")) {
            return new CommandResult(ui.formatTaskList(tasks), true);
        } else if (command.startsWith("list ")) {
            throw new TBladeException("The list command does not take extra text. Use: list");
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            int taskIndex = Parser.parseTaskIndex(command, "mark", tasks.size());
            Task task = tasks.get(taskIndex);
            task.markAsDone();
            storage.save(tasks.getAll());
            return new CommandResult(ui.formatMarked(task), true);
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            int taskIndex = Parser.parseTaskIndex(command, "unmark", tasks.size());
            Task task = tasks.get(taskIndex);
            task.unmarkAsDone();
            storage.save(tasks.getAll());
            return new CommandResult(ui.formatUnmarked(task), true);
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
            Task removedTask = tasks.remove(taskIndex);
            storage.save(tasks.getAll());
            return new CommandResult(ui.formatDeleted(removedTask, tasks.size()), true);
        } else if (command.equals("find") || command.startsWith("find ")) {
            String keyword = Parser.getFindKeyword(command);
            return new CommandResult(ui.formatMatchingTasks(tasks.find(keyword)), true);
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            String description = Parser.getTodoDescription(command);
            return addTask(new Todo(description));
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            Parser.DeadlineArgs args = Parser.parseDeadlineArgs(command);
            return addTask(new Deadline(args.description(), args.by()));
        } else if (command.equals("event") || command.startsWith("event ")) {
            Parser.EventArgs args = Parser.parseEventArgs(command);
            return addTask(new Event(args.description(), args.from(), args.to()));
        } else {
            throw new TBladeException("I don't know that command. "
                    + "Use: todo, deadline, event, list, find, mark, unmark, delete, or bye.");
        }
    }

    /**
     * Adds a task to the list, persists the updated list, and builds the confirmation response.
     *
     * @param task the task to add
     * @return the response text and whether the application should keep running
     * @throws TBladeException if the task list is already at capacity or storage cannot be written
     */
    private CommandResult addTask(Task task) throws TBladeException {
        tasks.add(task);
        storage.save(tasks.getAll());
        return new CommandResult(ui.formatAddedTask(task, tasks.size()), true);
    }

    /**
     * Starts the TBlade console application, persisting tasks to {@link #DEFAULT_DATA_FILE_PATH}.
     *
     * @param args command-line arguments, which are not used by this application
     */
    public static void main(String[] args) {
        new TBlade(DEFAULT_DATA_FILE_PATH).run();
    }
}
