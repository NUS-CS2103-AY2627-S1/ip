package suki;

/**
 * A personal assistant chatbot that keeps track of tasks.
 *
 * <p>This class only wires the pieces together and runs the main loop. The
 * actual work is delegated: {@link Ui} talks to the user, {@link Parser}
 * interprets what they typed, {@link TaskList} holds the tasks, and
 * {@link Storage} keeps them on disk.
 */
public class Suki {
    /** Where tasks are saved, relative to the working directory. */
    private static final String SAVE_FILE_PATH = "data/suki.txt";
    /** The word that ends the session. */
    private static final String BYE_COMMAND = "bye";

    /** Handles all reading from and printing to the console. */
    private final Ui ui;

    /** Keeps the task list on disk between runs. */
    private final Storage storage;

    /** The tasks currently being tracked. */
    private TaskList tasks;

    /**
     * Creates a Suki that saves to the given file, loading any tasks already
     * stored there.
     *
     * @param filePath where tasks are saved
     */
    public Suki(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (SukiException e) {
            // A broken save file should not stop the user from working, so we
            // warn and start from an empty list instead of exiting.
            ui.showError(e.getMessage());
            ui.showLine();
            this.tasks = new TaskList();
        }
    }

    /**
     * Creates a Suki that saves to the default file.
     *
     * <p>Convenience constructor for the GUI, which has no reason to choose a
     * different path.
     */
    public Suki() {
        this(SAVE_FILE_PATH);
    }

    /**
     * Returns the greeting the GUI shows when it opens.
     *
     * @return the greeting text
     */
    public String getGreeting() {
        ui.takeOutput();
        ui.showGreeting();
        return ui.takeOutput();
    }

    /**
     * Returns Suki's reply to a single line of input, for the GUI.
     *
     * <p>This is the GUI's counterpart to {@link #run()}: instead of looping
     * over the console it handles one line and hands back the text to display.
     * The command itself is carried out by the same {@link #execute} method the
     * console uses, so the two front ends cannot drift apart in behaviour; only
     * the delivery differs. Errors come back as their message text rather than
     * as thrown exceptions, since there is nowhere to propagate them to.
     *
     * @param input one line of user input, as {@code run} would have read it
     * @return the text to show the user
     */
    public String getResponse(String input) {
        // Drop anything left over from an earlier reply so this one stands alone.
        ui.takeOutput();

        if (isExitCommand(input)) {
            ui.showFarewell();
            return ui.takeOutput();
        }

        executeAndSave(input);
        return ui.takeOutput();
    }

    /** Reads and executes commands until the user says goodbye or input ends. */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (isExitCommand(input)) {
                break;
            }

            ui.showLine();
            executeAndSave(input);
            ui.showLine();
        }

        ui.close();
        ui.showGoodbye();
    }

    /**
     * Returns whether the input asks Suki to end the session.
     *
     * @param input one line entered by the user
     * @return true only for the {@code bye} command, ignoring surrounding spaces
     */
    public static boolean isExitCommand(String input) {
        return input.trim().equals(BYE_COMMAND);
    }

    /**
     * Runs one line of input and saves the result, reporting any problem to
     * the user.
     *
     * <p>Both front ends need exactly this sequence, so it lives here rather
     * than being written out twice: a change to how errors are reported, or to
     * when the file is saved, then cannot apply to only one of them.
     *
     * @param input one line of user input
     */
    private void executeAndSave(String input) {
        try {
            execute(Parser.parse(input));
            storage.save(tasks);
        } catch (SukiException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Carries out a single parsed command.
     *
     * @param parsed the command to run, as returned by {@link Parser#parse}
     * @throws SukiException if the command is unknown or its arguments are invalid
     */
    private void execute(Parser.ParsedInput parsed) throws SukiException {
        assert parsed != null : "the parser always returns a result, never null";
        assert tasks != null : "the constructor leaves a task list behind even when loading fails";
        String arguments = parsed.arguments();

        switch (parsed.commandType()) {
        case LIST:
            Parser.requireNoArguments(arguments, parsed.commandWord());
            ui.showTaskList(tasks);
            break;
        case MARK: {
            Task task = tasks.get(Parser.parseIndex(arguments, parsed.commandWord()));
            task.markAsDone();
            ui.showTaskMarked(task);
            break;
        }
        case UNMARK: {
            Task task = tasks.get(Parser.parseIndex(arguments, parsed.commandWord()));
            task.markAsNotDone();
            ui.showTaskUnmarked(task);
            break;
        }
        case DELETE: {
            Task removed = tasks.remove(Parser.parseIndex(arguments, parsed.commandWord()));
            ui.showTaskRemoved(removed, tasks.size());
            break;
        }
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
            ui.showFoundTasks(tasks.find(Parser.parseFindKeyword(arguments)));
            break;
        case SORT:
            Parser.requireNoArguments(arguments, parsed.commandWord());
            tasks.sortByScheduledDate();
            ui.showTasksSorted(tasks);
            break;
        default:
            throw new SukiException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Adds a task to the list and tells the user about it.
     *
     * @param task the task to add
     */
    private void addTask(Task task) {
        assert task != null : "the parser either builds a task or throws";
        tasks.add(task);
        assert !tasks.isEmpty() : "the list holds at least the task just added";
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Starts Suki.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        new Suki(SAVE_FILE_PATH).run();
    }
}
