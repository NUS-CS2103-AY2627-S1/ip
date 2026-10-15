package ev;

import java.nio.file.Path;
import java.nio.file.Paths;

import ev.command.Command;
import ev.parser.Parser;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.ConsoleUi;
import ev.ui.Ui;

/**
 * The EV chatbot, independent of how the user reaches it.
 *
 * <p>EV keeps a list of tasks that survives between runs. A caller feeds it one line at
 * a time through {@link #getResponse(String)} and shows whatever comes back, which is
 * what lets the same chatbot serve both the terminal and the window.
 */
public class EV {

    /** Where the task list is saved, relative to the folder the app is run from. */
    public static final Path DATA_FILE = Paths.get("data", "ev.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit = false;
    private boolean isErrorResponse = false;

    /**
     * Creates a chatbot that replies without printing anything itself.
     *
     * @param dataFile the save file. It does not have to exist yet.
     */
    public EV(Path dataFile) {
        this(dataFile, new Ui());
    }

    /**
     * Creates a chatbot that words its replies through the given user interface.
     *
     * @param dataFile the save file. It does not have to exist yet.
     * @param ui decides how replies are worded, and whether they are also printed.
     */
    public EV(Path dataFile, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(dataFile);
        this.tasks = new TaskList();
    }

    /**
     * Runs the chatbot in the terminal until the user says bye or the input runs out.
     *
     * @param args command line arguments, which are not used.
     */
    public static void main(String[] args) {
        ConsoleUi consoleUi = new ConsoleUi();
        EV ev = new EV(DATA_FILE, consoleUi);

        consoleUi.showBanner();
        ev.start();

        while (!ev.isExit() && consoleUi.hasNextCommand()) {
            String line = consoleUi.readCommand();
            if (line.isEmpty()) {
                continue;
            }
            ev.getResponse(line);
        }

        consoleUi.showFarewell();
    }

    /**
     * Greets the user and loads the saved tasks.
     *
     * @return the greeting, followed by a warning if part of the save file was unreadable.
     */
    public String start() {
        ui.showWelcome();
        loadTasks();
        return ui.takeResponse();
    }

    /**
     * Carries out one line of input.
     *
     * @param input one command as the user typed it.
     * @return what EV has to say about it, including any error message.
     */
    public String getResponse(String input) {
        assert tasks != null : "The task list is set in the constructor and only ever replaced by a load";

        isErrorResponse = false;
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (EvException e) {
            ui.showError(e.getMessage());
            isErrorResponse = true;
        }
        return ui.takeResponse();
    }

    /**
     * Returns whether the last command asked EV to stop.
     *
     * @return true once the user has said bye.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the last reply was EV refusing to do something.
     *
     * <p>A front end can use this to show a refusal differently from an answer. The text
     * itself already explains the problem, so this only says which kind of reply it is.
     *
     * @return true if the last call to {@link #getResponse(String)} ended in an error.
     */
    public boolean isErrorResponse() {
        return isErrorResponse;
    }

    /**
     * Returns EV's parting words.
     *
     * @return the farewell message.
     */
    public String getFarewell() {
        ui.showFarewell();
        return ui.takeResponse();
    }

    /**
     * Loads the saved tasks, starting with an empty list if the file cannot be read.
     * Lines that were skipped because they are not in the expected format are reported.
     */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (EvException e) {
            ui.showError(e.getMessage());
            return;
        }
        int skipped = storage.getSkippedLineCount();
        if (skipped > 0) {
            ui.showSkippedLines(skipped, storage.getFile());
        }
    }
}
