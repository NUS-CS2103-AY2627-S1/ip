package crack;

import crack.task.Task;
import crack.task.TaskDate;

/**
 * Entry point of the Crack chatbot.
 * Wires up the user interface, the save file and the task list, then runs the command loop.
 */
public class Crack {
    private static final String SAVE_PATH = "./data/data.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Wires up the chatbot against the given save file and loads whatever is in it.
     *
     * @param filePath Where the save file lives.
     */
    public Crack(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Wires up the chatbot against the given save file, talking through the given interface.
     *
     * @param filePath Where the save file lives.
     * @param ui How the chatbot talks to the user, console or window.
     */
    public Crack(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        this.tasks = loadTasks();
    }

    /**
     * Starts the chatbot.
     *
     * @param args Command line arguments, which Crack ignores.
     */
    public static void main(String[] args) {
        new Crack(SAVE_PATH).run();
    }

    /**
     * Greets the user, then handles commands until they say bye.
     */
    public void run() {
        ui.showWelcome();
        boolean isRunning = true;
        while (isRunning && ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.isEmpty()) {
                continue;
            }
            ui.showLine();
            isRunning = accept(input);
            ui.showLine();
        }
    }

    /**
     * Runs one line of input, reporting anything that went wrong through the interface.
     * The window uses this the same way the command loop does.
     *
     * @param input The whole line the user typed.
     * @return False once the user has said bye, true otherwise.
     */
    public boolean accept(String input) {
        try {
            return handle(input);
        } catch (CrackException e) {
            ui.showError(e.getMessage());
            return true;
        }
    }

    /**
     * Runs one command.
     *
     * @param input The whole line the user typed.
     * @return False once the user has said bye, true otherwise.
     * @throws CrackException If the command cannot be understood or carried out.
     */
    private boolean handle(String input) throws CrackException {
        Parser.Parsed parsed = Parser.parse(input);
        String arguments = parsed.arguments();

        switch (parsed.command()) {
            case BYE -> {
                ui.showGoodbye();
                return false;
            }
            case LIST -> ui.showList(tasks);
            case MARK -> markTask(Parser.parseIndex(arguments, tasks.size()));
            case UNMARK -> unmarkTask(Parser.parseIndex(arguments, tasks.size()));
            case DELETE -> deleteTask(Parser.parseIndex(arguments, tasks.size()));
            case TODO -> addTask(Parser.parseTodo(arguments));
            case DEADLINE -> addTask(Parser.parseDeadline(arguments));
            case EVENT -> addTask(Parser.parseEvent(arguments));
            case ON -> listOn(Parser.parseDate(arguments));
            case FIND -> ui.showFound(tasks.find(Parser.parseKeyword(arguments)));
            case HELP -> ui.showHelp();
            // Parser only ever hands back a command listed above.
            default -> { }
        }
        return true;
    }

    /**
     * Loads the saved tasks, falling back to an empty list if the file will not read.
     *
     * @return The tasks to start the session with.
     */
    private TaskList loadTasks() {
        try {
            TaskList loaded = storage.load();
            assert loaded != null : "Storage.load hands back a list, empty at worst, never null";
            return loaded;
        } catch (CrackException e) {
            ui.showError(e.getMessage());
            return new TaskList();
        }
    }

    /**
     * Writes the list out, grumbling to the user if it cannot.
     */
    private void save() {
        try {
            storage.save(tasks);
        } catch (CrackException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Adds a task and confirms it.
     *
     * @param task The task to add.
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        save();
    }

    /**
     * Drops a task off the list and confirms it.
     *
     * @param index Zero based position of the task to remove.
     */
    private void deleteTask(int index) {
        assert index >= 0 && index < tasks.size() : "Parser.parseIndex should have rejected index " + index;
        Task task = tasks.remove(index);
        ui.showRemoved(task, tasks.size());
        save();
    }

    /**
     * Marks a task done and confirms it.
     *
     * @param index Zero based position of the task to mark.
     */
    private void markTask(int index) {
        assert index >= 0 && index < tasks.size() : "Parser.parseIndex should have rejected index " + index;
        Task task = tasks.get(index);
        task.markAsDone();
        ui.showMarked(task);
        save();
    }

    /**
     * Marks a task not done and confirms it.
     *
     * @param index Zero based position of the task to unmark.
     */
    private void unmarkTask(int index) {
        assert index >= 0 && index < tasks.size() : "Parser.parseIndex should have rejected index " + index;
        Task task = tasks.get(index);
        task.markAsNotDone();
        ui.showUnmarked(task);
        save();
    }

    /**
     * Prints everything landing on one day.
     *
     * @param date The day the user asked about.
     */
    private void listOn(TaskDate date) {
        ui.showTasksOn(date.formatDay(), tasks.onDate(date.toLocalDate()));
    }
}
