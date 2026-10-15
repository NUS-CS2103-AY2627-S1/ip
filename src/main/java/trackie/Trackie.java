package trackie;

import trackie.exception.TrackieException;
import trackie.parser.Parser;
import trackie.storage.Storage;
import trackie.task.Task;
import trackie.task.TaskList;
import trackie.ui.Ui;

/** Coordinates Trackie's user interface, task list, parser, and storage. */
public class Trackie {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Trackie and loads tasks from the configured data file.
     *
     * @param filePath relative path of the task data file
     */
    public Trackie(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates Trackie with a specific user interface and loads saved tasks.
     *
     * @param filePath relative path of the task data file
     * @param ui user interface used for input and output
     */
    public Trackie(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        this.ui.showWelcome();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(this.storage.loadTasks());
        } catch (TrackieException exception) {
            this.ui.showError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
    }

    /** Reads and executes commands until the user exits or input ends. */
    public void run() {
        try (this.ui) {
            while (this.ui.hasNextCommand()) {
                String command = this.ui.readCommand();
                if (!processCommand(command)) {
                    return;
                }
            }
        }
    }

    /**
     * Starts Trackie using the default relative data file.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Trackie("data/trackie.txt").run();
    }

    /**
     * Processes one command and reports its result through the configured UI.
     *
     * @param command command to process
     * @return false if Trackie should exit, or true otherwise
     */
    public boolean processCommand(String command) {
        try {
            return execute(command);
        } catch (TrackieException exception) {
            this.ui.showError(exception.getMessage());
            return true;
        }
    }

    private boolean execute(String command) throws TrackieException {
        String normalizedCommand = command.trim();
        String commandWord = Parser.getCommandWord(normalizedCommand);
        switch (commandWord) {
            case "bye":
                requireExactCommand(normalizedCommand, "bye");
                this.ui.showGoodbye();
                return false;
            case "list":
                requireExactCommand(normalizedCommand, "list");
                this.ui.showTaskList(this.tasks);
                break;
            case "mark":
                updateTaskStatus(normalizedCommand, true);
                break;
            case "unmark":
                updateTaskStatus(normalizedCommand, false);
                break;
            case "delete":
                deleteTask(normalizedCommand);
                break;
            case "todo":
                addTask(Parser.parseTodo(normalizedCommand));
                break;
            case "deadline":
                addTask(Parser.parseDeadline(normalizedCommand));
                break;
            case "event":
                addTask(Parser.parseEvent(normalizedCommand));
                break;
            case "within":
                addTask(Parser.parseWithinPeriod(normalizedCommand));
                break;
            case "find":
                this.ui.showMatchingTasks(this.tasks.find(Parser.parseFindKeyword(normalizedCommand)));
                break;
            default:
                throw unknownCommandException();
        }
        return true;
    }

    private void updateTaskStatus(String command, boolean isDone) throws TrackieException {
        String commandName = isDone ? "mark" : "unmark";
        int taskIndex = Parser.parseTaskIndex(command, commandName, this.tasks.size());
        Task task = this.tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
            this.storage.saveTasks(this.tasks.asList());
            this.ui.showMarked(task);
        } else {
            task.markAsNotDone();
            this.storage.saveTasks(this.tasks.asList());
            this.ui.showUnmarked(task);
        }
    }

    private void deleteTask(String command) throws TrackieException {
        int taskIndex = Parser.parseTaskIndex(command, "delete", this.tasks.size());
        Task removedTask = this.tasks.remove(taskIndex);
        this.storage.saveTasks(this.tasks.asList());
        this.ui.showDeleted(removedTask, this.tasks.size());
    }

    private void addTask(Task task) throws TrackieException {
        this.tasks.add(task);
        this.storage.saveTasks(this.tasks.asList());
        this.ui.showAdded(task, this.tasks.size());
    }

    private void requireExactCommand(String command, String expectedCommand) throws TrackieException {
        if (!command.equals(expectedCommand)) {
            throw unknownCommandException();
        }
    }

    private TrackieException unknownCommandException() {
        return new TrackieException("Oops! I don't recognize that command.");
    }
}
