package pdd;

import pdd.command.Command;
import pdd.parser.Parser;
import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/**
 * Entry point for the PDD chatbot. Wires together the {@link Storage},
 * {@link TaskList} and {@link Ui} collaborators and runs the main
 * read-command / parse / execute loop.
 *
 * <p>{@link #getResponse(String)} offers the same command handling as a
 * single call instead of a loop, for the JavaFX GUI ({@code pdd.gui}) to
 * drive one command at a time.
 */
public class PDD {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExit = false;

    /** Creates the chatbot, loading any previously saved tasks from the given save file. */
    public PDD(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /** Runs the chatbot's main loop until the user issues an exit command. */
    public void run() {
        ui.showWelcome();
        boolean isRunning = true;
        while (isRunning) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isRunning = !c.isExit();
            } catch (PDDException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Parses and executes one line of input, returning the text a UI should show for it.
     * Used by the GUI, which drives one command per call instead of looping over
     * {@link #run()}. After this returns, {@link #isExit()} reports whether that command
     * was an exit command (e.g. {@code bye}), so the GUI knows to close its window.
     *
     * @param input one full command line, exactly as a user would type it.
     * @return the response text this command produces.
     */
    public String getResponse(String input) {
        StringBuilder response = new StringBuilder();
        Ui guiUi = new Ui(line -> response.append(line).append('\n'));
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, guiUi, storage);
            isExit = c.isExit();
        } catch (PDDException e) {
            guiUi.showError(e.getMessage());
        }
        return response.toString().strip();
    }

    /** Returns the greeting shown when the GUI opens. */
    public String getGreeting() {
        return "Hello! I'm PDD.\nWhat can I do for you?";
    }

    /** Whether the most recent {@link #getResponse(String)} call was an exit command. */
    public boolean isExit() {
        return isExit;
    }

    /** Starts the chatbot, saving/loading tasks at {@code ./data/pdd.txt}. */
    public static void main(String[] args) {
        new PDD("./data/pdd.txt").run();
    }
}
