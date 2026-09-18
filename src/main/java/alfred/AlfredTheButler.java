package alfred;

import alfred.command.Command;
import alfred.command.ExitCommand;
import alfred.task.TaskList;

/**
 * Runs Alfred the Butler, a personal chatbot that keeps a list of tasks.
 * The console uses {@link #run()}; the window uses {@link #getGreeting()} and
 * {@link #getResponse(String)} one line at a time.
 */
public class AlfredTheButler {
    private static final String SAVE_FILE = "data/alfred.txt";

    private final Ui ui;

    private final Storage storage;

    private TaskList tasks;

    /** The class name of the last command carried out, so the window can color its reply. */
    private String commandType;

    private boolean isLastResponseError;

    private boolean isLastCommandExit;

    /**
     * Sets up a run that keeps its tasks in one named file. Nothing is read
     * until the greeting is shown.
     *
     * @param filePath where to keep the tasks, such as {@code data/alfred.txt}.
     */
    public AlfredTheButler(String filePath) {
        assert filePath != null && !filePath.isBlank() : "a run needs a save file";

        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /** Sets up a run that keeps its tasks in the usual file. */
    public AlfredTheButler() {
        this(SAVE_FILE);
    }

    /**
     * Greets the user, then handles one command per line until {@code bye} or
     * the end of the input, which is treated the same way.
     */
    public void run() {
        boolean isExit = false;

        ui.showWelcome();
        restoreTasks();
        while (!isExit) {
            try {
                Command command = ui.hasNextCommand()
                        ? Parser.parse(ui.readCommand())
                        : new ExitCommand();
                command.execute(tasks, ui, storage);
                // Skipped if the command threw, so a refused command cannot end the session.
                isExit = command.isExit();
            } catch (AlfredException e) {
                ui.showError(e.getMessage());
            } catch (RuntimeException e) {
                // A bug in one command costs that command rather than the session.
                ui.showInternalError(e);
            }
        }
    }

    /**
     * Returns Alfred's opening words, and reads back the tasks the last run
     * left behind.
     *
     * @return the greeting, and what came of reading the save file.
     */
    public String getGreeting() {
        ui.startCapturing();
        ui.showWelcome();
        restoreTasks();
        return ui.stopCapturing();
    }

    /**
     * Returns what Alfred says back to one line sent from the window. Nothing
     * ends here: the window asks {@link #isLastCommandExit()} and closes itself.
     *
     * @param input the line the user typed.
     * @return the reply, as the console would have printed it.
     */
    public String getResponse(String input) {
        ui.startCapturing();
        try {
            Command command = Parser.parse(input.trim());
            command.execute(tasks, ui, storage);
            commandType = command.getClass().getSimpleName();
            isLastResponseError = false;
            isLastCommandExit = command.isExit();
        } catch (AlfredException e) {
            commandType = null;
            isLastResponseError = true;
            isLastCommandExit = false;
            ui.showError(e.getMessage());
        } catch (RuntimeException e) {
            commandType = null;
            isLastResponseError = true;
            isLastCommandExit = false;
            ui.showInternalError(e);
        }
        return ui.stopCapturing();
    }

    /**
     * Returns whether the last reply from {@link #getResponse(String)} was an
     * error rather than an answer.
     *
     * @return true if the last reply was an error.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether the last line asked to end the session, so that the
     * window can close once its farewell has been read.
     *
     * @return true if the last command was {@code bye}.
     */
    public boolean isLastCommandExit() {
        return isLastCommandExit;
    }

    /**
     * Returns the class name of the command last carried out.
     *
     * @return the class name, or null if the last line was refused.
     */
    public String getCommandType() {
        return commandType;
    }

    /** Reads back the tasks the last run left behind, and says what came of it. */
    private void restoreTasks() {
        try {
            Storage.LoadResult loadResult = storage.load();
            tasks = new TaskList(loadResult.tasks());
            if (!tasks.isEmpty()) {
                ui.showLoaded(tasks.size());
            }
            if (loadResult.skippedLines() > 0) {
                ui.showSkippedLines(loadResult.skippedLines(), loadResult.backup());
            }
        } catch (AlfredException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Starts one run of the chatbot, saving to the usual file.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        new AlfredTheButler(SAVE_FILE).run();
    }
}
