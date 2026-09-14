package nova;

import nova.command.Command;

/**
 * A chatbot that keeps track of todos, deadlines and events.
 *
 * <p>This class wires the pieces together and turns a line of input into a
 * reply. The work itself is split across {@link Ui} (wording the replies),
 * {@link Storage} (the save file), {@link TaskList} (the tasks themselves),
 * {@link Parser} (reading input) and the {@link Command} classes.
 *
 * <p>Replies are returned as text rather than printed, so the same chatbot
 * backs both the console front end in {@link Cli} and the GUI.
 */
public class Nova {
    /** Save file location, relative to the project root. */
    private static final String DATA_FILE = "data/nova.txt";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit = false;

    /**
     * Creates a chatbot that persists its tasks to the given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Nova(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
    }

    /** Creates a chatbot using the default save file. */
    public Nova() {
        this(DATA_FILE);
    }

    /**
     * Returns the greeting shown before the user has typed anything.
     *
     * @return the welcome message
     */
    public Reply getGreeting() {
        ui.showWelcome();
        return Reply.of(ui.flush());
    }

    /**
     * Loads the saved tasks and returns anything the user should know about
     * how that went.
     *
     * <p>A file that could not be read at all leaves an empty list; lines that
     * could not be understood are reported but do not stop the rest loading.
     * Loading is a separate step from the greeting so that a warning appears
     * after it rather than before.
     *
     * @return the warning to show, marked as an error; empty if the load was clean
     */
    public Reply loadTasks() {
        boolean hasProblem = false;
        try {
            tasks = new TaskList(storage.load());
            if (storage.getSkippedLineCount() > 0) {
                ui.showLoadingError(storage.getSkippedLineCount());
                hasProblem = true;
            }
        } catch (NovaException e) {
            tasks = new TaskList();
            ui.showError(e.getMessage());
            hasProblem = true;
        }
        return new Reply(ui.flush(), hasProblem);
    }

    /**
     * Carries out one line of input and returns what the chatbot says back.
     *
     * @param input the line the user typed
     * @return the reply to show them, marked as an error if the command failed
     */
    public Reply getResponse(String input) {
        assert input != null : "input should never be null";
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (NovaException e) {
            ui.showError(e.getMessage());
            return Reply.error(ui.flush());
        }
        return Reply.of(ui.flush());
    }

    /**
     * Returns whether the user has asked to stop.
     *
     * @return true once a command has ended the session
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts the chatbot as a console app.
     *
     * @param args command line arguments; none are used
     */
    public static void main(String[] args) {
        new Cli(new Nova(DATA_FILE)).run();
    }
}
