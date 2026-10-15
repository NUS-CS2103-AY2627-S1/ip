package rex;

import java.io.IOException;

import rex.command.Command;

/**
 * A task-tracking chatbot that reads commands from the console.
 *
 * This class holds the three parts the program is built from — the user
 * interface, the saved file, and the task list — and does little itself
 * beyond running the loop that reads a command, carries it out, and saves
 * the result.
 */
public class Rex {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Whether the last command asked to end the session.
     *
     * Both ways of running Rex use this one flag: the console loop reads it
     * to decide whether to go round again, and the GUI reads it through
     * isExit() to decide whether to close the window.
     */
    private boolean isExit = false;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * Note that the file is not read here. Loading is left to run(), because
     * anything the load has to report is shown to the user, and that has to
     * happen after the greeting rather than before it.
     *
     * @param first the first part of the path to the save file, e.g. "data".
     * @param more the remaining parts, e.g. "rex.txt".
     */
    public Rex(String first, String... more) {
        this.ui = new Ui();
        this.storage = new Storage(first, more);
    }

    /**
     * Starts the chatbot, keeping its tasks in data/rex.txt beside wherever
     * the program was run from.
     *
     * @param args not used; the save file is fixed rather than taken from the
     *     command line, so that the program is started the same way every time.
     */
    public static void main(String[] args) {
        new Rex("data", "rex.txt").run();
    }

    /** Greets the user, then reads and carries out commands until told to stop. */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();
        ui.showReady();

        while (!isExit) {
            try {
                Command command = Parser.parse(ui.readCommand());
                assert command != null : "The parser answers an unrecognized word with an UnknownCommand";
                command.execute(tasks, ui);

                // Reached only if the command succeeded, so the list is saved
                // exactly when it actually changed.
                if (command.isTaskListChanged()) {
                    saveTasks();
                }
                isExit = command.isExit();
            } catch (RexException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Greets the user and loads their tasks, for a GUI that has no loop of its
     * own.
     *
     * The console version of this is the opening of run(). It is separate here
     * because a window is already on screen before the user types anything, so
     * the greeting has to be handed back to be shown rather than printed.
     *
     * @return the greeting, together with anything the load had to report.
     */
    public String start() {
        ui.startCapture();
        ui.showWelcome();
        tasks = loadTasks();
        ui.showReady();
        return ui.takeCapture();
    }

    /**
     * Carries out one command and returns what the user should be shown.
     *
     * This is the GUI's equivalent of one turn of run()'s loop. Note that the
     * commands themselves are untouched: they still report through the Ui, and
     * it is the Ui that has been told to collect its output instead of
     * printing it.
     *
     * @param input the whole line the user typed.
     * @return Rex's reply, ready to be put in a dialog box.
     */
    public String getResponse(String input) {
        ui.startCapture();
        try {
            Command command = Parser.parse(input);
            assert command != null : "The parser answers an unrecognized word with an UnknownCommand";
            command.execute(tasks, ui);

            if (command.isTaskListChanged()) {
                saveTasks();
            }
            isExit = command.isExit();
        } catch (RexException e) {
            ui.showError(e.getMessage());
        }
        return ui.takeCapture();
    }

    /** Returns whether the last command asked to end the session. */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Loads the saved tasks, starting with an empty list if they could not be
     * read. Refusing to start would leave the user with no way to use the
     * program at all, which is worse than starting fresh.
     */
    private TaskList loadTasks() {
        try {
            TaskList loaded = new TaskList(storage.load());
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                ui.showSkippedLines(skipped, storage.getFile());
            }
            return loaded;
        } catch (IOException e) {
            ui.showLoadingError(storage.getFile());
            return new TaskList();
        }
    }

    /**
     * Saves the tasks, warning the user if the save failed but letting the
     * session carry on. A failed save is not the user's mistake, so it is not
     * reported as a RexException (which would suggest they typed something
     * wrong), and it must not end the session: the tasks they
     * have added are still usable in memory.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showSavingError(storage.getFile());
        }
    }
}
